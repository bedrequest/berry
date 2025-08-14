package com.berry.project.service.payment;

import com.berry.project.dto.cupon.CuponDTO;
import com.berry.project.dto.payment.MergePayloadDTO;
import com.berry.project.dto.payment.PBPDTO;
import com.berry.project.dto.payment.PaymentReceiptDTO;
import com.berry.project.dto.payment.ReturnCancelsDTO;
import com.berry.project.entity.alarm.Alarm;
import com.berry.project.entity.cupon.Cupon;
import com.berry.project.entity.lodge.Room;
import com.berry.project.entity.payment.PaymentBeforePayment;
import com.berry.project.entity.payment.PaymentReceipt;
import com.berry.project.entity.payment.Reservation;
import com.berry.project.entity.user.User;
import com.berry.project.repository.lodge.RoomRepository;
import com.berry.project.repository.payment.*;
import com.berry.project.repository.user.AlarmRepository;
import com.berry.project.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

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
  
  // 해찬
  // Alarm TABLE
  private final AlarmRepository alarmRepository;


  /**
   * insertMergePayload(MergePayloadDTO mpdto)
   * <p>
   * > JS 에서 받은 객체를 두 TABLE (PBP TABLE, reservation TABLE) 에 저장
   *
   *
   */
  @Transactional
  @Override
  public boolean insertMergePayload(MergePayloadDTO mpdto) {
    // 초기화
    boolean isTrue = false;

    /** 결제 전 구매 정보 저장 - PBPDTO 를 PBP Entity 로 변환하여 INSERT */
    Long isSavePbp = pbpRepository.save(extractPBPDtoAndConvertPBPEntity(mpdto)).getPaymentId();

    /** 예약 정보 저장 - ReservationDTO 를 Reservation Entity 로 변환하여 INSERT */
    Long isSaveReserve = reservationRepository.save(extractRdtoAndConvertEntity(mpdto)).getReservationId();

    if (isSavePbp > 0L && isSaveReserve > 0L) {
      isTrue = true;
    }

    return isTrue;
  }


  /**
   * getPbp() - 결제하기 버튼 클릭 시 결제 정보를 가져오는 메서드
   */
  @Override
  public PBPDTO getPbp(String orderId) {
    Optional<PaymentBeforePayment> optionalPbp = pbpRepository.findByOrderId(orderId);

    if (optionalPbp.isPresent()) {
      PBPDTO pdto = convertPBPEntityToPBPDto(optionalPbp.get());

      return pdto;
    }

    return null;
  }


  /**
   * getAmountFromOrderId() - successUrl 로 redirect 시 amount 를 가져오는 메서드
   */
  @Override
  public long getAmountFromOrderId(String orderId) {
    Optional<PaymentBeforePayment> optionalPbp = pbpRepository.findByOrderId(orderId);

    if (optionalPbp.isPresent()) {
      PBPDTO pdto = convertPBPEntityToPBPDto(optionalPbp.get());

      return pdto.getPbpTotalAmount();
    }

    return -1;
  }


  /**
   * registerPaymentReceipt(PaymentReceiptDTO prdto) - PaymentReceipt TABLE 에 Record INSERT
   * <p>
   * > res.setBookingStatus("DONE") 은 @Transactional 안에서만 가능한 방법으로 Dirty Checking (더티 체킹)
   * 을 이용한 방법
   * <p>
   * > @Transactional 이 없다면
   *
   */
  @Override
  @Transactional
  public void insertPaymentReceipt(PaymentReceiptDTO prdto, String orderId) {
    // 확인
    log.info("regiseter-convert: {}", convertPaymentReceiptDtoToPaymentReceiptEntity(prdto));
    log.info("regiseter-prdto : {}", prdto);

    /** 남은 방이 있는 지 확인 하고 결제 진행
     *
     *  > paymentReceipt TABLE 의 orderId 와 일치하는 Reservation Record 를 가져와
     *    해당 Record 의 roomId 로 남은 방이 있는 경우 (stock_count > 0) 에만 결제 진행
     * */
    // orderId 로 예약 정보 가져오기
    Reservation res
        = reservationRepository.findByOrderId(orderId)
        .orElseThrow(() -> new EntityNotFoundException("Can't found this Entity..!"));

    Room room1 = roomRepository.findById(res.getRoomId()).orElseThrow(() ->
        new EntityNotFoundException(("Can't found this entity..!")));

    if (room1.getStockCount() > 0) {
      // 반환받은 payment 객체를 DB 에 저장
      paymentReceiptRepository.save(convertPaymentReceiptDtoToPaymentReceiptEntity(prdto));

      /** cupon 의 isValid 를 false 로 변경 */
      // 결제 정보 가져오기
      PaymentBeforePayment pbp = pbpRepository.findByOrderId(prdto.getOrderId()).orElseThrow(()
          -> new EntityNotFoundException("Can't found this Entity..!"));

      // 결제 정보로 결제 시 사용한 쿠폰 정보 가져오기
      if (pbp.getCuponId() != null) {
        Cupon cupon = cuponRepository.findById(pbp.getCuponId()).orElseThrow(() ->
            new EntityNotFoundException(("Can't found this Entity..!")));

        // 쿠폰 사용 표시
        cupon.setValid(false);
      }


      /** user 의 userGragePoint 를 결제금액의 2% 만큼 증가 */
      // 예약 정보의 유저 ID 로 유저 정보 가져오기
      User user = userRepository.findById(res.getUserId()).orElseThrow(() ->
          new EntityNotFoundException("Can't fount this Entity..!"));

      // 예약 정보의 Point 측정
      int gp = res.getTotalAmount() * 2 / 100;

      // 저장
      user.setUserGradePoint(user.getUserGradePoint() + gp);

      // 등급 관련 로직
      if (user.getUserGradePoint() >= 3000 && user.getUserGradePoint() < 5000) {
        user.setUserGrade("GOLD");
      } else if (user.getUserGradePoint() >= 5000) {
        user.setUserGrade("PLATINUM");
      }

      /** room 의 stockCount - 1 */
      Room room2 = roomRepository.findById(res.getRoomId()).orElseThrow(() ->
          new EntityNotFoundException(("Can't found this entity..!")));

      room2.setStockCount(room2.getStockCount() - 1);

      // 해당 orderId 와 일치하는 Reservation TABLE 의 bookingStatus 를 DONE 으로 변경
      res.setBookingStatus("DONE");
      
      // 해찬
      // 결제 완료 알림



      Alarm alarm = Alarm.builder()
          .userId(user.getUserId()) // 로그인한 유저 id
          .targetId(Long.parseLong(res.getOrderId().substring(6,19)))
          .code("s_reservation") // 알림 코드 (자기가 직접 작성하거나 만들어놓은 예시 참고)
          .build();

      alarmRepository.save(alarm);
      
    }
    // 남는 객실이 없는 경우
    else {
      // 방법 1) 예외 던지기
      throw new IllegalStateException();


      // 방법 2) 롤백 표시
      // TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
    
    

  }


  /**
   * String getPaymnetKey(String orderId) - paymentKey 가져오기
   */
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
    Long paymentCancelId = paymentCancelRepository.save(convertPaymentCancelDtoToPaymentCancelEntity(rcdto.getCancels().get(0))).getPaymentCancelId();

    // 예약 정보 조회
    Reservation reservation = reservationRepository.findByOrderId(orderId).orElseThrow(
        () -> new EntityNotFoundException("Can't found this Entity..!"));

    /** 사용 했던 쿠폰 true 로 변경 */
    PaymentBeforePayment pbp = pbpRepository.findByOrderId(orderId).orElseThrow(() ->
        new EntityNotFoundException(("Can't found this Entity..!")));

    if (pbp.getCuponId() != null) {
      // 쿠폰 정보 조회
      Cupon cupon = cuponRepository.findById(pbp.getCuponId()).orElseThrow(() ->
          new EntityNotFoundException(("Can't found this Entity..!")));
      // 변경
      cupon.setValid(true);

      // 해찬
      // 결제 취소 알림 저장
      Alarm alarm = Alarm.builder()
          .userId(reservation.getUserId()) // 로그인한 유저 id
          .targetId(paymentCancelId) // 타겟 테이블 id
          .code("c_reservation") // 알림 코드 (자기가 직접 작성하거나 만들어놓은 예시 참고)
          .build();

      alarmRepository.save(alarm);
    }

    /** 포인트 반환 */
    User user = userRepository.findById(reservation.getUserId()).orElseThrow(() ->
        new EntityNotFoundException(("Can't found this Entity..!")));

    int gp = reservation.getTotalAmount() * 2 / 100;

    user.setUserGradePoint(user.getUserGradePoint() - gp);

    // 예약 정보를 취소로 변경
    reservation.setBookingStatus("CANCELED");
  }

  /**
   * 결제 페이지 이동 시 정보 - 객실 정보 가져오기
   */
  @Override
  public Room getRoomInfo(long roomId) {
    Room room = roomRepository.findById(roomId).orElseThrow(() ->
        new EntityNotFoundException("Can't found this Entity..!"));

    return room;
  }


  /**
   * 결제 페이지 이동 시 정보 - 유저 정보 가져오기
   */
  @Override
  public User getUserInfo(long userId) {
    User user = userRepository.findById(userId).orElseThrow(() ->
        new EntityNotFoundException("Can't found this Entity..!"));

    return user;
  }


  /**
   * 결제 페이지 이동 시 정보 - 쿠폰 정보
   */
  @Override
  public List<CuponDTO> getCuponList(long userId) {
    OffsetDateTime currentTime = OffsetDateTime.now();

    List<Cupon> cuponList = cuponRepository.findValidCuponsByUserId(userId, currentTime);

    List<CuponDTO> cdtoList = cuponList.stream().map(this::convertCuponEntityToCuponDto).toList();

    return cdtoList;
  }


  /**
   * 결제 페이지 이동 시 정보 - 쿠폰 개수
   */
  @Override
  public int getCuponCnt(long userId) {
    OffsetDateTime currentTIme = OffsetDateTime.now();

    return cuponRepository.countValidCuponsByUserId(userId, currentTIme);
  }


  /**
   * 기존 예약 정보를 가져와 형식에 맞게 반환
   */
  @Override
  public int[] getRoomsReserveInfo(long roomId) {
    // 초기화
    ArrayList<Integer> tempArr = new ArrayList<>();

    // 예약 정보 가져오기
    List<Reservation> roomsReserveInfo = reservationRepository.findByRoomId(roomId);

    // 아무 예약 정보가 없는 경우
    if (roomsReserveInfo.isEmpty()) {
      return new int[]{-1};
    }


    /** 예약 정보가 있는 경우 예약 정보에서 예약된 시간대를 추출하여 형식화
     *
     *  > 현재 대실 운영 시간을 일괄적으로  10:00 ~ 22:00 로 운영하고 있기에
     *    10:00 = 0, 11:00 = 1, ... 22:00 = 13 으로 지정
     *
     *  > 예약 정보가 있는 경우 예약 정보의 이용 시간대를 추출하여 위의 형식에 맞게 변환
     *
     *  > OffsetDateTime 의 형식 - 2025-08-01T15:00+09:00
     *
     * */
    for (int i = 0; i < roomsReserveInfo.size(); i++) {
      /** 이용 시작/종료 시간
       *
       * > int startNum = Integer.parseInt(roomsReserveInfo.get(i).getStartDate()
       *   .toString().substring(11,13));
       *   // 확인
       *   log.info("getRoomsReserveInfo(long roomId) 의 startNum : {}", startNum); 와
       *
       *   int endNum = Integer.parseInt(roomsReserveInfo.get(i).getEndDate()
       *   .toString().substring(11,13));
       *   // 확인
       *   log.info("getRoomsReserveInfo(long roomId) 의 endNum : {}", endNum); 는
       *
       *   UTC 기준 시각으로 출력되어 예약 정보가 10:00 와 15:00 인 경우 startNum = 1, endNum = 5 가 됨
       *
       * */
      // 이용 시작 시간
      OffsetDateTime startDate
          = roomsReserveInfo.get(i).getStartDate().withOffsetSameInstant(ZoneOffset.ofHours(9));

      int startNum = startDate.getHour();
      // 확인
      log.info("getRoomsReserveInfo(long roomId) 의 startNum : {}", startNum);

      // 이용 종료 시간
      OffsetDateTime endDate
          = roomsReserveInfo.get(i).getEndDate().withOffsetSameInstant(ZoneOffset.ofHours(9));

      int endNum = endDate.getHour();
      // 확인
      log.info("getRoomsReserveInfo(long roomId) 의 startNum : {}", endNum);

      // 형식화를 위한 연산
      int startSlot = startNum - 10;
      int endSlot = endNum - 10;


      for (int j = startSlot; j <= endSlot; j++) {
        tempArr.add(j);
      }
    }

    /** 오름차순 정렬
     *
     *  > 내림 차순 정렬은 Comparator.reverseOrder()
     * */
    tempArr.sort(Comparator.naturalOrder());


    /** tempArr -> resultArr 
     *
     *  > 방법 1) for 반복문을 이용하여 변환 
     *
     int[] resultArr = new int[tempArr.size()];

     // 언박싱
     for(int k = 0; k < tempArr.size(); k++){
     resultArr[k] = tempArr.get(k);
     }
     *
     *
     *  > 방법 2) stream() 사용
     *
     * */

    int[] resultArr
        = tempArr.stream()
        .mapToInt(Integer::intValue) // Integer -> int
        .toArray(); // int[] 로 변환

    return resultArr;
  }


  /**
   * getUserCustomerKey(long userId) - userId 로 customerKey 가져오기
   *
   *
   */
  @Override
  public String getUserCustomerKey(long userId) {
    User user = userRepository.findById(userId).orElseThrow(() ->
        new EntityNotFoundException("Can't fount this Entity..!"));

    return user.getCustomerKey();
  }


  /**
   * getCancelAmount(String orderId) - 환불 정책에 따른 금액 반환
   * <p>
   * > 체크인 2주 전 혹은 당일 예약 후 1시간 이내 취소는 100% 환불
   * 체크인 1주 전 취소는 50% 환불
   * 체크인 3일 전은 취소 불가
   *
   *
   */
  @Override
  public long getCancelAmount(String orderId) {
    // 초기화
    long cancelAmount = 0;

    // 예약 정보에서 최종 결제 금액 가져오기 위해 예약 정보 불러오기
    Reservation res = reservationRepository.findByOrderId(orderId).orElseThrow(() ->
        new EntityNotFoundException(("Can't found this entity..!")));

    // 예약 시간 정보
    OffsetDateTime reservedTime
        = res.getReservationRegDate().withOffsetSameInstant(ZoneOffset.ofHours(9));
    // 확인
    log.info("getCancelAmount 의 reserveTime : {}", reservedTime);

    // 체크인 정보
    OffsetDateTime checkInDateTime
        = res.getStartDate().withOffsetSameInstant(ZoneOffset.ofHours(9));

    /** 현재 시간
     *
     * > OffsetDateTime 은 UTC 와의 단순한 시간 차이(+09:00)만 표현하고
     *   ZonedDateTime 은 'Asia/Seoul' 과 같은 특정 지역의 전체 시간대 규칙
     *   (서머타임 등) 을 포함하여 표현
     *
     * > "Asia/Seoul" 시간대 규칙을 적용한 현재 시각 가져오기
     *
     * */
    ZonedDateTime zonedDateTimeKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    // OffsetDateTime 으로 변한
    OffsetDateTime now = zonedDateTimeKST.toOffsetDateTime();


    /** Duration.between(시작, 종료)
     *
     *  > 두 시점(Temporal)을 비교해서 시작 시점부터
     *    종료 시점까지의 “지속 시간(Duration)” 객체를 생성
     *
     */
    // 예약 시간과 현재 시간의 차이
    Duration diffTime = Duration.between(reservedTime, now);

    // checkIn 과 현재 시간의 차이
    Duration diffDays = Duration.between(checkInDateTime, now);


    /** 예약한지 1시간 이내이거나 2주일 전인 경우
     *
     *  > diff.toMinutes() 는 Duration 객체가 표현하는 전체 기간을 분(minute) 단위로 환산해
     *    long 타입으로 반환
     *
     *  > diff.toDays() 는 Duration 객체가 표현하고 있는 전체 기간을 일(Days) 단위로 변환하여
     *    long 타입으로 반환
     * */
    if (diffTime.toMinutes() <= 60) {
      cancelAmount = res.getTotalAmount();
      return cancelAmount;
    }

    if (diffDays.toDays() <= 3) {
      return cancelAmount = -1;
    }

    if (diffDays.toDays() > 3 && diffDays.toDays() <= 7) {
      return cancelAmount = res.getTotalAmount() / 2;
    }

    if (diffDays.toDays() >= 14) {
      return cancelAmount = res.getTotalAmount();
    }

    return cancelAmount;
  }
}
