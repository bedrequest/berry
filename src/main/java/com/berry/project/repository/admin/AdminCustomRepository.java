package com.berry.project.repository.admin;

import com.berry.project.entity.cupon.Cupon;
import com.berry.project.entity.cupon.CuponTemplate;
import com.berry.project.entity.lodge.Lodge;
import com.berry.project.entity.payment.PaymentBeforePayment;
import com.berry.project.entity.payment.PaymentCancel;
import com.berry.project.entity.payment.PaymentReceipt;
import com.berry.project.entity.payment.Reservation;
import com.berry.project.entity.qna.CustomerIqBoard;
import com.berry.project.entity.review.Review;
import com.berry.project.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminCustomRepository {
  Page<Lodge> pageLodge(String sortType, String keyword, Pageable pageable);

  Page<Reservation> pageReservation(String sortType, String keyword, Pageable pageable);

  Page<Review> pageReview(String separator, String sortType, String keyword, Pageable pageable);

  Page<PaymentCancel> pagePaymentCanceled(String sortType, String keyword, Pageable pageable);

  Page<PaymentBeforePayment> pagePaymentPBP(String sortType, String keyword, Pageable pageable);

  Page<PaymentReceipt> pagePaymentReceipt(String sortType, String keyword, Pageable pageable);

  Page<CustomerIqBoard> pageQna(String separator, String sortType, String keyword, Pageable pageable);

  Page<User> pageUser(String sortType, String keyword, Pageable pageable);

  Page<Cupon> pageCuponType(String sortType, String keyword, Pageable pageable);

  Page<CuponTemplate> pageCuponTemplate(String sortType, String keyword, Pageable pageable);
}
