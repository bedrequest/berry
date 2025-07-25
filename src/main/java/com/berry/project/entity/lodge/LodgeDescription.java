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
@Table(name = "lodge_description")
public class LodgeDescription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "lodge_description_id")
  private Long lodgeDescriptionId;

  @Column(name = "lodge_id", nullable = false)
  private long lodgeId;

  private String title;

  @Lob
  private String content;
}
