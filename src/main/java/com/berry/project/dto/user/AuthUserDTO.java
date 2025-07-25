package com.berry.project.dto.user;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthUserDTO {
  private Long authId;
  private Long userId;
  private String authRole;
}
