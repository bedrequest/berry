package com.berry.project.entity.lodge;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lodge {
  @Id
  @Column(name = "lodge_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long lodgeId;

  @Column(name = "lodge_name", nullable = false)
  private String lodgeName;

  @Column(name = "lodge_type", nullable = false)
  private String lodgeType;

  @Column(name = "lodge_addr", nullable = false)
  private String lodgeAddr;

  private int facility;

  @Lob
  private String intro;

  @Column(name = "business_call")
  private String businessCall;

  private double latitude;

  private double longitude;
}
