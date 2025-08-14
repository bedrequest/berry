package com.berry.project.dto.qna;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CustomerIqBoardFileDTO {

  private CustomerIqBoardDTO boardDTO;
  private List<CustomerIqFileDTO> fileList;


}
