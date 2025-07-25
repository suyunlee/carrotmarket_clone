package oreumi.group2.carrotClone.dto;

import lombok.Getter;
import lombok.Setter;
import oreumi.group2.carrotClone.model.ChatMessage;
import oreumi.group2.carrotClone.model.ChatRoom;

import java.time.LocalDateTime;

/**
 * 채팅방 목록 조회 및 표시를 위해 사용되는 데이터 전송 객체(DTO)
 * 
 * <p>
 *     채팅방 Id, 상대방 정보(username, nickname, userId),게시물 정보(postId, postImageUrl), <br>
 *     마지막 메세지 내용·작성일시, 읽지 않은 메세지 개수(unreadCount), 챗봇 여부(isChatBot) 등을 담는다.
 * </p>
 */
@Getter
@Setter
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

    /**
     * 최소 정보만 필터링 후 DTO로 변환
     * 
     * @param r 변환할 ChatRoom 엔티티
     * @return id, userId, postId, username,기본 unreadCount(0)만 세팅된 DTO
     */
    public static ChatRoomDTO fromEntity(ChatRoom r){
        return new ChatRoomDTO(
                r.getId(),
                r.getUser().getId(),
                r.getPost().getId(),
                r.getUser().getUsername(),
                0L
        );
    }

    /**
     * 읽지 않은 개수와 마지막 메세지를 포함한 DTO 변환
     * @param r           변환할 ChatRoom 엔티티
     * @param unreadCount 읽지않은 메세지 개수
     * @param lastMsg     마지막 메세지 엔티티(null일 수 있음)
     * @return 모든 필드를 채운 ChatRoomDTO 인스턴스
     */
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