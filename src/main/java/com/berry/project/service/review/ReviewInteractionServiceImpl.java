package com.berry.project.service.review;

import com.berry.project.entity.review.ReviewLike;
import com.berry.project.entity.review.ReviewReport;
import com.berry.project.entity.user.User;
import com.berry.project.repository.review.ReviewLikeRepository;
import com.berry.project.repository.review.ReviewReportRepository;
import com.berry.project.repository.review.ReviewRepository;
import com.berry.project.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewInteractionServiceImpl implements ReviewInteractionService {

  private final ReviewLikeRepository likeRepo;
  private final ReviewReportRepository reportRepo;
  private final ReviewRepository reviewRepo;
  private final UserRepository userRepo;

  @Override
  @Transactional
  public long toggleLike(Long reviewId, Long userId) {
    if (likeRepo.existsByReviewIdAndUserId(reviewId, userId)) {
      // 이미 좋아요가 눌린 상태면 취소
      likeRepo.deleteByReviewIdAndUserId(reviewId, userId);
    } else {
      // 아니면 새 좋아요 생성
      ReviewLike like = ReviewLike.builder()
          .reviewId(reviewId)
          .userId(userId)
          .build();
      likeRepo.save(like);
    }
    return likeRepo.countByReviewId(reviewId);
  }

  @Override
  @Transactional
  public long report(Long reviewId, Long userId) {
    if (!reportRepo.existsByReviewIdAndUserId(reviewId, userId)) {
      // 신고가 없을 때만 추가
      ReviewReport rpt = ReviewReport.builder()
          .reviewId(reviewId)
          .userId(userId)
          .build();
      reportRepo.save(rpt);
    }
    return reportRepo.countByReviewId(reviewId);
  }

  @Override
  @Transactional(readOnly = true)
  public long countLikes(Long reviewId) {
    return likeRepo.countByReviewId(reviewId);
  }

  @Override
  @Transactional(readOnly = true)
  public long countReports(Long reviewId) {
    return reportRepo.countByReviewId(reviewId);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasLiked(Long reviewId, String userEmail) {
    Long userId = userRepo.findByUserEmail(userEmail).stream()
        .findFirst() // 소셜/웹 구분 없이 첫 번째 사용자 사용
        .map(User::getUserId)
        .orElse(0L);
    return likeRepo.existsByReviewIdAndUserId(reviewId, userId);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasReported(Long reviewId, String userEmail) {
    Long userId = userRepo.findByUserEmail(userEmail).stream()
        .findFirst()
        .map(User::getUserId)
        .orElse(0L);
    return reportRepo.existsByReviewIdAndUserId(reviewId, userId);
  }
}
