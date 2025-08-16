package com.berry.project.entity.alarm;

import com.berry.project.entity.TimeBase;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alarm extends TimeBase {

  @Id
  @Column(name = "alarm_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long alarmId;
  @Column(name = "user_id", nullable = false)
  private Long userId;
  @Column(name = "target_id", nullable = false)
  private Long targetId;
  @Column(nullable = false)
  private String code;

}
