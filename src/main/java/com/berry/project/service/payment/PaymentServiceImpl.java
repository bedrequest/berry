package com.berry.project.service.payment;

import com.berry.project.dto.cupon.CuponDTO;
import com.berry.project.dto.payment.MergePayloadDTO;
import com.berry.project.dto.payment.PBPDTO;
import com.berry.project.dto.payment.PaymentReceiptDTO;
import com.berry.project.dto.payment.ReturnCancelsDTO;
import com.berry.project.entity.cupon.Cupon;
import com.berry.project.entity.lodge.Room;
import com.berry.project.entity.payment.PaymentBeforePayment;
import com.berry.project.entity.payment.PaymentReceipt;
import com.berry.project.entity.payment.Reservation;
import com.berry.project.entity.user.User;
import com.berry.project.repository.lodge.RoomRepository;
import com.berry.project.repository.payment.*;
import com.berry.project.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
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
   // Cupon TABLE
  private final CuponRepository cuponRepository;
   // Room TABLE
  private final RoomRepository roomRepository;
   // User TABLE
  private final UserRepository userRepository;

  
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

  /** 결제 페이지 이동 시 정보 - 객실 정보 가져오기  */
  @Override
  public Room getRoomInfo(long roomId) {
    Room room = roomRepository.findById(roomId).orElseThrow(() ->
        new EntityNotFoundException("Can't found this Entity..!"));

    return room;
  }


  /** 결제 페이지 이동 시 정보 - 유저 정보 가져오기 */
  @Override
  public User getUserInfo(long userId) {
    User user = userRepository.findById(userId).orElseThrow(() ->
        new EntityNotFoundException("Can't found this Entity..!"));

    return user;
  }


  /** 결제 페이지 이동 시 정보 - 쿠폰 정보 */
  @Override
  public List<CuponDTO> getCuponList(long userId) {
    OffsetDateTime currentTime = OffsetDateTime.now();

    List<Cupon> cuponList = cuponRepository.findValidCuponsByUserId(userId, currentTime);

    List<CuponDTO> cdtoList = cuponList.stream().map(this::convertCuponEntityToCuponDto).toList();

    return cdtoList;
  }

  /** 결제 페이지 이동 시 정보 - 쿠폰 개수 */
  @Override
  public int getCuponCnt(long userId) {
    OffsetDateTime currentTIme = OffsetDateTime.now();

    return cuponRepository.countValidCuponsByUserId(userId, currentTIme);
  }
}
