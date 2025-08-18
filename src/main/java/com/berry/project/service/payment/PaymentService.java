package com.berry.project.service.payment;

import com.berry.project.dto.cupon.CuponDTO;
import com.berry.project.dto.cupon.CuponTemplateDTO;
import com.berry.project.dto.payment.*;
import com.berry.project.dto.user.MyPageReservationDTO;
import com.berry.project.entity.cupon.Cupon;
import com.berry.project.entity.cupon.CuponTemplate;
import com.berry.project.entity.lodge.Room;
import com.berry.project.entity.payment.PaymentBeforePayment;
import com.berry.project.entity.payment.PaymentCancel;
import com.berry.project.entity.payment.PaymentReceipt;
import com.berry.project.entity.payment.Reservation;
import com.berry.project.entity.user.User;

import java.util.List;

public interface PaymentService {
  /** PBPDTO -> PaymentBeforePayment */
  default PaymentBeforePayment convertPBPDtoToPBPEntity(PBPDTO pbpdto){
    if(pbpdto == null) { return null; }

    return
        PaymentBeforePayment
            .builder()
            .paymentId(pbpdto.getPaymentId())
            .customerKey(pbpdto.getCustomerKey())
            .orderId(pbpdto.getOrderId())
            .cuponId(pbpdto.getCuponId())
            .method(pbpdto.getMethod())
            .cuponPrice(pbpdto.getCuponPrice())
            .strikePrice(pbpdto.getStrikePrice())
            .pbpTotalAmount(pbpdto.getPbpTotalAmount())
            .orderName(pbpdto.getOrderName())
            .orderRegDate(pbpdto.getOrderRegDate())
            .build();
  }


  /** PaymentBeforePayment -> PBPDTO */
  default PBPDTO convertPBPEntityToPBPDto(PaymentBeforePayment pbp){
    // null 처리
    if(pbp == null){ return null; }

    return
        PBPDTO.builder()
              .paymentId(pbp.getPaymentId())
              .customerKey(pbp.getCustomerKey())
              .orderId(pbp.getOrderId())
              .cuponId(pbp.getCuponId())
              .method(pbp.getMethod())
              .cuponPrice(pbp.getCuponPrice())
              .strikePrice(pbp.getStrikePrice())
              .pbpTotalAmount(pbp.getPbpTotalAmount())
              .orderName(pbp.getOrderName())
              .orderRegDate(pbp.getOrderRegDate())
              .build();
  }


  /** ReservationDTO -> Reservation */
  default Reservation convertReservationDtoToReservationEntity(ReservationDTO rdto){
    if(rdto == null){ return null; }

    return
        Reservation.builder()
                   .reservationId(rdto.getReservationId())
                   .roomId(rdto.getRoomId())
                   .userId(rdto.getUserId())
                   .startDate(rdto.getStartDate())
                   .endDate(rdto.getEndDate())
                   .totalAmount(rdto.getTotalAmount())
                   .guestsAmount(rdto.getGuestsAmount())
                   .reservationType(rdto.getReservationType())
                   .reservationRegDate(rdto.getReservationRegDate())
                   .build();
  }


  /** Reservation -> ReservationDTO */
  default ReservationDTO convertReservationEntityToReservationDto(Reservation reservation){
    if(reservation == null) { return null; }

    return
        ReservationDTO.builder()
                      .reservationId(reservation.getReservationId())
                      .roomId(reservation.getRoomId())
                      .userId(reservation.getUserId())
                      .orderId(reservation.getOrderId())
                      .startDate(reservation.getStartDate())
                      .endDate(reservation.getEndDate())
                      .totalAmount(reservation.getTotalAmount())
                      .guestsAmount(reservation.getGuestsAmount())
                      .reservationType(reservation.getReservationType())
                      .reservationRegDate(reservation.getReservationRegDate())
                      .build();
  }


  /** PaymentReceiptDTO -> PaymentReceipt */
  default PaymentReceipt convertPaymentReceiptDtoToPaymentReceiptEntity(PaymentReceiptDTO prdto){
    if(prdto == null) { return null; }

    return
        PaymentReceipt.builder()
                      .paymentKey(prdto.getPaymentKey())
                      .orderId(prdto.getOrderId())
                      .type(prdto.getType())
                      .orderName(prdto.getOrderName())
                      .status(prdto.getStatus())
                      .totalAmount(prdto.getTotalAmount())
                      .method(prdto.getMethod())
                      .requestedAt(prdto.getRequestedAt())
                      .approvedAt(prdto.getApprovedAt())
                      .lastTransactionKey(prdto.getLastTransactionKey())
                      .rawData(prdto.getRawData())
                      .build();
  }


  /** PaymentReceipt -> PaymentReceiptDTO */
  default PaymentReceiptDTO convertPaymentReceiptEntityToPaymentReceiptDto(PaymentReceipt pr){
    if(pr == null){ return null; }

    return
        PaymentReceiptDTO.builder()
                         .paymentKey(pr.getPaymentKey())
                         .orderId(pr.getOrderId())
                         .type(pr.getType())
                         .orderName(pr.getOrderName())
                         .status(pr.getStatus())
                         .totalAmount(pr.getTotalAmount())
                         .method(pr.getMethod())
                         .requestedAt(pr.getRequestedAt())
                         .approvedAt(pr.getApprovedAt())
                         .lastTransactionKey(pr.getLastTransactionKey())
                         .rawData(pr.getRawData())
                         .build();
  }


  /** PaymentCancelDTO -> PaymentCancel */
  default PaymentCancel convertPaymentCancelDtoToPaymentCancelEntity(PaymentCancelDTO pcdto){
    if(pcdto == null) { return null; }

    return
        PaymentCancel.builder()
                     .paymentCancelId(pcdto.getPaymentCancelId())
                     .paymentKey(pcdto.getPaymentKey())
                     .transactionKey(pcdto.getTransactionKey())
                     .cancelReason(pcdto.getCancelReason())
                     .cancelAmount(pcdto.getCancelAmount())
                     .canceledAt(pcdto.getCanceledAt())
                     .rawData(pcdto.getRawData())
                     .build();
  }


  /** PaymentCancel -> PaymentCancelDTO */
  default PaymentCancelDTO convertPaymentCancelEntityToPaymentCacnelDto(PaymentCancel pc){
    if(pc == null) { return null; }

    return
        PaymentCancelDTO.builder()
                        .paymentCancelId(pc.getPaymentCancelId())
                        .paymentKey(pc.getPaymentKey())
                        .transactionKey(pc.getTransactionKey())
                        .cancelReason(pc.getCancelReason())
                        .cancelAmount(pc.getCancelAmount())
                        .canceledAt(pc.getCanceledAt())
                        .rawData(pc.getRawData())
                        .build();
  }


  /** CuponDTO -> Cupon */
  default Cupon convertCuponDtoToCuponEntity(CuponDTO cdto){
    if(cdto == null){ return null; }

    return
        Cupon.builder()
             .cuponId(cdto.getCuponId())
             .userId(cdto.getUserId())
             .cuponType(cdto.getCuponType())
             .cuponRegDate(cdto.getCuponRegDate())
             .cuponEndDate(cdto.getCuponEndDate())
             .isValid(cdto.isValid())
             .build();
  }


  /** Cupon -> CuponDTO */
  default CuponDTO convertCuponEntityToCuponDto(Cupon cupon){
    if(cupon == null) { return null; }

    return
        CuponDTO.builder()
                .cuponId(cupon.getCuponId())
                .userId(cupon.getUserId())
                .cuponType(cupon.getCuponType())
                .cuponRegDate(cupon.getCuponRegDate())
                .cuponEndDate(cupon.getCuponEndDate())
                .isValid(cupon.isValid())
                .build();
  }


  /** MergePayloadDTO -> PBP Entity */
  default PaymentBeforePayment extractPBPDtoAndConvertPBPEntity(MergePayloadDTO mpdto){
    if(mpdto == null) { return null; }

    return
        PaymentBeforePayment
              .builder()
              .customerKey(mpdto.getPbpPayload().getCustomerKey())
              .cuponId(mpdto.getPbpPayload().getCuponId())
              .orderId(mpdto.getPbpPayload().getOrderId())
              .method(mpdto.getPbpPayload().getMethod())
              .cuponPrice(mpdto.getPbpPayload().getCuponPrice())
              .strikePrice(mpdto.getPbpPayload().getStrikePrice())
              .pbpTotalAmount(mpdto.getPbpPayload().getPbpTotalAmount())
              .orderName(mpdto.getPbpPayload().getOrderName())
              .build();
  }


  /** MergePayloadDTO -> Reservation Entity */
  default Reservation extractRdtoAndConvertEntity(MergePayloadDTO mpdto){
    if(mpdto == null) { return null; }

    return
        Reservation
            .builder()
            .roomId(mpdto.getReservePayload().getRoomId())
            .userId(mpdto.getReservePayload().getUserId())
            .orderId(mpdto.getReservePayload().getOrderId())
            .startDate(mpdto.getReservePayload().getStartDate())
            .endDate(mpdto.getReservePayload().getEndDate())
            .totalAmount(mpdto.getReservePayload().getTotalAmount())
            .guestsAmount(mpdto.getReservePayload().getGuestsAmount())
            .bookingStatus("PENDING")
            .reservationType(mpdto.getReservePayload().getReservationType())
            .build();
  }

  /** CuponTemplate -> CuponTemplateDTO */
  default CuponTemplateDTO convertCuponTemplatetoCuponTemplateDTO(CuponTemplate cuponTemplate){
    if(cuponTemplate == null){ return null; }

    return CuponTemplateDTO
        .builder()
        .ctId(cuponTemplate.getCtId())
        .cuponType(cuponTemplate.getCuponType())
        .cuponTitle(cuponTemplate.getCuponTitle())
        .cuponPrice(cuponTemplate.getCuponPrice())
        .theMinimumAmount(cuponTemplate.getTheMinimumAmount())
        .cuponImgName(cuponTemplate.getCuponImgName())
        .cuponEndDate(cuponTemplate.getCuponEndDate())
        .qty(cuponTemplate.getQty())
        .build();

  }

  // 결제하기 버튼 클릭 시 JS 에서 보낸 결제, 예약 정보를 저장하는 메서드
  boolean insertMergePayload(MergePayloadDTO mpdto);

  // 결제하기 버튼 클릭 시 저장한 결제 정보를 가져오는 메서드
  PBPDTO getPbp(String orderId);

  // amount 와 비교를 위한 메서드
  long getAmountFromOrderId(String orderId);

  // PaymentReceiptDTO 를 PaymentReceipt TABLE 에 저장
  void insertPaymentReceipt(PaymentReceiptDTO prdto, String orderId);

  // orderId 로 paymentKey 를 get
  String getPaymnetKey(String orderId);

  // return 받은 payment 객체에서 cancels 를 추출해 PaymentCancel TABLE 에 저장
  void insertPaymentCancel(String paymentKey, ReturnCancelsDTO rcdto, String orderId);

  // 결제 페이지 이동 시 정보 - 객실 정보
  Room getRoomInfo(long roomId);

  // 결제 페이지 이동 시 정보 - 유저 정보
  User getUserInfo(long userId);

  // 결제 페이지 이동 시 정보 - 쿠폰 정보
  List<CuponDTO> getCuponList(long userId);

  // 결제 페이지 이동 시 정보 - 쿠폰 개수
  int getCuponCnt(long userId);

  // 기존 예약 정보를 특정 형식으로 가져옴
  int[] getRoomsReserveInfo(RsvdInfoDTO rsvdInfoDTO);

  // userId 를 받아 customerKey 가져오기
  String getUserCustomerKey(long userId);

  // 환불 정책에 따른 금액 반환
  long getCancelAmount(String orderId);

  // 쿠폰 타입으로 쿠폰 조회
  CuponTemplateDTO getCuponTemplate(String cuponType);
}
