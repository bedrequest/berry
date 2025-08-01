package com.berry.project.dto.user;

import java.time.LocalDateTime;

public class MyPagePresentReservationDTO {

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
  // lodge
  private String lodgeName;
  // room
  private String roomImg;
  private String roomName;

}
