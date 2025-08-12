package com.berry.project.repository.search;

import com.berry.project.entity.search.Search;
import com.berry.project.util.RegionNameUtils;
import io.github.crizin.KoreanUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Slf4j
public class SearchCustomRepositoryImpl implements SearchCustomRepository {

  @Autowired
  private EntityManager entityManager;
  
  @SuppressWarnings("unchecked")
  @Override
  public List<Search> findThatContains(String keyword) {
    if (keyword == null || keyword.isEmpty()) return null;

    // 성능이 최우선이므로 steam 대신 반복문으로 직접 변환
    String[] split = keyword.split(" ");
    Set<String> jasoSet = new HashSet<>();
    for (String word : split) {
      String expandedRegionName = RegionNameUtils.expandRegionName(word),
          jaso = KoreanUtils.decompose(expandedRegionName);

      jasoSet.add(jaso);
    }

    StringBuilder queryBuilder = new StringBuilder("select * from search where ");
    Map<Integer, String> parameters = new HashMap<>();
    int idx = 1;
    for (String jaso : jasoSet) {
      queryBuilder.append("(jaso_keyword like ?").append(idx).append(" or jaso_detail like ?").append(idx).append(")");
      if (idx < jasoSet.size()) queryBuilder.append(" and ");

      parameters.put(idx, "%" + jaso + "%");
      idx++;
    }
    queryBuilder.append(" order by char_length(detail)");

    log.info(queryBuilder.toString());

    Query query = entityManager.createNativeQuery(queryBuilder.toString(), Search.class);
    for (int key : parameters.keySet())
      query.setParameter(key, parameters.get(key));
    
    return query.getResultList();
  }
}
