package oreumi.group2.carrotClone.DTO;


import lombok.Data;
import oreumi.group2.carrotClone.model.ChatMessage;
import oreumi.group2.carrotClone.model.ChatRoom;

@Data

public class ChatRoomDTO {

    private Long id, userId, postId;
    private String username, lastMessage;
    private long unreadCount;


    public ChatRoomDTO() {}

    public ChatRoomDTO(Long id, Long userId, Long postId, String username,long unreadCount){
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.unreadCount = unreadCount;
        this.username = username;
    }

    public ChatRoomDTO(Long id, Long userId, Long postId, String username,long unreadCount,String lastMessage){
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.unreadCount = unreadCount;
        this.username = username;
        this.lastMessage = lastMessage;
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
        return new ChatRoomDTO(
                r.getId(),
                r.getUser().getId(),
                r.getPost().getId(),
                r.getUser().getUsername(),
                unreadCount,
                lastMsg != null ? lastMsg.getContent() : ""
        );
    }
}