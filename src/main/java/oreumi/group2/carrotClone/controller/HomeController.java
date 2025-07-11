package oreumi.group2.carrotClone.controller;

import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class HomeController {

    private final UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showHome( Principal principal,
                           Model model){

        Optional<User> user = userRepository.findByUsername(principal.getName());
        System.out.println(user);

        model.addAttribute("user",user);
        return "home";
    }
}