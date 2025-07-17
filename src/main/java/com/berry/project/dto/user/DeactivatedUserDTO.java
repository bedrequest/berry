package com.berry.project.dto.user;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeactivatedUserDTO {

  private Long dUserId;
  private Long userId;
  private String dUserEmail;
  private String dUserName;
  private String dUserPhone;
  private String dReason;
  private LocalDateTime regDate, modDate;

}
