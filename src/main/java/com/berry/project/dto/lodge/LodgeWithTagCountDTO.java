package com.berry.project.dto.lodge;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LodgeWithTagCountDTO {

  private LodgeDTO lodgeDTO;
  private long tagCount;

  public double getTagRatio() {
    return tagCount/(double)lodgeDTO.getReviewCount();
  }

}
