package com.berry.project.entity.payment;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Table
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PaymentBeforePayment {
  // 초기화
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_id")
  private long paymentId;

  // user Entity 의 customer_key
  @Column(name = "customer_key", nullable = false)
  private String customerKey;

  // 주문 시 고유 ID
  @Column(name = "order_id", nullable = false, length = 200)
  private String orderId;

  // 쿠폰 사용 시 적용된 쿠폰 ID
  @Column(name = "cupon_id", length = 200)
  private Long cuponId;

  @Column(length = 50)
  private String method;

  // 쿠폰 사용 시 쿠폰이 적용된 가격
  @Column(name = "cupon_price")
  private Long cuponPrice;

  // 상품 (숙소) 원래의 가격
  @Column(name = "strike_price", nullable = false)
  private long strikePrice;

  // 총 결제 가격
  @Column(name = "pbp_total_amount", nullable = false)
  private long pbpTotalAmount;

  // 숙소 이름
  @Column(name = "order_name")
  private String orderName;

  // 결제 요청 시각
  @CreationTimestamp
  @Column(name = "order_reg_date", columnDefinition = "TIMESTAMP")
  private OffsetDateTime orderRegDate;
}
