package com.berry.project.handler.payment;

import com.berry.project.api.TossApi;
import com.berry.project.dto.payment.PaymentCancelDTOFromJS;
import com.berry.project.dto.payment.PaymentReceiptDTO;
import com.berry.project.dto.payment.ReturnCancelsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentsAPIHandler {
  // 초기화
  private final TossApi tossApi;
  private final ObjectMapper objectMapper;


  /**
   * callConfirmAPI() - 결제 승인 API (/vi/payments/confirm)호출
   */
  public PaymentReceiptDTO callConfirmAPI(String orderId, String paymentKey, long amount)
      throws IOException, InterruptedException {
    // 확인
    log.info("in callConfirmAPI");

    // 초기화
    // import java.net.http.HttpClient;
    HttpClient httpClient = HttpClient.newHttpClient();
    // Secret Key
    String secretKey = tossApi.getTossSecretApiKey();
    // 확인,
    log.info("Secret Key : {}", secretKey);
    // URL
    final String CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";

    // 헤더 생성
    String authHeader = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

    // 요청 객체 직렬화
    String jsonBody = objectMapper.writeValueAsString(Map.of(
        // 만료된 키 테스트, "paymentKey", "test_payment_key_expired",
        "paymentKey", paymentKey,
        "orderId", orderId,
        "amount", amount
    ));

    // 요청 생성 - import java.net.http.HttpRequest;
    HttpRequest req
        = HttpRequest.newBuilder()
        .uri(URI.create(CONFIRM_URL))
        .header("Authorization", "Basic " + authHeader)
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
        .build();


    /** 동기 호출 및 응답 파싱 - Tosspayments 가 반환한 payment 객체를 직렬화 (JSON 문자열로 return)
     *
     * > 해당 코드는 요청을 보내고, 응답 본문을 문자열(String) 형태로 받아오는 동기 호출로
     *   만약, Java 객체로 역직렬화 하고 싶다면 Jackson 라이브러리 등을 사용하여 직접 파싱
     *
     * > 메서드 호출 스레드는 응답이 올 때까지 Blocking 되며 예외 (IOException, InterruptedException 등 이)
     *   발생 시 호출부로 전파
     *
     * > req : HttpRequest 객체 (URI, HTTP 메서드, 헤더, 바디 등 설정 완료)
     *
     * > BodyHandlers.ofString() : 응답 바디를 문자열(String) 로 읽어들이는 핸들러
     *
     * > 반환 타입은 HttpResponse"<"String>으로 resp 의 메소드로는 .statusCode(), .headers(), .body() 가 있음
     *
     * > resp.statusCode() 는 HTTP 상태 코드, resp.headers() 는 응답 헤더 정보, resp.body() 는
     *   String 형태의 응답 본문을 호출
     *
     * > 해당 방식은 간단하게 응답 전체를 텍스트로 받아 바로 처리 가능한 장점을 가지고 있지만
     *   대용량 바디나 바이너리 처리에는 부적합(메모리 부담) 한 형태로 만약, 비동기 처리가 필요하다면
     *   sendAsync() 를 사용
     *
     */
    HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

    /** Exception 처리 */
    if (resp.statusCode() != 200) {
      throw new IllegalStateException("결제 확인 실패 :" + resp.body());
    }

    String rawData = resp.body();

    // PaymentReceipDTO 로 Parsing
    PaymentReceiptDTO prdto = objectMapper.readValue(resp.body(), PaymentReceiptDTO.class);
    // rawData 저장
    prdto.setRawData(rawData);

    // 확인
    log.info("Confirm API 의 prdto : {}", prdto);

    return prdto;
  }


  /**
   * callCancelAPI(String paymentKey, String cancelReason) - 결제 취소 API
   */
  public ReturnCancelsDTO callCancelAPI(String paymentKey, PaymentCancelDTOFromJS pcdtoFromJs, long cancelAmount) throws IOException, InterruptedException {
    // 확인
    log.info("in callCancelAPI");
    log.info("cancelAmount : {}", cancelAmount);
    log.info("pcdtoFromJs : {}", pcdtoFromJs);

    // 초기화
    HttpClient httpClient = HttpClient.newHttpClient();
    // Secret Key
    String secretKey = tossApi.getTossSecretApiKey();
    log.info("callCancelAPI 의 secretkey : {}", secretKey);
    // URL
    String CANCEL_URL = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

    // 헤더 생성
    String authHeader = Base64.getEncoder().encodeToString((secretKey + ": ").getBytes(StandardCharsets.UTF_8));

    // 요청 객체 직렬화
    String jsonBody = objectMapper.writeValueAsString(Map.of(
        "cancelReason", pcdtoFromJs.getCancelReason(),
        "cancelAmount", cancelAmount
    ));

    // 요청 생성
    HttpRequest req
        = HttpRequest.newBuilder()
        .uri(URI.create(CANCEL_URL))
        .header("Authorization", "Basic " + authHeader)
        .header("Content-Type", "application/json")
        // 멱등성 추가 - 같은 요청이 2번 일어나도 첫 번째 요청 응답과 같은 응답을 보내줌
        .header("Idempotency-Key", pcdtoFromJs.getOrderId())
        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
        .build();

    /** 동기 호출 및 응답 파싱 - 위의 설명 참고 */
    HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

    /** Exception 처리 */
    if (resp.statusCode() != 200) {
      throw new IllegalStateException("200 을 반환하지 않는 오류 발생" + resp.body());
    }

    // Parsing
    ReturnCancelsDTO rcdto = objectMapper.readValue(resp.body(), ReturnCancelsDTO.class);

    // 반환받은 payment 객체를 JSON 문자열로 직렬화하여 초기화
    String rawData = resp.body();
    // 저장
    rcdto.getCancels().get(0).setRawData(rawData);

    return rcdto;
  }
}
