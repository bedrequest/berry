package com.berry.project.controller;

import com.berry.project.dto.lodge.LodgeWithTagCountDTO;
import com.berry.project.dto.user.BookmarkLodgeDTO;
import com.berry.project.dto.user.UserDTO;
import com.berry.project.handler.PagingHandler;
import com.berry.project.service.IndexService;
import com.berry.project.service.user.UserService;
import com.berry.project.util.TagMaskDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Slf4j
@Controller
public class IndexController {

  private final UserService userService;
  private final IndexService indexService;

  private final TagMaskDecoder tagMaskDecoder;

  @GetMapping("/")
  public String index(Principal principal, Model model) {
    // 1. 로그인한 유저의 선호 태그로 lodgeDTO 목록 가져오기
    if (principal != null) {
      UserDTO user = userService.getUserInfo(principal.getName());
      Map<String, PagingHandler<LodgeWithTagCountDTO>> tagMap = new HashMap<>();
      int count = 1;
      for (int i = 1; i <= 9; i++) {
        if ((count & user.getUserFavoriteTag()) != 0)
          tagMap.put(tagMaskDecoder.get(i - 1), indexService.getLodgeListByTag(1, i));

        count <<= 1;
      }

      for (String key : tagMap.keySet())
        log.info(">> 태그 : {}, 숙소 : {}", key, tagMap.get(key));
      model.addAttribute("tagMap", tagMap);

      model.addAttribute("userId", user.getUserId());
      // 북마크 내역 가져오기
      List<BookmarkLodgeDTO> bookmarkLodgeList = userService.getBookmarkLodgeList(user.getUserId());
      log.info("bookmarkLodgeList > {}", bookmarkLodgeList);
      model.addAttribute("bookmarks", bookmarkLodgeList
          .stream().map(BookmarkLodgeDTO::getLodgeId).toList());
    }

    // 2. 최신 리뷰 (10개)
    try {
      model.addAttribute("recentReviews", indexService.getRecentReviews());
    } catch (NoSuchElementException ignored) {}

    return "index";
  }
}
