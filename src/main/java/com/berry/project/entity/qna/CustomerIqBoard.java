package com.berry.project.entity.qna;

import com.berry.project.entity.TimeBase;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_iq_board")
public class CustomerIqBoard extends TimeBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long bno;

  @Column(nullable = false)
  private String category;  // 공지 (admin) - 카테고리

  @Column(nullable = false)
  private String title;     // 제목글

  @Column(name = "user_email", nullable = false)
  private String userEmail; // id(메일형식)

  @Lob
  private String content;   // 내용 (TEXT 타입)

  @Column(name = "is_secret")
  private Boolean isSecret; // 비밀글 여부

  @Column(columnDefinition = "TEXT")
  private String comment;        // 코멘트(답글)

  @Column(name = "comment_reg_date")
  private LocalDateTime commentRegDate; // 코멘트 작성 시간
}