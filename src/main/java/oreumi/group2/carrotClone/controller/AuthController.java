package oreumi.group2.carrotClone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class AuthController {

    @GetMapping("/signup")
    public String showRegister(){
        return "";
    }

    @PostMapping
    public String postRegister(){
        return "";
    }
}