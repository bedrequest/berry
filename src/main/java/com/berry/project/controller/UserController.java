package com.berry.project.controller;

import com.berry.project.dto.user.DeactivatedUserDTO;
import com.berry.project.dto.user.UserDTO;
import com.berry.project.service.user.DeactivatedUserService;
import com.berry.project.service.user.UserService;
import jakarta.servlet.ServletException;
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

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

  private final PasswordEncoder passwordEncoder;

  @Value("${kakao.client.key}")
  private String kakaoClientId;
  @Value("${naver.client.key}")
  private String naverClientId;
  @Value("${naver.secret.key}")
  private String naverSecretKey;


  @GetMapping("/login")
  public void login(@RequestParam(name="redirectTo", required = false) String redirectTo){}

  @GetMapping("/signup")
  public String signup(Model model, @RequestParam(required = false) Boolean marketing){
    log.info("userTermOption >>> {}", marketing);

    model.addAttribute("userTermOption", marketing);
    return "/user/signup";

  }

  @GetMapping("/myPage")
  public void myPage(Principal principal, Model model){
    // web 은 email, oauth2 는 uid
    String username = principal.getName();
    log.info("myPage Principal username >>> {}", username);

    //--------------------- 윗부분 수정. 같고오는

    model.addAttribute("userDTO", userService.getUserInfo(username));
  }

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

  // 회원탈퇴(비활성전환) =============================================
  @PostMapping("/deactivatedTransferUser")
  public String deactivatedTransferUser(
      DeactivatedUserDTO deactivatedUserDTO,
      HttpServletRequest request
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

    return "redirect:/user/logout";
  }
  // 회원정보 수정 =============================================
  @PostMapping("/userInfoUpdate")
  public String userInfoUpdate(UserDTO userDTO){

    userService.userInfoUpadate(userDTO);

    return "redirect:/user/myPage";
  }



  // 회원가입 =============================================
  @PostMapping("/signup")
  public String signup(UserDTO userDTO){
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
    userDTO.setEmailCertified(false);


    /** userService.registerUser(userDTO)
     *
     *  > YSL, registerUser(userDTO) 부분에서 쿠폰 발급 코드 추가
     * */
    Long userId = userService.registerUser(userDTO);

    return (userId > 0) ? "redirect:/" : "/user/join";
  }



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

}
