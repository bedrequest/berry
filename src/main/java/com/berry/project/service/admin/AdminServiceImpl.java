package com.berry.project.service.admin;

import com.berry.project.dto.admin.AdminLodgeDTO;
import com.berry.project.dto.admin.AdminReservationDTO;
import com.berry.project.dto.admin.ReviewDTO;
import com.berry.project.dto.cupon.CuponDTO;
import com.berry.project.dto.cupon.CuponTemplateDTO;
import com.berry.project.dto.lodge.LodgeDTO;
import com.berry.project.dto.payment.PaymentCancelDTO;
import com.berry.project.dto.payment.PaymentReceiptDTO;
import com.berry.project.dto.payment.ReservationDTO;
import com.berry.project.dto.qna.CustomerIqBoardDTO;
import com.berry.project.dto.user.AuthUserDTO;
import com.berry.project.dto.user.MyPageReservationDTO;
import com.berry.project.dto.user.UserDTO;
import com.berry.project.entity.alarm.Alarm;
import com.berry.project.entity.cupon.CuponTemplate;
import com.berry.project.entity.payment.QReservation;
import com.berry.project.entity.payment.Reservation;
import com.berry.project.entity.review.Review;
import com.berry.project.entity.user.AuthUser;
import com.berry.project.entity.user.User;
import com.berry.project.handler.admin.AdminPagingHandler;
import com.berry.project.handler.payment.CuponHandler;
import com.berry.project.repository.lodge.LodgeRepository;
import com.berry.project.repository.payment.*;
import com.berry.project.repository.qna.CustomerIqBoardRepository;
import com.berry.project.repository.review.ReviewRepository;
import com.berry.project.repository.user.AuthUserRepository;
import com.berry.project.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {
  // 초기화
   // CuponTemplateRepository
  private final CuponTemplateRepository cuponTemplateRepository;
   // CuponRepository
  private final CuponRepository cuponRepository;
   // CuponHandler
  private final CuponHandler cuponHandler;
   // UserRepository
  private final UserRepository userRepository;
   // AuthRepository
  private final AuthUserRepository authUserRepository;
   // CustomerIqBoardRepository
  private final CustomerIqBoardRepository customerIqBoardRepository;
   // PaymentReceiptRepository
  private final PaymentReceiptRepository paymentReceiptRepository;
   // PaymentCancelRepository
  private final PaymentCancelRepository paymentCancelRepository;
   // ReviewRepository
  private final ReviewRepository reviewRepository;
   // LodgeRepository
  private final LodgeRepository lodgeRepository;
   //
  private final ReservationRepository reservationRepository;


  /* deleteReviewElem(long reviewId) - 관리자 페이지에서 해당 리뷰 삭제 */
  @Override
  @Transactional
  public boolean deleteReviewElem(long reviewId) {
    reviewRepository.deleteById(reviewId);

    return true;
  }

  /* deleteQnaElem(long bno) - 관리자 페이지에서 해당 게시글 삭제 */
  @Override
  @Transactional
  public boolean deleteQnaElem(long bno) {
    customerIqBoardRepository.deleteById(bno);

    return true;
  }


  /** deleteCuponTemplate(String ctId) - ctId 에 해당하는 Record 삭제 */
  @Override
  @Transactional
  public boolean deleteCuponTemplate(String ctId) {
    Long cuponTemplateId = Long.parseLong(ctId);

    // 쿠폰 템플릿 정보 가져오기
    CuponTemplate cuponTemplate = cuponTemplateRepository.findById(cuponTemplateId).orElseThrow(()
        -> new EntityNotFoundException("Can't found this Entity..!"));

    // Cupon TABLE 에서 cuponType 이 cuponTemplated 인 Cupon 들 삭제
    cuponRepository.deleteByCuponType(cuponTemplate.getCuponType());

    // deleteById() 의 반환값은 void
    cuponTemplateRepository.deleteById(cuponTemplateId);


    return true;
  }


  /* insertCuponAllUser(String ctId) - 발급 버튼 클릭 시 일괄적으로 발급 */
  @Override
  @Transactional
  public boolean insertCuponAllUser(String ctId) {
    // id 와 일치하는 쿠폰 템플릿 찾기
    CuponTemplate cuponTemplate = cuponTemplateRepository.findById(Long.parseLong(ctId)).orElseThrow(()
        -> new EntityNotFoundException("Cant't found this Entity..!"));

    /** CuponHandler 의 작업 과정
     *
     *  > 1) User TABLE 의 모든 유저의 id 를 List<long> 으로 가져오기
     *    2) i = 0; i < List.size(); i++
     *    3) cuponRepository.save()
     *
     */
    cuponHandler.callIssuanceCuponAllUser(cuponTemplate);

    return true;
  }




  /** getCuponTemplateDTOList() - CuponTemplate 의 Record 가져오기
   * 
   * */
  @Override
  public List<CuponTemplateDTO> getCuponTemplateDTOList() {
    // CuponTemplate TABLE 의 모든 레코드를 조회하여 CuponTemplateDTO 로 변환
    List<CuponTemplateDTO> cuponTemplateDTOList
        = cuponTemplateRepository.findAll().stream().map(this::convertCuponTemplatetoCuponTemplateDTO).toList();

    return cuponTemplateDTOList;
  }


  /** insertCuponTemplate(CuponTemplateDTO ctDto) - 새로운 쿠폰 생성 */
  @Override
  public boolean insertCuponTemplate(CuponTemplateDTO ctDto) {
    Integer result
        = cuponTemplateRepository.save(convertCuponTemplateDTOtoCuponTemplateEntity(ctDto)).getCuponType();

    return result > 0 ? true : false;
  }


  /** AdminPagingHandler<?> getPagingFragments - frag 별로 각각의 AdminPagingHandler 를 반환
   *
   * */
  @Override
  public AdminPagingHandler<?> getPagingFragments(String frag, int pageNo, int qty, String sortType
  , String keyword, int dataSet, String filterType) {


    return switch(frag){
      // 쿠폰 관리 - 쿠폰 삭제 (id=cupon-delete)
      case "cupon-delete" -> pageCuponTemplate(frag, pageNo, qty, sortType, keyword);

      /** 생성된 쿠폰 조회 (class="cupon-manage")
       *
       *  > Cupon TABLE 에서 CuponType 이 data-cupon-type 의 값인 Record 만을 조회해 출력
       * */
      case "cupon-manage" -> pageCuponType(frag, pageNo, qty, sortType, keyword, dataSet);

      /* 멤버 관리 - 전체 유저 보기 (id="all-user") */
      case "all-user" -> pageUser(frag, pageNo, qty, sortType, keyword);

      /* 고객 문의 관리 - 결제 (class="qna-payment")*/
      case "qna-payment" -> pageQna(frag, pageNo, qty, sortType, keyword, filterType);

      /* 고객 문의 관리 - 환불 (class="qna-cancel") */
      case "qna-cancel" -> pageQna(frag, pageNo, qty, sortType, keyword, filterType);

      /* 고객 문의 관리 - 시설 (class="qna-facilities") */
      case "qna-facilities" -> pageQna(frag, pageNo, qty, sortType, keyword, filterType);

      /* 고객 문의 관리 - 서비스 (class="qna-service") */
      case "qna-service" -> pageQna(frag, pageNo, qty, sortType, keyword, filterType);

      /* 고객 문의 관리 - 기타 (class="qna-others") */
      case "qna-others" -> pageQna(frag, pageNo, qty, sortType, keyword, filterType);

      /* 결제 관리 - 결제 완료 내역 (class="payment-completed")*/
      case "payment-completed" -> pagePaymentReceipt(frag, pageNo, qty, sortType, keyword);

      /* 결제 관리 - 미 결제 내역 (class="payment-wait") */
      // case "payment-wait" -> pagePaymentPBP(frag, pageNo, qty, sortType, keyword);

      /* 결제 관리 - 환불 완료 내역 (class="payment-cancel") */
      case "payment-cancel" -> pagePaymentCanceled(frag, pageNo, qty, sortType, keyword);

      /* 리뷰 관리 - 신고 관리 (class="review-report") */
      case "review-report" -> pageReview(frag, pageNo, qty, sortType, keyword);

      /* 리뷰 관리 - 리뷰 블랙 리스트 (class="review-blacklist) */
      // case "review-blacklist" -> pageReview(frag, pageNo, qty, sortType, keyword);

      /* 예약 내역 관리 - 최신 예약 내역 (class="reservation-order") */
      /* 예약 내역 관리 - 최신 예약 내역 (class="reservation-order") */
      case "reservation-order" -> pageReservation(frag, pageNo, qty, sortType, keyword);

      /* 예약 내역 관리 - 숙소 별 전화번호 (class="reservation-lodge")
      *
      *  > 숙소명 (lodge_name), 위치 (lodge_addr), 전화번호 (business_call) 를 보여줄 예정
       * */
      case "reservation-lodge" -> pageLodge(frag, pageNo, qty, sortType, keyword);

      default ->
        throw new IllegalArgumentException("해당 frag 와 일치하는 frag 가 존재하지 않습니다..!");
    };
  }


  /** pageCuponType() - 생성한 쿠폰 요소 <li class="cupon-manage"> 시 로드되는 페이지의 페이지네이션을 위한 메서드  */
  private AdminPagingHandler<CuponDTO> pageCuponType(String frag, int pageNo, int qty, String sortType
    , String keyword, int dataSet){
    // 에러 방지 초기화
     // pageNo
    if(pageNo < 1){ pageNo = 1; }
     // qty
    if(qty <= 0) { qty = 10; }
     // int -> Integer
    Integer dataSetCuponType = dataSet;


    /* 정렬 - CustomRepository 를 사용하지 않는 경우를 위한 초기화 */
    Sort sortKey = Sort.by(Sort.Direction.DESC, "cuponRegDate");

    /** Pageable
     *
     *  > PageRequest.of(int pageNo, int qty, Sort sortKey) 는 Pageable 을 return 하며
     *    sortKey 는 앞서 초기화된 변수를 넘겨 받음
     *
     *  > PageRequest.of(int pageNo, int qty, Sort sortKey) 시 Controller 에서의 사용과 혼동을 주의
     *
     *    - 컨트롤러는 1-base (pageNo=1), PageRequest 는 0-base (pageNo=0)
     * */
    Pageable pageable = PageRequest.of(pageNo - 1, qty, sortKey);


    /* 페이지네이션 */
    Page<CuponDTO> pgList
        = (keyword == null || keyword.isBlank())
          ? cuponRepository.findByCuponType(dataSetCuponType, pageable).map(this::convertCuponEntityToCuponDto)
          : cuponRepository.pageCuponType(keyword, pageable, dataSet).map(this::convertCuponEntityToCuponDto);

    /* AdminPagingHandler(Page<T> pgList, int pageNo, String sortType, String keyword, int qty, String frag) */
    return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
  }


  /** pageCuponTemplate() - 쿠폰 삭제 클릭 시 로드되는 페이지의 페이지네이션을 위한 메서드 */
  private AdminPagingHandler<CuponTemplateDTO> pageCuponTemplate(String frag, int pageNo, int qty, String sortType, String keyword){
    // 에러 방지 초기화
     // pageNo
    if(pageNo < 1){ pageNo = 1; }
     // qty
    if(qty <= 0) { qty = 10; }


    /** 정렬
     *
     *  > 초기화한 정렬 변수를 PageRequest.of(int pageNo, int qty, Sort sortKey) 의 sortKey 에 전달
     *    하기위한 초기화
     *
     *  > 등록된 쿠폰을 보여주는 경우 별도의 정렬 유형을 만들지 않음
     *
     *  > Sort.by(Sort.Direction.DESC, "ctId"); 는 CuponTemplate 의 PK 를 기준으로 내림차순으로 정렬
     *    함을 의미
     * */
    Sort sortKey = Sort.by(Sort.Direction.DESC, "ctId");



    /** Pageable
     *
     *  > PageRequest.of(int pageNo, int qty, Sort sortKey) 는 Pageable 을 return 하며
     *    sortKey 는 앞서 초기화된 변수를 넘겨 받음
     *
     *  > PageRequest.of(int pageNo, int qty, Sort sortKey) 시 Controller 에서의 사용과 혼동을 주의
     *
     *    - 컨트롤러는 1-base (pageNo=1), PageRequest 는 0-base (pageNo=0)
     * */
    Pageable pageable = PageRequest.of(pageNo - 1, qty, sortKey);


    /** 검색어
     *
     *  > 검색어에 따라 페이지네이션 된 Page<T> 를 PageImpl<T> 을 사용해 반환
     *
     *  > cuponTemplateRepository.findAll(pageable) 에서는 pageable 의 Sort가 자동으로 ORDER BY 에 반영 (자동 정렬)
     *    되지만 cuponTemplateRepository.pageCuponTemplate(keyword, pageable)처럼 직접 QueryDSL로 짠 커스텀 쿼리에서는
     *    pageable 의 정렬이 자동 반영되지 않기에 수동으로 정렬 (.orderBy(...) 를 사용) 해야 함
     *
     * */
    Page<CuponTemplateDTO> pgList
        = (keyword == null || keyword.isBlank())
          ? cuponTemplateRepository.findAll(pageable).map(this::convertCuponTemplatetoCuponTemplateDTO)
          : cuponTemplateRepository.pageCuponTemplate(keyword, pageable).map(this::convertCuponTemplatetoCuponTemplateDTO);

    /* AdminPagingHandler(Page<T> pgList, int pageNo, String sortType, String keyword, int qty, String frag) */
    return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
  }



  /** pageUser() - 전체 유저 보기 클릭 시 로드되는 페이지의 페이지네이션을 위한 메서드 */
  private AdminPagingHandler<UserDTO> pageUser(String frag, int pageNo, int qty, String sortType, String keyword){
    // 에러 방지 초기화
     // pageNo
    if(pageNo < 1){ pageNo = 1; }
     // qty
    if(qty <= 0){ qty = 10; }

    /** 페이지네이션
     *
     *  > 위에서 이전까지 했던 방식 (삼항 연산자를 이용하여 바로 DTO로 반환받는 방식) 을
     *    UserDTO 에 적용할 수 는 없음 (UserDTO 는 User + AuthUser 의 형태)
     *
     *  >
     * */
    // (default) 검색어가 없고 최신 가입 순 정렬인 경우 (default, 첫 페이지 로드 시)
    if((keyword == null || keyword.isBlank())
        && ("LATEST-SIGN-UP".equals(sortType) || sortType == null || sortType.isBlank())) {
      /* 정렬 - CustomRepository 를 사용하지 않는 경우의 정렬 */
      Sort sortKey = Sort.by(Sort.Direction.DESC, "regDate");

      /* Pageable */
      Pageable pageable = PageRequest.of(pageNo - 1, qty, sortKey);

      // 페이지네이션 1) 전체 유저 페이징 하여 가져오기
      Page<User> allUser = userRepository.findAll(pageable);

      // 페이지네이션 1.1) 이번 페이지의 유저 ID 만 가져오기
      List<User> content = allUser.getContent();

      // 페이지네이션 2) 전체 유저의 userId 모으기
      // List<Long> allIds = allUser.stream().map(User::getUserId).toList();

      // 페이지네이션 2.1) 이번 페이지의 유저 ID 만 모으기
      List<Long> allIds = content.stream().map(User::getUserId).toList();

      // 페이지네이션 3) userId 와 매핑되는 권한정보를 한 번에 가져오기
       // allIds 가 없는 경우 권한조회 건너뛰기
      Map<Long, List<AuthUserDTO>> authMap = allIds.isEmpty() ? Map.of() : authUserRepository
          .findByUserIdIn(allIds)
          .stream()
          .collect(Collectors.groupingBy(
              AuthUser::getUserId,
              Collectors.mapping(this::convertEntityToAuthDTO, Collectors.toList())
          )
        );

      // 페이지네이션 4) User Entity, AuthUserDTO 를 UserDTO 로 변환
      Page<UserDTO> pgList = allUser.map(user
          -> convertEntityToUserDTO(user, authMap.getOrDefault(user.getUserId(), List.of())));

      return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
    }

      // 검색어가 없고 최신 로그인 순 정렬인 경우
      else if((keyword == null || keyword.isBlank()) && "LATEST-LOGIN".equals(sortType)){
        /* 정렬 - CustomRepository 를 사용하지 않는 경우의 정렬 */
        Sort sortKey = Sort.by(Sort.Direction.DESC, "lastLogin");

        /* Pageable */
        Pageable pageable = PageRequest.of(pageNo - 1, qty, sortKey);

        // 페이지네이션 1) 전체 유저 페이징 하여 가져오기
        Page<User> allUser = userRepository.findAll(pageable);

        // 페이지네이션 1.1) 이번 페이지의 유저 ID 만 가져오기 위한 초기화
        List<User> content = allUser.getContent();

        // 페이지네이션 2) 전체 유저의 userId 모으기
        // List<Long> allIds = allUser.stream().map(User::getUserId).toList();

        // 페이지네이션 2.1) 이번 페이지의 유저 ID 만 모으기
        List<Long> allIds = content.stream().map(User::getUserId).toList();

        // 페이지네이션 3) userId 와 매핑되는 권한정보를 한 번에 가져오기
        // allIds 가 없는 경우 권한조회 건너뛰기
        Map<Long, List<AuthUserDTO>> authMap = allIds.isEmpty() ? Map.of() : authUserRepository
            .findByUserIdIn(allIds)
            .stream()
            .collect(Collectors.groupingBy(
                    AuthUser::getUserId,
                    Collectors.mapping(this::convertEntityToAuthDTO, Collectors.toList())
                )
            );

        // 페이지네이션 4) User Entity, AuthUserDTO 를 UserDTO 로 변환
        Page<UserDTO> pgList = allUser.map(user
            -> convertEntityToUserDTO(user, authMap.getOrDefault(user.getUserId(), List.of()))
        );

        return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
    }
      // 검색어가 있는 경우
      else if(keyword != null && !keyword.isBlank()) {
        // 정렬
        Sort sortKey = mapSortOfUser(sortType);
        // Pageable
        Pageable pageable = PageRequest.of(pageNo - 1, qty, sortKey);

        // 페이지네이션 1) 전체 유저 페이징하여 가져오기
        Page<User> allUser = userRepository.pageUser(keyword, pageable);

        // 페이지네이션 1.1) 이번 페이지의 userId 만 가져오기 위한 초기화
        List<User> content = allUser.getContent();

        // 페이지네이션 2) 이번 페이지의 userId 만 모으기
        List<Long> allIds = content.stream().map(User::getUserId).toList();

        // 페이지네이션 3) userId 로 권한 조회 하여 매핑
        Map<Long, List<AuthUserDTO>> authMap = allIds.isEmpty() ? Map.of() : authUserRepository
            .findByUserIdIn(allIds)
            .stream()
            .collect(Collectors.groupingBy(
              AuthUser::getUserId,
              Collectors.mapping(this::convertEntityToAuthDTO, Collectors.toList())
            )
          );

        // 페이지네이션 4) User Entity 와 AuthUserDTO 를 UserDTO 로 변환
        Page<UserDTO> pgList = allUser.map(user
            -> convertEntityToUserDTO(user, authMap.getOrDefault(user.getUserId(), List.of()))
        );

        return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
    }

    return null;
  }


  /* mapSortOfUser() - 유저 정렬 화이트리스트 매핑
  *
  *   > View 의 Value (토큰) 를 Sort 와 매핑시키는 헬퍼 함수로 UserCustomRepository 의
  *    pageableOfSortInPageUser() 으로 Sort.by(...) 의 값을 넘겨주는 역할
  *
  *   > properties 로 적힌 문자열을 pageableOfSortInPageUser() 에서 받기 때문에
  *    pageableOfSortInPageUser() 에서는 properties 를 적어야 함
  *
  *   > e.g., mapSortOfUser() 에서 return Sort.by(Sort.Direction.DESC, "LATEST-SIGN-UP");
  *    이라고 적었다면 pageableOfSortInPageUser() 에서 아래와 같은 추가 매핑 코드를 작성해야 함
  *
  *    String prop = switch (o.getProperty()) {
          case "LATEST-SIGN-UP" -> "regDate";
          case "LATEST-LOGIN"   -> "lastLogin";
          default               -> o.getProperty();
       };
  *
  * */
  private Sort mapSortOfUser(String sortType){
    switch(sortType){
      case "LATEST-LOGIN":
        return Sort.by(new Sort.Order(Sort.Direction.DESC, "lastLogin").nullsLast());

      default:
        break;
    }

    // 기본값 "LATEST-SIGN-UP"
    return Sort.by(Sort.Direction.DESC, "regDate");
  }



  /** pageQna() - 전체 유저 보기 클릭 시 로드되는 페이지의 페이지네이션을 위한 메서드
   *
   * */
  private AdminPagingHandler<CustomerIqBoardDTO> pageQna(String frag, int pageNo, int qty, String sortType, String keyword, String filterType){
    // 에러 방지 초기화
     // pageNo
    if(pageNo < 1){ pageNo = 1; }
     // qty
    if(qty <= 0){ qty = 10; }


    /* 페이지네이션 - 검색어가 없고 미등록 답변 보기를 클릭하지 않은 경우 */
    if((keyword == null || keyword.isBlank()) && filterType.isEmpty()){
      /* 정렬 - CustomRepository 를 사용하지 않는 경우의 default 정렬 */
      Sort sortKey = Sort.by(Sort.Direction.DESC, "regDate");

      /* Pageable */
      Pageable pageable = PageRequest.of(pageNo - 1, qty, sortKey);

      // frag 에 따라 분기
      switch(frag){
        case "qna-payment" -> {
          Page<CustomerIqBoardDTO> pgList = customerIqBoardRepository.findByCategory("결제", pageable).map(this::convertEntityToDto);
          
          return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
        }

        case "qna-cancel" -> {
          Page<CustomerIqBoardDTO> pgList = customerIqBoardRepository.findByCategory("환불", pageable).map(this::convertEntityToDto);

          return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
        }

        case "qna-facilities" -> {
          Page<CustomerIqBoardDTO> pgList = customerIqBoardRepository.findByCategory("시설", pageable).map(this::convertEntityToDto);

          return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
        }

        case "qna-service" -> {
          Page<CustomerIqBoardDTO> pgList = customerIqBoardRepository.findByCategory("서비스", pageable).map(this::convertEntityToDto);

          return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
        }

        case "qna-others" -> {
          Page<CustomerIqBoardDTO> pgList = customerIqBoardRepository.findByCategory("기타", pageable).map(this::convertEntityToDto);

          return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
        }

        default -> { break; }
      }
    } // if fin

      /* 페이지네이션 - 검색어가 없고 미등록 답변 보기를 클릭한 경우 */
      else if((keyword == null || keyword.isBlank()) && !filterType.isBlank()) {
        Pageable pageable = PageRequest.of(pageNo - 1, qty);

        switch (frag){
          case "qna-payment" -> {
            Page<CustomerIqBoardDTO> pgList
                = customerIqBoardRepository.pageQna("결제", keyword, pageable).map(this::convertEntityToDto);

            return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
          }

          case "qna-cancel" -> {
            Page<CustomerIqBoardDTO> pgList
                = customerIqBoardRepository.pageQna("환불", keyword, pageable).map(this::convertEntityToDto);

            return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
          }

          case "qna-facilities" -> {
            Page<CustomerIqBoardDTO> pgList
                = customerIqBoardRepository.pageQna("시설", keyword, pageable).map(this::convertEntityToDto);

            return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
          }

          case "qna-service" -> {
            Page<CustomerIqBoardDTO> pgList
                = customerIqBoardRepository.pageQna("서비스", keyword, pageable).map(this::convertEntityToDto);

            return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
          }

          case "qna-others" -> {
            Page<CustomerIqBoardDTO> pgList
                = customerIqBoardRepository.pageQna("기타", keyword, pageable).map(this::convertEntityToDto);

            return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
          }

          default -> { break; }
        }
      }
        /* 페이지네이션 - 검색어만 있고 미등록 답변 보기가 클릭되지 않은 경우 */
        else if((keyword != null && !keyword.isBlank()) && filterType.isBlank()){
          Pageable pageable = PageRequest.of(pageNo - 1, qty);

          switch(frag){
            case "qna-payment" -> {
              Page<CustomerIqBoardDTO> pgList
                  = customerIqBoardRepository.pageQnaKw("결제", keyword, pageable).map(this::convertEntityToDto);

              return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
            }

            case "qna-cancel" -> {
              Page<CustomerIqBoardDTO> pgList
                  = customerIqBoardRepository.pageQnaKw("환불", keyword, pageable).map(this::convertEntityToDto);

              return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
            }

            case "qna-facilities" -> {
              Page<CustomerIqBoardDTO> pgList
                  = customerIqBoardRepository.pageQnaKw("시설", keyword, pageable).map(this::convertEntityToDto);

              return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
            }

            case "qna-service" -> {
              Page<CustomerIqBoardDTO> pgList
                  = customerIqBoardRepository.pageQnaKw("서비스", keyword, pageable).map(this::convertEntityToDto);

              return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
            }

            case "qna-others" -> {
              Page<CustomerIqBoardDTO> pgList
                  = customerIqBoardRepository.pageQnaKw("기타", keyword, pageable).map(this::convertEntityToDto);


              return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
            }

            default -> { break; }
          }
      }

        /* 페이지네이션 - 검색어와 미등록 답변 보기가 모두 적용된 경우 */
        else if((keyword != null && !keyword.isBlank()) && !filterType.isBlank()){
          Pageable pageable = PageRequest.of(pageNo - 1, qty);
          
          // frag 따라 분기
          switch (frag){
            case "qna-payment" -> {
              Page<CustomerIqBoardDTO> pgList
                  = customerIqBoardRepository.pageQnaKwAns("결제", keyword, pageable).map(this::convertEntityToDto);


              return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
            }

            case "qna-cancel" -> {
              Page<CustomerIqBoardDTO> pgList
                  = customerIqBoardRepository.pageQnaKwAns("환불", keyword, pageable).map(this::convertEntityToDto);

              return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
            }

            case "qna-facilities" -> {
              Page<CustomerIqBoardDTO> pgList
                  = customerIqBoardRepository.pageQnaKwAns("시설", keyword, pageable).map(this::convertEntityToDto);

              return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
            }

            case "qna-service" -> {
              Page<CustomerIqBoardDTO> pgList
                  = customerIqBoardRepository.pageQnaKwAns("서비스", keyword, pageable).map(this::convertEntityToDto);

              return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
            }

            case "qna-others" -> {
              Page<CustomerIqBoardDTO> pgList
                  = customerIqBoardRepository.pageQnaKwAns("기타", keyword, pageable).map(this::convertEntityToDto);

              return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
            }

            default -> { break; }
           }

      }

    return null;
  }



  /* paymentReceipt() - 결제 완료 내역 클릭 시 로드되는 페이지의 페이지네이션을 위한 메서드 */
  public AdminPagingHandler<PaymentReceiptDTO> pagePaymentReceipt(String frag, int pageNo, int qty, String sortType, String keyword) {
    // 에러 방지 초기화
     // pageNo
    if(pageNo < 1) { pageNo = 1; }
     // qty
    if(qty <= 0) { qty = 10; }


    /* 페이지네이션 - 검색어가 없는 경우 */
    if(keyword == null || keyword.isBlank()){
      // 정렬 (default)
      Sort sortKey = Sort.by(Sort.Direction.DESC, "approvedAt");

      // Pageable
      Pageable pageable = PageRequest.of(pageNo - 1, qty, sortKey);
      
      // 페이지네이션 - 해당 Repository
      Page<PaymentReceiptDTO> pgList
          = paymentReceiptRepository.findAll(pageable).map(this::convertPaymentReceiptEntityToPaymentReceiptDto);

      return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
    }
      /* 페이지네이션 - 검색어가 있는 경우 */
      else if(keyword != null && !keyword.isBlank()){
        Pageable pageable = PageRequest.of(pageNo - 1, qty);

        Page<PaymentReceiptDTO> pgList
            = paymentReceiptRepository.pagePaymentReceipt(pageable, keyword).map(this::convertPaymentReceiptEntityToPaymentReceiptDto);

        return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
    }

    return null;
  }



  /* pagePaymentCanceled() - 환불 완료 내역 조회 시 로드되는 페이지의 페이지네이션을 위한 메서드 */
  public AdminPagingHandler<PaymentCancelDTO> pagePaymentCanceled(String frag, int pageNo, int qty, String sortType, String keyword){
    // 에러 방지 초기화
     // pageNo
    if(pageNo < 1) { pageNo = 1; }
     // qty
    if(qty <= 0) { qty = 10; }


    // 페이지네이션 - 검색어가 없는 경우
    if(keyword == null || keyword.isBlank()){
      // 정렬 (default)
      Sort sortKey = Sort.by(Sort.Direction.DESC, "paymentCancelId");

      // Pageable
      Pageable pageable = PageRequest.of(pageNo - 1, qty, sortKey);

      // 페이지네이션
      Page<PaymentCancelDTO> pgList = paymentCancelRepository
          .findAll(pageable)
          .map(this::convertPaymentCancelEntityToPaymentCacnelDto);

      return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
    }
      /* 페이지네이션 - 검색어가 있는 경우 */
      else if(keyword != null && !keyword.isBlank()){
        Pageable pageable = PageRequest.of(pageNo - 1, qty);

        Page<PaymentCancelDTO> pgList = paymentCancelRepository
            .pagePaymentCanceled(pageable, keyword).map(this::convertPaymentCancelEntityToPaymentCacnelDto);

        return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
    }

    return null;
  }


  /** pageReview() - 리뷰 관리의 신고 내역을 클릭했을 때, 로드되는 페이지의 페이지네이션을 위한 메서드 */
  private AdminPagingHandler<ReviewDTO> pageReview(String frag, int pageNo, int qty, String sortType, String keyword){
    // 에러 방지 초기화
     // pageNo
    if(pageNo < 1){ pageNo = 1; }
     // qty
    if(qty <= 0){ qty = 10; }

    /** (default) 페이지네이션 - 검색어가 없고 최신 가입 순 정렬인 경우 */
    if((keyword == null || keyword.isBlank())
        && ("LATEST".equals(sortType) || sortType == null || sortType.isBlank())) {
      /* 정렬 - CustomRepository 를 사용하지 않는 경우의 정렬 */
      Sort sortKey = Sort.by(Sort.Direction.DESC, "createdAt");

      /* Pageable */
      Pageable pageable = PageRequest.of(pageNo - 1, qty, sortKey);

      // 페이지네이션
      Page<ReviewDTO> pgList = reviewRepository.reportedReview(pageable).map(this::convertReviewEntityToReviewDto);

      return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
    }

    // 검색어가 없고 최신 로그인 순 정렬인 경우
    else if((keyword == null || keyword.isBlank()) && "MOST-REPORTED".equals(sortType)){
      /* 정렬 - CustomRepository 를 사용하지 않는 경우의 정렬 */
      Sort sortKey = Sort.by(Sort.Direction.DESC, "reportedCount");

      /* Pageable */
      Pageable pageable = PageRequest.of(pageNo - 1, qty, sortKey);

      // 페이지네이션
      Page<ReviewDTO> pgList = reviewRepository.findAll(pageable).map(this::convertReviewEntityToReviewDto);

      return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
    }
      /* 검색어가 있는 경우 */
      else if(keyword != null && !keyword.isBlank()) {
        // 정렬
        Sort sortKey = mapSortOfUser(sortType);
        // Pageable
        Pageable pageable = PageRequest.of(pageNo - 1, qty, sortKey);

        // 페이지네이션
        Page<ReviewDTO> pgList = reviewRepository.pageReview(pageable, keyword).map(this::convertReviewEntityToReviewDto);

        return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
    }

    return null;
  }


  /* mapSortOfReview() - 유저 정렬 화이트리스트 매핑
  *
  *   > View 의 Value (토큰) 를 Sort 와 매핑시키는 헬퍼 함수로 ReviewCustomRepository 의
  *    pageableOfSortInPageReview() 로 Sort.by(...) 의 값을 넘겨주는 역할
  *
  *   > properties 로 적힌 문자열을 pageableOfSortInPageReview() 에서 받기 때문에
  *    pageableOfSortInPageReview() 에서는 properties 를 적어야 함
  *
  *   > e.g., mapSortOfReview() 에서 return Sort.by(Sort.Direction.DESC, "LATEST-SIGN-UP");
  *    이라고 적었다면 pageableOfSortInPageReview() 에서 아래와 같은 추가 매핑 코드를 작성해야 함
  *
  *    String prop = switch (o.getProperty()) {
          case "LATEST" -> "createdAt";
          case "MOST-REPORTED"   -> "reportedCount";
          default               -> o.getProperty();
       };
  *
  * */
  private Sort mapSortOfReview(String sortType){
    switch(sortType){
      case "MOST-REPORTED":
        return Sort.by(new Sort.Order(Sort.Direction.DESC, "reportedCount"));

      default:
        break;
    }

    // 기본값
    return Sort.by(Sort.Direction.DESC, "createdAt");
  }


  /* pageReservation() - 예약 내역 보기 클릭 시 로드되는 페이지의 페이지네이션을 위한 메서드  */
  private AdminPagingHandler<AdminReservationDTO> pageReservation(String frag, int pageNo, int qty, String sortType, String keyword){
    // 에러 방지 초기화
     // pageNo
    if(pageNo < 1){ pageNo = 1; }
     // qty
    if(qty <= 0) { qty = 10; }

    // 정렬 (default)
    Sort sortKey = Sort.by(Sort.Direction.DESC, "reservationRegDate");

    // Pageable
    Pageable pageable = PageRequest.of(pageNo - 1, qty, sortKey);

    // 페이지네이션
    Page<AdminReservationDTO> pgList = reservationRepository.findAdminReservations(pageable, keyword);

    return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
  }


  /* pageLodge() - 숙소 별 정보 클릭 시 로드되는 페이지를 위한 페이지네이션 */
  private AdminPagingHandler<AdminLodgeDTO> pageLodge(String frag, int pageNo, int qty, String sortType, String keyword){
    // 에러 방지 초기화
     // pageNo
    if(pageNo < 1){ pageNo = 1; }
     // qty
    if(qty <= 0) { qty = 10; }


    /* 페이지네이션 - 검색어가 없는 경우 */
    if(keyword == null || keyword.isBlank()){
      // (default) 정렬
      Sort sortKey = Sort.by(Sort.Direction.DESC, "lodgeId");

      // Pageable
      Pageable pageable = PageRequest.of(pageNo - 1, qty, sortKey);

      // 페이지네이션
      Page<AdminLodgeDTO> pgList = lodgeRepository.findAll(pageable).map(this::convertLodgeEntityToAdminLodgeDto);

      return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
    }
      /* 페이지네이션 - 검색어가 있는 경우 */
      else if(keyword != null && !keyword.isBlank()){
        // Pageable
        Pageable pageabble = PageRequest.of(pageNo - 1, qty);

        // 페이지네이션
        Page<AdminLodgeDTO> pgList = lodgeRepository.pageLodge(pageabble, keyword).map(this::convertLodgeEntityToAdminLodgeDto);

        return new AdminPagingHandler<>(pgList, pageNo, sortType, keyword, qty, frag);
    }

    return null;
  }



}



