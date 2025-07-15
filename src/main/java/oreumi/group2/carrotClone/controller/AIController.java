package oreumi.group2.carrotClone.controller;


import lombok.RequiredArgsConstructor;
import oreumi.group2.carrotClone.model.ChatRoom;
import oreumi.group2.carrotClone.security.CustomUserPrincipal;
import oreumi.group2.carrotClone.service.ChatRoomService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AIController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/chat/post/ai")
    public String enterAIBot(@AuthenticationPrincipal CustomUserPrincipal principal){
        ChatRoom aiRoom = chatRoomService.getOrCreateAIBotRoom(principal.getUsername());
        return "redirect:/chat/room/" + aiRoom.getId();
    }
}
