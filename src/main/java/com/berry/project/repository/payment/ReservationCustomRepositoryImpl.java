package com.berry.project.repository.payment;

import com.berry.project.dto.admin.AdminReservationDTO;
import com.berry.project.entity.lodge.QRoom;
import com.berry.project.entity.payment.QReservation;
import com.berry.project.entity.user.QUser;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.swing.text.html.parser.Entity;
import java.util.List;


public class ReservationCustomRepositoryImpl implements ReservationCustomRepository {

  private final JPAQueryFactory queryFactory;

  public ReservationCustomRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<AdminReservationDTO> findAdminReservations(Pageable pageable, String keyword) {
    QReservation r = QReservation.reservation;
    QUser u = QUser.user;
    QRoom rm = QRoom.room;

    // 동적 검색 조건
    BooleanExpression cond = (org.springframework.util.StringUtils.hasText(keyword))
        ? u.userName.containsIgnoreCase(keyword)
        .or(rm.roomName.containsIgnoreCase(keyword))
        : null;

    // 내용 조회
    List<AdminReservationDTO> content = queryFactory
        .select(Projections.constructor(AdminReservationDTO.class,
            r.reservationId,
            r.roomId,
            r.userId,
            r.orderId,
            r.startDate,
            r.endDate,
            r.totalAmount,
            r.guestsAmount,
            r.reservationType,
            r.reservationRegDate,
            u.userName,
            rm.roomName
        ))
        .from(r)
        .join(u).on(r.userId.eq(u.userId))
        .join(rm).on(r.roomId.eq(rm.roomId))
        .where(r.bookingStatus.eq("DONE"), cond)
        .orderBy(r.reservationRegDate.desc()) // 필요 시 pageable.getSort() 매핑
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 전체 개수 (조인/조건 동일하게)
    Long total = queryFactory
        .select(r.count())  // 필요 시 r.reservationId.countDistinct()
        .from(r)
        .join(u).on(r.userId.eq(u.userId))
        .join(rm).on(r.roomId.eq(rm.roomId))
        .where(r.bookingStatus.eq("DONE"), cond)
        .fetchOne();

    return new PageImpl<>(content, pageable, total == null ? 0L : total);
  }
}