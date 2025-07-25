package com.berry.project.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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

  // payment 객체의 requestedAt
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime requestedAt;

  // payment 객체의 approvedAt
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime approvedAt;

  // payment 객체의 lastTransactionKey
  private String lastTransactionKey;

  // payment 객체의 원본을 문자열로 저장
  private String rawData;
}
