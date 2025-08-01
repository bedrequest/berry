package com.berry.project.controller;

import com.berry.project.api.TossApi;
import com.berry.project.dto.cupon.CuponDTO;
import com.berry.project.dto.payment.*;
import com.berry.project.entity.lodge.Room;
import com.berry.project.entity.user.User;
import com.berry.project.handler.payment.TossPaymentsAPIHandler;
import com.berry.project.service.payment.OrderIdGenerator;
import com.berry.project.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/payment/*")
public class PaymentController {
  // 초기화
   // API Key
  private final TossApi tossApi;
   // OrderId 생성기
  private final OrderIdGenerator orderIdGenerator;
   // Service
  private final PaymentService paymentservice;
   //
  private final TossPaymentsAPIHandler tpcApiHandler;

  /** 테스트용 페이지 */
  @GetMapping("/paymentBtn")
  public void paymentBtn(){}


  @PostMapping(value="/moveRent", consumes= MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public String moveRent(@ModelAttribute OrderPayloadDTO opDto, RedirectAttributes re){

    re.addFlashAttribute("opDto", opDto);

    return "redirect:/payment/paymentRent";
  }


  /** "@GetMapping("/paymentRent") - 대실 예약 페이지로 이동
   *
   *  > 추후 User authorize 와 LodgeDTO 가 생기면 추후 수정
   *
   *  > 대실인 경우는 startDate 와 endDate 를 LocalDateTime 으로 View 에 전송하고 View 에서
   *    OffsetDateTime 형식으로 가공해서 서버에 전송 (PaymentRentPageDTO 사용)
   *
   *  > LocalDateTime 의 형식
   *   - yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS (e.g.,2025-08-01T15:45:30.123456789)
   *
   * */
  @GetMapping("/paymentRent")
  public void paymentRent(@ModelAttribute("opDto") OrderPayloadDTO opDto, Model m){
    // 초기화
     // API Key
    String tossClientKey = tossApi.getTossClientApiKey();
     // paymentRent.html 전달용
    PaymentRentPageDTO ppDto = new PaymentRentPageDTO();

    // 객실 정보 가져오기
    Room room = paymentservice.getRoomInfo(opDto.getRoomId());
    // 유저 정보 가져오기
    User user = paymentservice.getUserInfo(opDto.getUserId());

    // 객실명
    String roomNameInfo = room.getRoomName();
    // 대실 이용 가능 시간
    String rentTimeInfo = room.getRentTime();
    // 이용 시작일시
    LocalDate startDateInfo = opDto.getStartDate();
    // 이용 종료일시
    LocalDate endDateInfo = opDto.getEndDate();
    // 예약자명
    String userNameInfo = user.getUserName();
    // 예약자 휴대폰 번호
    String userPhoneInfo = user.getUserPhone();
    // 휴대폰 본인 인증 여부
    boolean isMobileCertifiedInfo = user.isMobileCertified();
    // 숙박 인원 
    int guestsAmountInfo = opDto.getGuestsAmount();
    // 정가 (대실 가격)
    long strikePriceInfo = room.getRentPrice();
    // 해당 유저의 보유 쿠폰 
    List<CuponDTO> cuponListInfo = paymentservice.getCuponList(opDto.getUserId());
    // 해당 유저의 보유 쿠폰 개수
    int cuponCntInfo = paymentservice.getCuponCnt(opDto.getUserId());
    // 예약 정보 설정을 위한 변수
    boolean isBeforeInfo = false;
    boolean isTodayInfo = false;

    /** 대실 예약 출력을 위한 초기화
     *
     *  > 현재 날짜가 이용 시작일 이전 이라면 (today.isBefore(startDateInfo)) isBefore 가 true
     *
     *  > 현재 날짜가 이용 시작일과 같다면 (today.isEqual(startDateInfo)) isToday 가 true
     *
     * */
    LocalDate today = LocalDate.now();
    // isBefore
    if(today.isBefore(startDateInfo)){ isBeforeInfo = true; }
    // isToday
    if(today.isEqual(startDateInfo)){ isTodayInfo = true; }


    // PaymentPageDTO 로 빌드
    PaymentRentPageDTO ppdto = PaymentRentPageDTO
        .builder()
        .roomName(roomNameInfo)
        .rentTime(rentTimeInfo)
        .startDate(startDateInfo)
        .endDate(endDateInfo)
        .userName(userNameInfo)
        .userPhone(userPhoneInfo)
        .isMobileCertified(isMobileCertifiedInfo)
        .guestsAmount(guestsAmountInfo)
        .strikePrice(strikePriceInfo)
        .cuponList(cuponListInfo)
        .cuponCnt(cuponCntInfo)
        .isBefore(isBeforeInfo)
        .isToday(isTodayInfo)
        .build();

    m.addAttribute("tossClientKey", tossClientKey);
    m.addAttribute("pInfoDto", ppdto);
  }


  /** "@GetMapping("/paymentStay") - 숙박 예약 페이지로 이동
   *
   *  > 추후 User authorize 와 LodgeDTO 가 생기면 추후 수정
   *
   * > 숙박 예약의 경우에는 시간을 입력받을 필요가 없기에 OrderPayloadDTO 를 통해 받은
   *   LocalDateTime 의 startDate, endDate 를 서버에서 room 의 stayTime 을 조회해 OffsetDateTIme
   *   형식으로 변환하여 전송 (PaymentStayPageDTO 이용)
   *
   * */
  @GetMapping("/paymentStay")
  public void paymentStay(Model m){
    // 초기화
     // API Key
    String tossClientKey = tossApi.getTossClientApiKey();

    m.addAttribute("tossClientKey", tossClientKey);
  }


  /** "@PostMapping("/mergePayload") - 결제하기 버튼 클릭 시 Record INSERT (비동기 POST 요청 처리)
   *
   * >
   *
   * > 예약에 필요한 정보
   *  - user : userId, customerKey, userName, userPhone (authorize 로 가져오기)
   *  - room : roomId, roomName, stayTime (이용 시작/종료 시간)
   *  - additional info : guests_number (이용 인원수), reservation_type (예약 타입)
   *
   * > 이용 인원 수는 Hedaer 에서 가져오고, reservation Type 은 숙박/대실 예약 버튼이 다르기에
   *   버튼 별 밸류를 다르게 주는 형식 등으로 처리
   *
   */
  @ResponseBody
  @PostMapping("/mergePayload")
  public PBPDTO pbpObj(@RequestBody MergePayloadDTO mpdto){
    // 초기화
    boolean isTrue = false;
    PBPDTO pdto = new PBPDTO();

    // 확인
    log.info("/payment/pbpobj 의 pbp : {}", mpdto);

    // 결제하기 버튼 클릭 시 만들어진 PBPDTO 와 ReservationDTO 를 INSERT
    if(paymentservice.insertMergePayload(mpdto)){ isTrue = true; }

    if(isTrue){
      pdto = paymentservice.getPbp(mpdto.getPbpPayload().getOrderId());
    }

    return pdto;
  }


  /** "@PostMapping("/generateOrderId") - orderId 생성 */
  @PostMapping("/generateOrderId")
  @ResponseBody
  public String generateOrderId() {
    return orderIdGenerator.generateOrderId();
  }


  /** "@GetMapping("/success") - 성공 redirectUrl
   *
   *  > int 의 최대범위를 넘어선 결제 금액인 경우, amount 가 int 면 Error 발생 확률이 존재
   *
   * */
  @GetMapping("/success")
  public String success(@RequestParam("orderId") String orderId, @RequestParam("paymentKey") String paymentKey
      , @RequestParam("amount") long amount) throws IOException, InterruptedException {
    // 확인
    log.info("=============in success=============");

    // 초기화
     // 경로
    String targetUrl = "redirect:/payment/fail";
     // PBP TABLE 에서 파라미터의 orderId 와 일치하는 Record 의 pbpTotalAmount 와 amount 를 비교하기 위한 초기화
    long pbpTotalAmount = paymentservice.getAmountFromOrderId(orderId);
     // 금액 비교
    boolean isValidAmount = false;

    // 금액 조건 초기화
    if(pbpTotalAmount == amount){ isValidAmount = true; };


    try {
      // 구매 시 결제 금액과 최종 결제 금액이 같으면  
      if(isValidAmount){
        // 결제 승인 API 호출
        PaymentReceiptDTO prdto = tpcApiHandler.callConfirmAPI(orderId, paymentKey, amount);

        // 확인
        log.info("PaymentController 의 prdto : {}", prdto);

        // @Transactional 이 사용된 메서드로 실패 시에는 PersistenceException 계열의 Exception 이 발생
        paymentservice.insertPaymentReceipt(prdto, orderId);

        targetUrl = "redirect:/payment/completed";
      }
    } catch (Exception e) {
      // 확인
      log.info("결제 중 오류 발생 : ", e);

      targetUrl = "redirect:/payment/fail";
    }

    return targetUrl;
  }


  /** "@GetMapping("/completed") - 결제완료 페이지로 이동 */
  @GetMapping("/completed")
  public void completed(){}


  /** "@GetMapping("/fail") - 실패 redirectUrl*/
  @GetMapping("/fail")
  public String fail(@RequestParam("code") String code, @RequestParam("message") String msg
      , @RequestParam("orderId") String orderId, RedirectAttributes re){

    re.addFlashAttribute("e_code", code);
    re.addFlashAttribute("e_msg", msg);

    return "redirect:/payment/again";
  }


  /** "@GetMapping("/again") - 결제 실패 페이지로 이동 */
  @GetMapping("/again")
  public String again(@ModelAttribute("e_code") String e_code, @ModelAttribute("e_msg") String e_msg){

    return "/payment/again";
  }


  /** "@PostMapping("/cancel") - 결제 환불 */
  @PostMapping("/cancel")
  @ResponseBody
  public int cancel(@RequestBody PaymentCancelDTOFromJS pcdtoFromJs){
    // 초기화
    int result = 0;
     // orderId
    String orderId = pcdtoFromJs.getOrderId();

    // 해당 예약 정보의 orderId 로 payment_receipt 의 paymentKey 가져오기
    String paymentKey = paymentservice.getPaymnetKey(orderId);

    try{
      // 결제 취소 API 호출
      ReturnCancelsDTO rcdto = tpcApiHandler.callCancelAPI(paymentKey, pcdtoFromJs);

      // return 받은 payment 객체에서 cancels 를 추출해 저장
      paymentservice.insertPaymentCancel(paymentKey, rcdto, orderId);

    } catch (Exception e) {
      result = -1;

      log.info("결제 취소 중 ERROR 발생 : ", e);

    }

    return result;
  }
}