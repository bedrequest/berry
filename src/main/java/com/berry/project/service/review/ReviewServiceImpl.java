package com.berry.project.service.review;

import com.berry.project.entity.review.Review;
import com.berry.project.repository.review.ReviewRepository;
import com.berry.project.dto.review.ReviewRequestDTO;
import com.berry.project.dto.review.ReviewResponseDTO;
import com.berry.project.entity.review.ReviewTag;
import com.berry.project.entity.review.ReviewTagMapping;
import com.berry.project.repository.review.ReviewTagMappingRepository;
import com.berry.project.repository.review.ReviewTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewTagMappingRepository reviewTagMappingRepository;
    private final ReviewTagRepository reviewTagRepository;
//    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO dto) {
        // 1) 태그 개수 검증 (최대 3개)
        if (dto.getTagNames() != null && dto.getTagNames().size() > 3) {
            throw new IllegalArgumentException("태그는 최대 3개까지 선택 가능합니다.");
        }

        // 2) 리뷰 저장
        Review saved = reviewRepository.save(Review.builder()
                .userId(dto.getUserId())
                .lodgeId(dto.getLodgeId())
                .reservationId(dto.getReservationId())
                .rating(dto.getRating())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .build());

        // 3) 태그 매핑 동기화
        reviewTagMappingRepository.deleteAllByReviewId(saved.getReviewId());
        if (dto.getTagNames() != null) {
            dto.getTagNames().forEach(name -> {
                ReviewTag tag = reviewTagRepository.findByTagName(name)
                        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 태그: " + name));
                reviewTagMappingRepository.save(
                        ReviewTagMapping.builder()
                                .reviewId(saved.getReviewId())
                                .tagId(tag.getTagId())
                                .build()
                );
            });
        }

        return mapToDtoWithTags(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDTO getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 없습니다: " + reviewId));
        return mapToDtoWithTags(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByLodgeId(Long lodgeId, int page, int size) {
        return reviewRepository.findByLodgeId(lodgeId, PageRequest.of(page, size))
                .map(this::mapToDtoWithTags)
                .getContent();
    }

    @Override
    @Transactional
    public ReviewResponseDTO updateReview(Long reviewId, ReviewRequestDTO dto) {
        // 1) 태그 개수 검증
        if (dto.getTagNames() != null && dto.getTagNames().size() > 3) {
            throw new IllegalArgumentException("태그는 최대 3개까지 선택 가능합니다.");
        }

        // 2) 리뷰 수정
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 없습니다: " + reviewId));
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        reviewRepository.save(review);

        // 3) 태그 매핑 갱신
        reviewTagMappingRepository.deleteAllByReviewId(reviewId);
        if (dto.getTagNames() != null) {
            dto.getTagNames().forEach(name -> {
                ReviewTag tag = reviewTagRepository.findByTagName(name)
                        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 태그: " + name));
                reviewTagMappingRepository.save(
                        ReviewTagMapping.builder()
                                .reviewId(reviewId)
                                .tagId(tag.getTagId())
                                .build()
                );
            });
        }

        return mapToDtoWithTags(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        // 1) 매핑 먼저 삭제
        reviewTagMappingRepository.deleteAllByReviewId(reviewId);
        // 2) 리뷰 삭제
        reviewRepository.deleteById(reviewId);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDTO> getReviewsPageByLodgeId(Long lodgeId, int page, int size) {
        return reviewRepository
                .findByLodgeId(lodgeId, PageRequest.of(page, size))
                .map(this::mapToDtoWithTags);
    }

    private ReviewResponseDTO mapToDtoWithTags(Review review) {
        List<String> tagNames = reviewTagMappingRepository
                .findByReviewId(review.getReviewId())
                .stream()
                .map(mapping -> reviewTagRepository.findById(mapping.getTagId())
                        .map(ReviewTag::getTagName)
                        .orElse(null))
                .filter(name -> name != null)
                .collect(Collectors.toList());

        //  UserRepository가 준비되면 주석 해제
//    String email = userRepository.findById(review.getUserId())
//            .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다: " + review.getUserId()))
//            .getEmail();

        return ReviewResponseDTO.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUserId())
                .lodgeId(review.getLodgeId())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .tags(tagNames)
                // .userEmail(email)  // UserRepository 준비 후 주석 해제
                .build();
    }

}
