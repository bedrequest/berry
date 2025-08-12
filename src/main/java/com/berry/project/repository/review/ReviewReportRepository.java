package com.berry.project.repository.review;

import com.berry.project.entity.review.ReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
  long countByReviewId(Long reviewId);

  boolean existsByReviewIdAndUserId(Long reviewId, Long userId);

  void deleteByReviewIdAndUserId(Long reviewId, Long userId);
}
