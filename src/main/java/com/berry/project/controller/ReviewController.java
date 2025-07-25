package com.berry.project.controller;

import com.berry.project.dto.review.ReviewRequestDTO;
import com.berry.project.dto.review.ReviewResponseDTO;
import com.berry.project.service.review.ReviewService;
import com.berry.project.service.review.ReviewTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewTagService reviewTagService;

    // 1) 리뷰 목록 조회 (페이징 포함)
    @GetMapping("/list/{lodgeId}/{page}")
    public String list(
            @PathVariable Long lodgeId,
            @PathVariable int page,
            Model model
    ) {
        int size = 5;
        Page<ReviewResponseDTO> pageData =
                reviewService.getReviewsPageByLodgeId(lodgeId, page - 1, size);

        int pageGroupSize = 5;
        int currentPage = pageData.getNumber() + 1; // 1-based
        int totalPages = pageData.getTotalPages();
        int startPage = (currentPage - 1) / pageGroupSize * pageGroupSize + 1;
        int endPage   = Math.min(startPage + pageGroupSize - 1, totalPages);

        model.addAttribute("reviews",      pageData.getContent());
        model.addAttribute("lodgeId",      lodgeId);
        model.addAttribute("currentPage",  currentPage);
        model.addAttribute("startPage",    startPage);
        model.addAttribute("endPage",      endPage);
        model.addAttribute("hasPrev",      startPage > 1);
        model.addAttribute("hasNext",      endPage < totalPages);
        model.addAttribute("totalPages",   totalPages);
        model.addAttribute("tags",         reviewTagService.getAllTags());
        model.addAttribute("reviewRequest", new ReviewRequestDTO());
        return "/review/reviews";
    }

    // 2) 리뷰 작성
    @PostMapping("/post")
    public String post(@ModelAttribute("reviewRequest") ReviewRequestDTO dto) {
        reviewService.createReview(dto);
        return "redirect:/reviews/list/" + dto.getLodgeId() + "/1";
    }

    // 3) 리뷰 수정
    @PostMapping("/modify")
    public String modify(@ModelAttribute("reviewRequest") ReviewRequestDTO dto) {

        reviewService.updateReview(dto.getReviewId(), dto);
        return "redirect:/reviews/list/" + dto.getLodgeId() + "/1";
    }

    // 4) 리뷰 삭제
    @GetMapping("/remove/{reviewId}")
    public String remove(@PathVariable Long reviewId) {
        ReviewResponseDTO dto = reviewService.getReview(reviewId);
        reviewService.deleteReview(reviewId);
        return "redirect:/reviews/list/" + dto.getLodgeId() + "/1";
    }

    // 조회용 임시
    @GetMapping
    public String listByParams(
            @RequestParam Long lodgeId,
            @RequestParam(defaultValue = "1") int page,
            Model model
    ) {
        // 내부에서 아까 만든 list() 호출
        return list(lodgeId, page, model);
    }

}
