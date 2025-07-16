package oreumi.group2.carrotClone.service.impl;

import jakarta.persistence.EntityNotFoundException;
import oreumi.group2.carrotClone.dto.ChatRoomDTO;
import oreumi.group2.carrotClone.model.ChatMessage;
import oreumi.group2.carrotClone.model.ChatRoom;
import oreumi.group2.carrotClone.model.Post;
import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.repository.ChatMessageRepository;
import oreumi.group2.carrotClone.repository.ChatRoomRepository;
import oreumi.group2.carrotClone.repository.PostRepository;
import oreumi.group2.carrotClone.repository.UserRepository;
import oreumi.group2.carrotClone.service.ChatMessageService;
import oreumi.group2.carrotClone.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
public class ChatServiceImpl implements ChatRoomService, ChatMessageService {

    @Autowired ChatRoomRepository chatRoomRepository;
    @Autowired ChatMessageRepository chatMessageRepository;
    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;

    /* 채팅방 생성 */
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

    /* 개별 메세지 한 건 읽음 처리 */
    @Override
    public void markSingleRead(Long messageId) {
        ChatMessage m = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메세지가 존재하지않습니다."));
        m.setRead(true);
    }

    /* 보낸 사람이 아닌 상대 메세지를 모두 읽음 처리 */
    @Override
    public void markRead(Long roomId, String username) {
        chatMessageRepository.markAllRead(roomId,username);
    }

    /* 채팅방에서 읽지않는 메세지 개수 */
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

    /* 채팅방 업데이트 */
    @Override
    public ChatRoom updateChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.findById(chatRoom.getId()).map(
                existingRoom->{
                    existingRoom.setMessages(chatRoom.getMessages());
                    return chatRoomRepository.save(existingRoom);
                }).orElseThrow(()->new EntityNotFoundException("존재하지 않는 채팅방입니다."));
    }

    /* 채팅방 삭제 */
    @Override
    public void deleteChatRoom(ChatRoom chatRoom) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatRoom.getId());

        if(!chatRoomOptional.isEmpty()){
            chatRoomRepository.delete(chatRoomOptional.get());
        }else{
            throw new EntityNotFoundException("존재하지 않는 채팅방입니다.");
        }
    }

    /* Id 기준 채팅방 조회 */
    @Override
    public ChatRoom findChatRoomById(Long id) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(id);

        if(!chatRoomOptional.isEmpty()){
            return chatRoomOptional.get();
        }else{
            return null;
        }
    }

    /* 특정 게시물 채팅방 조회 */
    @Override
    public List<ChatRoom> findChatRoomsByPostId(Long id) {
        return chatRoomRepository.findAllByPostId(id);
    }

    /* 전체 조회 */
    @Override
    public List<ChatRoom> findAllChatRooms(){
        return chatRoomRepository.findAll();
    }

    /* UserId 기준 조회 */
    @Override
    public List<ChatRoom> findChatRoomsByUserId(Long userId) {
        return chatRoomRepository.findChatRoomsByUserId(userId);
    }

    /* postId , userId 기준 조회 */
    @Override
    public Optional<ChatRoom> findByPostIdAndUserId(Long postId, Long userId) {
        Optional<ChatRoom> c = chatRoomRepository.findByPostIdAndUserId(postId,userId);

        if(!c.isEmpty()){
            return c;
        }
        else{
            return null;
        }
    }

    /* 메세지 저장 */
    @Override
    public ChatMessage saveMessage(Long chatRoomId, String content, String username) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));

        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("유저가 없습니다."));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content)
                .build();

        ChatMessage saved = chatMessageRepository.save(message);
        saved.getChatRoom().getPost().getUser().getUsername();
        saved.getChatRoom().getUser().getUsername();
        return saved;
    }

    /* 메세지 get */
    public List<ChatMessage> getMessages(Long chatRoomId) {
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
        return messages;
    }
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