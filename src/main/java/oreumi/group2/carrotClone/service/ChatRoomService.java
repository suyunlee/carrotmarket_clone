package oreumi.group2.carrotClone.service;

import oreumi.group2.carrotClone.dto.ChatRoomDTO;
import oreumi.group2.carrotClone.model.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomService {
    ChatRoom createChatRoom(Long postId, Long userId); /* 채팅방 생성 */
    ChatRoom updateChatRoom(ChatRoom chatRoom); /* 채팅방 업데이트 (메세지) */
    void deleteChatRoom(ChatRoom chatRoom); /* 채팅방 삭제 */
    ChatRoom findChatRoomById(Long id); /* id 기준 채팅방 조회 */
    List<ChatRoom> findAllChatRooms(); /* 모든 채팅방 조회 */
    List<ChatRoom> findChatRoomsByUserId(Long userId); /* userId 기준 조회 */
    Optional<ChatRoom> findByPostIdAndUserId(Long postId, Long userId); /* 게시물 페이지 > 채팅하기 때 사용 */
    List<ChatRoom> findChatRoomsByPostId(Long id);

    List<ChatRoomDTO> getRoomsWithUnread(Long postId,String username); /* 채팅방 읽음 처리 */
}