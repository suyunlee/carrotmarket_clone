//package oreumi.group2.carrotClone.controller;
//
//import oreumi.group2.carrotClone.Config.CustomUserPrincipal;
//import oreumi.group2.carrotClone.model.User;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.web.bind.annotation.*;
//
//@ControllerAdvice
//public class GlobalUserController {
//
//    @ModelAttribute("user")
//    public User globalUser(@AuthenticationPrincipal Object principal) {
//        if (principal == null) return null;
//
//        if (principal instanceof CustomUserPrincipal customUser) {
//            return customUser.getUser();  // Form 로그인 사용자
//        }
//
//        if (principal instanceof OAuth2User oauth2User) {
//            return (User) oauth2User.getAttributes().get("user");  // OAuth2 사용자
//        }
//
//        return null;
//    }
//}