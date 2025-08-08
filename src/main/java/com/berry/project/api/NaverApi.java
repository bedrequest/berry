package com.berry.project.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class NaverApi {
  @Value("${naverMapApi.key}")
  private String naverMapApiKey;

  @Value("${naverSearchApi.key}")
  private String naverSearchApiKey;
  @Value("${naverSearchApi.secret.key}")
  private String naverSearchApiSecret;
}
