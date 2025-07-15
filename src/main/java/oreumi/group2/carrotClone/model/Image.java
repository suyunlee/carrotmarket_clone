package oreumi.group2.carrotClone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "image")
@Data
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

    //ToString 무한 순환참조 방지
    @Override
    public String toString(){
        return this.imageUrl;
    }

}