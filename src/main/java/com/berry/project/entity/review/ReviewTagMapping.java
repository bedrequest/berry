package com.berry.project.entity.review;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review_tag_mapping")
public class ReviewTagMapping {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "mapping_id")
  private Long mappingId;

  @Column(name = "review_id", nullable = false)
  private Long reviewId;

  @Column(name = "tag_id", nullable = false)
  private Long tagId;

  // 연관관계 추가
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "review_id", insertable = false, updatable = false)
  private Review review;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", insertable = false, updatable = false)
  private ReviewTag tag;
}
