package com.berry.project.controller;

import com.berry.project.dto.search.SearchDTO;
import com.berry.project.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/search/*")
@Controller
public class SearchController {

  private final SearchService searchService;

  /**
   * keyword를 포함하는 추천 검색어의 List를 반환<br>
   * keyword가 비어있으면 null, 검색 결과가 없으면 빈 List를 반환<br>
   * <br>
   * keyword가 여러 단어로 이루어져 있으면 각 단어를 전부 포함하는지 검색<br>
   * 이 때 순서는 지키지 않으며, 검색 결과가 없으면 빈 List를 반환
   */
  @ResponseBody
  @GetMapping("/{keyword}")
  List<SearchDTO> keywords(@PathVariable("keyword") String keyword) {
    return searchService.getKeywords(keyword);
  }

}
