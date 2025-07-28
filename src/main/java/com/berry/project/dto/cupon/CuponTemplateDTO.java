package com.berry.project.dto.cupon;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CuponTemplateDTO {
  // PK
  long ctId;
  // 쿠폰 타입
  int cuponType;
  // 쿠폰 이름
  String cuponTitle;
  // 금액
  long cuponPrice;
  // 최소 주문 금액
  Long theMinimumAmount;
  // 쿠폰 이미지 경로
  String cuponImgName;
  // 유효 기간
  OffsetDateTime cuponEndDate;
  // 수량
  Long qty;

  public String getCuponPriceFormatted() {
    return String.format("%,d", cuponPrice);
  }
}
