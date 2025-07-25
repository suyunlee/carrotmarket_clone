package oreumi.group2.carrotClone.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; /* pk */

    @Column(nullable = false, length = 100)
    private String name; /* 카테고리 이름 */
}