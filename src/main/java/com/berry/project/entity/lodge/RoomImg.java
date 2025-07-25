package com.berry.project.entity.lodge;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "room_img")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomImg {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "room_img_id")
  private Long roomImgId;

  @Column(name = "room_id", nullable = false)
  private long roomId;

  @Column(name = "room_img_url", nullable = false)
  private String roomImgUrl;
}
