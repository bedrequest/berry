package com.berry.project.dto.review;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSummaryDTO {
  private Long lodgeId;
  private String summaryText;
  private LocalDateTime summaryUpdate;
}
