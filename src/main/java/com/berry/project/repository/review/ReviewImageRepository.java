package com.berry.project.repository.review;

import com.berry.project.entity.review.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, String> {
  /**
   * 특정 리뷰에 속한 이미지 목록 조회
   */
  List<ReviewImage> findByReviewId(Long reviewId);

  void deleteAllByReviewId(Long reviewId);
}