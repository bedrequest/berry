package com.berry.project.repository.qna;

import com.berry.project.entity.qna.CustomerIqBoard;
import com.berry.project.entity.qna.QCustomerIqBoard;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.format.DateTimeFormatter;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Locale;

import static com.berry.project.entity.qna.QCustomerIqBoard.customerIqBoard;

@Slf4j
public class CustomerIqBoardCustomRepositoryImpl implements CustomerIqBoardCustomRepository {

  private final JPAQueryFactory queryFactory;

  public CustomerIqBoardCustomRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  // 공지글 목록 조회 (항상 상단)
  @Override
  public List<CustomerIqBoard> findNoticeBoards() {
    return queryFactory
        .selectFrom(QCustomerIqBoard.customerIqBoard)
        .where(QCustomerIqBoard.customerIqBoard.category.eq("공지"))
        .orderBy(QCustomerIqBoard.customerIqBoard.bno.desc())
        .limit(5)
        .fetch();
  }

  @Override
  public Page<CustomerIqBoard> searchcoustomeriqboard(String type, String keyword,
                                               String startDate, String endDate,
                                               Pageable pageable) {
    // String → LocalDateTime 변환
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime start = (startDate != null && !startDate.isEmpty())
        ? LocalDateTime.parse(startDate + " 00:00:00", formatter)
        : LocalDateTime.parse("2025-01-01 00:00:00", formatter);

    LocalDateTime end = (endDate != null && !endDate.isEmpty())
        ? LocalDateTime.parse(endDate + " 23:59:59", formatter)
        : LocalDateTime.now();

    // 날짜 조건 (필수)
    BooleanExpression condition = QCustomerIqBoard.customerIqBoard.regDate.between(start, end);
//    condition = condition.and(QCustomerIqBoard.customerIqBoard.category.ne("공지"));

    if (type != null && !type.isEmpty()) {
      condition = condition.and(QCustomerIqBoard.customerIqBoard.category.eq(type));
    }

    if (keyword != null && !keyword.isEmpty()) {
      condition = condition.and(QCustomerIqBoard.customerIqBoard.content.eq(keyword));
      condition = condition.or(QCustomerIqBoard.customerIqBoard.title.eq(keyword));
    }

    // 쿼리 작성 및 페이징 적용
    List<CustomerIqBoard> result = queryFactory
        .selectFrom(QCustomerIqBoard.customerIqBoard)
        .where(condition)
        .orderBy(
//            QCustomerIqBoard.customerIqBoard.category.eq("공지").desc(),
            QCustomerIqBoard.customerIqBoard.bno.desc()
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    long total = Optional.ofNullable(queryFactory
        .select(QCustomerIqBoard.customerIqBoard.count())
        .from(QCustomerIqBoard.customerIqBoard)
        .where(condition)
        .fetchOne())
        .orElse(0L);

    return new PageImpl<>(result, pageable, total);

  }


  /** duropeb, pageQna() - 고객문의에서 검색어가 없고 미등록 답변 보기 시 사용되는 페이지네이션 */
  @Override
  public Page<CustomerIqBoard> pageQna(String category, String keyword, Pageable pageable) {
    // 쿼리 작성 및 페이지 적용
    List<CustomerIqBoard> result = queryFactory
        .selectFrom(QCustomerIqBoard.customerIqBoard)
        .where(
            customerIqBoard.category.eq(category),
            customerIqBoard.comment.isNull()
        )
        .orderBy(customerIqBoard.bno.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();


    // 필터링된 전체 데이터 개수 조회
    Long total = queryFactory
        .select(customerIqBoard.count())
        .from(customerIqBoard)
        .where(
            customerIqBoard.category.eq(category),
            customerIqBoard.comment.isNull()
        )
        .fetchOne();

    return new PageImpl<>(result, pageable, (total == null) ? 0 : total);
  }


  /* duorpeb, pageQnaKw() - 고객문의에서 검색어만 있고 미등록 답변 보기는 클릭하지 않는 경우의 페이지네이션 */
  @Override
  public Page<CustomerIqBoard> pageQnaKw(String category, String keyword, Pageable pageable) {
    // 검색
    BooleanExpression predicate = anyOfNotNull(
        eqLong(customerIqBoard.bno, keyword),
        containsIgnoreCase(customerIqBoard.title, keyword),
        containsIgnoreCase(customerIqBoard.content, keyword),
        containsIgnoreCase(customerIqBoard.userEmail, keyword)
    );

    // 쿼리 작성 및 페이지 적용
    List<CustomerIqBoard> result = queryFactory
        .selectFrom(customerIqBoard)
        .where(
            customerIqBoard.category.eq(category),
            predicate
        )
        .orderBy(customerIqBoard.bno.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();


    // 필터링된 전체 데이터 개수 조회
    Long total = queryFactory
        .select(customerIqBoard.count())
        .from(customerIqBoard)
        .where(
            customerIqBoard.category.eq(category),
            predicate
        )
        .fetchOne();

    return new PageImpl<>(result, pageable, (total == null) ? 0 : total);
  }


  /* duorpeb, pageQnaKwAns() - 고객문의에서 검색어가 있고 미등록 답변 보기 시 사용되는 페이지네이션 */
  public Page<CustomerIqBoard> pageQnaKwAns(String category, String keyword, Pageable pageable){
    // 검색
    BooleanExpression predicate = anyOfNotNull(
      eqLong(customerIqBoard.bno, keyword),
      containsIgnoreCase(customerIqBoard.title, keyword),
      containsIgnoreCase(customerIqBoard.content, keyword),
      containsIgnoreCase(customerIqBoard.userEmail, keyword)
    );

    // 쿼리 작성 및 페이지 적용
    List<CustomerIqBoard> result = queryFactory
        .selectFrom(customerIqBoard)
        .where(
            customerIqBoard.category.eq(category),
            predicate
        )
        .orderBy(customerIqBoard.bno.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();


    // 필터링된 전체 데이터 개수 조회
    Long total = queryFactory
        .select(customerIqBoard.count())
        .from(customerIqBoard)
        .where(
            customerIqBoard.category.eq(category),
            predicate
        )
        .fetchOne();

    return new PageImpl<>(result, pageable, (total == null) ? 0 : total);
  }


  /** duorpeb, anyOfNotNull() - 여러 BooleanExpression 을 OR 로 묶되 null 인 쿼리는 건너뛰어 NPE 를 피하는 함수 */
  private BooleanExpression anyOfNotNull(BooleanExpression ...vargs){
    BooleanExpression cond = null;

    for(BooleanExpression b : vargs){
      if(b != null) { cond = (cond == null) ? b : cond.or(b); }
    }

    return cond;
  }


  /** duorpeb, eqLong() - 숫자로 파싱되면 eq 조건 아닌 경우 null 을 반환 */
  private  BooleanExpression eqLong(NumberPath<Long> path, String kw){
    if(kw == null){ return null; }

    try{ return path.eq(Long.parseLong(kw)); }

    catch(NumberFormatException e){ return null; }
  }

  /** lowerAsString
   *
   *  > "@Lob 으로 인해 containsIgnoreCase() 사용 시 발생하는 ERROR 를 해결 하기위한 헬퍼 함수 (cast)
   *
   *  */
  private StringExpression lowerAsString(StringPath path) {
    return Expressions.stringTemplate("lower(cast({0} as string))", path);
  }

  private BooleanExpression containsIgnoreCase(StringPath path, String kw) {
    if (kw == null || kw.isBlank()) return null;

    return lowerAsString(path).like("%" + kw.toLowerCase(Locale.ROOT) + "%");
  }
}
