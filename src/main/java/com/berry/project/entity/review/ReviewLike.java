package com.berry.project.entity.review;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review_likes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "review_id"}))
public class ReviewLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "likes_no")
  private Long likesNo;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "review_id", nullable = false)
  private Long reviewId;
}
