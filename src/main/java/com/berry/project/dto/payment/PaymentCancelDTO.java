package com.berry.project.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentCancelDTO {
  private long paymentCancelId;

  private String paymentKey;

  private String transactionKey;

  private String cancelReason;

  private int cancelAmount;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime canceledAt;

  private String rawData;


  /* getCanceledAtKST() - HTML 에서 한국 기준 시각으로 출력하기 위한 메서드 */
  public String getCanceledAtKST(){
    if(this.canceledAt == null) { return null; }

    // 로컬존 (한국 시간) 으로 설정
    ZoneId kstZone = ZoneId.of("Asia/Seoul");

    // KST 로 변환 후 포맷팅
    return this.canceledAt
        .atZoneSameInstant(kstZone)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }
}
