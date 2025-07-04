package oreumi.group2.carrotClone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("chat")
public class ChatController {

    @GetMapping("/{roomId}")
    public String showChat(@RequestParam Long roomId){
        return "";
    }

    @PostMapping("/{roomId}/send")
    public String postChat(@RequestParam Long roomId){
        return "";
    }
}