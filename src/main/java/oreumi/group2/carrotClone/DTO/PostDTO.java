package oreumi.group2.carrotClone.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import oreumi.group2.carrotClone.model.Image;

import javax.swing.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;    // 게시물 ID
    private String title;   // 게시글 제목
    private String description; // 내용
    private double price;   // 판매 가격
    private Boolean isSold;  // 판매 여부
    private LocalDate createdAt;    //등록 날짜
    private Spring location;    // 글 등록 위치
    private Integer viewCount;  // 조회수
    private Long userId;    // 게시글 작성 유저 아이디
    private List<String> images;
}