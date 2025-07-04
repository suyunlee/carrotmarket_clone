package oreumi.group2.carrotClone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/posts")
public class PostController {

    /* 전체 계시글 목록 */
    @GetMapping
    public String showPost(){
        return "";
    }

    /* 게시글 작성 폼 */
    @GetMapping("/new")
    public String showNewForm(){
        return "";
    }

    /* 게시글 등록 처리 */
    @PostMapping
    public String registerForm(){
        return "";
    }

    /* 특정 게시글 상세 보기 */
    @GetMapping("/{id}")
    public String specificFormDetail(){
        return "";
    }

    /* 게시글 수정 폼 */
    @GetMapping("/{id}/edit")
    public String editForm(){
        return "";
    }
    /* 게시글 수정 처리 */
    /* 게시글 삭제 처리 */
    /* 게시글 좋아요 추가 */
    /* 좋아요 취소 */
}