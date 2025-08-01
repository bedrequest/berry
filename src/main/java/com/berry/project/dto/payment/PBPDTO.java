package com.berry.project.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
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
  private long cuponId;
  private String method;
  private Long cuponPrice;
  private long strikePrice;
  private long pbpTotalAmount;
  private String orderName;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime orderRegDate;
}
