package com.berry.project.repository.payment;

import com.berry.project.entity.payment.PaymentReceipt;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static com.berry.project.entity.payment.QPaymentReceipt.paymentReceipt;

public class PaymentReceiptCustomRepositoryImpl implements PaymentReceiptCustomRepository {
  // 초기화
  private final JPAQueryFactory queryFactory;

  // EntityManager 를 파라미터로 받아서 초기화
  public PaymentReceiptCustomRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }


  /* pagePaymentReceipt() - 결제 완료 내역 페이지 로드시 필요한 페이지네이션 */
  @Override
  public Page<PaymentReceipt> pagePaymentReceipt(Pageable pageable, String keyword) {
    // 검색
    BooleanExpression predicate = anyOfNotNull(
      paymentReceipt.type.containsIgnoreCase(keyword),
      paymentReceipt.method.containsIgnoreCase(keyword),
      paymentReceipt.orderId.containsIgnoreCase(keyword),
      paymentReceipt.orderName.containsIgnoreCase(keyword),
      eqLong(paymentReceipt.totalAmount, keyword),
      /** 에러 발생 코드
       *
       *  > paymentReceipt.requestedAt.eq(OffsetDateTime.parse(keyword)),
       *    paymentReceipt.approvedAt.eq(OffsetDateTime.parse(keyword))
       */
      eqOffsetDateTime(paymentReceipt.requestedAt, keyword),
      eqOffsetDateTime(paymentReceipt.approvedAt, keyword)
    );

    // 쿼리 작성 및 페이징 적용
    List<PaymentReceipt> result = queryFactory
        .selectFrom(paymentReceipt)
        .where(predicate)
        .orderBy(paymentReceipt.approvedAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();


    // 필터링된 전체 데이터 개수 조회
    Long total = queryFactory
        .select(paymentReceipt.count())
        .from(paymentReceipt)
        .where(predicate)
        .fetchOne();

    return new PageImpl<>(result, pageable, (total == null) ? 0 : total);
  }


  /* anyOfNotNull() - 여러 BooleanExpression 을 OR 로 묶되 Null 이 있다면 건너뛰어 NPE 예방 */
  private BooleanExpression anyOfNotNull(BooleanExpression ...vargs){
    BooleanExpression cond = null;

    for(BooleanExpression b : vargs){
      if(b != null) { cond = (cond == null) ? b : cond.or(b); }
    }

    return cond;
  }


  /* eqLong() - 숫자로 파싱되면 eq 조건 아닌 경우 Null 을 반환 */
  private BooleanExpression eqLong(NumberPath<Long> path, String kw){
    if(kw == null){ return null; }

    try{ return path.eq(Long.parseLong(kw)); }

    catch(NumberFormatException e){ return null; }
  }


  /* eqOffsetDateTime() - 날짜 관련 NPE 방지 메서드
  *
  *  > paymentReceipt.requestedAt.eq(OffsetDateTime.parse(keyword)), paymentReceipt
  *    .approvedAt.eq(OffsetDateTime.parse(keyword)) 와 같이 사용하는 경우
  *    OffsetDateTime.parse(keyword)는 키워드가 날짜-시간 포맷이 아니면 즉시 예외를 던짐 (=500 위험).
  *
  *   > eqOffsetDateTime() 과 같은 메서드를 만들어 사용하면 조건부로 필터를 추가 (실패 시 null을 반환)
  *     하므로 predicate 로 무시할 수 있음
  *
  *   > 즉, paymentReceipt.requestedAt.eq(OffsetDateTime.parse(keyword)) 와 같이 사용할 때, 키워드가
  *     "할인"이면 해당 구문에서 바로 예외 발생
  * */
  private BooleanExpression eqOffsetDateTime(DateTimePath<OffsetDateTime> path, String kw){
    if(kw == null) { return null; }

    try{ return path.eq(OffsetDateTime.parse(kw)); }

    catch(DateTimeParseException e){ return null; }
  }
}
