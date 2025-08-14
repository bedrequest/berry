package com.berry.project.dto.admin;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
  private Long reviewId;

  private Long userId;

  private Long lodgeId;

  private String userEmail;

  private Long reservationId;

  private Integer rating;

  private String content;

  private String aiSummary;

  private LocalDateTime createdAt;

  private Integer reportedCount;
}
