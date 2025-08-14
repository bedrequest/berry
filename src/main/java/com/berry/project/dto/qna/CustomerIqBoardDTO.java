package com.berry.project.dto.qna;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerIqBoardDTO {

  private Long bno;
  private String category;
  private String title;
  private String userEmail;
  private String content;
  private Boolean secret;
  private String comment;
  private LocalDateTime commentRegDate;
  private LocalDateTime regDate, modDate;
}