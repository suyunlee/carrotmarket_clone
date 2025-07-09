package oreumi.group2.carrotClone.controller;

import lombok.RequiredArgsConstructor;
import oreumi.group2.carrotClone.DTO.ChatMessageDTO;
import oreumi.group2.carrotClone.DTO.ReadReceiptDTO;
import oreumi.group2.carrotClone.model.ChatMessage;
import oreumi.group2.carrotClone.service.ChatMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chat/room/{roomId}")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate template;

    /* 채팅방 입장 */
    @GetMapping
    public String enterChat(
            @PathVariable Long roomId,
            @RequestParam String username,
            /* Principal principal <- 로그인시 필요 */
            Model model)
    {
        // String username = principal.getName();

        //입장 전 상대방 메세지 읽음 처리
        chatMessageService.markRead(roomId,username);
        
        model.addAttribute("roomId", roomId);
        model.addAttribute("currentUser",username);

        List<ChatMessageDTO> dtos = chatMessageService.getMessages(roomId)
                        .stream()
                        .map(ChatMessageDTO :: fromEntity)
                        .toList();

        model.addAttribute("messages",dtos );
        return "chat-room";
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
            @Payload ChatMessageDTO payload
            /* Principal principal <- 로그인시 필요 */ )
    {
        // String username = principal.getName(); 로그인된 유저
        /* 메세지 저장 (방 ID, 내용, 보낸 사람아름) */
        ChatMessage saved = chatMessageService.saveMessage(
                roomId,
                payload.getContent(),
                payload.getSenderUsername()
                // username
        );
        // DTO 로 변환 (필터링)
        ChatMessageDTO dto = ChatMessageDTO.fromEntity(saved);
        // 같은 방 (topic) 구독자에게 메세지 전달
        template.convertAndSend("/topic/chat/" + roomId,dto);
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