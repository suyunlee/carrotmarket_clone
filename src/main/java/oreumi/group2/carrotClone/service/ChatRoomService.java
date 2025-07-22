package oreumi.group2.carrotClone.service;

import oreumi.group2.carrotClone.dto.ChatRoomDTO;
import oreumi.group2.carrotClone.model.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomService {
    ChatRoom createChatRoom(Long postId, Long userId); /* 채팅방 생성 */
    ChatRoom findChatRoomById(Long id); /* id 기준 채팅방 조회 */
    List<ChatRoomDTO> getRoomsWithUnread(Long postId,String username); /* 채팅방 읽음 처리 */
    void confirmPost(Long postId, String username); // 거래완료 처리
    ChatRoom getOrCreateAIBotRoom(String username);
    List<ChatRoomDTO> getRoomsForUser(String username);
}