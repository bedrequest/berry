package com.berry.project.dto.payment;

import com.berry.project.dto.cupon.CuponDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRentPageDTO {
  private String roomName;
  private String rentTime;
  private LocalDate startDate;
  private LocalDate endDate;
  private String userName;
  private String userPhone;
  private boolean isMobileCertified;
  private int guestsAmount;
  private long strikePrice;
  private List<CuponDTO> cuponList;
  private int cuponCnt;
  private boolean isBefore;
  private boolean isToday;
  private long userId;
  private long roomId;
}
