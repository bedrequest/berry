package com.berry.project.controller;

import com.berry.project.dto.alarm.AlarmDTO;
import com.berry.project.dto.cupon.CuponDTO;
import com.berry.project.dto.lodge.LodgeOptionDTO;
import com.berry.project.dto.user.*;
import com.berry.project.handler.user.CoolSMSHandler;
import com.berry.project.handler.user.StarterMailHandler;
import com.berry.project.service.lodge.LodgeService;
import com.berry.project.service.payment.PaymentService;
import com.berry.project.service.user.DeactivatedUserService;
import com.berry.project.service.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user/*")
@PropertySource("classpath:/external-api.properties")
public class UserController {

  // service
  private final UserService userService;
  private final DeactivatedUserService deactivatedUserService;
  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
  private final StarterMailHandler starterMailHandler;
  private final PaymentService paymentService;
  private final LodgeService lodgeService;

  private final PasswordEncoder passwordEncoder;

  @Value("${kakao.client.key}")
  private String kakaoClientId;
  @Value("${naver.client.key}")
  private String naverClientId;
  @Value("${naver.secret.key}")
  private String naverSecretKey;
  @Value("${coolsms.key}")
  private String coolSmsApiKey;
  @Value("${coolsms.secret.key}")
  private String coolSmsSecretKey;
  @Value("${coolsms.fromNumber}")
  private String fromNumber;

  // -- 동기 --
  @GetMapping("/login")
  public void login(
      @RequestParam(name="redirectTo", required = false) String redirectTo,
      HttpServletRequest request,
      Model model
  ){

    String errorMessage = (String) request.getSession().getAttribute("errorMessage");
    log.info("Login fail errorMessage >> {}", errorMessage);

    model.addAttribute("errorMessage", errorMessage);

    if(errorMessage != null && errorMessage != ""){
      request.getSession().removeAttribute("errorMessage");
    }

  }

  @GetMapping("/signup")
  public String signup(Model model, @RequestParam(required = false) Boolean marketing){
    log.info("userTermOption >>> {}", marketing);

    model.addAttribute("userTermOption", marketing);
    return "/user/signup";

  }

  @GetMapping("/myPage")
  public void myPage(
      Principal principal,
      Model model,
      LodgeOptionDTO lodgeOptionDTO
  ){
    // web 은 email, oauth2 는 uid
    String username = principal.getName();
    log.info("myPage Principal username >>> {}", username);
    UserDTO userDTO = userService.getUserInfo(username);
    
    // 예약 내역 가져오기
    List<MyPageReservationDTO> reservationList = userService.getReservationList(userDTO.getUserId());
    log.info("reservationList >> {}",reservationList);
    log.info("현재 로그인한 userId >> {}",userDTO.getUserId());

    List<MyPageReservationDTO> reservationPresentList = new ArrayList<>();

    // endDate 전 값만 보내주기
    for(MyPageReservationDTO mrDTO : reservationList){
      // 요소를 비교해서 아직 이용을 하지 않은 숙박이 있을 경우
      if(LocalDateTime.now().isBefore(mrDTO.getEndDate()) && mrDTO.getBookingStatus().equals("DONE")){
        reservationPresentList.add(mrDTO);
      }
    }
    log.info("이용 전 숙박내역 >>>>> {}", reservationPresentList);
    
    // 알림 내역 가져오기
    List<AlarmDTO> alarmList = userService.getAlarmList(userDTO.getUserId());
    log.info("alarmList >>> {}", alarmList);
    
    // 보유 쿠폰 내역 가져오기
    List<CuponDTO> cuponList = paymentService.getCuponList(userDTO.getUserId());
    
    // 북마크 내역 가져오기
    List<BookmarkLodgeDTO> bookmarkLodgeList = userService.getBookmarkLodgeList(userDTO.getUserId());
    log.info("bookmarkLodgeList > {}", bookmarkLodgeList);

    model.addAttribute("reservationPresentList", reservationPresentList);
    model.addAttribute("userDTO", userDTO);
    model.addAttribute("reservationList", reservationList);
    model.addAttribute("alarmList", alarmList);
    model.addAttribute("cuponList", cuponList);
    model.addAttribute("lodgeOption", lodgeOptionDTO);
    model.addAttribute("bookmarkLodgeList", bookmarkLodgeList);
  }

  

  // 1. 회원탈퇴(비활성전환) =============================================
  @PostMapping("/deactivatedTransferUser")
  public String deactivatedTransferUser(
      DeactivatedUserDTO deactivatedUserDTO,
      HttpServletRequest request,
      HttpServletResponse response,
      RedirectAttributes redirectAttributes
  ) throws ServletException {

    UserDTO userDTO = userService.getUserFindById(deactivatedUserDTO.getUserId());

    if(userDTO == null){
      return "redirect:/";
    }

    deactivatedUserDTO.setDUserEmail(userDTO.getUserEmail());
    deactivatedUserDTO.setDUserPhone(userDTO.getUserPhone());
    deactivatedUserDTO.setDUserName(userDTO.getUserName());
    log.info("DeactivatedUserDTO >>>> {}", deactivatedUserDTO);

    deactivatedUserService.registerDeactivatedUser(deactivatedUserDTO);
    request.logout();

    Cookie cookie = new Cookie("JSESSIONID", null);
    cookie.setMaxAge(0); // 쿠키 만료
    cookie.setPath("/"); // 쿠키 경로 설정
    response.addCookie(cookie); // 쿠키 추가 (삭제)
    redirectAttributes.addFlashAttribute("membershipWithdrawal", "ok");
    log.info("회원탈퇴 성공");

    return "redirect:/user/login";
  }
  // 2. 회원정보 수정 =============================================
  @PostMapping("/userInfoUpdate")
  public String userInfoUpdate(
      UserDTO userDTO,
      HttpServletRequest request,
      HttpServletResponse response
      ) throws ServletException {
    log.info("userUpdateInfo userDTO > {}", userDTO);
    // 변경 후 이메일
    String updateEmail = userDTO.getUserEmail();
    log.info("updateEmail > {}", updateEmail);

    // 변경 전 이메일
    String beforeEmail = userService.userInfoUpdate(userDTO);
    log.info("beforeEmail > {}", beforeEmail);

    boolean isChangeEmail = false;
    if(!updateEmail.equals(beforeEmail)){
      // 이메일이 변경 되었다면 logout 시키기
      request.logout();

      Cookie cookie = new Cookie("JSESSIONID", null);
      cookie.setMaxAge(0); // 쿠키 만료
      cookie.setPath("/"); // 쿠키 경로 설정
      response.addCookie(cookie); // 쿠키 추가 (삭제)
      isChangeEmail = true;
    }

    return isChangeEmail ?  "redirect:/user/login" : "redirect:/user/myPage";
  }



  // 3. 회원가입 =============================================
  @PostMapping("/signup")
  public String signup(
      UserDTO userDTO,
      @RequestParam boolean isEmailCertified,
      @RequestParam boolean isMobileCertified
  ){
    userDTO.setEmailCertified(isEmailCertified);
    userDTO.setMobileCertified(isMobileCertified);

    log.info("signup userDTO {}", userDTO);

    String uuid = UUID.randomUUID().toString();
    userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
    userDTO.setProvider("web");
    userDTO.setUserUid(uuid);
    userDTO.setCustomerKey(uuid + "_" + userDTO.getProvider());
    userDTO.setUserTermOption(userDTO.isUserTermOption());

    String birthYear = userDTO.getBirthday().substring(0, 4);


    // 생년월일 현재랑 비교해서 성인인지 아닌지 확인하기.
    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
    String formatDate = today.format(formatter);
    int presentDate = Integer.parseInt(formatDate); // 현재년도
    int userBirthDate = Integer.parseInt(birthYear); // 사용자의 생일 (yyyy)
    log.info(">> web presentDate >> {}", presentDate);
    log.info(">> web previousDate >> {}", userBirthDate);

    if(presentDate - userBirthDate >= 19){
      userDTO.setAdult(true);
    }else{
      userDTO.setAdult(false);
    }

    /** userService.registerUser(userDTO)
     *
     *  > YSL, registerUser(userDTO) 부분에서 쿠폰 발급 코드 추가
     * */
    Long userId = userService.registerUser(userDTO);

    return (userId > 0) ? "redirect:/user/login" : "/user/signup";
  }


  // 4. 로그아웃
  @GetMapping("/logout")
  public String customLogout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication,
      RestTemplate restTemplate
      ) throws ServletException {

    String email = request.getUserPrincipal().getName();
    log.info("web Email? >>>> {}", email);
    request.logout(); // 기존 웹 사용자 로그아웃
    log.info("현재 로그인 된 clientId > {}",kakaoClientId);

    // OAuth 사용자 로그아웃
    if(authentication instanceof OAuth2AuthenticationToken authenticationToken){

      // 로그아웃을 시도하는 계정의 provider
      String provider = authenticationToken.getAuthorizedClientRegistrationId();

      // 로그아웃을 시도하는 provider 의 accessToken 값 가져오기
      // access token 조회
      OAuth2AuthorizedClient oAuth2AuthorizedClient =
          oAuth2AuthorizedClientService.loadAuthorizedClient(provider, authenticationToken.getName());

      String accessToken = oAuth2AuthorizedClient.getAccessToken().getTokenValue(); // 토큰 값

      log.info("Logout Provider >> {}", provider);
      log.info("token >> {}", accessToken);

      switch (provider){
        case "kakao":
          // 접근해야 하는 url
          // https://kauth.kakao.com/oauth/logout?client_id={kakao.client.id}&logout_redirect_uri=http://localhost:8088/
          String kakaoUrl = "kauth.kakao.com/oauth/logout";
          String redirectUrl = "http://localhost:8088/";
          return "redirect:https://" + kakaoUrl + "?client_id=" + kakaoClientId + "&logout_redirect_uri=" + redirectUrl;

        case "naver":
          // 접근해야 하는 url
          // https://nid.naver.com/oauth2.0/token?grant_type=delete&client_id={naver.client.id}&client_secret={naver.secret.id}&access_token={accessToken}&service_provider=NAVER
          String naverUrl = "nid.naver.com/oauth2.0/token?grant_type=delete";

          String naverLogoutUrl = "http://" + naverUrl + "&client_id=" + naverClientId
              + "&client_secret=" + naverSecretKey + "&access_token=" + accessToken + "&service_provider=NAVER"; // 네이버는 따로 uri 를 주지 않기 때문에 내부처리

          restTemplate.getForObject(naverLogoutUrl, String.class);

          return "redirect:/"; // 로그아웃 후 루트로 이동

        case "google":
          // 접근해야 하는 url
          // https://accounts.google.com/o/oauth2/revoke?token={YOUR_ACCESS_TOKEN}

          String googleUrl = "accounts.google.com/o/oauth2/revoke";

          String googleLogoutUrl = "https://" + googleUrl + "?token=" + accessToken;

          restTemplate.getForObject(googleLogoutUrl, String.class);

          return "redirect:/";

        default :
          return "redirect:/";
      }

    }

    return "redirect:/";
  }
  // 5. 비밀번호 변경
  @PostMapping("changePassword")
  public String changePassword(ChangePwDTO changePwDTO, RedirectAttributes redirectAttributes){
    log.info("changePwDTO >>>> {}", changePwDTO);
    // 이전 비밀번호와 변경되는 비밀번호 비교...
    boolean checkedPassword = passwordEncoder.matches(
        changePwDTO.getCurrentPassword(),
        userService.getUserFindById(changePwDTO.getUserId()).getPassword()
    );
    if(checkedPassword){
      changePwDTO.setChangePassword(passwordEncoder.encode(changePwDTO.getChangePassword()));
      userService.updatePassword(changePwDTO.getChangePassword(), changePwDTO.getUserId());
      // 비밀번호 변경 시 로그아웃
      return "redirect:/user/logout";
    }else{
      // addFlashAttribute > url 뒤에 parameter 숨김
      // addAttribute > ? 달고감
      redirectAttributes.addFlashAttribute("checkedPassword", "fail");
      // 비밀번호 변경 실패시 문구 출력
      return "redirect:/user/myPage";
    }

  }


  // --비동기--
  
  // 비밀번호 재설정 이메일 확인
  @GetMapping("/findWebUserEmail/{email}")
  @ResponseBody
  public String findWebUserEmail(@PathVariable("email") String userEmail){

    log.info("inputEmail > {}", userEmail);
    Long userId = userService.findWebUserEmail(userEmail);

    return userId > 0 ? userId.toString() : "fail";
  }
  
  // 비밀번호 재설정
  @GetMapping("/resetPassword/{userId}")
  @ResponseBody
  public String resetPassword(@PathVariable("userId") Long userId){

    UserDTO userDTO = userService.getUserFindById(userId);
    String password = starterMailHandler.generateRandomMixStr(15);
    String encodePassword = passwordEncoder.encode(password);

    if(userDTO.getUserId() > 0 && password != null){
      starterMailHandler.sendPasswordHtml(userDTO.getUserEmail(), password);
      userService.updatePassword(encodePassword, userDTO.getUserId());

      return "ok";
    }else{

      return "fail";
    }
  }


  // 휴대폰 인증
  @GetMapping("/getCertifiedNumber/{myPageUserId}")
  @ResponseBody
  public String getCertifiedNumber(@PathVariable("myPageUserId") Long userId){

    CoolSMSHandler coolSMSHandler = new CoolSMSHandler();

    UserDTO userDTO = userService.getUserFindById(userId); // 객체 불러오기

    // Math.random 보다 보안이 더 좋은 SecureRandom을 사용해보자.
    String secureNumber = coolSMSHandler.createSecureNumber(6);
    log.info("secureNumber >>> {}", secureNumber);

    coolSMSHandler.sendCertifiedNumber(userDTO.getUserPhone(), secureNumber, coolSmsApiKey, coolSmsSecretKey, fromNumber);

    return secureNumber != null ? secureNumber : "fail";
  }

  @GetMapping("/certifiedPhoneOk/{myPageUserId}")
  @ResponseBody
  public String certifiePhoneOk(@PathVariable("myPageUserId") Long userId){

    Long isOk = userService.updateMobileCertified(userId);
    return isOk > 0 ? "ok" : "fail";
  }

  // 이메일 인증
  @ResponseBody
  @GetMapping("/getCertifiedCode/{myPageUserId}")
  public String getCertifiedCode(@PathVariable("myPageUserId") Long userId){

    UserDTO userDTO = userService.getUserFindById(userId);

    String secureCode = starterMailHandler.generateRandomMixStr(10);
    log.info("secureCode >>> {}", secureCode);
    starterMailHandler.sendCertifiedCodeHtml(userDTO.getUserEmail(), secureCode);

    return secureCode != null ? secureCode : "fail";
  }

  @GetMapping("/certifiedEmailOk/{myPageUserId}")
  @ResponseBody
  public String certifiedEmailOk(@PathVariable("myPageUserId") Long userId){

    Long isOk = userService.updateEmailCertified(userId);
    return isOk > 0 ? "ok" : "fail";
  }

  // 아이디 중복검사
  @GetMapping("/duplicateCheckedEmail/{userEmail}")
  @ResponseBody
  public String duplicateCheckedEmail(@PathVariable("userEmail") String userEmail){
    log.info("duplicateCheckedEmail");
    log.info("userEmail >>>>> {}", userEmail);
    if(userEmail.equals("")){
      return "fail";
    }
    Long isOk = userService.isDuplicateUser(userEmail);
    log.info("duplicateCheckedEmail isOk >>>> {}",isOk);

    return isOk == 0 ? "ok" : "fail";

  }

  // 북마크 등록
  @ResponseBody
  @PostMapping("/toggleBookmark")
  public String toggleBookmark(@RequestBody UserBookmarkDTO userBookmarkDTO){
    log.info("userBookmarkDTO >> {}",userBookmarkDTO);
    Long isOk = userService.toggleBookmark(userBookmarkDTO);

    return isOk > 0 ? "1" : "0";

  }

  // 알림 리스트
  @ResponseBody
  @GetMapping("/getAlarmList/{userId}")
  public List<AlarmDTO> getAlarmList(@PathVariable Long userId){

    List<AlarmDTO> alarmList = userService.getAlarmList(userId);

    return alarmList != null ? alarmList : Collections.emptyList();
  }
  
  // 회원가입 이메일 인증
  @ResponseBody
  @GetMapping("/getSignInCertifiedCode/{email}")
  public String getSignInCertifiedCode(@PathVariable("email") String email){


    String secureCode = starterMailHandler.generateRandomMixStr(10);
    starterMailHandler.sendCertifiedCodeHtml(email, secureCode);

    return secureCode != null ? secureCode : "fail";
  }
  
  // 회원가입 모바일 인증
  @GetMapping("/getSignInCertifiedNumber/{phoneNumber}")
  @ResponseBody
  public String getSignInCertifiedNumber(@PathVariable("phoneNumber") String phoneNumber){

    CoolSMSHandler coolSMSHandler = new CoolSMSHandler();

    // Math.random 보다 보안이 더 좋은 SecureRandom을 사용해보자.
    String secureNumber = coolSMSHandler.createSecureNumber(6);

    coolSMSHandler.sendCertifiedNumber(phoneNumber, secureNumber, coolSmsApiKey, coolSmsSecretKey, fromNumber);

    return secureNumber != null ? secureNumber : "fail";
  }

}
