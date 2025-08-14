package com.berry.project.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentReceiptDTO {
  // payment 객체의 paymentKey
  private String paymentKey;

  // payment 객체의 orderId
  private String orderId;

  // payment 객체의 type
  private String type;

  // payment 객체의 ordername
  private String orderName;

  // payment 객체의 status
  private String status;

  // payment 객체의 totalAmount
  private long totalAmount;

  // payment 객체의 method
  private String method;

  // payment 객체의 requestedAt 는 "yyyy-MM-dd'T'HH:mm:ssXXX" 형식으로 반환
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime requestedAt;

  // payment 객체의 approvedAt
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime approvedAt;

  // payment 객체의 lastTransactionKey
  private String lastTransactionKey;

  // payment 객체의 원본을 문자열로 저장
  private String rawData;


  /** getReqWithKSTFormatted() - HTMl 에서 UTC 기준 시각으로 출력되는 것을 해결하기위한 메서드 */
  public String getReqWithKSTFormatted(){
    if(this.requestedAt == null) { return null; }

    // 로컬존 (한국 시간) 으로 설정
    ZoneId kstZone = ZoneId.of("Asia/Seoul");

    // KST 로 변환후 포맷팅
    return this.requestedAt
        .atZoneSameInstant(kstZone)
        .format(DateTimeFormatter.ofPattern(("yyyy-MM-dd HH:mm:ss")));

  }


  /** getAppWithKSTFormatted() - HTMl 에서 UTC 기준 시각으로 출력되는 것을 해결하기위한 메서드 */
  public String getAppWithKSTFormatted(){
    if(this.approvedAt == null) { return null; }

    // 로컬존 (한국 시간) 으로 설정
    ZoneId kstZone = ZoneId.of("Asia/Seoul");

    // KST 로 변환후 포맷팅
    return this.requestedAt
        .atZoneSameInstant(kstZone)
        .format(DateTimeFormatter.ofPattern(("yyyy-MM-dd HH:mm:ss")));

  }
}
