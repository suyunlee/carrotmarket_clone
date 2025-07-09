package oreumi.group2.carrotClone.controller;

import jakarta.servlet.http.HttpSession;
import oreumi.group2.carrotClone.DTO.PostDTO;
import oreumi.group2.carrotClone.model.Category;
import oreumi.group2.carrotClone.model.Post;
import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.model.enums.AuthProvider;
import oreumi.group2.carrotClone.model.enums.UserRole;
import oreumi.group2.carrotClone.repository.CategoryRepository;
import oreumi.group2.carrotClone.repository.UserRepository;
import oreumi.group2.carrotClone.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired private PostService postService;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;


    /* 테스트 로그인 */
    @GetMapping("/test-login")
    public String testLogin(HttpSession session) {
        // 이미 DB에 같은 username이 있으면 가져오고, 없으면 새로 만듦
        String username = "testuser";
        User user = userRepository.findByUsername(username).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword("testpassword"); // 실제로는 암호화 필요
            newUser.setNickname("테스트유저");
            newUser.setPhoneNumber("010-0000-0000");
            newUser.setLocation("서울");
            newUser.setRole(UserRole.USER);
            newUser.setNeighborhoodVerified(true);
            newUser.setProvider(AuthProvider.LOCAL);
            return userRepository.save(newUser);
        });

        // 세션에 저장
        session.setAttribute("user", user);

        return "redirect:/posts/new"; // 테스트 후 리다이렉트
    }

    /* 전체 게시글 목록 */
    @GetMapping
    public String showPost(){
        return "";
    }

    /* 게시글 작성 폼 */
    @GetMapping("/new")
    public String showNewForm(HttpSession session,
                              @AuthenticationPrincipal OAuth2User oAuth2User,
                              Model model,
                              RedirectAttributes redirectAttributes){
        User user = getCurrentUser(session, oAuth2User);
        if(user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        if (!user.isNeighborhoodVerified()) {
            redirectAttributes.addFlashAttribute("error", "동네 인증이 필요합니다.");
            return "redirect:/maps";
        }
        model.addAttribute("mode", "new");
        model.addAttribute("user", user);
        return "post_form";
    }

    /* 게시글 등록 처리 */
    @PostMapping
    public String registerForm(@RequestParam("files") List<MultipartFile> files,
                               @RequestParam String title,
                               @RequestParam BigDecimal price,
                               @RequestParam String description,
                               @RequestParam String location,
                               @RequestParam String category,
                               HttpSession session,
                               @AuthenticationPrincipal OAuth2User oAuth2User,
                               RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(session, oAuth2User);
        Category categoryEntity = categoryRepository.findByName(category)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

//      private final S3Uploader s3Uploader;
        List<String> images = new ArrayList<>();
//      for(MultipartFile file : files) {
//            String image = s3Uploader.upload(file, "folder-name");
//            images.add(image);
//      }
        try {
            if(title.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "제목을 입력하세요.");
                return "redirect:/posts/new";
            }
            if(price == null) {
                redirectAttributes.addFlashAttribute("error", "가격을 입력하세요.");
                return "redirect:/posts/new";
            }
            if(description.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "설명을 입력하세요.");
                return "redirect:/posts/new";
            }
            if(location.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "위치를 입력하세요.");
                return "redirect:/posts/new";
            }
            Post post = postService.createPost(user, title.trim(), description.trim(), price, location.trim(), images, categoryEntity);
            redirectAttributes.addFlashAttribute("success", "게시글이 성공적으로 등록되었습니다.");
            return "redirect:/posts/" + post.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "게시글 등록 중 오류가 발생했습니다.");
            return "redirect:/posts/new";
        }
    }

    /* 특정 게시글 상세 보기 */
    @GetMapping("/{id}")
    public String specificFormDetail(@PathVariable Long id,
                                     Model model,
                                     HttpSession session,
                                     @AuthenticationPrincipal OAuth2User oAuth2User) {

        postService.increaseViewCount(id);
        Optional<Post> postOpt = postService.findById(id);
        if(postOpt.isEmpty()) return "post_detail";
        // return "redirect:/posts";

        Post post = postOpt.get();
        User user = getCurrentUser(session, oAuth2User);

        model.addAttribute("post", post);
        model.addAttribute("user", user);
        model.addAttribute("chatCount", post.getChatRoomsCount());
        model.addAttribute("likeCount", post.getLikeCount());

        if(user != null) {
            model.addAttribute("isLikedByCurrentUser", postService.isLikedByUser(id, user));
        } else {
            model.addAttribute("isLikedByCurrentUser", false);
        }

        return "post_detail";
    }

    /* 게시글 수정 폼 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           Model model,
                           HttpSession session,
                           @AuthenticationPrincipal OAuth2User oAuth2User){
        Optional<Post> postOpt = postService.findById(id);
        if(postOpt.isEmpty()) return "redirect:/posts";
        Post post = postOpt.get();

        User user = getCurrentUser(session, oAuth2User);
        if (user == null) return "redirect:/login";

        if(!post.getUser().getId().equals(user.getId())) return "redirect:/posts";

        model.addAttribute("post", post);
        model.addAttribute("user", user);
        model.addAttribute("mode", "edit");

        return "post_form";
    }

    /* 게시글 수정 처리 */
    @PostMapping("/{id}/edit")
    public String registerEditForm(@PathVariable Long id,
                                   @RequestParam("files") List<MultipartFile> files,
                                   @ModelAttribute PostDTO postDTO,
                                   @RequestParam String category,
                                   HttpSession session,
                                   @AuthenticationPrincipal OAuth2User oAuth2User,
                                   RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(session, oAuth2User);
        Category categoryEntity = categoryRepository.findByName(category)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

//      private final S3Uploader s3Uploader;
        List<String> images = new ArrayList<>();
//      for(MultipartFile file : files) {
//            String image = s3Uploader.upload(file, "folder-name");
//            images.add(image);
//      }
//        postDTO.setImages(images);
        try {
            if(postDTO.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "제목을 입력하세요.");
                return "redirect:/posts/" + id + "/edit";
            }
            if(postDTO.getPrice() == null) {
                redirectAttributes.addFlashAttribute("error", "가격을 입력하세요.");
                return "redirect:/posts/" + id + "/edit";
            }
            if(postDTO.getDescription().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "설명을 입력하세요.");
                return "redirect:/posts/" + id + "/edit";
            }
            if(postDTO.getLocation().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "위치를 입력하세요.");
                return "redirect:/posts/" + id + "/edit";
            }
            postDTO.setTitle(postDTO.getTitle().trim());
            postDTO.setDescription(postDTO.getDescription().trim());
            postDTO.setLocation(postDTO.getLocation().trim());
            postDTO.setSold(true); //예시

            Post post = postService.updatePost(user, categoryEntity, id, postDTO);
            redirectAttributes.addFlashAttribute("success", "게시글이 성공적으로 수정되었습니다.");
            return "redirect:/posts/" + post.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "게시글 수정 중 오류가 발생했습니다.");
            return "redirect:/posts/" + id + "/edit";
        }
    }

    /* 게시글 삭제 처리 */
    @PostMapping("/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "post_detail";
    }
    /* 게시글 좋아요 추가 */
    /* 좋아요 취소 */

    /* OAuth2 로그인과 기존 세션 로그인을 모두 지원하는 헬퍼 메서드 */
    private User getCurrentUser(HttpSession session, OAuth2User oauth2User) {

        if (oauth2User != null) {
            Map<String, Object> attributes = oauth2User.getAttributes();
            Object userObj = attributes.get("user");
            if (userObj instanceof User) {
                return (User) userObj;
            }
        }
        Object sessionUser = session.getAttribute("user");
        if (sessionUser instanceof User) {
            return (User) sessionUser;
        }

        return null;
    }
}