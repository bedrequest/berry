package com.berry.project.service.review;

import com.berry.project.dto.review.ReviewRequestDTO;
import com.berry.project.dto.review.ReviewResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO createReview(ReviewRequestDTO dto);
    ReviewResponseDTO getReview(Long reviewId);
    List<ReviewResponseDTO> getReviewsByLodgeId(Long lodgeId, int page, int size);
    ReviewResponseDTO updateReview(Long reviewId, ReviewRequestDTO dto);
    void deleteReview(Long reviewId);
    Page<ReviewResponseDTO> getReviewsPageByLodgeId(Long lodgeId, int page, int size);
}
