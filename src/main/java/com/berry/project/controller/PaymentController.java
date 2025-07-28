package com.berry.project.controller;

import com.berry.project.api.TossApi;
import com.berry.project.dto.payment.*;
import com.berry.project.handler.payment.TossPaymentsAPIHandler;
import com.berry.project.service.payment.OrderIdGenerator;
import com.berry.project.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

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


  /** "@GetMapping("/paymentRent") - 대실 예약 페이지로 이동
   *
   *  > 추후 User authorize 와 LodgeDTO 가 생기면 추후 수정
   *
   *
   * */
  @GetMapping("/paymentRent")
  public void paymentRent(Model m){
    // 초기화
     // API Key
    String tossClientKey = tossApi.getTossClientApiKey();

    m.addAttribute("tossClientKey", tossClientKey);
  }


  /** "@GetMapping("/paymentStay") - 숙박 예약 페이지로 이동
   *
   *  > 추후 User authorize 와 LodgeDTO 가 생기면 추후 수정
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

    // PBP TABLE 에서 파라미터의 orderId 와 일치하는 Record 의 pbpTotalAmount 와 amount 를 비교
    long pbpTotalAmount = paymentservice.getAmountFromOrderId(orderId);

    //
    try {
      // 구매 시 결제 금액과 최종 결제 금액이 같으면  
      if(pbpTotalAmount == amount){
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


  /** "@PostMapping("/fail") - 실패 redirectUrl*/
  @PostMapping("/fail")
  public String fail(@RequestParam("code") String code, @RequestParam("message") String msg
      , @RequestParam("orderId") String orderId, RedirectAttributes re){

    re.addFlashAttribute("e_code", code);
    re.addFlashAttribute("e_msg", msg);

    return "redirect:/payment/again";
  }


  /** "@GetMapping("/again") - 결제 실패 페이지로 이동 */
  @GetMapping("/again")
  public void again(@ModelAttribute("e_code") String e_code, @ModelAttribute("e_msg") String e_msg){

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