package com.berry.project.service.lodge;

import com.berry.project.dto.lodge.*;
import com.berry.project.dto.review.ReviewResponseDTO;
import com.berry.project.entity.lodge.Lodge;
import com.berry.project.entity.lodge.LodgeDescription;
import com.berry.project.entity.lodge.Room;
import com.berry.project.handler.PagingHandler;
import com.berry.project.util.FacilityMaskDecoder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

public interface LodgeService {
  LodgeDTO detail(long lodgeId, LodgeOptionDTO lodgeOptionDTO);

  default LodgeDTO convertEntityToDto(
      Lodge lodge,
      FacilityMaskDecoder facilityMaskDecoder,
      List<LodgeDescription> descriptions,
      long reviewCount,
      Double averageReviewScore,
      ReviewResponseDTO bestReview) {
    List<LodgeDescriptionDTO> descriptionDTOList = new ArrayList<>();
    JSONParser parser = new JSONParser();

    for (LodgeDescription entity : descriptions) {
      List<String> contents = new ArrayList<>();
      try {
        JSONArray parsed = (JSONArray) parser.parse(entity.getContent());
        for (Object item : parsed) contents.add(item.toString());
      } catch (Exception e) {
        continue;
      }

      descriptionDTOList.add(LodgeDescriptionDTO.builder()
          .title(entity.getTitle())
          .contents(contents)
          .build());
    }

    return LodgeDTO.builder()
        .lodgeId(lodge.getLodgeId())
        .lodgeName(lodge.getLodgeName())
        .lodgeType(lodge.getLodgeType())
        .lodgeAddr(lodge.getLodgeAddr())
        .facilities(facilityMaskDecoder.decode(lodge.getFacility()))
        .intro(lodge.getIntro())
        .description(descriptionDTOList)
        .businessCall(lodge.getBusinessCall())
        .latitude(lodge.getLatitude())
        .longitude(lodge.getLongitude())
        .rooms(new ArrayList<>())
        .reviewCount(reviewCount)
        .averageReviewScore(averageReviewScore)
        .bestReview(bestReview)
        .build();
  }

  default RoomDTO convertEntityToDto(Room room) {
    return RoomDTO.builder()
        .roomId(room.getRoomId())
        .lodgeId(room.getLodgeId())
        .roomName(room.getRoomName())
        .info(room.getInfo())
        .rentPrice(room.getRentPrice())
        .rentTime(room.getRentTime())
        .stayPrice(room.getStayPrice())
        .stayOption(room.getStayOption())
        .stayTime(room.getStayTime())
        .stockCount(room.getStockCount())
        .standardCount(room.getStandardCount())
        .maxCount(room.getMaxCount())
        .build();
  }

  PagingHandler<LodgeDTO> getLodgeList(int pageNo, ListOptionDTO listOptionDTO, LodgeOptionDTO lodgeOptionDTO);

  // ===== Top N 예약 숙소 집계 =====

  /**
   * 예약 건수 상위 Top N개의 숙소를 조회합니다.
   *
   * @param topN 가져올 개수 (예: 5)
   * @return LodgeSummaryDTO 리스트
   */
  List<LodgeSummaryDTO> getTopBookedLodges(int topN);

}
