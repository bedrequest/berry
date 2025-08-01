package com.berry.project.dto.lodge;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LodgeDescriptionDTO {

  private String title;
  private List<String> contents;
}
