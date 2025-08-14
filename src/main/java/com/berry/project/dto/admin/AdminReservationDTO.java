package com.berry.project.dto.admin;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminReservationDTO {
  private Long reservationId;
  private Long roomId;
  private Long userId;
  private String orderId;
  private OffsetDateTime startDate;
  private OffsetDateTime endDate;
  private int totalAmount;
  private int guestsAmount;
  private String reservationType;
  private OffsetDateTime reservationRegDate;
  private String userName;
  private String roomName;


  /* getStartDateTimeKST()- HTML 에서 한국 기준 시각으로 출력하기 위한 메서드 */
  public String getStartDateTimeKST(){
    if(this.startDate == null) { return null; }

    // 로컬존 (한국 시간) 으로 설정
    ZoneId kstZone = ZoneId.of("Asia/Seoul");

    // KST 로 변환 후 포맷팅
    return this.startDate
        .atZoneSameInstant(kstZone)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }


  /* getStartDateTimeKST()- HTML 에서 한국 기준 시각으로 출력하기 위한 메서드 */
  public String getEndDateTimeKST(){
    if(this.endDate == null) { return null; }

    // 로컬존 (한국 시간) 으로 설정
    ZoneId kstZone = ZoneId.of("Asia/Seoul");

    // KST 로 변환 후 포맷팅
    return this.endDate
        .atZoneSameInstant(kstZone)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }


  /* getReservationRegDateTimeKST()- HTML 에서 한국 기준 시각으로 출력하기 위한 메서드 */
  public String getReservationRegDateTimeKST(){
    if(this.reservationRegDate == null) { return null; }

    // 로컬존 (한국 시간) 으로 설정
    ZoneId kstZone = ZoneId.of("Asia/Seoul");

    // KST 로 변환 후 포맷팅
    return this.reservationRegDate
        .atZoneSameInstant(kstZone)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }
}