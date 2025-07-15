package oreumi.group2.carrotClone.controller;

import oreumi.group2.carrotClone.security.CustomUserPrincipal;
import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/maps")
public class MapsController {
    @Value("${google.maps.api.key}")
    private String apiKey;

    @Autowired
    UserService userService;

    @GetMapping("/permission")
    public String showPermissionPage(@AuthenticationPrincipal CustomUserPrincipal principal, Model model){
        if (principal == null || principal.toString().equals("anonymousUser") ) {
            return "redirect:/users/login";
        }

        if(principal != null) {
            User user = principal.getUser();
            model.addAttribute("user", user);
        }

        return "location/permission";
    }

    @GetMapping("/verify")
    public String showMaps(@AuthenticationPrincipal CustomUserPrincipal principal, Model model){
        if (principal == null) {
            return "redirect:/users/login";
        }

        if(principal != null) {
            User user = principal.getUser();
            model.addAttribute("user", user);
        }
        model.addAttribute("googleMapsApiKey", apiKey);
        return "location/verify";
    }

    @PostMapping("/verify")
    public String verifyLocation(@AuthenticationPrincipal CustomUserPrincipal principal,
                                 @RequestParam String userCurrentAddress,
                                 RedirectAttributes redirectAttributes){

        User user = null;
        if(principal != null) {
            user = principal.getUser();
        }

        if (user == null) {
            return "redirect:/users/login";
        }
        if(user != null){
            user.setLocation(userCurrentAddress);
            user.setNeighborhoodName(userCurrentAddress);
            user.setNeighborhoodVerified(true);
            user.setNeighborhoodVerifiedAt(LocalDateTime.now());

            userService.updateUser(user);

            redirectAttributes.addFlashAttribute("message", "위치 인증이 완료되었습니다. 내 동네 : " + userCurrentAddress);
        }else{
            redirectAttributes.addFlashAttribute("message", "위치 인증에 실패했습니다. 다시 시도해주세요");
        }

        return "redirect:/";
    }
}