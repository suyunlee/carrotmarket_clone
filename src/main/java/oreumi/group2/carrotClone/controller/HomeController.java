package oreumi.group2.carrotClone.controller;

import oreumi.group2.carrotClone.security.CustomUserPrincipal;
import oreumi.group2.carrotClone.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String showHome(@AuthenticationPrincipal CustomUserPrincipal principal,
                           Model model) {

        if (principal != null) {

            User user = principal.getUser();
            model.addAttribute("user", user);
        }
        return "home";
    }
}