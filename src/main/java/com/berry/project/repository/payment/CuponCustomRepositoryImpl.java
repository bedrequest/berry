package com.berry.project.repository.payment;

import com.berry.project.entity.cupon.Cupon;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.berry.project.entity.cupon.QCupon.cupon;

@Slf4j
public class CuponCustomRepositoryImpl implements CuponCustomRepository{
  // 초기화
  private final JPAQueryFactory queryFactory;

  public CuponCustomRepositoryImpl(EntityManager em){
    this.queryFactory = new JPAQueryFactory(em);
  }

  
  /** pageCuponType(String keyword, Pageable pageable, int dataSet)
   *  - where cuponType = dataset 로 조회한 Record 를 Page<Cupon> 으로 반환
   *
   * */
  @Override
  public Page<Cupon> pageCuponType(String keyword, Pageable pageable, Integer dataSet) {
    /** 쿼리 작성 및 페이지 적용
     *
     *  > selectFrom() 은 엔티티 전체를 조회할 때 사용하는 메서드이고
     *    select() 는  부분 컬럼, DTO 프로젝션, 집계( count/sum … ), 여러 표현식 등에서 사용하는 메서드
     * */
    List<Cupon> result = queryFactory
        .selectFrom(cupon)
        .where(
          // 쿠폰 타입이 dataSet 인 Record 만 조회
          cuponTypeEq(dataSet),
          // keyword 검색
          userIdPropertiesContain(keyword)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(cupon.cuponRegDate.desc())
        .fetch();


    /** 검색된 데이터의 전체 개수를 직접 조회
     *
     * > keyword 가 null 인 경우 (cuponTemplateRepository.findAll(pageable)) 에는 알아서 페이징 및 전체 개수
     *   를 조회했지만 동적 쿼리를 작성하여 페이징을 하는 경우 직접 전체 개수를 조회 해야 함
     *
     * > NPE 처리를 위해 Long 으로 받아 null 인 경우를 처리
     * */
    Long total = queryFactory
        .select(cupon.count())
        .from(cupon)
        .where(
            // 쿠폰 타입이 dataSet 인 Record 만 조회
            cuponTypeEq(dataSet),
            // keyword 검색
            userIdPropertiesContain(keyword)
        )
        .fetchOne();

    return new PageImpl<>(result, pageable, (total == null) ? 0 : total);
  }


  /** cuponTypeEq(int dataSet) - Cupon TABLE 에서 cuponType 이 dataSet 인 Record 를 조회
   *
   */
  private BooleanExpression cuponTypeEq(Integer dataSetCuponType){
    // cupon 을 사용하기 위해서는 import static com.berry.project.entity.cupon.QCupon.cupon; 작성 필요
    return cupon.cuponType.eq(dataSetCuponType);
  }


  /** userIdPropertiesContain(String keyword)
   *  - Cupon TABLE 에서 userId 를 대상으로 keyword 를 검색하는 헬퍼 함수
   *
   * */
  private BooleanExpression userIdPropertiesContain(String keyword){
    if(keyword == null || keyword.isBlank()) { return null; }

    try {
      long value = Long.parseLong(keyword.trim());

      return cupon.userId.eq(value);

    } // 숫자가 아닌 경우 userId 조건은 적용하지 않음 (전체 검색 유지)
    catch (NumberFormatException e){

      return null;
    }
  }

}

