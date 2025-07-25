package com.berry.project.repository.review;

import com.berry.project.entity.review.ReviewTagMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewTagMappingRepository extends JpaRepository<ReviewTagMapping, Long> {
    List<ReviewTagMapping> findByReviewId(Long reviewId);
    void deleteAllByReviewId(Long reviewId);
}