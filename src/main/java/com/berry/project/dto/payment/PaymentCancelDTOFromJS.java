package com.berry.project.dto.payment;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentCancelDTOFromJS {
  private String orderId;
  private String cancelReason;
}
