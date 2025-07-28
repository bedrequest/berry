package com.berry.project.repository.review;

import com.berry.project.entity.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewCustomRepository {
  Page<Review> pageReview(Pageable pageable, String keyword);
}
