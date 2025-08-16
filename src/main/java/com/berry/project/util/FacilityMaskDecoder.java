package com.berry.project.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * facility 테이블에서 값을 불러올 때 int를 List<String>으로 변환해주는 핸들러<br>
 * RequiredArgsConstructor나 Autowired로 불러올 것
 */
@Component
public class FacilityMaskDecoder {
  // 마스크 표
  private final String[] strArray = {
      "사우나", "수영장", "바베큐", "레스토랑", "피트니스", "물놀이시설", "공용샤워실", "공용화장실", "매점", "주방/식당", "건조기", "탈수기",
      "스파/월풀", "객실스파", "미니바", "무선인터넷", "에어컨", "욕실용품", "샤워실", "개인콘센트",
      "조식제공", "무료주차", "반려견동반", "사우나/찜질방", "객실내취사", "픽업서비스", "캠프파이어", "개인사물함", "객실내흡연", "짐보관가능", "스프링클러"
  };

  /**
   * 옵션들을 받아서 int로 변환<br>
   * 크롤링/테스트 용도
   */
  public int encode(List<String> options) {
    int answer = 0;
    for (int i = 0; i < strArray.length; i++)
      if (options.contains(strArray[i]))
        answer |= (1 << i);

    return answer;
  }

  /**
   * int를 옵션 List로 변환<br>
   * entity -> DTO 변환 과정에서 사용
   */
  public List<String> decode(int mask) {
    List<String> answer = new ArrayList<>();

    int count = 1;
    for (String option : strArray) {
      if ((mask & count) > 0) answer.add(option);
      count <<= 1;
    }

    return answer;
  }
}
