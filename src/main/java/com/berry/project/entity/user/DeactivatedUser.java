package com.berry.project.entity.user;

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
public class DeactivatedUser extends TimeBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "d_user_id")
  private Long dUserId; // auto_increments 번호
  @Column(name = "user_id", nullable = false)
  private Long userId;
  @Column(name = "d_user_email", nullable = false)
  private String dUserEmail;
  @Column(name = "d_user_name", nullable = false)
  private String dUserName;
  @Column(name = "d_user_phone", nullable = false)
  private String dUserPhone;
  @Column(name = "d_reason")
  private String dReason;

}
