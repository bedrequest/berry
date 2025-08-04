package com.berry.project.entity.alarm;

import com.berry.project.entity.TimeBase;
import jakarta.persistence.Entity;
import lombok.*;

//@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alarm extends TimeBase {

  private Long alarmId;
  private Long userId;
  private Long targetId;
  private String code;

}
