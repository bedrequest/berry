package com.berry.project;

import com.berry.project.entity.payment.PaymentCancel;
import com.berry.project.repository.payment.PaymentCancelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.IntStream;

@SpringBootTest
public class PaymentCancelTest {
  @Autowired
  PaymentCancelRepository repo;

  @Test
  @Transactional
  @Commit
    // ← 실제 DB에 커밋(테스트 기본 롤백 방지)
  void seed_payment_cancel_for_paging_search_sort() {
    final Random rnd = new Random(42); // 재현 가능하도록 고정 시드
    final OffsetDateTime base = OffsetDateTime.now().minusDays(90).withOffsetSameInstant(ZoneOffset.UTC);

    final String[] reasons = {
        "단순 변심", "품절", "중복 결제", "결제 오류", "테스트 취소",
        "배송 지연", "환불 테스트 EN", "refund test", "CARD_FAIL", "ADDRESS_ERROR"
    };

    // 500건 생성 (원하면 수 늘려도 됨)
    List<PaymentCancel> bulk = new ArrayList<>(500);

    IntStream.rangeClosed(1, 500).forEach(i -> {
      // 일부는 같은 paymentKey가 반복되도록(검색/그룹 테스트에 도움)
      String paymentKey = "pay_" + String.format("%06d", (i % 120)); // 0~119 로테이션

      // transactionKey는 유니크하게(키워드 prefix 검색에도 쓰기 좋게)
      String transactionKey = "tx_" + String.format("%04d", i) + "_" +
          UUID.randomUUID().toString().replace("-", "").substring(0, 16);

      // 사유: 한글/영문 혼합(containsIgnoreCase 테스트용)
      String cancelReason = reasons[i % reasons.length];
      if (i % 25 == 0) cancelReason = null; // null 케이스도 소량 포함 (is null 테스트)

      // 금액: 1,000 ~ 500,000 (100원 단위)
      int cancelAmount = (rnd.nextInt(5000) + 10) * 100;

      // 취소 시각: 90일 윈도우 내 고르게 분산
      long minutes = (long) (i * 13L + rnd.nextInt(200));
      OffsetDateTime canceledAt = base.plusMinutes(minutes);

      // rawData: JSON 형태 문자열(검색 테스트에 키워드 삽입)
      String rawData = """
                    {"ok":true,"reason":"%s","amount":%d,"paymentKey":"%s","tag":"seed-%d"}
                    """.formatted(Objects.toString(cancelReason, "NONE"), cancelAmount, paymentKey, i);

      PaymentCancel pc = PaymentCancel.builder()
          .paymentKey(paymentKey)
          .transactionKey(transactionKey)
          .cancelReason(cancelReason)
          .cancelAmount(cancelAmount)
          .canceledAt(canceledAt)
          .rawData(rawData)
          .build();

      bulk.add(pc);
    });

    repo.saveAll(bulk);
    // 필요 시 즉시 검증용 flush
    // repo.flush();
  }
}
