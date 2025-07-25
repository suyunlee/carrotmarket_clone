package oreumi.group2.carrotClone.controller;

import lombok.RequiredArgsConstructor;
import oreumi.group2.carrotClone.dto.ChatRoomDTO;
import oreumi.group2.carrotClone.model.Post;
import oreumi.group2.carrotClone.security.CustomUserPrincipal;
import oreumi.group2.carrotClone.dto.ChatMessageDTO;
import oreumi.group2.carrotClone.dto.ReadReceiptDTO;
import oreumi.group2.carrotClone.model.ChatMessage;
import oreumi.group2.carrotClone.model.ChatRoom;
import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.service.ChatMessageService;
import oreumi.group2.carrotClone.service.ChatRoomService;
import oreumi.group2.carrotClone.service.GeminiService;
import oreumi.group2.carrotClone.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * 채팅방 로직 중 채팅방 입장, 메세지 전달/읽음 담당하는 컨트롤러 클래스
 *
 * <ul>
 *      <li>채팅방 진입 (GET /chat/room/{roomId})</li>
 *      <li>채팅기록 전체 반환 (GET /chat/room/{roomId}/messages)</li>
 *      <li>채팅 메세지 전달 (MessageMapping /app/room/{roomId}/send)</li>
 *      <li>메세지 실시간 읽음 (MessageMapping /app/room/{roomId}/read)</li>
 *      <li>메세지 초기 읽음 (Post /chat/room/{roomId}/read)</li>
 * </ul>
 */

@Controller
@RequestMapping("/chat/room/{roomId}")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate template;
    private final ChatRoomService chatRoomService;
    private final UserService userService;
    private final GeminiService geminiService;

    /**
     * 채팅방 페이지 진입 (기존 채팅방있을 시 반환)
     *
     * @param roomId    조회할 채팅방 ID
     * @param principal 현재 로그인한 사용자 정보
     * @param model     뷰에 전달할 모델 객체
     * @return 채팅방 페이지 (chat/chat_message_room)
     */
    @GetMapping
    public String enterChat(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            Model model)
    {
        String username = principal.getUsername();

        List<ChatRoomDTO> myRooms = chatRoomService.getRoomsForUser(username);
        model.addAttribute("chatRooms",myRooms);
        chatMessageService.markRead(roomId,username);

        ChatRoom chatRoom = chatRoomService.findChatRoomById(roomId);
        model.addAttribute("roomId", roomId);
        model.addAttribute("currentUser",username);
        model.addAttribute("user",principal.getUser());

        Post post = chatRoom.getPost();
        if(post == null){
            model.addAttribute("post", null);
            model.addAttribute("postId", 0);
            model.addAttribute("postOwner","");
        }else{
            model.addAttribute("postId", post.getId());
            model.addAttribute("post",post);
            model.addAttribute("postOwner",post.getUser().getUsername());
            if(post.getImages().size() > 0){
                model.addAttribute("postImage", post.getImages().get(0).getImageUrl());
            }
        }
        model.addAttribute("isChatBot", chatRoom.isChatBot());
        List<ChatMessageDTO> dtos = chatMessageService.getMessages(roomId)
                        .stream()
                        .map(ChatMessageDTO :: fromEntity)
                        .toList();

        model.addAttribute("messages",dtos );
        return "chat/chat_message_room";
    }

    /**
     * 채팅방 전체 메세지 이력을 JSON 으로 반환
     *
     * @param roomId 조회할 채팅방 ID
     * @return 메세지 DTO 리스트
     */
    @GetMapping("/messages")
    @ResponseBody
    public List<ChatMessageDTO> getHistory(@PathVariable Long roomId) {
        return chatMessageService.getMessages(roomId)
                .stream()
                .map(ChatMessageDTO :: fromEntity) // DTO랑 비교해서 필터링
                .toList();
    }

    /**
     * 클라이언트가 보낸 채팅 메세지를 저장하고 브로드캐스트
     *
     * @param roomId 메세지를 보낼 채팅방 ID
     * @param payload 클라이언트에서 보낸 메세지 DTO
     * @param principal 메세지를 보낸 사용자 정보
     */
    @MessageMapping("/room/{roomId}/send")
    public void stompMessage(
            @DestinationVariable Long roomId,
            // @Payload 역직렬화 (JSON -> 객체)
            @Payload ChatMessageDTO payload,
           Principal principal)
    {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("로그인 유저를 찾을 수 없습니다." + principal.getName()));

        /* 메세지 저장 (방 ID, 내용, 보낸 사람아름) */
        ChatMessage saved = chatMessageService.saveMessage(
                roomId,
                payload.getContent(),
                principal.getName()
        );

        // DTO 로 변환 (필터링)
        ChatMessageDTO dto = ChatMessageDTO.fromEntity(saved);
        template.convertAndSend("/topic/chat/" + roomId,dto);

        ChatRoom room = chatRoomService.findChatRoomById(roomId);
        if (room.isChatBot()) {
            chatMessageService.markSingleRead(saved.getId());
            ReadReceiptDTO receipt = new ReadReceiptDTO();
            receipt.setReaderUsername("chatbot");
            receipt.setMessageIds(List.of(saved.getId()));

            template.convertAndSend("/topic/chat/"
                    + roomId +
                    "/read",receipt
            );
            // Gemini 답변 생성
            String aiReplyText = geminiService.generateReply(payload.getContent());
            var aiSaved = chatMessageService.saveMessage(
                    roomId,
                    aiReplyText,
                    "chatbot"
            );
            ChatMessageDTO aiDto = ChatMessageDTO.fromEntity(aiSaved);
            template.convertAndSend("/topic/chat/" + roomId , aiDto);
        }
    }

    /**
     * 실시간 읽음 처리 요청을 받아 해당 메세지를 읽음 처리하고 알림 전송
     *
     * @param roomId 읽음 처리할 채팅방 ID
     * @param receiptDTO 읽음 처리할 메세지 ID 리스트를 담은 DTO
     */
    @MessageMapping("/room/{roomId}/read")
    public void stompRead(
            @DestinationVariable Long roomId,
            @Payload ReadReceiptDTO receiptDTO)
    {
        receiptDTO.getMessageIds().forEach(chatMessageService::markSingleRead);
        template.convertAndSend("/topic/chat/" + roomId + "/read", receiptDTO);
    }

    /**
     * HTTP Post 요청으로 채팅방 입장 시 일괄 읽음 처리
     * 
     * @param roomId 읽음 처리할 채팅방 ID
     * @param username 읽음 처리할 사용자 이름
     */
    @PostMapping("/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(@PathVariable Long roomId,
                         @RequestParam String username){
        chatMessageService.markRead(roomId,username);
    }
}