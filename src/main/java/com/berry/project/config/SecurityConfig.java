package com.berry.project.config;

import com.berry.project.security.CustomOAuth2UserService;
import com.berry.project.security.CustomUserDetailService;
import com.berry.project.security.LoginFailureHandler;
import com.berry.project.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

  // kakao
  // naver
  // google
  private final CustomOAuth2UserService customOAuth2UserService;

  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    return http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(
                "/", "/css/**", "/js/**", "/image/**", "/upload/**", "/user/signup/**", "/user/login/**"
                , "/user/duplicateCheckedEmail/**", "/user/findWebUserEmail/**", "/user/getCertifiedCode/**",
                "/user/resetPassword/**", "/user/getSignInCertifiedCode/**", "/user/getSignInCertifiedNumber/**",
                "/user/getCertifiedNumber/**",
                "/lodge/**", "/search/**",
                "/reviews/list/**", "/review-tags/**", "/.well-known/**", "/error/**",
                "/reviews/view/**",
                "/outerSearch/**", "/indexTest/**",
                "/error/**"
            )
            .permitAll()
            /** duorpeb, 비로그인 유저가 로그인 버튼 누르는 경우 로그인 페이지로 redirect 를 하기 위한 코드
             *
             * > authenticated() 는 익명 사용자도 isAuthenticated()==true 로 처리하기 때문에
             *   .requestMatchers.authenticated() 를 사용하면 userId=undefined 로 400 ERROR 발생
             *
             * */
            .requestMatchers("/payment/**").fullyAuthenticated()
            .anyRequest().authenticated()
        )
        .formLogin(login -> login
            .usernameParameter("userEmail")
            .passwordParameter("password")
            .loginPage("/user/login")
            .successHandler(authenticationSuccessHandler())
            .failureHandler(authenticationFailureHandler())
            .permitAll()
        )
        .oauth2Login(oauth2 -> oauth2
            .loginPage("/user/test") // 테스트 완료 후 실제 로그인 페이지로 변경
            .defaultSuccessUrl("/") // 테스트 완료 후 success handler 로 변경
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService)
            ))
        .logout(logout -> logout
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .logoutSuccessUrl("/")) // 로그 아웃시 루트로 이동

        .build();
  }

  @Bean
  UserDetailsService userDetailsService() {
    return new CustomUserDetailService();
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  AuthenticationSuccessHandler authenticationSuccessHandler() {
    return new LoginSuccessHandler();
  }

  @Bean
  AuthenticationFailureHandler authenticationFailureHandler() {
    return new LoginFailureHandler();
  }


}
