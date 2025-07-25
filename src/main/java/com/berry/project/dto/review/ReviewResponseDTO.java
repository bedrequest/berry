package com.berry.project.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {
    private Long reviewId;
    private Long userId;
    private Long lodgeId;
    private String userEmail;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
    private List<String> tags;
}
