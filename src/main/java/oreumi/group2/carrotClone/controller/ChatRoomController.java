package oreumi.group2.carrotClone.controller;

import lombok.RequiredArgsConstructor;
import oreumi.group2.carrotClone.DTO.ChatRoomDTO;
import oreumi.group2.carrotClone.DTO.CreateRoomRequest;
import oreumi.group2.carrotClone.model.ChatRoom;
import oreumi.group2.carrotClone.model.Post;
import oreumi.group2.carrotClone.repository.PostRepository;
import oreumi.group2.carrotClone.repository.UserRepository;
import oreumi.group2.carrotClone.service.ChatRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/post")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @GetMapping("/{postId}/rooms")
    public List<ChatRoomDTO> listRooms(@PathVariable Long postId){
        return chatRoomService.findChatRoomsByPostId(postId)
                .stream()
                .map(ChatRoomDTO :: fromEntity)
                .toList();
    }

    @PostMapping("/{postId}/rooms")
    @ResponseBody
    public ChatRoomDTO createRoom(
            @PathVariable Long postId,
            @RequestBody CreateRoomRequest req // 로그인시 삭제
            /* Principal principal <- 로그인시 필요 */ )
    {
        // String username = principal.getName();

        // 사용자 확인
        Long userId = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() ->
                        new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "해당 유저는 찾을 수 없습니다."
                        )
                )
                .getId();

        /* 게시물 확인 */
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "게시물이 없습니다."
                        )
                );

        /* 판매자는 방 생성 금지 */
        if(post.getUser().getId().equals(userId)){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "판매자는 방을 만들수 없습니다."
            );
        }

        /* 채팅방 생성 (기존방 있으면 기존 방 전환) */
        ChatRoom room;
        try {
            room = chatRoomService.createChatRoom(postId, userId);
        }catch (IllegalArgumentException e){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }
        /* DTO 반환 후 리턴 */
        return ChatRoomDTO.fromEntity(room);
    }
}