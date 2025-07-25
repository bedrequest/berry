package com.berry.project.entity.lodge;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "room_id")
  private Long roomId;

  @Column(name = "lodge_id", nullable = false)
  private long lodgeId;

  @Column(name = "room_name", nullable = false)
  private String roomName;

  @Lob
  private String info;

  @Column(name = "rent_price")
  private Integer rentPrice;

  @Column(name = "rent_time")
  private String rentTime;

  @Column(name = "stay_price", nullable = false)
  private int stayPrice;

  @Column(name = "stay_option")
  private String stayOption;

  @Column(name = "stay_time")
  private String stayTime;

  @Column(name = "stock_count", nullable = false)
  private int stockCount;

  @Column(name = "standard_count", nullable = false)
  private int standardCount;

  @Column(name = "max_count", nullable = false)
  private int maxCount;
}
