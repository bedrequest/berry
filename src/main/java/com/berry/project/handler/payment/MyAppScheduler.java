package com.berry.project.handler.payment;

import com.berry.project.repository.payment.CuponRepository;
import com.berry.project.repository.payment.PBPRepository;
import com.berry.project.repository.payment.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class MyAppScheduler {
  // 초기화
  // Cupon
  private final CuponRepository cuponRepository;
  // PaymentBeforePayment
  private final PBPRepository pbpRepository;
  // Reservation
  private final ReservationRepository reservationRepository;


  /**
   * midnightScheduler() - 매 자정 아래의 작업을 수행
   * <p>
   * > 1) Cupon Entity 의 cuponEndDate 가 LocalDateTime.now() 이전인 Record 를 삭제
   * <p>
   * 2) PaymentBeforePayment Entity 의 orderRegDate 가 OffsetDateTime.now() 으로부터
   * 1시간 이전의 Record 를 삭제
   * <p>
   * 3) Reservation Entity 의 reservationRegDate 가 OffsetDateTime.now() 이전이고
   * bookingStatus 가 PENDING 인 Record 를 삭제
   *
   *
   */
  @Scheduled(cron = "0 0 0 * * *")
  @Transactional
  public void midnightScheduler() {
    // 초기화
    OffsetDateTime currentDate = OffsetDateTime.now();

    // 1번 작업 수행
    cuponRepository.deleteByCuponEndDateBefore(currentDate);

    // 2번 작업 수행
    OffsetDateTime oneHourAgo = currentDate.minusHours(1);
    pbpRepository.deleteByOrderRegDateBefore(oneHourAgo);

    // 3번 작업 수행
    reservationRepository.deleteByBookingStatusAndReservationRegDateBefore("PENDING", currentDate);


  }
}
