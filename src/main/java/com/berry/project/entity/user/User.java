package com.berry.project.entity.user;

import com.berry.project.entity.TimeBase;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends TimeBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId; // auto_increments 번호

  @Column
  private String password;

  @Column(nullable = false, name = "user_email")
  private String userEmail; // 실제 사용자 email

  @Column(nullable = false, name = "user_name")
  private String userName; // 사용자 이름

  @Column(nullable = false, name = "user_phone")
  private String userPhone;

  @Column(nullable = false, name = "user_uid")
  private String userUid; // provider 별로 제공되는 고유번호

  @Column(nullable = false)
  private String provider; // 회원 가입 유형 (e.g. kakao, naver, google, web)

  @Column(nullable = false)
  private String birthday; // 생년월일 yyyymmdd 의 형태 (주민번호 앞자리와 동일)

  @Column(nullable = false, columnDefinition = "Boolean default false", name = "is_mobile_certified")
  private boolean isMobileCertified; // 모바일 인증 여부

  @Column(nullable = false, columnDefinition = "Boolean default false", name = "user_term_option")
  private boolean userTermOption; // 마켓팅 수신 동의

  @Column(nullable = false, columnDefinition = "Boolean default false", name = "is_adult")
  private boolean isAdult; // 성인 여부

  @Column(nullable = false, columnDefinition = "Boolean default false", name = "is_email_certified")
  private boolean isEmailCertified; // 사용자 이메일 인증

  @Column(nullable = false, name = "customer_key")
  private String customerKey; // 결제시 사용되는 사용자 key

  @Column(nullable = false, columnDefinition = "int default 0", name = "user_favorite_tag")
  private int userFavoriteTag; // 마이페이지 에서 적용 되는 선호 태그

  @Column(nullable = false, columnDefinition = "varchar(255) default 'silver'", name = "user_grade")
  private String userGrade; // 유저 등급

  @Column(name = "last_login")
  private LocalDateTime lastLogin; // 마지막 로그인 일시


  /**
   * duorpeb, int - userGradePoint : 등급 업 관련 변수
   * <p>
   * > 결제 시 마다 userGradePoint += 결제금액의 2%
   * <p>
   * > userGardPoint 가 0 이상이면 브론즈 50 이상이면 실버, 100 이상이면 골드 , 200 이상이면 플레티넘
   *
   */
  @Column(name = "user_grade_point", columnDefinition = "int default 0")
  private int userGradePoint;
}
