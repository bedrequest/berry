package com.berry.project;

import com.berry.project.dto.lodge.ListOptionDTO;
import com.berry.project.dto.lodge.LodgeOptionDTO;
import com.berry.project.entity.lodge.Lodge;
import com.berry.project.repository.lodge.LodgeRepository;
import com.berry.project.util.FacilityMaskDecoder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@SpringBootTest
public class LodgeCustomRepositoryTest {

  @Autowired
  private LodgeRepository lodgeRepository;

  @Autowired
  private FacilityMaskDecoder facilityMaskDecoder;

  private static final Logger log = LoggerFactory.getLogger(LodgeCustomRepositoryTest.class);

  @Test
  void test() {
    Pageable pageable = PageRequest.of(0, 10);

    // 1. 지역 기준 검색
    log.info("1. 경주 지역 검색");
    printResult(lodgeRepository.searchLodges(
        new ListOptionDTO("경주", false),
        new LodgeOptionDTO(),
        pageable
    ));

    // 2. 전체 검색
    log.info("2. '여' 전체 기준 검색");
    printResult(lodgeRepository.searchLodges(
        new ListOptionDTO("여", true),
        new LodgeOptionDTO(),
        pageable
    ));

    log.info("3. 광주의 펜션 검색");
    printResult(lodgeRepository.searchLodges(
        new ListOptionDTO("광주", false, "펜션", 0, 9999999, 0, null),
        new LodgeOptionDTO(),
        pageable
    ));

    log.info("4. 서울에서 조식을 제공해주는 숙소");
    printResult(lodgeRepository.searchLodges(
        new ListOptionDTO("서울", false, null, 0, 9999999, facilityMaskDecoder.encode(List.of("조식제공")), null),
        new LodgeOptionDTO(),
        pageable
    ));

    log.info("5. 인천에서 5만원 이상인 객실이 존재하는 숙소");
    printResult(lodgeRepository.searchLodges(
        new ListOptionDTO("인천", false, null, 50000, 9999999, null, null),
        new LodgeOptionDTO(),
        pageable
    ));

    log.info("6. 나비 키워드로 가격이 3만원 ~ 7만원인 숙소");
    printResult(lodgeRepository.searchLodges(
        new ListOptionDTO("나비", true, null, 30000, 70000, null, null),
        new LodgeOptionDTO(),
        pageable
    ));

    log.info("7. 띄어쓰기 테스트");
    printResult(lodgeRepository.searchLodges(
        new ListOptionDTO("서울 강남", true, null, 0, 9999999, null, null),
        new LodgeOptionDTO(),
        pageable
    ));

    log.info("8. 태그 테스트");
    ListOptionDTO listOptionDTO = new ListOptionDTO("서울", true, null, 0, 9999999, null, null);
    listOptionDTO.setFavoriteMask(4);
    printResult(lodgeRepository.searchLodges(
        listOptionDTO,
        new LodgeOptionDTO(),
        pageable
    ));

    /*
    log.info("9. 태그 온리 테스트");
    printResult(lodgeRepository.searchByTag(4, pageable));
    */
  }

  private void printResult(Page<Lodge> result) {
    log.info(">>> {}", result.toList());
    log.info(">>> {}", result.getTotalElements());
  }
}
