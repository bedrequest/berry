package com.berry.project.handler.user;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@Slf4j
public class CoolSMSHandler {

  public void sendCertifiedNumber(
      String userPhone,
      String certifiedNumber,
      String coolSmsApiKey,
      String coolSmsSecretKey,
      String fromNumber
  ) {
    DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(coolSmsApiKey, coolSmsSecretKey, "https://api.solapi.com");
    log.info("apiKey >>> {}", coolSmsApiKey);
    log.info("secretKey >>> {}", coolSmsSecretKey);
    log.info("messageHead >>> {}", messageService);

    Message message = new Message();
    message.setFrom(fromNumber);
    message.setTo(userPhone);
    message.setText("[Berry] 인증번호는 " + certifiedNumber + "입니다.");

    try {
      // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
      messageService.send(message);
    } catch (NurigoMessageNotReceivedException exception) {
      // 발송에 실패한 메시지 목록을 확인할 수 있습니다!
      log.info("MessageError : {}", exception.getFailedMessageList());
      log.info("MessageError : {}", exception.getMessage());
    } catch (Exception exception) {
      log.info(exception.getMessage());
    }

  }

  public String createSecureNumber(int length) {
    SecureRandom secureRandom = new SecureRandom();
    int upperLimit = (int) Math.pow(10, length);
    int number = secureRandom.nextInt(upperLimit);
    return String.format("%0" + length + "d", number);
  }


}
