package com.berry.project.dto.user;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
  private Long userId;
  private String password;
  private String userEmail;
  private String userPhone;
  private String userName;
  private String userUid;
  private String provider;
  private String birthday;
  private String userGrade;
  private String customerKey;
  private boolean userTermOption;
  private boolean isAdult;
  private boolean isEmailCertified;
  private boolean isMobileCertified;
  private int userFavoriteTag;
  private LocalDateTime regDate, modDate, lastLogin;
  private List<AuthUserDTO> authList;

  public String convertRegDate(){
    String dateTime = regDate.toString().substring(0, regDate.toString().indexOf("T"));
    return dateTime;
  }

}

