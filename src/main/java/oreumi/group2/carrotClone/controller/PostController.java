package oreumi.group2.carrotClone.controller;

import jakarta.validation.Valid;
import oreumi.group2.carrotClone.security.CustomUserPrincipal;
import oreumi.group2.carrotClone.dto.PostDTO;
import oreumi.group2.carrotClone.model.Category;
import oreumi.group2.carrotClone.model.Post;
import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.repository.CategoryRepository;
import oreumi.group2.carrotClone.repository.UserRepository;
import oreumi.group2.carrotClone.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    /* 전체 게시글 목록 */
    @GetMapping
    public String showPost(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "false") boolean fragment,
                           Model model,
                           @AuthenticationPrincipal CustomUserPrincipal principal){
        User user = null;
        if (principal != null) { user = principal.getUser(); }
        model.addAttribute("user", user);

        Pageable pageable = PageRequest.of(page, 12, Sort.by("createdAt").descending());
        Slice<Post> postSlice = postService.findAllSlice(pageable);
        model.addAttribute("page", postSlice);

        if(fragment) {
            return "post/post :: postCards";
        }

        return "post/post";
    }

    /* 검색 게시글 목록 */
    @GetMapping("/search")
    public String searchPost(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) Long category,
                             @RequestParam(required = false) Integer priceMin,
                             @RequestParam(required = false) Integer priceMax,
                             @RequestParam(required = false) Boolean isSold,
                             @RequestParam(required = false) String dong,
                             Model model,
                             @AuthenticationPrincipal CustomUserPrincipal principal){
        User user = null;
        if (principal != null) { user = principal.getUser(); }
        model.addAttribute("user", user);

        String location;
        if(user == null || user.getLocation() == null|| user.getLocation().isEmpty()) {
            location = "서울특별시 강서구 화곡동";
        } else {
            location = user.getLocation();
        }

        if (location != null && location.contains(" ")) {
            int spaceIndex = location.lastIndexOf(" ");
            String guName = location.substring(0, spaceIndex);
            model.addAttribute("guName", guName);
        } else {
            model.addAttribute("guName", location != null ? location : "지역 없음");
        }

        String[] parts = location != null ? location.trim().split(" ") : new String[0];

        if(dong == null && parts.length >= 2) dong = parts[parts.length - 1];
        if (parts.length >= 2) {
            String gu = parts[parts.length - 2];
            List<String> dongList = new ArrayList<>();
            dongList.add(dong);
            for(String dongName : postService.getRegionData(gu, dong))
                dongList.add(dongName);
            model.addAttribute("dongList", dongList);
        } else {
            model.addAttribute("dongList", null);
        }

        if (priceMin == null) priceMin = 0;
        if (priceMax == null) priceMax = Integer.MAX_VALUE;

        Pageable pageable = PageRequest.of(page, 8);
        Page<Post> postPage;

        if(keyword != null && !keyword.isBlank() && keyword.trim().length() < 2) {
            model.addAttribute("error", "검색어는 두 글자 이상 입력해주세요.");
            postPage = Page.empty(pageable);
            model.addAttribute("hasKeyword", false);
        } else {
            postPage = postService.searchPosts(keyword, category, priceMin, priceMax, isSold, dong, pageable);
            model.addAttribute("hasKeyword", keyword != null && !keyword.isBlank());
        }
        model.addAttribute("page", postPage);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("priceMin", priceMin);
        model.addAttribute("priceMax", priceMax == Integer.MAX_VALUE ? null : priceMax);
        model.addAttribute("priceMaxInput", priceMax == Integer.MAX_VALUE ? 0 : priceMax);
        model.addAttribute("isSold", isSold);
        model.addAttribute("selectedDong", dong);

        return "post/post_search";
    }

    /* 게시글 작성 폼 */
    @GetMapping("/new")
    public String showNewForm(@AuthenticationPrincipal CustomUserPrincipal principal,
                              Model model,
                              RedirectAttributes redirectAttributes){
        User user = null;
        if (principal != null) { user = principal.getUser(); }
        if(user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/users/login";
        }
        if (!user.isNeighborhoodVerified()) {
            redirectAttributes.addFlashAttribute("error", "동네 인증이 필요합니다.");
            return "redirect:/maps/permission";
        }
        PostDTO postDTO = new PostDTO();
        postDTO.setLocation(user.getLocation());
        model.addAttribute("mode", "new");
        model.addAttribute("user", user);
        model.addAttribute("post", postDTO);
        model.addAttribute("categories", categoryRepository.findAll());
        return "post/post_form";
    }

    /* 게시글 등록 처리 */
    @PostMapping
    public String registerForm(@RequestParam("files") List<MultipartFile> files,
                               @Valid @ModelAttribute PostDTO postDTO,
                               BindingResult bindingResult,
                               @AuthenticationPrincipal CustomUserPrincipal principal,
                               RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/posts/new";
        }

        User user = principal.getUser();
        Category category = categoryRepository.findById(postDTO.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        postDTO.setCategory(category);

        try {
            Post post = postService.createPost(user, postDTO, files);
            redirectAttributes.addFlashAttribute("success", "게시글이 성공적으로 등록되었습니다.");
            return "redirect:/posts/" + post.getId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/posts/new";
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
                                     @AuthenticationPrincipal CustomUserPrincipal principal) {

        postService.increaseViewCount(id);
        Optional<Post> postOpt = postService.findById(id);
        if(postOpt.isEmpty()) return "redirect:/posts";

        Post post = postOpt.get();
        User user = null;
        if (principal != null) { user = principal.getUser(); }

        model.addAttribute("post", post);
        model.addAttribute("user", user);
        model.addAttribute("chatCount", post.getChatRoomsCount());

        model.addAttribute("isLikedByCurrentUser", false);

        return "post/post_detail";
    }

    /* 게시글 수정 폼 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           Model model,
                           @AuthenticationPrincipal CustomUserPrincipal principal){
        Optional<Post> postOpt = postService.findById(id);
        if(postOpt.isEmpty()) return "redirect:/posts";
        Post post = postOpt.get();

        User user = principal.getUser();
        if (user == null) return "redirect:/users/login";

        if(!post.getUser().getId().equals(user.getId())) return "redirect:/posts";

        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setTitle(post.getTitle());
        postDTO.setDescription(post.getDescription());
        postDTO.setLocation(post.getLocation());
        postDTO.setCategory(post.getCategory());
        postDTO.setImages(post.getImages());
        postDTO.setPrice(post.getPrice() != null ? BigDecimal.valueOf(post.getPrice().longValue()) : null);

        model.addAttribute("post", postDTO);
        model.addAttribute("user", user);
        model.addAttribute("mode", "edit");
        model.addAttribute("categories", categoryRepository.findAll());

        return "post/post_form";
    }

    /* 게시글 수정 처리 */
    @PostMapping("/{id}/edit")
    public String registerEditForm(@PathVariable Long id,
                                   @RequestParam("files") List<MultipartFile> files,
                                   @Valid @ModelAttribute PostDTO postDTO,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal CustomUserPrincipal principal,
                                   RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/posts/" + id + "/edit";
        }

        User user = principal.getUser();
        Category category = categoryRepository.findById(postDTO.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        postDTO.setCategory(category);

        try {
            Post post = postService.updatePost(id, postDTO, files);
            redirectAttributes.addFlashAttribute("success", "게시글이 성공적으로 수정되었습니다.");
            return "redirect:/posts/" + post.getId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/posts/" + id + "/edit";
        } catch (Exception e) {
            e.printStackTrace();
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
}