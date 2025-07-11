package oreumi.group2.carrotClone.controller;

import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/maps")
public class MapsController {
    @Value("${google.maps.api.key}")
    private String apiKey;

    @Autowired
    UserService userService;

    @GetMapping("/permission")
    public String showPermissionPage(Principal principal, Model model){
        Optional<User> user = userService.findByUsername(principal.getName());
        if (user.isEmpty()) {
            return "users/login";
        }
        return "location/permission";
    }

    @GetMapping("/verify")
    public String showMaps(Principal principal, Model model){
        Optional<User> user = userService.findByUsername(principal.getName());
        if (user.isEmpty()) {
            return "users/login";
        }
        model.addAttribute("googleMapsApiKey", apiKey);
        return "location/verify";
    }

//    @PostMapping("/verify")
//    주소 명 >
//    어느 유저 저장할거냐? > 세션(priciple) > authen >>> 저장하기.
    @PostMapping("/verify")
    public String verifyLocation(Principal principal,
                                 @RequestParam String userCurrentAddress,
                                 RedirectAttributes redirectAttributes){


        //임시로 1번 유저에 무조건 위치 저장하도록 (h2-console로 유저 삽입)
        Optional<User> userOptional = userService.findByUsername(principal.getName());
        if (userOptional.isEmpty()) {
            return "users/login";
        }
        if(!userOptional.isEmpty()){
            User user = userOptional.get();
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
