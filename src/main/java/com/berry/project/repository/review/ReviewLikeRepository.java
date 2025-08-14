package com.berry.project.repository.review;

import com.berry.project.entity.review.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
  long countByReviewId(Long reviewId);

  boolean existsByReviewIdAndUserId(Long reviewId, Long userId);

  void deleteByReviewIdAndUserId(Long reviewId, Long userId);
}
