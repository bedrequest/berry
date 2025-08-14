package com.berry.project.repository.payment;

import com.berry.project.entity.payment.PaymentReceipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentReceiptCustomRepository {
  /* pagePaymentReceipt() - 결제 완료 내역 페이지 로드 시 필요한 페이지네이션 */
  Page<PaymentReceipt> pagePaymentReceipt(Pageable pageable, String keyword);
}
