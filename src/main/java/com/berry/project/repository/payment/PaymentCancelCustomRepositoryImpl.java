package com.berry.project.repository.payment;

import com.berry.project.entity.payment.PaymentCancel;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static com.berry.project.entity.payment.QPaymentCancel.paymentCancel;

public class PaymentCancelCustomRepositoryImpl implements PaymentCancelCustomRepository {
  // 초기화
  private final JPAQueryFactory queryFactory;

  public PaymentCancelCustomRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }


  /* pagePaymentCanceled() - 환불 내역 클릭 시 로드되는 페이지의 페이지네이션을 위한 메서드 */
  @Override
  public Page<PaymentCancel> pagePaymentCanceled(Pageable pageable, String keyword) {
    // 검색
    BooleanExpression predicate = anyOfNotNull(
      paymentCancel.paymentKey.containsIgnoreCase(keyword),
      paymentCancel.cancelReason.containsIgnoreCase(keyword),
      eqInt(paymentCancel.cancelAmount, keyword),
      eqOffsetDateTime(paymentCancel.canceledAt, keyword)
    );

    // 쿼리 작성 및 페이징 적용
    List<PaymentCancel> result = queryFactory
        .selectFrom(paymentCancel)
        .where(predicate)
        .orderBy(paymentCancel.paymentCancelId.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 필터링된 전체 데이터 개수 조회
    Long total = queryFactory
        .select(paymentCancel.count())
        .from(paymentCancel)
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


  /** eqOffsetTime() - 날짜로 파싱되면 eq 조건 반환 아닌 경우 null 반환 */
  private BooleanExpression eqOffsetDateTime(DateTimePath<OffsetDateTime> path, String kw){
    if(kw == null) { return null; }

    try{ return path.eq(OffsetDateTime.parse(kw)); }

    catch(DateTimeParseException e){ return null; }
  }
}
