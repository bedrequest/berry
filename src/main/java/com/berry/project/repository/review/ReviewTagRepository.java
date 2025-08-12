package com.berry.project.repository.review;

import com.berry.project.entity.review.ReviewTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewTagRepository extends JpaRepository<ReviewTag, Long> {
  Optional<ReviewTag> findByTagName(String tagName);
}