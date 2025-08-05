package com.berry.project.repository.review;

import com.berry.project.entity.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 1) lodgeId로 페이징 조회
    Page<Review> findByLodgeId(Long lodgeId, Pageable pageable);

    // 2) 유저별 전체 리뷰 조회
    List<Review> findByUserId(Long userId);

    // 3) lodgeId별 평균 평점 계산
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.lodgeId = :lodgeId")
    Optional<Double> findAverageRatingByLodgeId(@Param("lodgeId") Long lodgeId);

    // 4) lodgeId별 리뷰 개수 계산
    long countByLodgeId(Long lodgeId);

    // 5) lodgeId별 좋아요 수 내림차순 정렬 페이징 조회
    @Query(
            value = "SELECT r FROM Review r " +
                    "WHERE r.lodgeId = :lodgeId " +
                    "ORDER BY (SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.reviewId = r.reviewId) DESC",
            countQuery = "SELECT COUNT(r) FROM Review r WHERE r.lodgeId = :lodgeId"
    )
    Page<Review> findByLodgeIdOrderByLikeCountDesc(
            @Param("lodgeId") Long lodgeId,
            Pageable pageable
    );


    // 이유현 : index.html용 메서드
  List<Review> findTop10ByOrderByCreatedAtDesc();
}
