package com.berry.project.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.*;

import java.time.OffsetDateTime;

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

  @Column(name = "cupon_id", nullable = false)
  private Long cuponId;

  private String method;
  private Long cuponPrice;
  private long strikePrice;
  private long pbpTotalAmount;
  private String orderName;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
  private OffsetDateTime orderRegDate;
}
