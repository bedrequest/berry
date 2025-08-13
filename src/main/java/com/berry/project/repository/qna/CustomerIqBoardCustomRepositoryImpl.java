package com.berry.project.repository.qna;

import com.berry.project.entity.qna.CustomerIqBoard;
import com.berry.project.entity.qna.QCustomerIqBoard;
import com.querydsl.core.types.dsl.BooleanExpression;
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
////    condition = condition.and(QCustomerIqBoard.customerIqBoard.category.ne("공지"));

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
}
