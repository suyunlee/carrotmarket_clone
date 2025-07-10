package oreumi.group2.carrotClone.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        String username = "testuser";
        User user = userRepository.findByUsername(username).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword("testpassword");
            newUser.setNickname("테스트유저");
            newUser.setPhoneNumber("010-0000-0000");
            newUser.setLocation("서울");
            newUser.setRole(UserRole.USER);
            newUser.setNeighborhoodVerified(true);
            newUser.setProvider(AuthProvider.LOCAL);
            return userRepository.save(newUser);
        });

        session.setAttribute("user", user);

        return "redirect:/posts";
    }

    /* 전체 게시글 목록 */
    @GetMapping
    public String showPost(@RequestParam(defaultValue = "0") int page,
                           Model model,
                           HttpSession session,
                           @AuthenticationPrincipal OAuth2User oAuth2User){
        User user = getCurrentUser(session, oAuth2User);
        model.addAttribute("user", user);

        Pageable pageable = PageRequest.of(page, 12);
        Page<Post> postPage = postService.findAll(pageable);

        model.addAttribute("page", postPage);

        return "post";
    }

    /* 검색 게시글 목록 */
    @GetMapping("/search")
    public String searchPost(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) Long category,
                             Model model,
                             HttpSession session,
                             @AuthenticationPrincipal OAuth2User oAuth2User){
        User user = getCurrentUser(session, oAuth2User);
        model.addAttribute("user", user);

        Pageable pageable = PageRequest.of(page, 8);
        Page<Post> postPage = postService.searchPosts(keyword, category, pageable);

        model.addAttribute("page", postPage);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("hasKeyword", keyword != null && !keyword.isBlank());
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);

        return "post_search";
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
        PostDTO postDTO = new PostDTO();
        postDTO.setLocation(user.getLocation());
        model.addAttribute("mode", "new");
        model.addAttribute("user", user);
        model.addAttribute("post", postDTO);
        model.addAttribute("categories", categoryRepository.findAll());
        return "post_form";
    }

    /* 게시글 등록 처리 */
    @PostMapping
    public String registerForm(@RequestParam("files") List<MultipartFile> files,
                               @Valid @ModelAttribute PostDTO postDTO,
                               BindingResult bindingResult,
                               HttpSession session,
                               @AuthenticationPrincipal OAuth2User oAuth2User,
                               RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/posts/new";
        }

        User user = getCurrentUser(session, oAuth2User);
        Category category = categoryRepository.findById(postDTO.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        postDTO.setCategory(category);

//      private final S3Uploader s3Uploader;
        List<String> images = new ArrayList<>();
//      for(MultipartFile file : files) {
//            String image = s3Uploader.upload(file, "folder-name");
//            images.add(image);
//      }
        try {
            Post post = postService.createPost(user, postDTO, images);
            redirectAttributes.addFlashAttribute("success", "게시글이 성공적으로 등록되었습니다.");
            return "redirect:/posts/" + post.getId();
        } catch (Exception e) {
            e.printStackTrace();
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
        if(postOpt.isEmpty()) return "redirect:/posts";

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
        model.addAttribute("categories", categoryRepository.findAll());

        return "post_form";
    }

    /* 게시글 수정 처리 */
    @PostMapping("/{id}/edit")
    public String registerEditForm(@PathVariable Long id,
                                   @RequestParam("files") List<MultipartFile> files,
                                   @Valid @ModelAttribute PostDTO postDTO,
                                   BindingResult bindingResult,
                                   HttpSession session,
                                   @AuthenticationPrincipal OAuth2User oAuth2User,
                                   RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/posts/" + id + "/edit";
        }

        User user = getCurrentUser(session, oAuth2User);
        Category category = categoryRepository.findById(postDTO.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        postDTO.setCategory(category);

//      private final S3Uploader s3Uploader;
        List<String> images = new ArrayList<>();
//      for(MultipartFile file : files) {
//            String image = s3Uploader.upload(file, "folder-name");
//            images.add(image);
//      }
//        postDTO.setImages(images);
        try {
            postDTO.setSold(true); //예시
            Post post = postService.updatePost(id, postDTO);
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
        return "redirect:/posts";
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