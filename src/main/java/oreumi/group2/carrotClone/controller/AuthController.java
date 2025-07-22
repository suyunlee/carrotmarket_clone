package oreumi.group2.carrotClone.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oreumi.group2.carrotClone.dto.UserDTO;
import oreumi.group2.carrotClone.security.CustomUserPrincipal;
import oreumi.group2.carrotClone.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 페이지 표시
    @GetMapping("/signup")
    public String showRegister(Model model,
                               @AuthenticationPrincipal CustomUserPrincipal principal) {
        if(principal != null){
            return "redirect:/";
        }
        model.addAttribute("userDTO", new UserDTO());
        return "auth/signup";
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String postRegister(@ModelAttribute("user") @Valid UserDTO userDTO,
                               BindingResult result, Model model) {

        if (userService.findByUsername(userDTO.getUsername()).isPresent()) {
            model.addAttribute("usernameError", "이미 존재하는 이메일입니다.");
            return "auth/signup";
        }

        if (userDTO.getPassword().length() <= 2) {
            model.addAttribute("passwordError", "비밀번호는 3자리 이상이어야 합니다.");
            return "auth/signup";
        }
        userService.register(userDTO);
        return "redirect:/users/login"; // 로그인 페이지로 이동
    }

    @GetMapping("/login")
    public String showLogin(@AuthenticationPrincipal CustomUserPrincipal principal){
        if(principal != null){
            return "redirect:/";
        }
        return "auth/login";
    }
}