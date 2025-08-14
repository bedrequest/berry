package com.berry.project.repository.payment;

import com.berry.project.entity.payment.PaymentCancel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentCancelCustomRepository {
  /* pagePaymentCanceled() - 환불 내역 클릭 시 로드되는 페이지의 페이지네이션을 위한 메서드 */
  Page<PaymentCancel> pagePaymentCanceled(Pageable pageable, String keyword);
}
