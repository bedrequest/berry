package com.berry.project.dto.lodge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class LodgeSummaryDTO {
  private Long lodgeId;           // lodge.lodge_id
  private String lodgeName;       // lodge.lodge_name
  private String lodgeAddr;       // lodge.lodge_addr
  private Integer minStayPrice;   // room.stay_price 중 최소값
  private String lodgeImgUrl;     // lodge_img.lodge_img_url (대표 이미지)
  private Long bookingCount;      // 예약 건수 집계
  private String tagStatsJson;    // 태그 통계 JSON
  private String aiSummary;       // lodge_description.content 혹은 미리 생성된 요약
}