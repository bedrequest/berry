package com.berry.project.repository.review;

import com.berry.project.entity.review.QReview;
import com.berry.project.entity.review.Review;
import com.berry.project.entity.user.QUser;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static com.berry.project.entity.review.QReview.review;

public class ReviewCustomRepositoryImpl implements ReviewCustomRepository{
  // 초기화
  private final JPAQueryFactory queryFactory;

  public ReviewCustomRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }


  /* pageReview() - 신고 내역 클릭 시 로드되는 페이지의 페이지네이션을 위한 메서드 */
  @Override
  public Page<Review> pageReview(Pageable pageable, String keyword) {
    // 검색
    BooleanExpression predicate = anyOfNotNull(
      eqInt(review.reportedCount, keyword),
      review.userEmail.containsIgnoreCase(keyword),
      review.content.containsIgnoreCase(keyword)
    );

    List<Review> result = queryFactory
        .selectFrom(review)
        .where(predicate, review.reportedCount.gt(1))
        .orderBy(pageableOfSortInPageReview(pageable.getSort(), review))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = queryFactory
        .select(review.count())
        .from(review)
        .where(predicate)
        .fetchOne();

    return new PageImpl<>(result, pageable, (total == null) ? 0 : total);
  }


  /* anyOfNotNull() - 여러 BooleanExpression 을 OR 로 묶되 Null 이 있다면 건너뜀 */
  private BooleanExpression anyOfNotNull(BooleanExpression ...vargs){
    BooleanExpression cond = null;

    for(BooleanExpression b : vargs){
      if(b != null){ cond = (cond == null) ? b : cond.or(b); }
    }

    return cond;
  }


  /** eqLong() - 숫자로 파싱되면 eq 조건 반환 아닌 경우 Null 을 반환 */
  private BooleanExpression eqInt(NumberPath<Integer> path, String kw){
    if(kw == null) { return null; }

    try{ return path.eq(Integer.parseInt(kw)); }

    catch(NumberFormatException e){ return null; }
  }


  /** (헬퍼 함수) pageableOfSortInPageReview() - 화이트리스트 */
  private OrderSpecifier<?>[] pageableOfSortInPageReview(Sort sort, QReview qReview){
    // 초기화
    List<OrderSpecifier<?>> list = new ArrayList<>();

    for(Sort.Order o : sort){
      Order dir = o.isAscending() ? Order.ASC : Order.DESC;

      // switch는 뷰에서 온 Value (e.g., LATEST-SIGN-UP) 이 아니라 정렬에 사용할 엔티티 필드명을 받아야 함
      switch(o.getProperty()){
        // 정렬 기준을 추가하고 싶다면 case 를 추가 하면 됨
        case "LATEST" -> list.add(new OrderSpecifier<>(dir, qReview.createdAt));
        case "MOST-REPORTED" -> list.add(new OrderSpecifier<>(dir, qReview.reportedCount));
        default -> { break; }
      }
    }

    return list.toArray(new OrderSpecifier<?>[0]);
  }
}
