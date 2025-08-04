package com.berry.project.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentCancelDTO {
  private long paymentCancelId;

  private String paymentKey;

  private String transactionKey;

  private String cancelReason;

  private int cancelAmount;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime canceledAt;

  private String rawData;
}
