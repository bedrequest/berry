package com.berry.project.entity.review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_img")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewImage {

  @Id
  @Column(name = "review_uuid", length = 255)
  private String reviewUuid;

  @Column(name = "review_id", nullable = false)
  private Long reviewId;

  @Column(name = "review_save_dir", nullable = false, length = 255)
  private String reviewSaveDir;

  @Column(name = "review_file_name", nullable = false, length = 255)
  private String reviewFileName;

  @Column(name = "review_file_size")
  private Long reviewFileSize;

  @Column(name = "reg_date")
  @Builder.Default
  private LocalDateTime regDate = LocalDateTime.now();

  @Column(name = "mod_date")
  private LocalDateTime modDate;
}