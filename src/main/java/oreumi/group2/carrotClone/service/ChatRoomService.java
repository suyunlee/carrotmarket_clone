package oreumi.group2.carrotClone.service;

import oreumi.group2.carrotClone.model.ChatRoom;

import java.util.List;

public interface ChatRoomService {
    ChatRoom createChatRoom(Long postId, Long userId);
    ChatRoom updateChatRoom(ChatRoom chatRoom);
    void deleteChatRoom(ChatRoom chatRoom);
    ChatRoom findChatRoomById(Long id);
    List<ChatRoom> findAllChatRooms();
    List<ChatRoom> findChatRoomsByUserId(Long userId);
}
