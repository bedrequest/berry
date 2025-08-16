package com.berry.project.entity.qna;

import com.berry.project.entity.TimeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerIqFile extends TimeBase {

  @Id
  @Column(length = 255)
  private String uuid; // 고유 식별자 (UUID), Primary Key

  @Column(name = "save_dir", nullable = false, length = 255)
  private String saveDir; // 저장 디렉토리 경로

  @Column(name = "file_name", nullable = false, length = 255)
  private String fileName; // 원본 파일 이름

  @Column(name = "file_type", columnDefinition = "TINYINT(1) DEFAULT 0")
  @Builder.Default
  private Integer fileType = 0; // 파일 타입 (0: 일반, 1: 썸네일 등)

  @Column(nullable = false)
  private Long bno; // 연결된 게시글 번호

  @Column(name = "file_size")
  private Long fileSize; // 파일 크기 (bytes)

}
