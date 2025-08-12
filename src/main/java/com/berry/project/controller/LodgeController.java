package com.berry.project.controller;

import com.berry.project.api.NaverMapApi;
import com.berry.project.data.LodgeData;
import com.berry.project.dto.lodge.ListOptionDTO;
import com.berry.project.dto.lodge.LodgeDTO;
import com.berry.project.dto.lodge.LodgeOptionDTO;
import com.berry.project.dto.user.BookmarkLodgeDTO;
import com.berry.project.dto.user.UserDTO;
import com.berry.project.service.lodge.LodgeService;
import com.berry.project.service.review.ReviewService;
import com.berry.project.service.user.UserService;
import com.berry.project.util.FacilityMaskDecoder;
import com.berry.project.util.TagMaskDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RequestMapping("/lodge")
@RequiredArgsConstructor
@Slf4j
@Controller
public class LodgeController {

  private final LodgeService lodgeService;
  private final UserService userService;
  private final ReviewService reviewService;

  private final FacilityMaskDecoder facilityMaskDecoder;
  private final NaverMapApi naverMapApi;
  private final LodgeData lodgeData;

  @GetMapping("/list")
  public String list(Model model,
                     ListOptionDTO listOptionDTO,
                     LodgeOptionDTO lodgeOptionDTO,
                     @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
                     Principal principal) {
    getDateLog(lodgeOptionDTO);

    if (listOptionDTO.getKeyword().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

    int userFavorites = principal == null ? 0 : userService.getUserInfo(principal.getName()).getUserFavoriteTag();
    if (listOptionDTO.getFacilityMask() == null) listOptionDTO.setFacilityMask(userFavorites);

    model.addAttribute("pagingHandler",
        lodgeService.getLodgeList(pageNo, listOptionDTO, lodgeOptionDTO));
    model.addAttribute("lodgeOption", lodgeOptionDTO);
    model.addAttribute("facilities",
        facilityMaskDecoder.decode(listOptionDTO.getFacilityMask()));
    model.addAttribute("selectedFavorites", new TagMaskDecoder().decode(listOptionDTO.getFavoriteMask()));

    // 고정값들
    model.addAttribute("lodgeTypes", lodgeData.getLodgeTypes());
    model.addAttribute("publicFacilities", lodgeData.getPublicFacilities());
    model.addAttribute("innerFacilities", lodgeData.getInnerFacilities());
    model.addAttribute("otherFacilities", lodgeData.getOtherFacilities());
    model.addAttribute("favorites", lodgeData.getFavorites());
    model.addAttribute("sortOptions", lodgeData.getLodgeSortOptions());
    model.addAttribute("priceTable", lodgeData.getPriceTable());

    if (principal != null) {
      UserDTO userDTO = userService.getUserInfo(principal.getName());
      model.addAttribute("userDTO", userDTO);

      // 북마크 내역 가져오기
      List<BookmarkLodgeDTO> bookmarkLodgeList = userService.getBookmarkLodgeList(userDTO.getUserId());
      model.addAttribute("bookmarks", bookmarkLodgeList
          .stream().map(BookmarkLodgeDTO::getLodgeId).toList());
    }

    return "/lodge/list";
  }

  @GetMapping("/detail/{lodgeId}")
  public String detail(Model model,
                       @PathVariable("lodgeId") long lodgeId,
                       LodgeOptionDTO lodgeOptionDTO,
                       Principal principal) {
    getDateLog(lodgeOptionDTO);
    log.info(">> lodgeOption >> {}", lodgeOptionDTO);

    LodgeDTO lodgeDTO = lodgeService.detail(lodgeId, lodgeOptionDTO);
    if (lodgeDTO == null) return "/";
    lodgeDTO.setBestReview(reviewService.getTopLikedReviewByLodge(lodgeId));

    model.addAttribute("lodgeOption", lodgeOptionDTO);
    model.addAttribute("lodgeDTO", lodgeDTO);
    if (principal != null) {
      UserDTO userDTO = userService.getUserInfo(principal.getName());
      log.info(">> user : {}", userDTO);
      model.addAttribute("userDTO", userDTO);
      model.addAttribute("bookmark", userService.getBookmarkLodgeList(userDTO.getUserId())
          .stream().anyMatch(bookmarkLodgeDTO -> bookmarkLodgeDTO.getLodgeId().equals(lodgeId)));
    } else model.addAttribute("bookmark", false);

    // 고정값들
    model.addAttribute("naverMapId", naverMapApi.getNaverMapApiKey());
    model.addAttribute("facilityIconMap", lodgeData.getFacilityIconMap());
    model.addAttribute("sortOptions", lodgeData.getReviewSortOptions());
    return "/lodge/detail";
  }

  private void getDateLog(LodgeOptionDTO lodgeOptionDTO) {
    log.info(">> start date > {}", lodgeOptionDTO.getCheckIn());
    log.info(">> end date > {}", lodgeOptionDTO.getCheckOut());
  }
}
