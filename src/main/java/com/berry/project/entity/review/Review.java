package com.berry.project.entity.review;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "lodge_id", nullable = false)
    private Long lodgeId;

    @Column(name = "reservation_id")
    private Long reservationId;

    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "ai_summary")
    private String aiSummary;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "reported_count")
    private Integer reportedCount = 0;
}
