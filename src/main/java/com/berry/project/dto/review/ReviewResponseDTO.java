package com.berry.project.dto.review;

import com.berry.project.entity.review.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {
  private Long reviewId;
  private Long userId;
  private Long lodgeId;
  private String userEmail;
  private Integer rating;
  private String content;
  private LocalDateTime createdAt;
  private List<String> tags;
  private List<ReviewImageDTO> images;
  private long likeCount;
  private long reportCount;
  private boolean likedByMe;
  private boolean reportedByMe;

  public static ReviewResponseDTO from(
      Review review,
      List<String> tagNames,
      List<ReviewImageDTO> images
  ) {
    return ReviewResponseDTO.builder()
        .reviewId(review.getReviewId())
        .userId(review.getUserId())
        .lodgeId(review.getLodgeId())
        .userEmail(review.getUserEmail())
        .rating(review.getRating())
        .content(review.getContent())
        .createdAt(review.getCreatedAt())
        .tags(tagNames)
        .images(images)
        .build();
  }

  public static ReviewResponseDTO from(
      Review review,
      List<String> tagNames,
      List<ReviewImageDTO> images,
      long likeCount,
      boolean likedByMe,
      long reportCount,
      boolean reportedByMe
  ) {
    return ReviewResponseDTO.builder()
        .reviewId(review.getReviewId())
        .userId(review.getUserId())
        .lodgeId(review.getLodgeId())
        .userEmail(review.getUserEmail())
        .rating(review.getRating())
        .content(review.getContent())
        .createdAt(review.getCreatedAt())
        .tags(tagNames)
        .images(images)
        .likeCount(likeCount)
        .likedByMe(likedByMe)
        .reportCount(reportCount)
        .reportedByMe(reportedByMe)
        .build();
  }

}
