package com.berry.project;

import com.berry.project.entity.lodge.Lodge;
import com.berry.project.entity.search.Search;
import com.berry.project.repository.lodge.LodgeRepository;
import com.berry.project.repository.search.SearchRepository;
import io.github.crizin.KoreanUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Rollback(false)
@SpringBootTest
public class SearchInsertionTest {

  @Autowired
  private LodgeRepository lodgeRepository;

  @Autowired
  private SearchRepository searchRepository;

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * 숙소의 이름과 주소를 기반으로 Search 테이블을 구성하는 test
   */
  @Test
  void searchInsertionTest() {

    Map<String, Search> names = new HashMap<>(), locations = new HashMap<>();

    List<Lodge> lodgeList = lodgeRepository.findAll();

    for (Lodge lodge : lodgeList) {
      String lodgeName = lodge.getLodgeName(),
          lodgeAddress = lodge.getLodgeAddr();
      String[] split = lodgeAddress.split(" ");

      String detailForLodge = lodge.getLodgeType() + ", "
          + String.join(" ", Arrays.copyOfRange(split, 0, 3));
      names.put(lodgeName, Search.builder()
          .keyword(lodgeName)
          .detail(detailForLodge)
          .jasoKeyword(KoreanUtils.decompose(lodgeName.replace(" ", "")))
          .jasoDetail(KoreanUtils.decompose(detailForLodge.replace(" ", "")))
          .lodgeId(lodge.getLodgeId())
          .build()
      );

      int maxLen = Math.min(split.length, 2);
      for (int idx = 0; idx < maxLen; idx++) {
        String totalAddress = String.join(" ",
            Arrays.copyOfRange(split, 0, idx + 1));
        locations.put(split[idx],
            Search.builder()
                .keyword(split[idx])
                .detail(totalAddress)
                .jasoKeyword(KoreanUtils.decompose(split[idx]))
                .jasoDetail(KoreanUtils.decompose(totalAddress.replace(" ", "")))
                .build()
        );
      }

    }

    int i = 0;
    for (Search search : names.values()) {
      searchRepository.save(search);
      i++;

      if (i % 100 == 0) {
        entityManager.flush();
        entityManager.clear();
      }
    }
    for (Search search : locations.values()) {
      searchRepository.save(search);
      i++;

      if (i % 100 == 0) {
        entityManager.flush();
        entityManager.clear();
      }
    }
    entityManager.flush();
    entityManager.clear();

  }
}
