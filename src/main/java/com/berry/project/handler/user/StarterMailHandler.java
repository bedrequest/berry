package com.berry.project.handler.user;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class StarterMailHandler {

  private final JavaMailSender javaMailSender;

  public void sendCertifiedCode(String userEmail, String certifiedCode) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(userEmail);
    message.setSubject("[Berry] 이메일 인증 코드");
    message.setText("인증 코드: " + certifiedCode);
    javaMailSender.send(message);
  }

  public void sendCertifiedCodeHtml(String userEmail, String certifiedCode) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(userEmail);
      helper.setSubject("[Berry] 이메일 인증 코드");

      String htmlContent = "<html><body>" +
          "<h1>[Berry] 이메일 인증 코드</h1>" +
          "<h3>인증 코드</h3>" +
          "<strong style='font-size: 18px;'>" + certifiedCode + "</strong>" +
          "</body></html>";

      helper.setText(htmlContent, true);

      javaMailSender.send(message);
      log.info("HTML 인증 코드 이메일 전송 성공: to={}, code={}", userEmail, certifiedCode);
    } catch (MessagingException e) {
      log.error("HTML 인증 코드 이메일 전송 실패: to={}", userEmail, e);
    }
  }

  public void sendPasswordHtml(String userEmail, String certifiedCode) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(userEmail);
      helper.setSubject("[Berry] 비밀번호 변경");

      String htmlContent = "<html><body>" +
          "<h1>[Berry] 변경된 비밀번호</h1>" +
          "<h3>비밀번호</h3>" +
          "<strong style='font-size: 18px;'>" + certifiedCode + "</strong>" +
          "<p>비밀번호 변경은 마이페이지에서 할 수 있습니다.</p>" +
          "</body></html>";

      helper.setText(htmlContent, true);

      javaMailSender.send(message);
      log.info("HTML 비밀번호 이메일 전송 성공: to={}, code={}", userEmail, certifiedCode);
    } catch (MessagingException e) {
      log.error("HTML 비밀번호 이메일 전송 실패: to={}", userEmail, e);
    }
  }

  public String generateRandomMixStr(int length) {

    String alphaNum = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    String specialChars = "!@#$%^*=";
    SecureRandom random = new SecureRandom();
    StringBuilder sb = new StringBuilder(length);

    // 특수문자 하나 미리 선택
    char specialChar = specialChars.charAt(random.nextInt(specialChars.length()));

    // (length - 1) 만큼 일반 문자 생성
    for (int i = 0; i < length - 1; i++) {
      int index = random.nextInt(alphaNum.length());
      sb.append(alphaNum.charAt(index));
    }

    // 특수문자를 무작위 위치에 삽입
    int insertPos = random.nextInt(length);
    sb.insert(insertPos, specialChar);

    return sb.toString().toLowerCase();
  }
}