package com.berry.project.dto.payment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PBPDTO {
  private Long paymentId;
  private String customerKey;
  private String orderId;
  private long cuponId;
  private String method;
  private Long cuponPrice;
  private long strikePrice;
  private long pbpTotalAmount;
  private String orderName;
  private LocalDateTime orderRegDate;
}
