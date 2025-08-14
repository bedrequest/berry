package com.berry.project.dto.payment;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPayloadDTO {
  private Long userId;
  private long roomId;
  private int guestsAmount;
  private LocalDate startDate;
  private LocalDate endDate;
}
