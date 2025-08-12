package com.berry.project.dto.lodge;

import com.berry.project.entity.lodge.Lodge;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LodgeWithTagCountDTO {

  private LodgeDTO lodgeDTO;
  private long tagCount;

  public LodgeWithTagCountDTO(Lodge lodge, long score) {
    lodgeDTO = LodgeDTO.builder().build();
    tagCount = score;
  }

  public double getTagRatio() {
    return tagCount/(double)lodgeDTO.getReviewCount();
  }

}
