package com.berry.project.handler.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class StarterMailHandler {

  private final JavaMailSender javaMailSender;

  public void sendCertifiedCode(String userEmail, String certifiedCode){

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(userEmail);
    message.setSubject("[Berry] 이메일 인증 코드");
    message.setText("인증 코드: " + certifiedCode);
    javaMailSender.send(message);

  }

  public String generateRandomMixStr(int length) {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    SecureRandom random = new SecureRandom();
    StringBuilder sb = new StringBuilder(length);

    for (int i = 0; i < length; i++) {
      int index = random.nextInt(characters.length());
      sb.append(characters.charAt(index));
    }
    return sb.toString().toLowerCase();
  }

}
