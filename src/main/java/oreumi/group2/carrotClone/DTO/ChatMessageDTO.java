package oreumi.group2.carrotClone.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import oreumi.group2.carrotClone.model.ChatMessage;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    private Long id;
    private String content;
    private String senderUsername;
    private String senderNickname;
    private LocalDateTime createdAt;
    private boolean isRead;

    /* 필터링된 DTO 반환 */
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