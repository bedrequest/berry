package com.berry.project.dto.cupon;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CuponDTO {
  private long cuponId;
  private long userId;
  private int cuponType;
  private LocalDateTime cuponRegDate;
  private LocalDateTime cuponEndDate;
  private boolean isValid;
}
