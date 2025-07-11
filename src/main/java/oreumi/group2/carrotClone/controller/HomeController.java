package oreumi.group2.carrotClone.controller;

import oreumi.group2.carrotClone.Config.CustomUserPrincipal;
import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String showHome(Principal principal,
                           Model model){

        if (principal != null) {
            User user = null;

            // OAuth2 로그인
            if (principal instanceof OAuth2User oauth2User) {
                Map<String, Object> attributes = oauth2User.getAttributes();
                user = (User) attributes.get("user");
            }

            // Form 로그인
            else if (principal instanceof CustomUserPrincipal customUser) {
                user = customUser.getUser();
            }

            if (user != null) {
                System.out.println("로그인 유저: " + user.getNickname());
                System.out.println(user.getUsername());
                model.addAttribute("user", user);
            }
        }

        return "home";
    }
}