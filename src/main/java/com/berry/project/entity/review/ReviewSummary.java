package com.berry.project.entity.review;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_summary")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ReviewSummary {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "summary_id")
  private Long summaryId;

  @Column(name = "lodge_id", nullable = false)
  private Long lodgeId;

  @Column(name = "summary_text", columnDefinition = "TEXT")
  private String summaryText;

  @Column(name = "summary_update", nullable = false)
  @Builder.Default
  private LocalDateTime summaryUpdate = LocalDateTime.now();
}
