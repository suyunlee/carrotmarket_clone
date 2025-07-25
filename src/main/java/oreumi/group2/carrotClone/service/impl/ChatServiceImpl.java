package oreumi.group2.carrotClone.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import oreumi.group2.carrotClone.dto.ChatRoomDTO;
import oreumi.group2.carrotClone.model.ChatMessage;
import oreumi.group2.carrotClone.model.ChatRoom;
import oreumi.group2.carrotClone.model.Post;
import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.model.enums.UserRole;
import oreumi.group2.carrotClone.repository.ChatMessageRepository;
import oreumi.group2.carrotClone.repository.ChatRoomRepository;
import oreumi.group2.carrotClone.repository.PostRepository;
import oreumi.group2.carrotClone.repository.UserRepository;
import oreumi.group2.carrotClone.service.ChatMessageService;
import oreumi.group2.carrotClone.service.ChatRoomService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * ChatRoomService 와 ChatMessageService 인터페이스를 구현하는 서비스 클래스 <br>
 * 채팅방 생성·조회, 메세지 저장·조회, 읽음 처리, 거래 확정 등 <br>
 * 채팅 관련 비지니스 로직 담당
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatRoomService, ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * 지정된 게시물(postId)과 구매자(buyerId)로 채팅방을 생성하거나,
     * 기존 채팅방이 있으면 이를 반환
     *
     * @param postId    채팅방 대상 게시물 ID
     * @param buyerId   구매자 사용자 ID
     * @return  생성 또는 조회된 ChatRoom 엔티티
     * @throws EntityNotFoundException  게시물이 존재하지 않을 경우
     * @throws IllegalArgumentException 판매자는 채팅방을 생성할 수 없을 때
     */
    @Override
    public ChatRoom createChatRoom(Long postId, Long buyerId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new EntityNotFoundException("게시물이 존재하지않습니다."));
        /* 판매자 아이디 */
        Long sellerId = post.getUser().getId();
        /* 구매자인 경우 */
        if(sellerId.equals(buyerId)){
            throw new IllegalArgumentException("판매자는 방을 생성할 수 없습니다.");
        }

        return chatRoomRepository.findByPostIdAndUserId(postId, buyerId)
                .orElseGet(()->{

                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.setPost(post);
                    chatRoom.setUser(
                            userRepository.findById(buyerId)
                                    .orElseThrow(() -> new EntityNotFoundException("유저가 없습니다."))
                    );
                    chatRoom.setChatBot(false);

                    return chatRoomRepository.save(chatRoom);
                });
    }

    /**
     * 특정 메세지 하나를 읽음 처리
     * 
     * @param messageId 읽음 처리할 메세지 ID
     * @throws RuntimeException 메세지가 없을 때
     */
    @Override
    public void markSingleRead(Long messageId) {
        ChatMessage m = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메세지가 존재하지않습니다."));
        m.setRead(true);
    }

    /**
     * 채팅방(roomId) 내에서 지정 사용자(username) 가
     * 아직 읽지 않은 모든 메세지를 읽음 처리(markAllRead)한다
     * @param roomId    채팅방 ID
     * @param username  처리 대상 사용자 이름
     */
    @Override
    public void markRead(Long roomId, String username) {
        chatMessageRepository.markAllRead(roomId,username);
    }

    /**
     * 특정 게시물(postId)에 속한 모든 채팅방을 조회하고,<br>
     * 각 방별로 읽지 않은 메세지 개수(unreadCount)와 <br>
     * 마지막 메세지(lastMsg)를 포함한 DTO 리스트를 반환
     *
     * @param postId    게시물 ID
     * @param username  현재 사용자 이름(읽음 카운터 계산용)
     * @return  ChatRoomDTO 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomDTO> getRoomsWithUnread(Long postId, String username) {
        List<ChatRoom> rooms = chatRoomRepository.findAllByPostId(postId);

        return rooms.stream()
                .map(room ->{
                    long unread = chatMessageRepository.countUnread(room.getId(), username);

                    ChatMessage lastMsg = chatMessageRepository
                            .findTopByChatRoomIdOrderByCreatedAtDesc(room.getId())
                            .orElse(null);
                    return ChatRoomDTO.of(room, unread,lastMsg);
                })
                .toList();
    }

    /**
     * ID로 채팅방을 조회
     * 
     * @param id 조회할 채팅방 ID
     * @return  ChatRoom 엔티티 (없으면 null)
     */
    @Override
    public ChatRoom findChatRoomById(Long id) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(id);

        if(!chatRoomOptional.isEmpty()){
            return chatRoomOptional.get();
        }else{
            return null;
        }
    }

    /**
     * 채팅방에 새로운 메시지를 생성·저장한다.
     * username이 실제 유저가 아니면 챗봇 계정으로 간주하여 처리한다.
     *
     * @param chatRoomId 채팅방 ID
     * @param content    메시지 내용
     * @param username   발신자 사용자 이름 또는 "chatbot"
     * @return 저장된 ChatMessage 엔티티
     * @throws RuntimeException 채팅방이 없을 때
     */
    @Override
    @Transactional
    public ChatMessage saveMessage(Long chatRoomId, String content, String username) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));

        User sender;
        // 2) 먼저 실제 로그인 유저인지 확인
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            sender = userOpt.get();
        }
        // 3) 아니라면 챗봇 메시지로 간주해서, 챗봇 계정을 조회하거나 생성
        else {
            sender = userRepository.findByUsername("chatbot@naver.com")
                    // 챗봇 계정이 없으면 새로 만들어서 저장
                    .orElseGet(() -> {
                        User chatbot = new User();
                        chatbot.setUsername("chatbot@naver.com");
                        chatbot.setPassword("qwe123@!#q");
                        chatbot.setRole(UserRole.USER);
                        chatbot.setNickname("chatbot");
                        return userRepository.save(chatbot);
                    });
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content)
                .build();

        ChatMessage saved = chatMessageRepository.save(message);
        return saved;
    }

    /**
     * 채팅방(chatRoomId)에 속한 모든 메시지를 작성일 순으로 조회한다.
     *
     * @param chatRoomId 채팅방 ID
     * @return 메시지 리스트
     */

    public List<ChatMessage> getMessages(Long chatRoomId) {
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
        return messages;
    }

    /**
     * 게시물 거래 확정(confirm) 처리.
     * 작성자만 호출 가능하다.
     *
     * @param postId   확정할 게시물 ID
     * @param username 요청자 사용자 이름
     * @throws EntityNotFoundException 게시물이 없을 때
     * @throws AccessDeniedException   작성자가 아닐 때
     */
    @Override
    public void confirmPost(Long postId, String username){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

        if(!post.getUser().getUsername().equals(username)){
            throw new AccessDeniedException("작성자만 거래 확정할 수 있습니다.");
        }

        post.setSold(true);
        postRepository.save(post);
    }

    /**
     * 사용자의 AI 챗봇 전용 채팅방을 조회하거나,
     * 없으면 새로 생성하여 반환한다.
     *
     * @param username 사용자 이름
     * @return 챗봇 채팅방 ChatRoom 엔티티
     */

    @Override
    public ChatRoom getOrCreateAIBotRoom(String username) {
        return chatRoomRepository.findByUser_UsernameAndPostIsNullAndIsChatBotTrue(username)
                .orElseGet(() -> {
                    User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
                    ChatRoom aiRoom = ChatRoom.builder()
                            .user(user)
                            .post(null)
                            .isChatBot(true)
                            .build();
                    return chatRoomRepository.save(aiRoom);
                });
    }

    /**
     * 지정된 사용자(username)가 참여 중인 모든 채팅방을 DTO로 반환한다.
     *
     * @param username 사용자 이름
     * @return ChatRoomDTO 목록
     */
    @Override
    public List<ChatRoomDTO> getRoomsForUser(String username) {
        List<ChatRoom> byUser = chatRoomRepository.findAllByUser_Username(username);

        // 2) seller 로서 post.user 인 방들
        List<ChatRoom> byPostOwner = chatRoomRepository.findAllByPost_User_Username(username);

        // 3) 둘 합치고 중복 제거
        return Stream.concat(byUser.stream(), byPostOwner.stream())
                .distinct()
                .map(room -> {
                    long unread = chatMessageRepository.countUnread(room.getId(), username);
                    ChatMessage last = chatMessageRepository
                            .findTopByChatRoomIdOrderByCreatedAtDesc(room.getId())
                            .orElse(null);
                    return ChatRoomDTO.of(room, unread, last);
                })
                .toList();
    }
}