package com.berry.project.service.admin;

import com.berry.project.dto.admin.AdminLodgeDTO;
import com.berry.project.dto.admin.ReviewDTO;
import com.berry.project.dto.cupon.CuponDTO;
import com.berry.project.dto.cupon.CuponTemplateDTO;
import com.berry.project.dto.payment.PBPDTO;
import com.berry.project.dto.payment.PaymentCancelDTO;
import com.berry.project.dto.payment.PaymentReceiptDTO;
import com.berry.project.dto.payment.ReservationDTO;
import com.berry.project.dto.qna.CustomerIqBoardDTO;
import com.berry.project.dto.user.AuthUserDTO;
import com.berry.project.dto.user.UserDTO;
import com.berry.project.entity.cupon.Cupon;
import com.berry.project.entity.cupon.CuponTemplate;
import com.berry.project.entity.lodge.Lodge;
import com.berry.project.entity.payment.PaymentBeforePayment;
import com.berry.project.entity.payment.PaymentCancel;
import com.berry.project.entity.payment.PaymentReceipt;
import com.berry.project.entity.payment.Reservation;
import com.berry.project.entity.qna.CustomerIqBoard;
import com.berry.project.entity.review.Review;
import com.berry.project.entity.user.AuthUser;
import com.berry.project.entity.user.User;
import com.berry.project.handler.admin.AdminPagingHandler;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminService {
  /** CuponTemplateDTO -> CuponTemplate */
  default CuponTemplate convertCuponTemplateDTOtoCuponTemplateEntity(CuponTemplateDTO ctdto){
    if(ctdto == null){ return null; }

    return CuponTemplate
        .builder()
        .cuponType(ctdto.getCuponType())
        .cuponTitle(ctdto.getCuponTitle())
        .cuponPrice(ctdto.getCuponPrice())
        .theMinimumAmount(ctdto.getTheMinimumAmount())
        .cuponImgName(ctdto.getCuponImgName())
        .cuponEndDate(ctdto.getCuponEndDate())
        .qty(ctdto.getQty())
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


  /* UserDTO -> User */
  default User oauthConvertUserDTOToUserEntity(UserDTO userDTO) {
    return User.builder()
        .userId(userDTO.getUserId())
        .userEmail(userDTO.getUserEmail())
        .password(userDTO.getPassword())
        .userPhone(userDTO.getUserPhone())
        .userName(userDTO.getUserName())
        .userUid(userDTO.getUserUid())
        .provider(userDTO.getProvider())
        .birthday(userDTO.getBirthday())
        .customerKey(userDTO.getCustomerKey())
        .isAdult(userDTO.isAdult())
        .lastLogin(userDTO.getLastLogin())
        .isEmailCertified(userDTO.isEmailCertified())
        .isMobileCertified(userDTO.isMobileCertified())
        .userTermOption(userDTO.isUserTermOption())
        .userGrade("silver")
        .build();
  }


  /* UserDTO -> Auth */
  default AuthUser convertUserDTOToAuthEntity(UserDTO userDTO) {
    return AuthUser.builder()
        .userId(userDTO.getUserId())
        .authRole("USER_ROLE")
        .build();
  }


  /* Auth -> AuthDTO */
  default AuthUserDTO convertEntityToAuthDTO(AuthUser authUser) {
    return AuthUserDTO.builder()
        .authId(authUser.getAuthId())
        .userId(authUser.getUserId())
        .authRole(authUser.getAuthRole())
        .build();
  }


  /* User -> UserDTO */
  default UserDTO convertEntityToUserDTO(User user, List<AuthUserDTO> authUserDTOList) {
    return UserDTO.builder()
        .userId(user.getUserId())
        .userEmail(user.getUserEmail())
        .password(user.getPassword())
        .userUid(user.getUserUid())
        .userGrade(user.getUserGrade())
        .userName(user.getUserName())
        .userFavoriteTag(user.getUserFavoriteTag())
        .userPhone(user.getUserPhone())
        .userTermOption(user.isUserTermOption())
        .regDate(user.getRegDate())
        .lastLogin(user.getLastLogin())
        .modDate(user.getModDate())
        .provider(user.getProvider())
        .birthday(user.getBirthday())
        .authList(authUserDTOList)
        .isAdult(user.isAdult())
        .isMobileCertified(user.isMobileCertified())
        .customerKey(user.getCustomerKey())
        .isEmailCertified(user.isEmailCertified())
        .build();
  }

  /* CustomerIqBoardDTO -> CustomerIqBoard */
  default CustomerIqBoard convertDtoToEntity(CustomerIqBoardDTO customeriqboardDTO){

    return CustomerIqBoard.builder()
        .bno(customeriqboardDTO.getBno())
        .category(customeriqboardDTO.getCategory())
        .title(customeriqboardDTO.getTitle())
        .userEmail(customeriqboardDTO.getUserEmail())
        .content(customeriqboardDTO.getContent())
        .isSecret(customeriqboardDTO.getSecret())
        .comment(customeriqboardDTO.getComment())
        .commentRegDate(customeriqboardDTO.getCommentRegDate())
        .build();
  }


  /* CustomerIqBoard -> CustomerIqBoardDTO */
  default CustomerIqBoardDTO convertEntityToDto(CustomerIqBoard customeriqboard) {
    boolean isSecret = customeriqboard.getIsSecret() == null ? false : customeriqboard.getIsSecret();

    return CustomerIqBoardDTO.builder()
        .bno(customeriqboard.getBno())
        .category(customeriqboard.getCategory())
        .title(customeriqboard.getTitle())
        .userEmail(customeriqboard.getUserEmail())
        .content(customeriqboard.getContent())
        .regDate(customeriqboard.getRegDate())
        .modDate(customeriqboard.getModDate())
        .secret(isSecret)
        .comment(customeriqboard.getComment())
        .commentRegDate(customeriqboard.getCommentRegDate())
        .build();
  }


  /* Review -> ReviewDTO */
  default ReviewDTO convertReviewEntityToReviewDto(Review review){

    return ReviewDTO
        .builder()
        .reviewId(review.getReviewId())
        .userId(review.getReviewId())
        .lodgeId(review.getLodgeId())
        .userEmail(review.getUserEmail())
        .reservationId(review.getReservationId())
        .rating(review.getRating())
        .content(review.getContent())
        .aiSummary(review.getAiSummary())
        .createdAt(review.getCreatedAt())
        .reportedCount(review.getReportedCount())
        .build();
  }

  /* Lodge -> AdminLodgeDTO */
  default AdminLodgeDTO convertLodgeEntityToAdminLodgeDto(Lodge lodge){

    return AdminLodgeDTO
        .builder()
        .lodgeId(lodge.getLodgeId())
        .lodgeName(lodge.getLodgeName())
        .lodgeType(lodge.getLodgeType())
        .lodgeAddr(lodge.getLodgeAddr())
        .facility(lodge.getFacility())
        .intro(lodge.getIntro())
        .businessCall(lodge.getBusinessCall())
        .latitude(lodge.getLatitude())
        .longitude(lodge.getLongitude())
        .build();
  }

  // CuponTemplate TABLE 의 Record 가져오기
  List<CuponTemplateDTO> getCuponTemplateDTOList();

  // 새로운 쿠폰 생성
  boolean insertCuponTemplate(CuponTemplateDTO ctDto);

  // frag 에 따라 다른 AdminPagingHandler 를 반환
  AdminPagingHandler<?> getPagingFragments(String frag, int pageNo, int qty, String sortType, String keyword, int dataSet, String filterType);

  // 쿠폰 삭제 카테고리에서 쿠폰 삭제 클릭 시 해당 레코드 삭제
  boolean deleteCuponTemplate(String ctId);

  // 쿠폰 발급 버튼 클릭 시 모든 유저에게 쿠폰 발급
  boolean insertCuponAllUser(String ctId);

  // 관리자 페이지에서 해당 게시글 삭제
  boolean deleteQnaElem(long bno);

  // 관리자 페이지에서 리뷰 삭제
  boolean deleteReviewElem(long reviewId);
}
