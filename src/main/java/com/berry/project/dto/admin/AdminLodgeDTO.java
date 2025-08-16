package com.berry.project.dto.admin;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLodgeDTO {
  private Long lodgeId;

  private String lodgeName;

  private String lodgeType;

  private String lodgeAddr;

  private int facility;

  private String intro;

  private String businessCall;

  private double latitude;

  private double longitude;
}
