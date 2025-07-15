package oreumi.group2.carrotClone.repository;

import oreumi.group2.carrotClone.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findChatRoomsByUserId(Long userId); /* 로그인 한 유저 > 내 채팅방 호출용 */
    Optional<ChatRoom> findByPostIdAndUserId(Long postId, Long userId); /* 게시물 페이지 > 채팅하기 때 사용 */
    List<ChatRoom> findAllByPostId(Long postId); /* 특정 게시물 조회 */
    Optional<ChatRoom> findByUser_UsernameAndPostIsNull(String username);

    List<ChatRoom> findAllByUser_Username(String username);
    List<ChatRoom> findAllByPost_User_Username(String username);
}