package oreumi.group2.carrotClone.dto;

import lombok.*;
import oreumi.group2.carrotClone.model.ChatMessage;

import java.time.LocalDateTime;


/**
 * 채팅 메세지 전송·조회 시 클라이언트와 주고받는 데이터 전송 객체(DTO)
 * 
 * <p>
 *     ChatMessage 엔티티의 주요 정보(id,내용, 발신자, 작성일시, 읽음 여부)를 담아 전송
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    private Long id;
    private String content;
    private String senderUsername;
    private String senderNickname;
    private LocalDateTime createdAt;
    private boolean isRead;

    /**
     * ChatMessage 엔티티를 ChatMessageDTO로 변환
     * 
     * @param m 변환할 ChatMessage 엔티티
     * @return 변환된 ChatMessageDTO 인스턴스
     */
    public static ChatMessageDTO fromEntity(ChatMessage m){
        return new ChatMessageDTO(
                m.getId(),
                m.getContent(),
                m.getSender().getUsername(),
                m.getSender().getNickname(),
                m.getCreatedAt(),
                m.isRead()
        );
    }
}