package oreumi.group2.carrotClone.controller;

import lombok.RequiredArgsConstructor;
import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.security.CustomUserPrincipal;
import oreumi.group2.carrotClone.dto.ChatRoomDTO;
import oreumi.group2.carrotClone.model.ChatRoom;
import oreumi.group2.carrotClone.model.Post;
import oreumi.group2.carrotClone.repository.PostRepository;
import oreumi.group2.carrotClone.repository.UserRepository;
import oreumi.group2.carrotClone.service.ChatRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 게시물(post) 기준으로 채팅방 생성, 조회, 확인 (확정) 기능을 제공하는 컨트롤러 클래스
 *
 * <ul>
 *     <li>채팅방 목록 반환 (GET /chat/post/{postId}/rooms)</li>
 *     <li>채팅방 생성 (POST /chat/post/{postId}/rooms)</li>
 *     <li>거래 확정하기 기능(POST /chat/post/{postId}/confirm)</li>
 *     <li>로그인한 사용자 채팅방 목록반환 (GET /chat/post/rooms)</li>
 * </ul>
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/post")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * 특정 게시물에대한 채팅방 목록 조회 (판매자/ 구매자)
     *
     * @param postId    조회할 게시물 Id
     * @param principal 인증된 사용자 정보
     * @param full      true면 전체 방(all), false면 해당 게시물 내에서 본인과 관련된 방만
     * @return ChatRoomDTO 리스트
     * @throws ResponseStatusException 게시물이 존재하지 않을 경우 400 에러
     */
    @GetMapping("/{postId}/rooms")
    public List<ChatRoomDTO> listRooms(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(required = false, defaultValue = "false") boolean full)
    {
        String username = principal.getUsername();
        if(full) { return chatRoomService.getRoomsForUser(username); }

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

    /**
     * 게시물에 대한 채팅방을 생성
     * 기존에 동일한 구매자-판매자 방이 있으면 해당 방을 반환
     *
     * @param postId    채팅방을 생성할 게시물 OID
     * @param principal 인증된 사용자 정보
     * @return  생성(또는 조회) 된 ChatRoomDTO
     * @throws ResponseStatusException 본인 게시물에는 방 생성 불가, 사용자나 게시물이 없는 경우 400
     */
    @PostMapping("/{postId}/rooms")
    @ResponseBody
    public ChatRoomDTO createRoom(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserPrincipal principal)
    {
        String username = principal.getUsername();

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

    /**
     * 판매자가 거래 확정 버튼을 눌렀을 때 호출
     * 트랜잭션을 통해 채팅방 상태 등 업데이트
     * 
     * @param postId    확정한 게시물 Id
     * @param principal 인증된 사용자 정보
     * @return HTTTP 200 OK
     * @throws ResponseStatusException 사용자를 찾을 수 없을 경우 400 에러
     */
    @PostMapping("/{postId}/confirm")
    @Transactional
    public ResponseEntity<Void> confirmPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserPrincipal principal)
    {

        User user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."
                ));

        chatRoomService.confirmPost(postId, principal.getUsername());
        return ResponseEntity.ok().build();
    }

    /**
     *  로그인한 사용자가 참여 중인 모든 채팅방 목록을 조회
     *  
     * @param principal 인증된 사용자 정보
     * @return ChatRoomDTO 리스트를 감싼 ResponseEntity
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDTO>> getAllRoomsByUser(
            @AuthenticationPrincipal  CustomUserPrincipal principal ) {
        List<ChatRoomDTO> rooms = chatRoomService.getRoomsForUser(principal.getUsername());
        return ResponseEntity.ok(rooms);
    }
}