package com.berry.project.repository.review;

import com.berry.project.dto.review.TagCountDTO;
import com.berry.project.entity.review.ReviewTagMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewTagMappingRepository extends JpaRepository<ReviewTagMapping, Long> {

  List<ReviewTagMapping> findByReviewId(Long reviewId);

  void deleteAllByReviewId(Long reviewId);

  // 1) lodgeId별 태그 통계 집계 메서드 추가

  @Query("""
      SELECT new com.berry.project.dto.review.TagCountDTO(
          m.tag.tagName,
          COUNT(m)
      )
      FROM ReviewTagMapping m
      JOIN m.review r
      WHERE r.lodgeId = :lodgeId
      GROUP BY m.tag.tagName
      """)
  List<TagCountDTO> countTagsByLodge(@Param("lodgeId") Long lodgeId);
}
