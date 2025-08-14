// src/main/java/com/berry/project/service/review/ReviewServiceImpl.java
package com.berry.project.service.review;

import com.berry.project.dto.review.ReviewImageDTO;
import com.berry.project.dto.review.ReviewRequestDTO;
import com.berry.project.dto.review.ReviewResponseDTO;
import com.berry.project.dto.user.UserDTO;
import com.berry.project.entity.alarm.Alarm;
import com.berry.project.entity.review.*;
import com.berry.project.handler.ReviewFileHandler;
import com.berry.project.repository.review.*;
import com.berry.project.repository.user.AlarmRepository;
import com.berry.project.repository.user.UserRepository;
import com.berry.project.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
  private final ReviewRepository reviewRepository;
  private final ReviewTagMappingRepository reviewTagMappingRepository;
  private final ReviewTagRepository reviewTagRepository;
  private final UserRepository userRepository;
  private final ReviewFileHandler reviewFileHandler;
  private final ReviewImageRepository reviewImageRepository;
  private final ReviewLikeRepository reviewLikeRepository;
  private final ReviewReportRepository reviewReportRepository;
  private final UserService userService;
  private final ReviewInteractionService reviewInteractionService;

  // 해찬
  private final AlarmRepository alarmRepository;

  @Override
  @Transactional
  public ReviewResponseDTO createReview(ReviewRequestDTO dto, MultipartFile[] files) {
    // 태그 제한 검사
    if (dto.getTagNames() != null && dto.getTagNames().size() > 3) {
      throw new IllegalArgumentException("태그는 최대 3개까지 선택 가능합니다.");
    }
    // 사용자 이메일 조회
    String email = userRepository.findById(dto.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다: " + dto.getUserId()))
        .getUserEmail();
    // 리뷰 저장
    Review saved = reviewRepository.save(
        Review.builder()
            .userId(dto.getUserId())
            .userEmail(email)
            .lodgeId(dto.getLodgeId())
            .reservationId(dto.getReservationId())
            .rating(dto.getRating())
            .content(dto.getContent())
            .createdAt(LocalDateTime.now())
            .build()
    );
    // 태그 매핑
    reviewTagMappingRepository.deleteAllByReviewId(saved.getReviewId());
    if (dto.getTagNames() != null) {
      dto.getTagNames().forEach(name -> {
        reviewTagRepository.findByTagName(name)
            .ifPresentOrElse(tag -> reviewTagMappingRepository.save(
                ReviewTagMapping.builder()
                    .reviewId(saved.getReviewId())
                    .tagId(tag.getTagId())
                    .build()
            ), () -> {
              throw new IllegalArgumentException("유효하지 않은 태그: " + name);
            });
      });
    }
    // 이미지 저장
    List<ReviewImageDTO> images = reviewFileHandler.storeReviewFiles(saved.getReviewId(), files);
    List<String> tags = dto.getTagNames() != null ? dto.getTagNames() : List.of();

    return ReviewResponseDTO.from(
        saved, tags, images,
        0L, false,
        0L, false
    );
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
  public Page<ReviewResponseDTO> getReviewsPageByLodgeId(
      Long lodgeId, int pageNo, int size, String sortKey) {

    Page<Review> pages;
    Pageable pageable;

    if ("likes".equals(sortKey)) {
      // 좋아요 순으로 정렬
      pageable = PageRequest.of(pageNo, size);
      pages = reviewRepository.findByLodgeIdOrderByLikeCountDesc(lodgeId, pageable);
    } else {
      // 최신순/오래된순(createdAt 기준)
      Sort sort = "oldest".equals(sortKey)
          ? Sort.by("createdAt").ascending()
          : Sort.by("createdAt").descending();
      pageable = PageRequest.of(pageNo, size, sort);
      pages = reviewRepository.findByLodgeId(lodgeId, pageable);
    }
    return pages.map(this::mapToDtoWithTags);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ReviewResponseDTO> getReviewsPageByLodgeId(
      Long lodgeId, int pageNo, int size) {
    return getReviewsPageByLodgeId(lodgeId, pageNo, size, "newest");
  }

  @Override
  @Transactional
  public ReviewResponseDTO updateReview(Long reviewId, ReviewRequestDTO dto) {
    // 단순 재조회
    return getReview(reviewId);
  }

  @Override
  @Transactional
  public ReviewResponseDTO updateReview(
      Long reviewId,
      ReviewRequestDTO dto,
      MultipartFile[] files,
      List<String> deleteImageUuids) {

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 없습니다: " + reviewId));
    review.setRating(dto.getRating());
    review.setContent(dto.getContent());
    reviewRepository.save(review);

    // 태그 재매핑
    reviewTagMappingRepository.deleteAllByReviewId(reviewId);
    if (dto.getTagNames() != null) {
      dto.getTagNames().forEach(name -> {
        reviewTagRepository.findByTagName(name)
            .ifPresentOrElse(tag -> reviewTagMappingRepository.save(
                ReviewTagMapping.builder()
                    .reviewId(reviewId)
                    .tagId(tag.getTagId())
                    .build()
            ), () -> {
              throw new IllegalArgumentException("유효하지 않은 태그: " + name);
            });
      });
    }

    // 이미지 삭제
    if (deleteImageUuids != null) {
      deleteImageUuids.forEach(uuid -> reviewImageRepository.findById(uuid).ifPresent(img -> {
        reviewFileHandler.deleteFile(img);
        reviewImageRepository.delete(img);
      }));
    }

    // 새로운 이미지 저장
    List<ReviewImageDTO> newImages = reviewFileHandler.storeReviewFiles(reviewId, files);
    List<ReviewImageDTO> remainingImages = reviewImageRepository.findByReviewId(reviewId).stream()
        .map(ReviewImageDTO::fromEntity)
        .toList();
    List<ReviewImageDTO> allImages = new ArrayList<>(remainingImages);
    allImages.addAll(newImages);

    // DTO 변환
    return ReviewResponseDTO.from(
        review,
        reviewTagMappingRepository.findByReviewId(reviewId).stream()
            .map(m -> reviewTagRepository.findById(m.getTagId())
                .map(ReviewTag::getTagName).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList()),
        allImages,
        reviewInteractionService.countLikes(reviewId),
        reviewInteractionService.hasLiked(reviewId, getCurrentUserEmail()),
        reviewInteractionService.countReports(reviewId),
        reviewInteractionService.hasReported(reviewId, getCurrentUserEmail())
    );
  }

  @Override
  @Transactional
  public void deleteReview(Long reviewId) {
    reviewImageRepository.findByReviewId(reviewId)
        .forEach(reviewFileHandler::deleteFile);
    reviewImageRepository.deleteAllByReviewId(reviewId);
    reviewTagMappingRepository.deleteAllByReviewId(reviewId);
    reviewRepository.deleteById(reviewId);
  }


  @Override
  @Transactional(readOnly = true)
  public double getAverageRatingByLodge(Long lodgeId) {
    // 평균 평점 조회
    return reviewRepository.findAverageRatingByLodgeId(lodgeId).orElse(0.0);
  }

  @Override
  @Transactional(readOnly = true)
  public long getTotalReviewCountByLodge(Long lodgeId) {
    // 총 리뷰 개수 조회
    return reviewRepository.countByLodgeId(lodgeId);
  }

  @Override
  @Transactional(readOnly = true)
  public ReviewResponseDTO getTopLikedReviewByLodge(Long lodgeId) {
    // 좋아요 최다 리뷰 조회 (Page 0, size 1)
    Page<Review> page = reviewRepository.findByLodgeIdOrderByLikeCountDesc(
        lodgeId, PageRequest.of(0, 1));
    if (page.isEmpty()) return null;
    return mapToDtoWithTags(page.getContent().get(0));
  }


  private ReviewResponseDTO mapToDtoWithTags(Review review) {
    List<String> tags = reviewTagMappingRepository.findByReviewId(review.getReviewId()).stream()
        .map(m -> reviewTagRepository.findById(m.getTagId())
            .map(ReviewTag::getTagName).orElse(null))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    List<ReviewImageDTO> images = reviewImageRepository.findByReviewId(review.getReviewId()).stream()
        .map(ReviewImageDTO::fromEntity)
        .collect(Collectors.toList());

    String currentUserEmail = getCurrentUserEmail();
    long likeCnt = reviewInteractionService.countLikes(review.getReviewId());
    boolean liked = reviewInteractionService.hasLiked(review.getReviewId(), currentUserEmail);
    long reportCnt = reviewInteractionService.countReports(review.getReviewId());
    boolean reported = reviewInteractionService.hasReported(review.getReviewId(), currentUserEmail);

    return ReviewResponseDTO.from(
        review, tags, images,
        likeCnt, liked,
        reportCnt, reported
    );
  }

  private String getCurrentUserEmail() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth != null ? auth.getName() : null;
  }

  @Override
  @Transactional
  public Map<String, Object> toggleLike(Long reviewId, String userIdentifier) {
    UserDTO userDTO = userService.getUserInfo(userIdentifier);
    if (userDTO == null) {
      userDTO = userService.isSocialDuplicateUser(userIdentifier);
    }
    if (userDTO == null) {
      throw new IllegalArgumentException("유효하지 않은 사용자입니다: " + userIdentifier);
    }
    Long userId = userDTO.getUserId();

    boolean already = reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId);
    if (already) {
      reviewLikeRepository.deleteByReviewIdAndUserId(reviewId, userId);
    } else {
      reviewLikeRepository.save(ReviewLike.builder()
          .reviewId(reviewId)
          .userId(userId)
          .build());
      
      // 해찬
      // 좋아요 알림
      Alarm alarm = Alarm.builder()
          .userId(userId) // 로그인한 유저 id
          .targetId(userId) // 타겟 테이블 id
          .code("l_review") // 알림 코드 (자기가 직접 작성하거나 만들어놓은 예시 참고)
          .build();

      alarmRepository.save(alarm);
      
    }
    long count = reviewLikeRepository.countByReviewId(reviewId);
    return Map.of("likeCount", count, "likedByMe", !already);
  }

  @Override
  @Transactional
  public Map<String, Object> toggleReport(Long reviewId, String userIdentifier) {
    UserDTO userDTO = userService.getUserInfo(userIdentifier);
    if (userDTO == null) {
      userDTO = userService.isSocialDuplicateUser(userIdentifier);
    }
    if (userDTO == null) {
      throw new IllegalArgumentException("유효하지 않은 사용자입니다: " + userIdentifier);
    }
    Long userId = userDTO.getUserId();

    boolean already = reviewReportRepository.existsByReviewIdAndUserId(reviewId, userId);

    if (already) {
      reviewReportRepository.deleteByReviewIdAndUserId(reviewId, userId);
    } else {
      reviewReportRepository.save(ReviewReport.builder()
          .reviewId(reviewId)
          .userId(userId)
          .build());
    }

    long count = reviewReportRepository.countByReviewId(reviewId);

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
    review.setReportedCount((int) count);

    return Map.of("reportCount", count, "reportedByMe", !already);
  }
}
