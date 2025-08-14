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

  /**
   * duorpeb, int - userGradePoint : 등급 업 관련 변수
   * <p>
   * > 결제 시 마다 userGradePoint += 결제금액의 2%
   * <p>
   * > userGardPoint 가 0 이상이면 브론즈 50 이상이면 실버, 100 이상이면 골드 , 200 이상이면 플레티넘
   *
   */
  private int userGradePoint;

  public String convertRegDate() {
    return regDate.toString().substring(0, regDate.toString().indexOf("T"));
  }

}

