package com.berry.project.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class TossApi {
  @Value("${tossClientApi.key}")
  private String tossClientApiKey;

  @Value("${tossSecretApi.key}")
  private String tossSecretApiKey;

  @Value("${tossSecurityApi.key}")
  private String tossSecurityApiKey;
}
