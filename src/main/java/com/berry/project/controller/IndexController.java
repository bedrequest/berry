package com.berry.project.controller;

import com.berry.project.api.NaverApi;
import com.berry.project.dto.lodge.LodgeWithTagCountDTO;
import com.berry.project.dto.user.BookmarkLodgeDTO;
import com.berry.project.dto.user.UserDTO;
import com.berry.project.handler.PagingHandler;
import com.berry.project.service.IndexService;
import com.berry.project.service.lodge.LodgeService;
import com.berry.project.service.user.UserService;
import com.berry.project.util.TagMaskDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
  private final LodgeService lodgeService;

  private final TagMaskDecoder tagMaskDecoder;

  private final NaverApi naverApi;

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

    // 네이버 검색 키
    model.addAttribute("naverSearchApiKey", naverApi.getNaverSearchApiKey());
    model.addAttribute("naverSearchApiSecret", naverApi.getNaverSearchApiSecret());

    return "index";
  }

  @GetMapping("/indexTest")
  public void indexTest(Model model, Principal principal) {
    model.addAttribute("bestLodges", lodgeService.getTop5Lodges());

    if (principal != null) {
      UserDTO userDTO = userService.getUserInfo(principal.getName());
      model.addAttribute("bookmarks",
          userService.getBookmarkLodgeList(userDTO.getUserId())
              .stream().map(BookmarkLodgeDTO::getLodgeId)
              .toList());
    }
  }

  // 네이버 검색
  @GetMapping("/outerSearch/{keyword}")
  @ResponseBody
  public String outerSearch(@PathVariable("keyword") String keyword) {
    String text = URLEncoder.encode(keyword + " 관광", StandardCharsets.UTF_8);

    String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + text;

    Map<String, String> requestHeaders = Map.of("X-Naver-Client-Id", naverApi.getNaverSearchApiKey(),
        "X-Naver-Client-Secret", naverApi.getNaverSearchApiSecret());

    return get(apiURL, requestHeaders);
  }

  private static String get(String apiUrl, Map<String, String> requestHeaders) {
    HttpURLConnection con = connect(apiUrl);
    try {
      con.setRequestMethod("GET");
      for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
        con.setRequestProperty(header.getKey(), header.getValue());
      }


      int responseCode = con.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
        return readBody(con.getInputStream());
      } else { // 오류 발생
        return readBody(con.getErrorStream());
      }
    } catch (IOException e) {
      throw new RuntimeException("API 요청과 응답 실패", e);
    } finally {
      con.disconnect();
    }
  }

  private static HttpURLConnection connect(String apiUrl) {
    try {
      URL url = new URL(apiUrl);
      return (HttpURLConnection) url.openConnection();
    } catch (MalformedURLException e) {
      throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
    } catch (IOException e) {
      throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
    }
  }

  private static String readBody(InputStream body) {
    InputStreamReader streamReader = new InputStreamReader(body);


    try (BufferedReader lineReader = new BufferedReader(streamReader)) {
      StringBuilder responseBody = new StringBuilder();


      String line;
      while ((line = lineReader.readLine()) != null) {
        responseBody.append(line);
      }


      return responseBody.toString();
    } catch (IOException e) {
      throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
    }
  }
}
