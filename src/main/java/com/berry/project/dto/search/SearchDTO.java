package com.berry.project.dto.search;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchDTO {

  private String keyword;

  private String detail;

  private Long lodgeId;

}
