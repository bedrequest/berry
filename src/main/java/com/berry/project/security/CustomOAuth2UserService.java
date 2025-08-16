package com.berry.project.security;

import com.berry.project.dto.user.UserDTO;
import com.berry.project.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserService userService;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    OAuth2User oAuth2User;

    oAuth2User = super.loadUser(userRequest);

    // platform 이름 들어오는지 확인
    String provider = userRequest.getClientRegistration().getRegistrationId();

    OAuth2AccessToken oAuth2AccessToken = userRequest.getAccessToken();
    String tokenValue = oAuth2AccessToken.getTokenValue();

    Map<String, Object> attributes = oAuth2User.getAttributes();

    // 요청한 정보가 들어오는지 log 확인
    log.info(">> attributes > {}", attributes);

    // kakao 하나로만 처리 했을 경우

//      log.info(">> kakao registrationId > {}", provider);
//
//      Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
//      Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile"); // profile image
//      String userName = (String) kakaoAccount.get("name"); // user_name
//      String userEmail = (String) kakaoAccount.get("email"); // user_email
//      String userPhone = (String) kakaoAccount.get("phone_number"); // user_phone
//      String birthYear = (String) kakaoAccount.get("birthyear"); // year
//      String birthDay = (String) kakaoAccount.get("birthday"); // month,day
//      String userUid = oAuth2User.getName(); // Uid
//      log.info("kakao login------------------------");
//      log.info("kakao info >> {}",kakaoAccount);
//      log.info(">> user_uid >> {}", userUid); // Uid
//      log.info(">> profile >> {}",profile);
//      log.info(">> user_name >> {}",userName);
//      log.info(">> user_email >> {}",userEmail);
//      log.info(">> user_phone >> {}",userPhone);
//      log.info(">> year >> {}",birthYear);
//      log.info(">> day >> {}",birthDay);
//      log.info("kakao login------------------------");
//
//      // 로그인 시 들어오는 데이터 DB 에 저장하기
//      UserDTO userDTO = new UserDTO();
//      userDTO.setUserUid(userUid);
//      userDTO.setUserName(userName);
//      userDTO.setUserEmail(userEmail);
//      userDTO.setUserPhone(userPhone);
//      userDTO.setProvider(provider);
//      userDTO.setBirthday(birthYear + birthDay);
//
//      // 생년월일 현재랑 비교해서 성인인지 아닌지 확인하기.
//      LocalDate today = LocalDate.now();
//      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
//      String formatDate = today.format(formatter);
//      int presentDate = Integer.parseInt(formatDate); // 현재년도
//      int previousDate = Integer.parseInt(birthYear); // 사용자의 생일 (yyyy)
//      log.info(">> presentDate >> {}", presentDate);
//      log.info(">> previousDate >> {}", previousDate);
//
//
//      if(presentDate - previousDate >= 19){
//        userDTO.setAdult(true);
//      }
//      String customerKey = userUid + "_" + provider;
//      userDTO.setCustomerKey(customerKey); // 결제시 사용 할 키
//      userDTO.setEmailCertified(true); // 소셜 로그인은 email 인증이 따로 필요하지않음.
//
//      // 프로필 이미지는 차후에...
//
//      log.info("kakao userDTO > {}", userDTO);
//
//      // 먼저 User 테이블에 소셜 계정 값이 있는지 확인.
//      UserDTO isDuplicateUser = userService.isDuplicateUser(userUid);
//      log.info(">> isDuplicateUser >> {}", isDuplicateUser);
//
//      if(isDuplicateUser == null){ // User 테이블에 계정 값이 없을 경우
//        userService.insertOauthUser(userDTO);
//      }
//
//      return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
//          oAuth2User.getAttributes(), "id");
    // new SimpleGrantedAuthority("ROLE_USER") > oauth2 로 로그인한 사용자의 권한 부여
    // oAuth2User.getAttributes() 사용자 정보
    // String (id) > principal.getName()의 반환 값 id 는 getAttributes() 안에 있는 id 값...


    // kakao, google, naver 3개 동시에 처리
    switch (provider) {
      case "kakao":
        return providerKakao(attributes, oAuth2User, provider);
      case "naver":
        return providerNaver(attributes, oAuth2User, provider);
      case "google":
        return providerGoogle(attributes, oAuth2User, provider, tokenValue);
      default:
        throw new OAuth2AuthenticationException("Unknown Provider : " + provider);
    }

  }

  // -------------------------- GOOGLE -----------------------------
  private OAuth2User providerGoogle(Map<String, Object> attributes, OAuth2User oAuth2User, String provider, String tokenValue) {

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setBearerAuth(tokenValue);

    HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
    RestTemplate restTemplate = new RestTemplate();

    // google people api url
    String url = "https://people.googleapis.com/v1/people/me?personFields=birthdays,phoneNumbers";

    ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

    Map<String, Object> peopleAttributes = response.getBody(); // people Api 에서 주는 응답객체 저장 (생일, 휴대전화번호)

    log.info("google people Api Data >> {}", peopleAttributes);


    String userUid = (String) attributes.get("sub");
    String userName = (String) attributes.get("name");
    String userEmail = (String) attributes.get("email"); // user_email
    String userProfile = (String) attributes.get("picture");

//      List<Map<String, Object>> birthdays = (List<Map<String, Object>>) peopleAttributes.get("birthdays");
//      Map<String, Object> date = (Map<String, Object>) birthdays.get(1).get("date");
//      String birthyear = (String) date.get("year");
//      String month = (String) date.get("month");
//      String day = (String) date.get("day");
//      if(month.length() < 2){
//        month = "0" + month;
//      }
//      String userFullBirth = birthyear + month + day;
//      log.info("google user_birth >> {}", userFullBirth);

    List<Map<String, Object>> birthdays = (List<Map<String, Object>>) peopleAttributes.get("birthdays");

    String userFullBirth = "";
    String birthYear = "";

    for (Map<String, Object> birthAttributes : birthdays) {
      if (birthAttributes.containsKey("date")) {
        Map<String, Object> date = (Map<String, Object>) birthAttributes.get("date");

        Integer year = (Integer) date.get("year");
        Integer month = (Integer) date.get("month");
        Integer day = (Integer) date.get("day");

        birthYear = String.valueOf(year);

        userFullBirth = String.format("%04d%02d%02d", year, month, day); // yyyymmdd 형식으로 format
        log.info("userFullBirth >> {}", userFullBirth);
        break;
      }
    }

    log.info("google attributes >> {}", attributes);
    log.info("google login------------------------");
    log.info("google Info >> {}", userUid);
    log.info("google user_name >> {}", userName);
    log.info("google user_email >> {}", userEmail);
    log.info("google user_profile >> {}", userProfile);
    log.info("google provider >> {}", provider);
    log.info("google birthday >> {}", userFullBirth);
    log.info("google login------------------------");

    // 로그인 시 들어오는 데이터 DB 에 저장하기
    UserDTO userDTO = new UserDTO();
    userDTO.setUserUid(userUid);
    userDTO.setUserName(userName);
    userDTO.setUserEmail(userEmail);
    userDTO.setLastLogin(LocalDateTime.now());
    userDTO.setUserPhone("GOOGLE_NUMBERS");
    // userDTO.setUserPhone(userPhone); 제공 유무가 불확실함
    // myPage 에서 인증 받는 방법이 더욱 확실하다.

    userDTO.setProvider(provider);
    userDTO.setBirthday(userFullBirth);

    // 생년월일 현재랑 비교해서 성인인지 아닌지 확인하기.
    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
    String formatDate = today.format(formatter);
    int presentDate = Integer.parseInt(formatDate); // 현재년도
    int previousDate = Integer.parseInt(birthYear); // 사용자의 생일 (yyyy)
    log.info(">> google presentDate >> {}", presentDate);
    log.info(">> google previousDate >> {}", previousDate);


    userDTO.setAdult(presentDate - previousDate >= 19);
    String customerKey = userUid + "_" + provider;
    userDTO.setCustomerKey(customerKey); // 결제시 사용 할 키
    userDTO.setEmailCertified(true); // 소셜 로그인은 email 인증이 따로 필요하지않음.
    // 구글 로그인 같은 경우에는 기본적으로 휴대전화 인증을 해주지 않는다.
    userDTO.setUserTermOption(true);

    // 프로필 이미지는 차후에...

    log.info("google userDTO > {}", userDTO);

    // 먼저 User 테이블에 소셜 계정 값이 있는지 확인.
    UserDTO isDuplicateUser = userService.isSocialDuplicateUser(userUid);
    log.info(">> google isDuplicateUser >> {}", isDuplicateUser);

    if (isDuplicateUser == null) { // User 테이블에 계정 값이 없을 경우
      userService.insertOauthUser(userDTO);
    } else {
      userService.updateSocialLastLogin(userDTO);
    }


    return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
        oAuth2User.getAttributes(), "sub");
  }

  // -------------------------- NAVER -----------------------------
  private OAuth2User providerNaver(Map<String, Object> attributes, OAuth2User oAuth2User, String provider) {

    Map<String, Object> naverAccount = (Map<String, Object>) attributes.get("response");
    String profile = (String) naverAccount.get("profile_image");
    String userName = (String) naverAccount.get("name");
    String userEmail = (String) naverAccount.get("email"); // user_email
    String userPhone = (String) naverAccount.get("mobile"); // user_phone
    String birthYear = (String) naverAccount.get("birthyear"); // year
    String birthDay = (String) naverAccount.get("birthday"); // month-day

    // 카카오와 동일한 형태 yyyymmdd 이렇게 연결되게 수정.
    birthDay = birthDay.replace("-", "");
    String userUid = (String) naverAccount.get("id"); // Uid
    log.info("naver login------------------------");
    log.info("naver Info >> {}", naverAccount);
    log.info(">> naver user_uid >> {}", userUid); // Uid
    log.info(">> naver profile >> {}", profile);
    log.info(">> naver user_name >> {}", userName);
    log.info(">> naver user_email >> {}", userEmail);
    log.info(">> naver user_phone >> {}", userPhone);
    log.info(">> naver year >> {}", birthYear);
    log.info(">> naver day >> {}", birthDay);
    log.info("naver login------------------------");

    // 로그인 시 들어오는 데이터 DB 에 저장하기
    UserDTO userDTO = new UserDTO();
    userDTO.setUserUid(userUid);
    userDTO.setUserName(userName);
    userDTO.setUserEmail(userEmail);
    userDTO.setUserPhone(userPhone);
    userDTO.setLastLogin(LocalDateTime.now());
    userDTO.setProvider(provider);
    userDTO.setBirthday(birthYear + birthDay);

    // 생년월일 현재랑 비교해서 성인인지 아닌지 확인하기.
    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
    String formatDate = today.format(formatter);
    int presentDate = Integer.parseInt(formatDate); // 현재년도
    int previousDate = Integer.parseInt(birthYear); // 사용자의 생일 (yyyy)
    log.info(">> naver presentDate >> {}", presentDate);
    log.info(">> naver previousDate >> {}", previousDate);


    userDTO.setAdult(presentDate - previousDate >= 19);
    String customerKey = userUid + "_" + provider;
    userDTO.setCustomerKey(customerKey); // 결제시 사용 할 키
    userDTO.setEmailCertified(true); // 소셜 로그인은 email 인증이 따로 필요하지않음.
    userDTO.setMobileCertified(true); // 모바일 인증 위와 동일
    userDTO.setUserTermOption(true);

    // 프로필 이미지는 차후에...

    log.info("naver userDTO > {}", userDTO);

    // 먼저 User 테이블에 소셜 계정 값이 있는지 확인.
    UserDTO isDuplicateUser = userService.isSocialDuplicateUser(userUid);
    log.info(">> naver isDuplicateUser >> {}", isDuplicateUser);

    if (isDuplicateUser == null) { // User 테이블에 계정 값이 없을 경우
      userService.insertOauthUser(userDTO);
    } else {
      userService.updateSocialLastLogin(userDTO);
    }

    return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
        naverAccount, "id");
  }

  // -------------------------- KAKAO -----------------------------
  private OAuth2User providerKakao(Map<String, Object> attributes, OAuth2User oAuth2User, String provider) {

    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile"); // profile image
    String userName = (String) kakaoAccount.get("name"); // user_name
    String userEmail = (String) kakaoAccount.get("email"); // user_email
    String userPhone = (String) kakaoAccount.get("phone_number"); // user_phone
    String birthYear = (String) kakaoAccount.get("birthyear"); // year
    String birthDay = (String) kakaoAccount.get("birthday"); // month,day
    String userUid = oAuth2User.getName(); // Uid
    userPhone = userPhone.replace("+82 ", "0");

    log.info("kakao login------------------------");
    log.info("kakao Info >> {}", kakaoAccount);
    log.info(">> kakao user_uid >> {}", userUid); // Uid
    log.info(">> kakao profile >> {}", profile);
    log.info(">> kakao user_name >> {}", userName);
    log.info(">> kakao user_email >> {}", userEmail);
    log.info(">> kakao user_phone >> {}", userPhone);
    log.info(">> kakao year >> {}", birthYear);
    log.info(">> kakao day >> {}", birthDay);
    log.info("kakao login------------------------");
    userPhone = userPhone.replace("+82 ", "0");
    userPhone = userPhone.replace("-", "");

    // 로그인 시 들어오는 데이터 DB 에 저장하기
    UserDTO userDTO = new UserDTO();
    userDTO.setUserUid(userUid);
    userDTO.setUserName(userName);
    userDTO.setUserEmail(userEmail);
    userDTO.setUserPhone(userPhone);
    userDTO.setProvider(provider);
    userDTO.setLastLogin(LocalDateTime.now());
    userDTO.setBirthday(birthYear + birthDay);

    // 생년월일 현재랑 비교해서 성인인지 아닌지 확인하기.
    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
    String formatDate = today.format(formatter);
    int presentDate = Integer.parseInt(formatDate); // 현재년도
    int previousDate = Integer.parseInt(birthYear); // 사용자의 생일 (yyyy)
    log.info(">> kakao presentDate >> {}", presentDate);
    log.info(">> kakao previousDate >> {}", previousDate);


    userDTO.setAdult(presentDate - previousDate >= 19);
    String customerKey = userUid + "_" + provider;
    userDTO.setCustomerKey(customerKey); // 결제시 사용 할 키
    userDTO.setEmailCertified(true); // 소셜 로그인은 email 인증이 따로 필요하지않음.
    userDTO.setMobileCertified(true); // 모바일 인증 위와 동일
    userDTO.setUserTermOption(true);

    // 프로필 이미지는 차후에...

    log.info("kakao userDTO > {}", userDTO);

    // 먼저 User 테이블에 소셜 계정 값이 있는지 확인.
    UserDTO isDuplicateUser = userService.isSocialDuplicateUser(userUid);
    log.info(">> kakao isDuplicateUser >> {}", isDuplicateUser);

    if (isDuplicateUser == null) { // User 테이블에 계정 값이 없을 경우
      userService.insertOauthUser(userDTO);
    } else {
      userService.updateSocialLastLogin(userDTO);
    }

    return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
        oAuth2User.getAttributes(), "id");
  }

}
