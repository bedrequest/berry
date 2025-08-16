package com.berry.project.controller;


import com.berry.project.dto.MainSlideDTO;
import com.berry.project.dto.lodge.LodgeSummaryDTO;
import com.berry.project.service.lodge.LodgeService;
import com.berry.project.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
public class IndexController {

  private final UserService userService;
  private final LodgeService lodgeService;

  @GetMapping("/")
  public String index(Principal principal, Model model) {
    model.addAttribute("mainSlide", List.of(
        new MainSlideDTO("색다른 하루를 보내고 싶다면,",
            "비 오는 날 가기 좋은 서울 실내 공간",
            "https://cdn.visitkorea.or.kr/img/call?cmd=VIEW&id=5043511a-4051-472c-9985-b5e52173af4e&mode=raw",
            "#d1fdff"),
        new MainSlideDTO("넷플릭스 <대환장 기안장> 속 그곳",
            "특별한 자연과 낭만이 있는 울릉도",
            "https://cdn.visitkorea.or.kr/img/call?cmd=VIEW&id=288910a6-63a3-4fe6-b630-dc345d620ad3&mode=raw",
            "#8cd9ff"),
        new MainSlideDTO("지구마불 따라, 남도 여행\uD83D\uDC9A",
            "푸릇한 매력을 머금은 담양",
            "https://cdn.visitkorea.or.kr/img/call?cmd=VIEW&id=d4464378-353b-437c-9606-07d9be0bd3ca&mode=raw",
            "#f3faaa"),
        new MainSlideDTO("자연의 신비를 간직한",
            "서산·제주로 떠나는<br>비밀의 목장 투어",
            "https://cdn.visitkorea.or.kr/img/call?cmd=VIEW&id=edb7f8f6-94ec-4522-b968-88f2063be2b3&mode=raw",
            "#e5f0bb"),
        new MainSlideDTO("강원도 속 작은 쉼터",
            "쉼이 필요할 때,<br>강원도 양양",
            "https://cdn.visitkorea.or.kr/img/call?cmd=VIEW&id=11c5b0b4-9be5-4fda-baa1-dd440e5ab961&mode=raw",
            "#ddf6fc"),
        new MainSlideDTO("선비의 발걸음을 따라,",
            "아이와 함께 떠나는<br>경북 영주",
            "https://cdn.visitkorea.or.kr/img/call?cmd=VIEW&id=ac72a997-699d-4707-8126-93c14e3b1a0d&mode=raw",
            "#fff4d6")
    ));

    List<LodgeSummaryDTO> top5 = lodgeService.getTopBookedLodges(5);
    log.info(">>>> top5");
    for (LodgeSummaryDTO lodgeSummaryDTO : top5) log.info(">> {}", lodgeSummaryDTO);
    model.addAttribute("top5", top5);

    return "index";
  }
}
