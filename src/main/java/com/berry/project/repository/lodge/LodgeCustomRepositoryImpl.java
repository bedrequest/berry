package com.berry.project.repository.lodge;

import com.berry.project.dto.admin.AdminLodgeDTO;
import com.berry.project.dto.lodge.ListOptionDTO;
import com.berry.project.dto.lodge.LodgeOptionDTO;
import com.berry.project.entity.lodge.Lodge;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// duorpeb, JpaQueryFactory 초기화
import static com.berry.project.entity.lodge.QLodge.lodge;

@Slf4j
public class LodgeCustomRepositoryImpl implements LodgeCustomRepository {

  @Autowired
  private EntityManager entityManager;

  // duorpeb, JPAQueryFactory 를 사용하기 위한 초기화
  private JPAQueryFactory queryFactory;

  public LodgeCustomRepositoryImpl(EntityManager entityManager){
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Page<Lodge> searchLodges(ListOptionDTO listOptionDTO, LodgeOptionDTO lodgeOptionDTO, Pageable pageable) {
    Map<String, String> parameters = new HashMap<>();

    String searchKeyword = "%" + listOptionDTO.getKeyword() + "%";
    String subquery = "with subquery as ",
        source = " from lodge l left join subquery s on (s.lodge_id = l.lodge_id)",
        sortOption = " order by s.sort_option is null, ";
    StringBuilder condition = new StringBuilder(" where (l.lodge_addr like :keyword");
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
    String subQuery = " and l.lodge_id in " +
        "(select distinct lodge_id from room r where " +
        "greatest(coalesce(rent_price, 0), coalesce(stay_price, 0)) >= :lowestPrice" +
        " and least(coalesce(rent_price, 9999999), coalesce(stay_price, 9999999)) <= :highestPrice)";
    parameters.put("lowestPrice", String.valueOf(min));
    parameters.put("highestPrice", String.valueOf(max));
    condition.append(subQuery);

    // 5. 태그 대응(리뷰 완성 이후)

    // 6. 정렬 옵션
    switch (listOptionDTO.getSort()) {
      case "평점높은순" -> {
        subquery += "(select lodge_id, avg(rating) sort_option from review group by lodge_id) ";
        sortOption += "sort_option desc, s.lodge_id";
      }
      case "리뷰많은순" -> {
        subquery += "(select lodge_id, count(review_id) sort_option from review group by lodge_id) ";
        sortOption += "sort_option desc, s.lodge_id";
      }
      case "낮은가격순" -> {
        subquery += "(select lodge_id, least(coalesce(min(rent_price), 9999999), min(stay_price)) sort_option from room group by lodge_id) ";
        sortOption += "sort_option, s.lodge_id";
      }
      case "높은가격순" -> {
        subquery += "(select lodge_id, greatest(coalesce(max(rent_price), 0), max(stay_price)) sort_option from room group by lodge_id) ";
        sortOption += "sort_option desc, s.lodge_id";
      }
      default -> {
        subquery = "";
        source = " from lodge l";
        sortOption = "";
      }
    }

    // 쿼리 작성
    Query query = entityManager.createNativeQuery(subquery + "select l.* " + source + condition + sortOption, Lodge.class),
        totalCount = entityManager.createNativeQuery("select count(lodge_id) from lodge l" + condition, Long.class);
    for (String key : parameters.keySet()) {
      query.setParameter(key, parameters.get(key));
      totalCount.setParameter(key, parameters.get(key));
    }

    // 메인 쿼리에 페이징 적용(JPQL에는 limit이 없음)
    query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize());
    query.setMaxResults(pageable.getPageSize());

    return new PageImpl<Lodge>(query.getResultList(), pageable, (long) totalCount.getSingleResult());
  }


  /** duorpeb, pageLodge() - 관리자 페이지에서 숙소 별 정보 클릭 시 사용하는 메서드 */
  @Override
  public Page<Lodge> pageLodge(Pageable pageable, String keyword) {
    // 검색
    BooleanExpression predicate = anyOfNotNull(
      lodge.lodgeName.containsIgnoreCase(keyword),
      lodge.lodgeAddr.containsIgnoreCase(keyword),
      lodge.businessCall.containsIgnoreCase(keyword)
    );

    // 쿼리 및 페이징 적용
    List<Lodge> result = queryFactory
        .selectFrom(lodge)
        .where(predicate)
        .orderBy(lodge.lodgeId.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 필터링된 전체 데이터 개수 조회
    Long total = queryFactory
        .select(lodge.count())
        .from(lodge)
        .where(predicate)
        .fetchOne();

    return new PageImpl<>(result, pageable, (total == null) ? 0 : total);
  }


  /* duorpeb, anyOfNotNull() - 여러 BooleanExpression 을 OR 로 묶되 Null 이 있다면 건너뜀 */
  private BooleanExpression anyOfNotNull(BooleanExpression ...vargs){
    BooleanExpression cond = null;

    for(BooleanExpression b : vargs){
      if(b != null){ cond = (cond == null) ? b : cond.or(b); }
    }

    return cond;
  }
}
