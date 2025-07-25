package com.berry.project.repository.search;

import com.berry.project.entity.search.Search;
import com.berry.project.util.RegionNameUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.crizin.KoreanUtils;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.berry.project.entity.search.QSearch.search;

public class SearchCustomRepositoryImpl implements SearchCustomRepository {

  private final JPAQueryFactory queryFactory;

  public SearchCustomRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

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

    BooleanExpression condition = null;
    for (String jaso : jasoSet) {
      String searchBy = "%" + jaso + "%";
      BooleanExpression entry = search.jasoKeyword.likeIgnoreCase(searchBy)
          .or(search.jasoDetail.likeIgnoreCase(searchBy));

      condition = condition == null ? entry : condition.and(entry);
    }

    return queryFactory.selectFrom(search)
        .where(condition)
        .orderBy(search.lodgeId.asc().nullsFirst())
        .fetch();
  }
}
