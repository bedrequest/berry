package com.berry.project.dto.payment;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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
