package com.berry.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper om = new ObjectMapper();
    // Jackson 에 LocalDateTime 등의 JavaTimeModule 을 등록
    om.registerModule(new JavaTimeModule());

    /** 추가 설정
     *
     *  > 알 수 없는 속성 무시 설정 등의 추가 구성을 설정
     * */
    om.configure(
        com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false
    );

    return om;
  }
}
