package com.berry.project.repository.lodge;

import com.berry.project.dto.lodge.ListOptionDTO;
import com.berry.project.dto.lodge.LodgeOptionDTO;
import com.berry.project.entity.lodge.Lodge;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LodgeCustomRepositoryImpl implements LodgeCustomRepository {

  @Autowired
  private EntityManager entityManager;

  @Override
  public Page<Lodge> searchLodges(ListOptionDTO listOptionDTO, LodgeOptionDTO lodgeOptionDTO, Pageable pageable) {
    Map<String, String> parameters = new HashMap<>();

    String searchKeyword = "%" + listOptionDTO.getKeyword() + "%";

    StringBuilder condition = new StringBuilder("from lodge l where (l.lodge_addr like :keyword");
    parameters.put("keyword", searchKeyword);

    // 1. freeForm 대응
    if (listOptionDTO.isFreeForm()) condition.append(" or l.lodge_name like :keyword)");
    else condition.append(")");

    // 2. lodgeType 대응
    if (listOptionDTO.getLodgeType() != null) {
      condition.append(" and l.lodge_type = :lodgeType");
      parameters.put("lodgeType", listOptionDTO.getLodgeType());
    }

    // 3. facilityMask 대응
    if (listOptionDTO.getFacilityMask() > 0) {
      condition.append(" and (l.facility & :facilityMask) = :facilityMask");
      parameters.put("facilityMask", String.valueOf(listOptionDTO.getFacilityMask()));
    }

    // 4. 가격 대응
    Integer min = listOptionDTO.getLowestPrice(), max = listOptionDTO.getHighestPrice();
    if (min != null || max != null) {
      if (min == null) min = 1;
      parameters.put("lowestPrice", min.toString());
      StringBuilder subQuery = new StringBuilder("and l.lodge_id in " +
          "(select distinct lodge_id from room r where " +
          "greatest(coalesce(rent_price, 0), coalesce(stay_price, 0)) >= :lowestPrice");
      if (max != null) {
        subQuery.append(" and least(coalesce(rent_price, 9999999), coalesce(stay_price, 9999999)) <= :highestPrice)");
        parameters.put("highestPrice", max.toString());
      }
      condition.append(subQuery);
    }

    // 5. 태그 대응(리뷰 완성 이후)


    // 쿼리 작성
    Query query = entityManager.createNativeQuery("select * " + condition, Lodge.class),
        totalCount = entityManager.createNativeQuery("select count(lodge_id) " + condition, Long.class);
    for (String key : parameters.keySet()) {
      query.setParameter(key, parameters.get(key));
      totalCount.setParameter(key, parameters.get(key));
    }

    // 메인 쿼리에 페이징 적용(JPQL에는 limit이 없음)
    query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize());
    query.setMaxResults(pageable.getPageSize());

    return new PageImpl<Lodge>(query.getResultList(), pageable, (long) totalCount.getSingleResult());
  }

}
