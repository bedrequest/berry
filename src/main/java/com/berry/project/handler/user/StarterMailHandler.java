package com.berry.project.handler.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StarterMailHandler {

  private final JavaMailSender javaMailSender;

  // sendEmailMarketing
  public void sendEmailMarketing(List<String> userEmails){
    for(int i = 0; i < userEmails.size(); i++){
      // Text 만 보내면 SimpleMailMessage, HTML 로 보내면 다른 메서드 사용
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(userEmails.get(i));
      // 제목, setSubject() e.g., message.setSubject("[Web발신] Berry 마케팅 정보");
      // 내용, setText() e.g., message.setText("내용");
      javaMailSender.send(message);

    }

  }


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
