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

@Controller
@RequestMapping("/chat/room/{roomId}")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate template;
    private final ChatRoomService chatRoomService;
    private final UserService userService;
    private final GeminiService geminiService;

    /* 채팅방 입장 */
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

    // 클라이언트가 이 엔드포인트를 구독하면 과거 메시지 전체를 반환
    @GetMapping("/messages")
    @ResponseBody
    public List<ChatMessageDTO> getHistory(@PathVariable Long roomId) {
        return chatMessageService.getMessages(roomId)
                .stream()
                .map(ChatMessageDTO :: fromEntity) // 만들어둔 Entity 랑 비교해서 필터링
                .toList();
    }

    @MessageMapping("/room/{roomId}/send")
    public void stompMessage(
            @DestinationVariable Long roomId,
            // @Payload 역직렬화 (JSON -> 객체)
            @Payload ChatMessageDTO payload,
           Principal principal)
    {
        String username = principal.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("로그인 유저를 찾을 수 없습니다." + username));

        /* 메세지 저장 (방 ID, 내용, 보낸 사람아름) */
        ChatMessage saved = chatMessageService.saveMessage(
                roomId,
                payload.getContent(),
                username
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

    /* 실시간 읽음 처리 */
    @MessageMapping("/room/{roomId}/read")
    public void stompRead(
            @DestinationVariable Long roomId,
            @Payload ReadReceiptDTO receiptDTO)
    {
        receiptDTO.getMessageIds().forEach(chatMessageService::markSingleRead);
        template.convertAndSend("/topic/chat/" + roomId + "/read", receiptDTO);
    }

    @PostMapping("/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(@PathVariable Long roomId,
                         @RequestParam String username){
        chatMessageService.markRead(roomId,username);
    }
}