package com.berry.project.entity.review;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review_report",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "review_id"}))
public class ReviewReport {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "report_id")
  private Long reportId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "review_id", nullable = false)
  private Long reviewId;
}
