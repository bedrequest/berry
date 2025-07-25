package com.berry.project.entity.lodge;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "lodge_img")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LodgeImg {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "lodge_img_id")
  private Long lodgeImgId;

  @Column(name = "lodge_id", nullable = false)
  private long lodgeId;

  @Column(name = "lodge_img_url", nullable = false)
  private String lodgeImgUrl;
}
