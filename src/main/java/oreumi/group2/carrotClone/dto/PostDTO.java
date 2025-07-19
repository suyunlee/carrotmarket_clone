package oreumi.group2.carrotClone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import oreumi.group2.carrotClone.model.Category;
import oreumi.group2.carrotClone.model.Image;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;    // 게시물 ID

    @NotBlank(message = "제목을 입력하세요.")
    private String title;   // 게시글 제목
    @NotBlank(message = "설명을 입력하세요.")
    private String description; // 내용
    @NotNull(message = "가격을 입력하세요.")
    private BigDecimal price;   // 판매 가격
    private boolean sold;  // 판매 여부
    private LocalDateTime createdAt;    //등록 날짜
    @NotBlank(message = "위치를 입력하세요.")
    private String location;    // 글 등록 위치
    private Long viewCount;  // 조회수
    private Long userId;    // 게시글 작성 유저 아이디
    private List<Image> images;
    private Category category;
}