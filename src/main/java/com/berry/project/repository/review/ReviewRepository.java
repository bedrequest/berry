package com.berry.project.repository.review;

import com.berry.project.entity.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByLodgeId(Long lodgeId, Pageable pageable);

    List<Review> findByUserId(Long userId);
}
