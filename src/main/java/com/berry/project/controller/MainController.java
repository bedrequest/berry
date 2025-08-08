package com.berry.project.controller;

import com.berry.project.dto.lodge.LodgeSummaryDTO;
import com.berry.project.service.lodge.LodgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final LodgeService lodgeService;

    /**
     * 메인 페이지
     * - Top5 예약 숙소 데이터를 가져와 뷰에 전달
     */
    @GetMapping("/")
    public String showMainPage(Model model) {
        // Top5 예약 숙소 조회
        List<LodgeSummaryDTO> top5 = lodgeService.getTopBookedLodges(5);
        model.addAttribute("top5", top5);

        // 추가로 메인에 필요한 다른 모델이 있으면 여기에 추가...
        return "index-test";  // resources/templates/main/index.html
    }
}
