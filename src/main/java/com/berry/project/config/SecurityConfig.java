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
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(
                "/","/css/**","/js/**","/image/**","/user/signup/**","/user/login/**"
                ,"/user/duplicateCheckedEmail/**",
                "/lodge/**", "/search/**",
                "/reviews/list/**", "/review-tags/**",
                "/.well-known/**", "/error/**"

            )
            .permitAll()
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
