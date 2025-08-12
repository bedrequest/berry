package com.berry.project.repository.review;

import com.berry.project.entity.review.ReviewSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewSummaryRepository extends JpaRepository<ReviewSummary, Long> {
  Optional<ReviewSummary> findByLodgeId(Long lodgeId);

  void deleteByLodgeId(Long lodgeId);
}

