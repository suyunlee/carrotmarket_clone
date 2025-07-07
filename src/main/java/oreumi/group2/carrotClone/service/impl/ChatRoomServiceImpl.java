package oreumi.group2.carrotClone.service.impl;

import jakarta.persistence.EntityNotFoundException;
import oreumi.group2.carrotClone.model.ChatRoom;
import oreumi.group2.carrotClone.repository.ChatRoomRepository;
import oreumi.group2.carrotClone.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChatRoomServiceImpl implements ChatRoomService {
    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Override
    public ChatRoom createChatRoom(Long postId, Long userId) {
        return chatRoomRepository.findByPostIdAndUserId(postId, userId)
                .orElseGet(()->{
                    /* TODO : user, post 에 대한 repository 와 연결 필요 */

                    //user 생성 (parameter userId)
                    //post 생성 (parameter postId)

                    //post, user, isChatBot = false로 생성
                    ChatRoom chatRoom = new ChatRoom();
                    return chatRoomRepository.save(chatRoom);
                });
    }

    @Override
    public ChatRoom updateChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.findById(chatRoom.getId()).map(
                existingRoom->{
                    existingRoom.setMessages(chatRoom.getMessages());
                    return chatRoomRepository.save(existingRoom);
                }).orElseThrow(()->new EntityNotFoundException("존재하지 않는 채팅방입니다."));
    }

    @Override
    public void deleteChatRoom(ChatRoom chatRoom) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatRoom.getId());

        if(!chatRoomOptional.isEmpty()){
            chatRoomRepository.delete(chatRoomOptional.get());
        }else{
            throw new EntityNotFoundException("존재하지 않는 채팅방입니다.");
        }
    }

    @Override
    public ChatRoom findChatRoomById(Long id) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(id);

        if(!chatRoomOptional.isEmpty()){
            return chatRoomOptional.get();
        }else{
            return null;
        }
    }

    @Override
    public List<ChatRoom> findAllChatRooms(){
        return chatRoomRepository.findAll();
    }

    @Override
    public List<ChatRoom> findChatRoomsByUserId(Long userId) {
        return chatRoomRepository.findChatRoomsByUserId(userId);
    }
}
