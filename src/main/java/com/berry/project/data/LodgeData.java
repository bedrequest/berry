package com.berry.project.data;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * /lodge의 static data를 제공해주는 클래스
 */
@Component
@Getter
public class LodgeData {
  //  1. /lodge/list용
  private final List<String> lodgeTypes
      = List.of("모텔", "호텔·리조트", "펜션", "캠핑");
  private final List<String> publicFacilities
      = List.of("사우나", "수영장", "바베큐", "레스토랑", "피트니스",
      "물놀이시설", "공용샤워실", "공용화장실", "매점", "주방/식당", "건조기", "탈수기"),
      innerFacilities
          = List.of("스파/월풀", "객실스파", "미니바", "무선인터넷",
          "에어컨", "욕실용품", "샤워실", "개인콘센트"),
      otherFacilities
          = List.of("조식제공", "무료주차", "반려견동반", "사우나/찜질방",
          "객실내취사", "픽업서비스", "캠프파이어", "개인사물함",
          "객실내흡연", "짐보관가능", "스프링클러");
  private final List<String> favorites
      = List.of("깨끗해요", "경치가 좋아요", "인테리어가 좋아요", "친절해요",
      "방음이 좋아요", "대중교통이 편해요", "아이와 가기 좋아요",
      "즐길거리가 많아요", "조용히 쉬기 좋아요");
  private final List<String> lodgeSortOptions = List.of("추천순", "평점높은순", "리뷰많은순", "낮은가격순", "높은가격순");

  //  2. /lodge/detail용
  private final Map<String, String> facilityIconMap
      = Map.ofEntries(Map.entry("사우나", "spa"),
      Map.entry("수영장", "swim"),
      Map.entry("바베큐", "barbecue"),
      Map.entry("레스토랑", "restaurant"),
      Map.entry("피트니스", "fitness"),
      Map.entry("물놀이시설", "waterpark"),
      Map.entry("공용샤워실", "shower"),
      Map.entry("공용화장실", "default"),
      Map.entry("매점", "store"),
      Map.entry("주방/식당", ("restaurant")),
      Map.entry("건조기", "dryer"),
      Map.entry("탈수기", "dryer"),
      Map.entry("스파/월풀", "bathtub"),
      Map.entry("객실스파", "bathtub"),
      Map.entry("미니바", "minibar"),
      Map.entry("무선인터넷", "wifi"),
      Map.entry("에어컨", "air_conditioning"),
      Map.entry("욕실용품", "bathroom_supplies"),
      Map.entry("샤워실", "shower"),
      Map.entry("개인콘센트", "plug"),
      Map.entry("조식제공", "breakfast_croissant"),
      Map.entry("무료주차", "car"),
      Map.entry("반려견동반", "pet"),
      Map.entry("사우나/찜질방", "spa"),
      Map.entry("객실내취사", "cooking"),
      Map.entry("픽업서비스", "pickup"),
      Map.entry("캠프파이어", "campfire"),
      Map.entry("개인사물함", "locker"),
      Map.entry("객실내흡연", "smoking"),
      Map.entry("짐보관가능", "baggage"),
      Map.entry("스프링클러", "sprinkler"),
      Map.entry("금연", "no_smoking"));
  private final List<String> reviewSortOptions = List.of("추천순", "최신순", "평점 높은순", "평점 낮은순");

  private final int[] priceTable = {0, 30000, 50000, 100000, 200000, 300000, 400000, 9999999};
}
