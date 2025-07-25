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


/**
 * 지도 인증 로직 중 URL처리를 담당하는 컨트롤러 클래스
 *
 * <ul>
 *     <li>지도 권한 페이지 진입 (GET /maps/permission)</li>
 *     <li>지도 인증 페이지 진입 (GET /maps/verify)</li>
 *     <li>지도 인증 정보 주입 (POST /maps/verify)</li>
 * </ul>
 *
 */
@Controller
@RequestMapping("/maps")
public class MapsController {
    @Value("${google.maps.api.key}")
    private String apiKey;

    @Autowired
    UserService userService;

    /**
     * 유저 로그인 여부 체크 후, 지도 권한 체크 페이지 진입
     * @param principal 유저 정보 CustomPrincipal
     * @param model View 모델
     * @return location/permission 지도 권한 페이지
     */
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

    /**
     * 유저 정보, 위치 권한 체크 후 지도 인증 페이지 진입
     * 
     * @param principal 유저 정보 CustomPrincipal
     * @param model View 모델
     * @return location/verify 지도 인증 페이지
     */
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

    /**
     * 위치 인증 조건 만족 시, 해당 정보를 유저 엔티티에 전달 후 홈으로 복귀
     *
     * @param principal 유저 정보
     * @param userCurrentAddress 유저 위치(지역 스트링)
     * @param redirectAttributes 결과 전달을 위한 리다이렉트 속성
     * @return "redirect:/"  유저 정보 저장 후, 홈으로 리다이렉트
     */
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