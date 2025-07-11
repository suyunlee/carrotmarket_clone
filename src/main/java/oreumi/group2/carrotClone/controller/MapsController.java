package oreumi.group2.carrotClone.controller;

import oreumi.group2.carrotClone.Config.CustomUserPrincipal;
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

import java.security.Principal;
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
    public String showPermissionPage(@AuthenticationPrincipal Object principal, Model model){
        if (principal == null || principal.toString().equals("anonymousUser") ) {
            return "redirect:/users/login";
        }
        System.out.println(principal);
        return "location/permission";
    }

    @GetMapping("/verify")
    public String showMaps(@AuthenticationPrincipal Object principal, Model model){
        if (principal == null) {
            return "redirect:/users/login";
        }
        model.addAttribute("googleMapsApiKey", apiKey);
        return "location/verify";
    }

//    @PostMapping("/verify")
//    주소 명 >
//    어느 유저 저장할거냐? > 세션(priciple) > authen >>> 저장하기.
    @PostMapping("/verify")
    public String verifyLocation(@AuthenticationPrincipal Object principal,
                                 @RequestParam String userCurrentAddress,
                                 RedirectAttributes redirectAttributes){

        User user = null;

        if (principal != null) {
            // OAuth2 로그인
            if (principal instanceof OAuth2User oauth2User) {
                Map<String, Object> attributes = oauth2User.getAttributes();
                user = (User) attributes.get("user");
            }

            // Form 로그인
            else if (principal instanceof CustomUserPrincipal customUser) {
                user = customUser.getUser();
            }
        }
        //임시로 1번 유저에 무조건 위치 저장하도록 (h2-console로 유저 삽입)
        Optional<User> userOptional = userService.findByUsername(user.getUsername());
        if (userOptional.isEmpty()) {
            return "redirect:/users/login";
        }
        if(!userOptional.isEmpty()){
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
