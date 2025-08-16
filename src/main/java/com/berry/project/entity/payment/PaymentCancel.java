package com.berry.project.entity.payment;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Table(name = "payment_cancel")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentCancel {
  @Id
  @Column(name = "payment_cancel_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long paymentCancelId;

  // paymentReceipt 의 paymentKey 를 의미
  @Column(name = "paymentkey")
  private String paymentKey;

  // 취소 건의 키 값
  @Column(name = "transaction_key", length = 128)
  private String transactionKey;

  // 결제 취소 사유
  @Column(name = "cancel_reason")
  private String cancelReason;

  // 결제 취소 금액
  @Column(name = "cancel_amount")
  private int cancelAmount;

  // 결제 취소 시각
  @Column(name = "canceled_at", columnDefinition = "TIMESTAMP")
  private OffsetDateTime canceledAt;

  // 원본 데이터
  @Lob
  @Column(name = "raw_data", columnDefinition = "TEXT")
  private String rawData;
}
