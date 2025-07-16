package oreumi.group2.carrotClone.controller;

import oreumi.group2.carrotClone.model.Post;
import oreumi.group2.carrotClone.security.CustomUserPrincipal;
import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/")
public class HomeController {
    @Autowired private PostService postService;

    @GetMapping
    public String showHome(@AuthenticationPrincipal CustomUserPrincipal principal,
                           Model model) {

        if(principal != null) {
            User user = principal.getUser();
            model.addAttribute("user", user);
        }

        Pageable pageable = PageRequest.of(0, 8);
        Page<Post> postPage = postService.findAll(pageable);
        model.addAttribute("page", postPage);
        return "home";
    }
}