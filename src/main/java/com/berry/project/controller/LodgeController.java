package com.berry.project.controller;

import com.berry.project.dto.lodge.ListOptionDTO;
import com.berry.project.dto.lodge.LodgeOptionDTO;
import com.berry.project.service.lodge.LodgeService;
import com.berry.project.util.FacilityMaskDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/lodge")
@RequiredArgsConstructor
@Slf4j
@Controller
public class LodgeController {

    private final LodgeService lodgeService;
    private final FacilityMaskDecoder facilityMaskDecoder;

    @GetMapping("/list")
    public String list(Model model,
                       ListOptionDTO listOptionDTO,
                       LodgeOptionDTO lodgeOptionDTO,
                       @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo) {
        model.addAttribute("pagingHandler",
                lodgeService.getLodgeList(pageNo, listOptionDTO, lodgeOptionDTO));
        model.addAttribute("lodgeOption", lodgeOptionDTO);
        model.addAttribute("facilities",
                facilityMaskDecoder.decode(listOptionDTO.getFacilityMask()));
        model.addAttribute("selectedFavorites", List.of());

        model.addAttribute("lodgeTypes", List.of("모텔", "호텔·리조트", "펜션", "캠핑"));
        model.addAttribute("publicFacilities",
                List.of("사우나", "수영장", "바베큐", "레스토랑", "피트니스",
                        "물놀이시설", "공용샤워실", "공용화장실", "매점", "주방/식당", "건조기", "탈수기"));
        model.addAttribute("innerFacilities",
                List.of("스파/월풀", "객실스파", "미니바", "무선인터넷",
                        "에어컨", "욕실용품", "샤워실", "개인콘센트"));
        model.addAttribute("otherFacilities",
                List.of("조식제공", "무료주차", "반려견동반", "사우나/찜질방",
                        "객실내취사", "픽업서비스", "캠프파이어", "개인사물함",
                        "객실내흡연", "짐보관가능", "스프링클러"));
        model.addAttribute("favorites",
                List.of("깨끗해요", "경치가 좋아요", "인테리어가 좋아요", "친절해요",
                        "방음이 좋아요", "대중교통이 편해요", "아이와 가기 좋아요",
                        "즐길거리가 많아요", "조용히 쉬기 좋아요"));
        return "/lodge/list";
    }

    @GetMapping("/detail/{lodgeId}")
    public String detail(Model model,
                         @PathVariable("lodgeId") long lodgeId,
                         LodgeOptionDTO lodgeOptionDTO) {
        model.addAttribute("lodgeOption", lodgeOptionDTO);
        model.addAttribute("lodgeDTO", lodgeService.detail(lodgeId, lodgeOptionDTO));
        return "/lodge/detail";
    }
}