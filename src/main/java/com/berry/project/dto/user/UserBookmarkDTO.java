package com.berry.project.dto.user;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserBookmarkDTO {

  private Long userBookmarkId;
  private Long userId;
  private Long lodgeId;
  private LocalDateTime regDate;
  private LocalDateTime modDate;

}
