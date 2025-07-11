package oreumi.group2.carrotClone.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import oreumi.group2.carrotClone.Config.CustomUserPrincipal;
import oreumi.group2.carrotClone.DTO.ChatRoomDTO;
import oreumi.group2.carrotClone.model.ChatRoom;
import oreumi.group2.carrotClone.model.Post;
import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.repository.PostRepository;
import oreumi.group2.carrotClone.repository.UserRepository;
import oreumi.group2.carrotClone.service.ChatRoomService;
import oreumi.group2.carrotClone.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/post")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /* 채팅방 목록 */
    @GetMapping("/{postId}/rooms")
    public List<ChatRoomDTO> listRooms(
            @PathVariable Long postId,
            @AuthenticationPrincipal Object principal)
    {

        User user = getCurrentUser(principal);
        String username = user.getUsername();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "게시물이 존재하지 않습니다."
                ));
        String seller = post.getUser().getUsername(); // 판매자

        List<ChatRoomDTO> allRooms = chatRoomService.getRoomsWithUnread(postId,username);

        // 3) 구매자 모드면 → 본인(username) 방만 필터
        if (!username.equals(seller)) {
            return allRooms.stream()
                    .filter(r -> r.getUsername().equals(username))
                    .toList();
        }
        // 4) 판매자 모드 → 전체 방 반환
        return allRooms;
    }

    /* 채팅방 생성 */
    @PostMapping("/{postId}/rooms")
    @ResponseBody
    public ChatRoomDTO createRoom(
            @PathVariable Long postId,
            @AuthenticationPrincipal Object principal)
    {
        User user = getCurrentUser(principal);
        String username = user.getUsername();

        // 사용자 확인
        Long userId = userRepository.findByUsername(username)
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

    private User getCurrentUser(Object principal) {

        if (principal != null) {
            User user = null;

            if (principal instanceof OAuth2User oauth2User) {
                Map<String, Object> attributes = oauth2User.getAttributes();
                user = (User) attributes.get("user");
                return user;
            }

            else if (principal instanceof CustomUserPrincipal customUser) {
                user = customUser.getUser();
                return user;
            }
        }
        return null;
    }
}