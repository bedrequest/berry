package com.berry.project.security;

import com.berry.project.service.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

  private final RequestCache requestCache = new HttpSessionRequestCache();

  @Autowired
  private UserService userService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    // duropeb, last_login Update
    boolean isOk = userService.updateLastLogin(authentication.getName()); // username
    log.info("successHandler username >> {}", authentication.getName());

    // duropeb, 세션이 없으면 새로 생성
    HttpSession httpSession = request.getSession();
    if (!isOk || httpSession == null) {
      return;
    } else {
      // 이전 로그인 실패기록 지우기
      httpSession.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }


    /** duorpeb, 비로그인 상태로 예약 버튼을 누르는 경우, 다시 예약 페이지로 돌아가기 위한 코드
     *
     * > /user/login?redirectTo={ref}
     * */
    // Step 1
    String queryParam = request.getParameter("redirectTo");
    // Step 2
    if (queryParam != null) {
      redirectStrategy.sendRedirect(request, response, queryParam);
      return;
    }

    // 이전 맵핑 경로 가져오기
    SavedRequest savedRequest = requestCache.getRequest(request, response);

    redirectStrategy.sendRedirect(request, response,
        savedRequest != null ? savedRequest.getRedirectUrl() : "/"); // 로그인 이전 저장 경로가 없을경우 "/"로

  }
}
