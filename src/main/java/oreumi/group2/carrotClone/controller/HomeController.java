package oreumi.group2.carrotClone.controller;

import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class HomeController {
    @Autowired
    UserService userService;

    @GetMapping
    public String showHome(Principal principal, Model model){
        if(principal==null){
            return "home";
        }

        //principle > username 가져옴(아이디)
        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);

        if(!user.isEmpty()){
            model.addAttribute("user", user.get());
        }

        return "home";
    }
}