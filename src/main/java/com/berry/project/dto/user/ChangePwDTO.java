package com.berry.project.dto.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangePwDTO {
  private Long userId;
  private String currentPassword;
  private String changePassword;
}
