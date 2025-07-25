package com.berry.project.dto.payment;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MergePayloadDTO {
  private PBPDTO pbpPayload;
  private ReservationDTO reservePayload;
}
