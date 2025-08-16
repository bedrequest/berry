package com.berry.project.entity.cupon;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/** 쿠폰 관리
 *
 *  > 유저에게 직접 발급 혹은 클릭 시 다운로드 가능하도록 설정
 *
 *  > CuponTemplate TABLE 에 있는 쿠폰이라면 유효한 쿠폰이고 없으면 해당 쿠폰이 유효하지 않음을 의미
 *
 *  > 쿠폰 발급 시 쿠폰 타입을 파라미터로 받아 쿠폰 발급
 *
 *    CuponTemplate cuponTemplate = cuponTemplateRepository.findById(cuponType);
 *
 *    Cupon cupon =
 *      cupon
 *      .build()
 *      .userId(userId)
 *      .cuponType(cuponTemplate.cuponType)
 *      .cuponEndDate(cuponTemplate.cuponEndDate)
 *      .isValid(true)
 *      .builder()
 *
 *  > 엔티티 내부 메서드 작성 시 순수 데이터 + 최소한의 도메인 로직만을 가져야 하며 서비스 계층과
 *    적절히 역할을 분담하는 구조를 유지
 *
 *  */
@Table(name="cupon_template")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CuponTemplate {
  // PK
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="ct_id")
  long ctId;
  // 쿠폰 타입
  @Column(name="cupon_type", nullable = false)
  int cuponType;
  // 쿠폰 이름
  @Column(name="cupon_title", nullable = false)
  String cuponTitle;
  // 금액
  @Column(name="cupon_price", nullable = false)
  long cuponPrice;
  // 쿠폰 사용을 위한 최소 결제 금액
  @Column(name="the_minimum_amount")
  Long theMinimumAmount;
  // 쿠폰 이미지 경로
  @Column(name="cupon_img_name", nullable = false)
  String cuponImgName;
  // 유효 기간
  @Column(name="cupon_end_date", nullable = true,  columnDefinition = "TIMESTAMP")
  OffsetDateTime cuponEndDate;
  // 수량
  @Column(nullable = true)
  Long qty;
}
