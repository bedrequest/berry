package com.berry.project.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewChatResponseDTO {
  private List<ChoiceDTO> choices;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChoiceDTO {
    private int index;
    private ReviewChatMessageDTO message;
  }
}
