package com.berry.project.controller;

import com.berry.project.dto.review.*;
import com.berry.project.dto.user.UserDTO;
import com.berry.project.repository.review.ReviewImageRepository;
import com.berry.project.service.review.ReviewService;
import com.berry.project.service.review.ReviewSummaryService;
import com.berry.project.service.review.ReviewTagService;
import com.berry.project.service.review.ReviewStatsService;
import com.berry.project.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewTagService reviewTagService;
    private final ReviewStatsService reviewStatsService;
    private final UserService userService;
    private final ReviewImageRepository reviewImageRepository;
    private final ObjectMapper objectMapper;
    private final ReviewSummaryService reviewSummaryService;

    //  리뷰 fragment 렌더링
    @GetMapping("/view")
    public String reviewFragment(
            @RequestParam Long lodgeId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "sort", defaultValue = "newest") String sort,
            Model model,
            Authentication authentication
    ) throws com.fasterxml.jackson.core.JsonProcessingException {
        // 1) 페이징·정렬 (sort: "newest", "oldest", "likes")
        int size = 5;
        Page<ReviewResponseDTO> pageData =
                reviewService.getReviewsPageByLodgeId(lodgeId, page - 1, size, sort);

        // 2) 페이징 계산
        int currentPage   = pageData.getNumber() + 1;
        int totalPages    = pageData.getTotalPages();
        int pageGroupSize = 5;
        int startPage     = (currentPage - 1) / pageGroupSize * pageGroupSize + 1;
        int endPage       = Math.min(startPage + pageGroupSize - 1, totalPages);

        model.addAttribute("reviews",     pageData.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage",   startPage);
        model.addAttribute("endPage",     endPage);
        model.addAttribute("hasPrev",     startPage > 1);
        model.addAttribute("hasNext",     endPage < totalPages);
        model.addAttribute("sort",        sort);
        model.addAttribute("lodgeId",     lodgeId);

        // 3) 작성 폼·태그·통계
        model.addAttribute("reviewRequest",
                ReviewRequestDTO.builder().lodgeId(lodgeId).build());
        model.addAttribute("tags", reviewTagService.getAllTags());
        List<TagCountDTO> tagStats = reviewStatsService.getTagStatsByLodge(lodgeId);
        model.addAttribute("tagStats", tagStats);
        List<TagCountDTO> topTags = tagStats.stream()
                .sorted(Comparator.comparingLong(TagCountDTO::count).reversed())
                .limit(3)
                .toList();
        model.addAttribute("topTags", topTags);

        // 차트용 레이블·카운트 리스트 생성
        List<String> tagLabels = tagStats.stream()
                .map(TagCountDTO::tagName)
                .toList();
        List<Long> tagCounts = tagStats.stream()
                .map(TagCountDTO::count)
                .toList();

        // JSON 직렬화하여 모델에 추가
        model.addAttribute("tagLabelsJson", objectMapper.writeValueAsString(tagLabels));
        model.addAttribute("tagCountsJson", objectMapper.writeValueAsString(tagCounts));

        // 기존 리스트도 유지
        model.addAttribute("tagLabels", tagLabels);
        model.addAttribute("tagCounts", tagCounts);

        // 4) 현재 사용자 이메일
        String currentUserEmail = null;
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();
            currentUserEmail = principal instanceof OAuth2User
                    ? ((OAuth2User) principal).getAttribute("email")
                    : authentication.getName();
        }
        model.addAttribute("currentUserEmail", currentUserEmail);

        // 5) 평균 평점과 총 리뷰 수 추가
        double avgRating  = reviewStatsService.getAverageRatingByLodge(lodgeId);
        long totalReviews = reviewStatsService.getTotalReviewCountByLodge(lodgeId);
        model.addAttribute("avgRating",    String.format("%.1f", avgRating));
        model.addAttribute("totalReviews", totalReviews);

        ReviewSummaryDTO brief = reviewSummaryService.findByLodgeIdOrGenerate(lodgeId);
        if (brief != null) {
            model.addAttribute("aiSummary", brief.getSummaryText());
        }


        return "fragments/review :: review";
    }


    //  리뷰 작성
    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String post(
            @ModelAttribute("reviewRequest") ReviewRequestDTO dto,
            @RequestParam(name = "files", required = false) MultipartFile[] files,
            @RequestParam("pageParam") int pageParam,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/user/login";
        }
        if (authentication.getPrincipal() instanceof OAuth2User) {
            String userUid = authentication.getName();
            UserDTO userDTO = userService.isSocialDuplicateUser(userUid);
            if (userDTO == null) {
                return "redirect:/user/login";
            }
            dto.setUserId(userDTO.getUserId());
            dto.setUserEmail(userDTO.getUserEmail());
        } else {
            String email = authentication.getName();
            UserDTO userDTO = userService.selectUserEmail(email);
            if (userDTO == null) {
                return "redirect:/user/login";
            }
            dto.setUserId(userDTO.getUserId());
            dto.setUserEmail(email);
        }
        ReviewResponseDTO result = reviewService.createReview(dto, files);
        return "redirect:/lodge/detail/"
                + dto.getLodgeId()
                + "?page=" + pageParam
                + "#reviewArea";
    }

    //  리뷰 수정 폼
    @GetMapping("/modify")
    public String showModifyForm(
            @RequestParam Long reviewId,
            @RequestParam(name="page", defaultValue="1") int pageParam,
            Model model
    ) {
        ReviewResponseDTO existing = reviewService.getReview(reviewId);
        ReviewRequestDTO formDto = new ReviewRequestDTO();
        formDto.setReviewId(existing.getReviewId());
        formDto.setLodgeId(existing.getLodgeId());
        formDto.setRating(existing.getRating());
        formDto.setContent(existing.getContent());
        formDto.setTagNames(existing.getTags());

        List<ReviewImageDTO> existingImages =
                reviewImageRepository.findByReviewId(reviewId)
                        .stream()
                        .map(ReviewImageDTO::fromEntity)
                        .toList();

        model.addAttribute("reviewRequest", formDto);
        model.addAttribute("tags", reviewTagService.getAllTags());
        model.addAttribute("existingImages", existingImages);
        model.addAttribute("pageParam", pageParam);
        return "review/review_modify";
    }

    //  리뷰 수정 처리
    @PostMapping("/modify")
    public String modifySubmit(
            @ModelAttribute("reviewRequest") ReviewRequestDTO dto,
            @RequestParam(name = "files", required = false) MultipartFile[] files,
            @RequestParam(name = "deleteImageUuids", required = false) List<String> deleteImageUuids,
            @RequestParam("pageParam") int pageParam
    ) {
        ReviewResponseDTO updated = reviewService.updateReview(
                dto.getReviewId(),
                dto,
                files,
                deleteImageUuids
        );
        return "redirect:/lodge/detail/"
                + dto.getLodgeId()
                + "?page=" + pageParam
                + "#review-" + updated.getReviewId();
    }

    //  리뷰 삭제
    @GetMapping("/remove/{reviewId}")
    public String remove(@PathVariable Long reviewId) {
        ReviewResponseDTO dto = reviewService.getReview(reviewId);
        reviewService.deleteReview(reviewId);
        return "redirect:/lodge/detail/"
                + dto.getLodgeId()
                + "?page=1#reviewArea";
    }

    //  좋아요 토글
    @PostMapping("/{reviewId}/like")
    public String like(
            @PathVariable Long reviewId,
            @RequestParam("pageParam") int pageParam,
            Authentication authentication
    ) {
        String identifier = authentication.getName();
        reviewService.toggleLike(reviewId, identifier); 
        Long lodgeId = reviewService.getReview(reviewId).getLodgeId();
        return "redirect:/lodge/detail/" + lodgeId
                + "?page=" + pageParam
                + "#review-" + reviewId;
    }

    //  신고 토글
    @PostMapping("/{reviewId}/report")
    public String report(
            @PathVariable Long reviewId,
            @RequestParam("pageParam") int pageParam,
            Authentication authentication
    ) {
        String identifier = authentication.getName();
        reviewService.toggleReport(reviewId, identifier);
        Long lodgeId = reviewService.getReview(reviewId).getLodgeId();
        return "redirect:/lodge/detail/" + lodgeId
                + "?page=" + pageParam
                + "#review-" + reviewId;
    }
    @PostMapping("/{lodgeId}/generate-summary")
    public String generateSummaryManually(@PathVariable Long lodgeId) {
        // 1) 강제 생성 (업서트)
        reviewSummaryService.generateSummary(lodgeId);
        // 2) 다시 리뷰 프래그먼트 페이지로
        return "redirect:/lodge/detail/" + lodgeId + "?page=1";
    }



}
