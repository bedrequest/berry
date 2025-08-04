package com.berry.project.dto.alarm;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmDTO {

  private Long alarmId;
  private Long userId;
  private Long targetId;
  private String code; // 구분용 code ex) 예약은 reservation, 리뷰 review
  private LocalDateTime regDate;
  private LocalDateTime modDate; // 사실상 사용 안할 예정

}
