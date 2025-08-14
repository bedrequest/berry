package com.berry.project.service.review;

import com.berry.project.dto.review.TagCountDTO;
import com.berry.project.repository.review.ReviewRepository;
import com.berry.project.repository.review.ReviewTagMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewStatsService {
  private final ReviewTagMappingRepository mappingRepository;
  private final ReviewRepository reviewRepository;

  /* 태그별 사용 통계 조회 (상위 태그용) */
  public List<TagCountDTO> getTagStatsByLodge(Long lodgeId) {
    return mappingRepository.countTagsByLodge(lodgeId);
  }

  /* 해당 숙소의 평균 평점 계산 (소수점 첫째자리까지) */
  public double getAverageRatingByLodge(Long lodgeId) {
    double avgStars = reviewRepository
        .findAverageRatingByLodgeId(lodgeId)
        .orElse(0.0);
    double avgRating = avgStars * 2;
    return avgRating;
  }

  /* 해당 숙소의 총 리뷰 수 조회 */
  public long getTotalReviewCountByLodge(Long lodgeId) {
    return reviewRepository.countByLodgeId(lodgeId);
  }
}
