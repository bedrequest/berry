package com.berry.project.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewChatRequestDTO {
  private String model;
  private List<ReviewChatMessageDTO> messages;
  private Double temperature;
}
