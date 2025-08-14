package com.berry.project.repository.payment;

import com.berry.project.entity.cupon.CuponTemplate;
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

import static com.berry.project.entity.cupon.QCuponTemplate.cuponTemplate;


/**
 *
 *  > BooleanExpression 은 QueryDSL 의 불리언 조건 표현식 타입으로 JPQL 의 where ... 자리에
 *    들어가는 타입의 안전한 조건을 객체로 표현한 것
 * */
@Slf4j
public class CuponTemplateCustomRepositoryImpl implements CuponTemplateCustomRepository {
    // 초기화
    private final JPAQueryFactory queryFactory;

    public CuponTemplateCustomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    /** pageCuponTemplate(String keyword, Pageable pageable)
     *  - CuponTemplate 의 Record 를 Page<CuponTemplate> 로 반환 */
    @Override
    public Page<CuponTemplate> pageCuponTemplate(String keyword, Pageable pageable) {
        // 쿼리 작성 및 페이지 적용
        List<CuponTemplate> result = queryFactory
                .selectFrom(cuponTemplate)
                .where(allPropertiesContain(keyword))
                .orderBy(cuponTemplate.ctId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        /** 검색된 데이터의 전체 개수를 직접 조회
         *
         * > keyword 가 null 인 경우 (cuponTemplateRepository.findAll(pageable)) 에는 알아서 페이징 및 전체 개수
         *   를 조회했지만 동적 쿼리를 작성하여 페이징을 하는 경우 직접 전체 개수를 조회 해야 함
         *
         * > NPE 처리를 위해 Long 으로 받아 null 인 경우를 처리  
         * */
        Long total = queryFactory
                .select(cuponTemplate.count())
                .from(cuponTemplate)
                .where(allPropertiesContain(keyword))
                .fetchOne();

        return new PageImpl<>(result, pageable, (total == null) ? 0 : total);
    }


    /** allPropertiesContain(String keyword)
     *  - CuponTemplate TABLE 에서 모든 컬럼을 대상으로 Keyword 를 검색하는 헬퍼 함수
     *
     * */
    private Predicate allPropertiesContain(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        /* NPE 처리
        *
        * > ExpressionUtils.anyOf(); 로 NPE 처리
        *
        * > 기존 코드
        *
        *   - 반환 타입 : BooleanExpression
        *
        *   return cuponTemplate.cuponTitle.containsIgnoreCase(keyword)
                .or(cuponTemplate.cuponImgName.containsIgnoreCase(keyword))
                .or(likeInteger(keyword))
                .or(likeLong(keyword));

        * */
         // 변경 코드
        return ExpressionUtils.anyOf(
            cuponTemplate.cuponTitle.containsIgnoreCase(keyword),
            cuponTemplate.cuponImgName.containsIgnoreCase(keyword),
            likeInteger(keyword),
            likeLong(keyword)
        );
    }


    /** likeInteger(String keyword)
     *  - keyword를 int 로 파싱해 cuponTemplate.cuponType.eq(numericValue) 조건을 반환
     *    (CuponType 에서 keyword 를 검색하는 헬퍼 함수)
     *
     *  > .eq() 는 QueryDSL에서 “컬럼 = 값” 비교식을 만드는 메서드로 항상 BooleanExpression 을 return 하며
     *    where(...) 에 넣어 필터링에 사용
     *
     * */
    private BooleanExpression likeInteger(String keyword) {
        try {
            int numericValue = Integer.parseInt(keyword);
            return cuponTemplate.cuponType.eq(numericValue);

        } catch (NumberFormatException e) {
            return null;
        }
    }


    /** likeLong(String keyword) - 
     *  - keyword 를 long 으로 파싱해 cuponPrice/theMinimumAmount/qty 중 하나 일치의 OR 조건을 반환
     *    (쿠폰 가격, 최소 주문금액, 수량에서 keyword 를 검색하는 헬퍼 함수)
     *
     * */
    private BooleanExpression likeLong(String keyword) {
        try {
            long numericValue = Long.parseLong(keyword);
            return cuponTemplate.cuponPrice.eq(numericValue)
                    .or(cuponTemplate.theMinimumAmount.eq(numericValue))
                    .or(cuponTemplate.qty.eq(numericValue));

        } catch (NumberFormatException e) {
            return null;
        }
    }
}