package oreumi.group2.carrotClone.controller;

import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.repository.UserRepository;
import oreumi.group2.carrotClone.security.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.Optional;

@Controller
@RequestMapping("/")
public class HomeController {

    private final UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showHome(@AuthenticationPrincipal PrincipalDetails principal,
                           Model model){

        Optional<User> user = userRepository.findByUsername(principal.getUser().getUsername());
        System.out.println(user);


        model.addAttribute("user", user);


        return "home";
    }
}