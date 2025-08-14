package com.berry.project.service.review;


public interface ReviewInteractionService {


  long toggleLike(Long reviewId, Long userId);

  long report(Long reviewId, Long userId);


  long countLikes(Long reviewId);

  long countReports(Long reviewId);


  boolean hasLiked(Long reviewId, String userEmail);

  boolean hasReported(Long reviewId, String userEmail);
}
