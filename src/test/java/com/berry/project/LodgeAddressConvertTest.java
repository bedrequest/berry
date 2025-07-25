package com.berry.project;

import com.berry.project.entity.lodge.Lodge;
import com.berry.project.repository.lodge.LodgeRepository;
import com.berry.project.util.RegionNameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lodge의 주소에 있는 축약어를 풀어주는 DB 수정 테스트
 * 예) 서울 -> 서울특별시
 * 참고: /util/RegionNameUtils
 **/
@Transactional
@Rollback(false)
@SpringBootTest
public class LodgeAddressConvertTest {

  @Autowired
  private LodgeRepository lodgeRepository;

  @Test
  void lodgeAddressConvertTest() {
    List<Lodge> lodges = lodgeRepository.findAll();

    for (Lodge lodge : lodges) {
      String[] split = lodge.getLodgeAddr().split(" "),
          converted = new String[split.length];
      for (int i = 0; i < split.length; i++)
        converted[i] = RegionNameUtils.expandRegionName(split[i]);

      lodge.setLodgeAddr(String.join(" ", converted));
    }
  }

}
