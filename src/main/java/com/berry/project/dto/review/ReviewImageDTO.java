package com.berry.project.dto.review;

import com.berry.project.entity.review.ReviewImage;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewImageDTO {
  /**
   * 저장된 파일 UUID (primary key)
   */
  private String reviewUuid;
  /**
   * 리뷰 ID (foreign key)
   */
  private Long reviewId;
  /**
   * 파일이 저장된 디렉토리 경로
   */
  private String reviewSaveDir;
  /**
   * 원본 파일명
   */
  private String reviewFileName;
  /**
   * 파일 크기 (bytes)
   */
  private Long reviewFileSize;
  /**
   * 등록 시각 (LocalDateTime으로 변환)
   */
  private java.time.LocalDateTime regDate;
  /**
   * 수정 시각
   */
  private java.time.LocalDateTime modDate;

  public static ReviewImageDTO fromEntity(ReviewImage e) {
    return ReviewImageDTO.builder()
        .reviewUuid(e.getReviewUuid())
        .reviewId(e.getReviewId())
        .reviewSaveDir(e.getReviewSaveDir())
        .reviewFileName(e.getReviewFileName())
        .reviewFileSize(e.getReviewFileSize())
        .regDate(e.getRegDate())
        .modDate(e.getModDate())
        .build();
  }

}