package oreumi.group2.carrotClone.controller;

import lombok.RequiredArgsConstructor;
import oreumi.group2.carrotClone.model.ChatRoom;
import oreumi.group2.carrotClone.security.CustomUserPrincipal;
import oreumi.group2.carrotClone.service.ChatRoomService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 헤더 채팅하기 URL를 당하는 컨틀롤러 클래스
 *
 * <ul>
 *      <li>채팅하기 페이지 진입(GET /chat/post/ai)</li> 
 *  </ul>
 */
@Controller
@RequiredArgsConstructor
public class AIController {

    private final ChatRoomService chatRoomService;

    /**
     * 헤더 채팅하기 접속시 기존 AI 채팅방 확인후 AI 채팅방으로 이동
     *
     * @param principal 현재 로그인한 사용자 정보
     * @return /chat/room
     */
    @GetMapping("/chat/post/ai")
    public String enterAIBot(@AuthenticationPrincipal CustomUserPrincipal principal){
        ChatRoom aiRoom = chatRoomService.getOrCreateAIBotRoom(principal.getUsername());
        return "redirect:/chat/room/" + aiRoom.getId();
    }
}