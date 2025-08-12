package com.berry.project.service.review;


import com.berry.project.dto.review.ReviewSummaryDTO;

public interface ReviewSummaryService {
  /**
   * 조회 혹은 생성
   */
  ReviewSummaryDTO findByLodgeIdOrGenerate(Long lodgeId);

  /**
   * 삭제
   */
  void deleteByLodgeId(Long lodgeId);

  ReviewSummaryDTO generateSummary(Long lodgeId);
}
