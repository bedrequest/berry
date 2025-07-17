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
public class Ways {
  @Id
  @Column(name = "ways_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long waysId;

  @Column(name = "lodge_id", nullable = false)
  private long lodgeId;

  @Column(nullable = false)
  private String content;
}
