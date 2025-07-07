package oreumi.group2.carrotClone.repository;

import oreumi.group2.carrotClone.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId); /* 날짜 순으로 채팅 메세지 찾기 */
}