package com.berry.project.controller;

import com.berry.project.entity.review.ReviewTag;
import com.berry.project.service.review.ReviewTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review-tags")
public class ReviewTagController {
  private final ReviewTagService reviewTagService;

  @GetMapping
  @ResponseBody
  public List<ReviewTag> getTags() {
    return reviewTagService.getAllTags();
  }
}