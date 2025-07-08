package oreumi.group2.carrotClone.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import oreumi.group2.carrotClone.model.ChatRoom;

@Data
@AllArgsConstructor
public class ChatRoomDTO {

    private Long id, userId, postId;
    /* 채팅방 필터링 DTO */
    public static ChatRoomDTO fromEntity(ChatRoom r){
        return new ChatRoomDTO(
                r.getId(),
                r.getUser().getId(),
                r.getPost().getId()
        );
    }
}