package com.berry.project.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class NaverMapApi {
  @Value("${naverMapApi.key}")
  private String naverMapApiKey;
}
