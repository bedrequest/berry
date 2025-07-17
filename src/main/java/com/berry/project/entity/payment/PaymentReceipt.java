package com.berry.project.entity.payment;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name="payment_receipt")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentReceipt {
  @Id
  @Column(name="paymentkey", length = 200)
  private String paymentKey;

  // paymentBeforePayment 의 주문번호
  @Column(name="order_id", length = 200)
  private String orderId;

  // Tosspayments 의 type 으로 결제 타입 정보를 의미
  @Column(name="order_type", nullable = false, length = 50)
  private String orderType;

  // 상품 이름
  @Column(name="order_name", nullable = false, length = 100)
  private String orderName;

  // 결제 상태
  @Column(name="order_status", nullable = false, length = 128)
  private String orderStatus;

  // 결제 가격
  @Column(name="total_amount", nullable = false)
  private int totalAmount;

  // 결제 수단
  @Column(length = 100)
  private String method;

  // 결제 요청 시각
  @Column(name="order_requested")
  private LocalDateTime orderRequested;

  // 결제 승인 시각
  @Column(name="order_approved")
  private LocalDateTime orderApproved;

  // 마지막 거래의 키 값
  @Column(name="last_transcation_key", length = 128)
  private String lastTransactionKey;

  // 반환받은 payment 객체의 원본
  @Lob
  @Column(name="raw_data", columnDefinition = "TEXT")
  private String rawData;
}
