package com.berry.project.crawling;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review_crawled")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CrawledReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crawled_id")
    private Long crawledId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "lodge_id", nullable = false)
    private Long lodgeId;

    private Integer rating;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    @Builder.Default
    @Column(name = "reported_count")
    private Integer reportedCount = 0;

    @Column(name = "reservation_id")
    private Long reservationId;
}
