package oreumi.group2.carrotClone.repository;

import oreumi.group2.carrotClone.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId); /* 날짜 순으로 채팅 메세지 찾기 */

    @Query("SELECT count(m) " +
            " FROM ChatMessage m " +
            " WHERE m.chatRoom.id = :roomId " +
            " AND m.sender.username <> :username " +
            " AND m.isRead = false ")
    long countUnread(@Param("roomId") Long roomId,
                     @Param("username") String username);

    @Modifying
    @Query("UPDATE ChatMessage m " +
            " SET m.isRead = true " +
            " WHERE m.chatRoom.id = :roomId " +
            " AND m.sender.username <> :username " +
            " AND m.isRead = false ")
    int markAllRead(
            @Param("roomId") Long roomId,
            @Param("username") String username);
}