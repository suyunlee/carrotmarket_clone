package oreumi.group2.carrotClone.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id",nullable = false)
    private Post post; /* post 정보 */

    @Column(nullable = false,name = "url")
    private String imageUrl; /* 이미지 url  */

    @Column(nullable = false, name = "s3_key")
    private String s3Key; /* S3에서의 파일 키 */

    @Column(nullable = false, name = "original_filename")
    private String originalFilename;

    public Image(Post post, String imageUrl, String s3Key, String originalFilename) {
        this.post = post;
        this.imageUrl = imageUrl;
        this.s3Key = s3Key;
        this.originalFilename = originalFilename;
    }

    //ToString 무한 순환참조 방지
    @Override
    public String toString(){
        return this.imageUrl;
    }

}