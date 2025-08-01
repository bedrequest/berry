package com.berry.project.dto.payment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDTO {
  private long reservationId;
  private long roomId;
  private long userId;
  private String orderId;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private int totalAmount;
  private int guestsAmount;
  private String bookingStatus;
  private String reservationType;
  private LocalDateTime reservationRegDate;


}
