package com.berry.project.controller;

import com.berry.project.dto.review.ReviewRequestDTO;
import com.berry.project.dto.review.ReviewResponseDTO;
import com.berry.project.service.review.ReviewService;
import com.berry.project.service.review.ReviewTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review-test")
public class ReviewTestController {
    private final ReviewService reviewService;
    private final ReviewTagService reviewTagService;

    // 1) 테스트 폼 페이지
    @GetMapping
    public String reviewTestPage(Model model) {
        model.addAttribute("reviewRequest", new ReviewRequestDTO());
        model.addAttribute("tags", reviewTagService.getAllTags());
        return "/review/review-test";
    }

    // 2) 동기 호출 없이 JS fetch로 JSON 반환
    @PostMapping("/create")
    @ResponseBody
    public ReviewResponseDTO createTest(@RequestBody ReviewRequestDTO dto) {
        return reviewService.createReview(dto);
    }
}
