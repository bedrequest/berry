package com.berry.project.entity.payment;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Table(name = "reservation")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reservation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "reservation_id")
  private long reservationId;

  // 객실 PK
  @Column(name = "room_id", nullable = false)
  private long roomId;

  // 유저 PK
  @Column(name = "user_id", nullable = false)
  private long userId;

  // 주문 번호 (Unique)
  @Column(name = "order_id", nullable = false, length = 200)
  private String orderId;

  // 이용 시작일
  @Column(name = "start_date", nullable = false, columnDefinition = "TIMESTAMP")
  private OffsetDateTime startDate;

  // 이용 종료일
  @Column(name = "end_date", nullable = false, columnDefinition = "TIMESTAMP")
  private OffsetDateTime endDate;

  // 결제 금액
  @Column(name = "total_amount", nullable = false)
  private int totalAmount;

  // 인원 수
  @Column(name = "guests_amount", nullable = false)
  private int guestsAmount;

  // 예약 상태, default = 'PENDING'
  @Column(name = "booking_status",
      columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
  private String bookingStatus;

  // 예약 타입 - 숙박 (stay) 인지 대실 (rent) 인지
  @Column(name = "reservation_type", length = 10)
  private String reservationType;

  // 예약 시각
  @CreationTimestamp
  @Column(name = "reservation_reg_date", columnDefinition = "TIMESTAMP")
  private OffsetDateTime reservationRegDate;
}
