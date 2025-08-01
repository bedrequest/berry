package com.berry.project.service.payment;

import com.berry.project.dto.payment.MergePayloadDTO;
import com.berry.project.dto.payment.PBPDTO;
import com.berry.project.dto.payment.PaymentReceiptDTO;
import com.berry.project.dto.payment.ReturnCancelsDTO;
import com.berry.project.entity.payment.PaymentBeforePayment;
import com.berry.project.entity.payment.PaymentReceipt;
import com.berry.project.entity.payment.Reservation;
import com.berry.project.repository.lodge.LodgeRepository;
import com.berry.project.repository.payment.PBPRepository;
import com.berry.project.repository.payment.PaymentCancelRepository;
import com.berry.project.repository.payment.PaymentReceiptRepository;
import com.berry.project.repository.payment.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
  // 초기화
   // payment_before_payment TABLE
  private final PBPRepository pbpRepository;
   // Reservation TABLE
  private final ReservationRepository reservationRepository;
   // PaymentCancel TABLE
  private final PaymentCancelRepository paymentCancelRepository;
   // PaymentReceipt TABLE
  private final PaymentReceiptRepository paymentReceiptRepository;
   // 해찬 LodgeRepository
  private final LodgeRepository lodgeRepository;


  
  /** insertMergePayload(MergePayloadDTO mpdto) 
   * 
   *  > JS 에서 받은 객체를 두 TABLE (PBP TABLE, reservation TABLE) 에 저장
   *
   * */
  @Transactional
  @Override
  public boolean insertMergePayload(MergePayloadDTO mpdto) {
    // 초기화
    boolean isTrue = false;

    /** 결제 전 구매 정보 저장 - PBPDTO 를 PBP Entity 로 변환하여 INSERT */
    Long isSavePbp = pbpRepository.save(extractPBPDtoAndConvertPBPEntity(mpdto)).getPaymentId();

    /** 예약 정보 저장 - ReservationDTO 를 Reservation Entity 로 변환하여 INSERT */
    Long isSaveReserve = reservationRepository.save(extractRdtoAndConvertEntity(mpdto)).getReservationId();

    if(isSavePbp > 0L && isSaveReserve > 0L){ isTrue = true; }

    return isTrue;
  }


  /** getPbp() - 결제하기 버튼 클릭 시 결제 정보를 가져오는 메서드 */
  @Override
  public PBPDTO getPbp(String orderId) {
    Optional<PaymentBeforePayment> optionalPbp = pbpRepository.findByOrderId(orderId);

    if(optionalPbp.isPresent()){
      PBPDTO pdto = convertPBPEntityToPBPDto(optionalPbp.get());

      return pdto;
    }

    return null;
  }


  /** getAmountFromOrderId() - successUrl 로 redirect 시 amount 를 가져오는 메서드 */
  @Override
  public long getAmountFromOrderId(String orderId) {
    Optional<PaymentBeforePayment> optionalPbp = pbpRepository.findByOrderId(orderId);

    if(optionalPbp.isPresent()){
      PBPDTO pdto = convertPBPEntityToPBPDto(optionalPbp.get());

      return pdto.getPbpTotalAmount();
    }

    return -1;
  }


  /** registerPaymentReceipt(PaymentReceiptDTO prdto) - PaymentReceipt TABLE 에 Record INSERT */
  @Override
  @Transactional
  public void insertPaymentReceipt(PaymentReceiptDTO prdto, String orderId) {
    // 확인
    log.info("regiseter-convert: {}", convertPaymentReceiptDtoToPaymentReceiptEntity(prdto));
    log.info("regiseter-prdto : {}", prdto);

    // 반환받은 payment 객체를 DB 에 저장
    paymentReceiptRepository.save(convertPaymentReceiptDtoToPaymentReceiptEntity(prdto));
    
    // 해당 orderId 와 일치하는 Reservation TABLE 의 bookingStatus 를 DONE 으로 변경
    Reservation res
        = reservationRepository.findByOrderId(orderId)
                               .orElseThrow(() -> new EntityNotFoundException("Can't found this Entity..!"));
    
    res.setBookingStatus("DONE");
  }


  /** String getPaymnetKey(String orderId) - paymentKey 가져오기 */
  @Override
  public String getPaymnetKey(String orderId) {
    PaymentReceipt paymentReceipt
        = paymentReceiptRepository.findByOrderId(orderId)
                                  .orElseThrow(() -> new EntityNotFoundException("Can't found this Entity..!"));


    return paymentReceipt.getPaymentKey();
  }

  @Override
  @Transactional
  public void insertPaymentCancel(String paymentKey, ReturnCancelsDTO rcdto, String orderId) {
    // paymentKey 초기화
    rcdto.getCancels().get(0).setPaymentKey(paymentKey);

    // payment_cancel TABLE 에 Record 저장
    paymentCancelRepository.save(convertPaymentCancelDtoToPaymentCancelEntity(rcdto.getCancels().get(0)));

    // 예약 정보를 결제 취소로 변경
    Reservation reservation = reservationRepository.findByOrderId(orderId).orElseThrow(
        () -> new EntityNotFoundException("Can't found this Entity..!"));

    reservation.setBookingStatus("CANCELED");
  }

}
