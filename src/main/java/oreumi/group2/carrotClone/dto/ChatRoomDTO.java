package oreumi.group2.carrotClone.dto;

import lombok.Data;
import oreumi.group2.carrotClone.model.ChatMessage;
import oreumi.group2.carrotClone.model.ChatRoom;

import java.time.LocalDateTime;

@Data
public class ChatRoomDTO {

    private Long id, userId, postId;
    private String username, lastMessage,nickname,postImageUrl;
    private LocalDateTime lastMessageAt;
    private long unreadCount;
    private boolean isChatBot;

    public ChatRoomDTO() {}

    public ChatRoomDTO(Long id, Long userId, Long postId, String username,long unreadCount){
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.unreadCount = unreadCount;
        this.username = username;
        this.postImageUrl = "";
    }

    /* 채팅방 필터링 DTO */
    public static ChatRoomDTO fromEntity(ChatRoom r){
        return new ChatRoomDTO(
                r.getId(),
                r.getUser().getId(),
                r.getPost().getId(),
                r.getUser().getUsername(),
                0L
        );
    }

    /* 채팅방 메세지 수 DTO */
    public static ChatRoomDTO of(ChatRoom r, long unreadCount,ChatMessage lastMsg){
        ChatRoomDTO dto = new ChatRoomDTO();
        dto.id = r.getId();
        dto.userId = r.getUser().getId();
        dto.postId = (r.getPost() != null ? r.getPost().getId() : 0L);
        dto.username = r.getUser().getUsername();
        dto.nickname = r.getUser().getNickname();
        dto.unreadCount = unreadCount;
        dto.lastMessage = (lastMsg != null ? lastMsg.getContent() : "");
        dto.lastMessageAt = (lastMsg != null ? lastMsg.getCreatedAt() : null);
        if (r.getPost() != null && !r.getPost().getImages().isEmpty()) {
            dto.postImageUrl = r.getPost().getImages().get(0).getImageUrl();
        }
        dto.isChatBot = r.isChatBot();
        return dto;
    }
}