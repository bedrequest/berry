package com.berry.project.dto.cupon;

import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JsCuponTemplateDTO {
  // 쿠폰 타입
  int cuponType;
  // 쿠폰 이름
  String cuponTitle;
  // 금액
  long cuponPrice;
  // 최소 결제 금액
  Long theMinimumAmount;
  // 쿠폰 이미지 경로
  String cuponImgName;
  // 유효 기간
  LocalDateTime cuponEndDate;
  // 수량
  Long qty;
}