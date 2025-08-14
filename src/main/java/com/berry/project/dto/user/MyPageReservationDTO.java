package com.berry.project.dto.user;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@Builder
@ToString
public class MyPageReservationDTO {
  private long reservationId;
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
  private Long lodgeId;
  private String lodgeName; // 숙박업소명
  private String lodgeAddr; // 숙박업소 주소
  private String lodgeType; // 호텔, 모텔, 리조트 등등..
  private String businessCall; // 숙박업소 연락처
  // room
  private Long roomId;
  private String roomName; // 객실명
  private String rentTime; // 대실 이용시간
  private String stayTime; // 숙박 이용시간
  // lodgeImg
  private List<String> lodgeImageUrls;

  public String customOrderId() {
    String customOrderId = orderId.replace("order_", "");

    return customOrderId.substring(0, customOrderId.lastIndexOf("_"));
  }

  // <td>yyyy.mm.dd - yyyy.mm.dd</td> 체크인 체크아웃
  public String customCheckDate() {
    LocalDateTime startDateUTF = this.startDate.plusHours(9);
    LocalDateTime endDateUTF = this.endDate.plusHours(9);
    String sDate = startDateUTF.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
    String eDate = endDateUTF.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));

    return sDate + " - " + eDate;
  }

  // <td>yyyy.mm.dd hh:mm</td> 예약일시
  public String customReservationRegDate() {
    return reservationRegDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm:ss"));
  }


}
