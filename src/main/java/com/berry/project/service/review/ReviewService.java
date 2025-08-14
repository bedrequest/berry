package com.berry.project.service.review;

import com.berry.project.dto.review.ReviewRequestDTO;
import com.berry.project.dto.review.ReviewResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ReviewService {
  /* 단일 리뷰 조회 */
  ReviewResponseDTO getReview(Long reviewId);

  /* 리뷰 작성 */
  ReviewResponseDTO createReview(ReviewRequestDTO dto, MultipartFile[] files);

  /* 리뷰 수정 */

  ReviewResponseDTO updateReview(Long reviewId, ReviewRequestDTO dto);

  /* 리뷰 삭제 */
  void deleteReview(Long reviewId);

  /* 페이지 단위로 lodgeId 별 리뷰 목록 조회. */
  Page<ReviewResponseDTO> getReviewsPageByLodgeId(
      Long lodgeId, int pageNo, int size, String sortKey
  );

  /* 기본 정렬버전 */
  default Page<ReviewResponseDTO> getReviewsPageByLodgeId(Long lodgeId, int pageNo, int size) {
    return getReviewsPageByLodgeId(lodgeId, pageNo, size, "newest");
  }

  /* 리뷰 수정 */
  ReviewResponseDTO updateReview(
      Long reviewId,
      ReviewRequestDTO dto,
      MultipartFile[] files,
      List<String> deleteImageUuids
  );

  /* 리뷰 좋아요 토글 */
  Map<String, Object> toggleLike(Long reviewId, String userEmail);

  /* 리뷰 신고 토글 */
  Map<String, Object> toggleReport(Long reviewId, String userEmail);

  /* 1. lodgeId의 review 평점 */
  double getAverageRatingByLodge(Long lodgeId);

  /* 2. lodgeId에 달린 review 갯수 */
  long getTotalReviewCountByLodge(Long lodgeId);

  /* 3. lodgeId의 review 중에 좋아요를 가장 많이 받은 리뷰(전체 객체) */
  ReviewResponseDTO getTopLikedReviewByLodge(Long lodgeId);
}
