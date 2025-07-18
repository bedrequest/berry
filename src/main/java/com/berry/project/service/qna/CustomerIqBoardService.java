package com.berry.project.service.qna;

import com.berry.project.dto.qna.CustomerIqBoardDTO;
import com.berry.project.dto.qna.CustomerIqBoardFileDTO;
import com.berry.project.entity.qna.CustomerIqBoard;

import java.util.List;

public interface CustomerIqBoardService  {

  Long insert(CustomerIqBoardDTO customeriqboardDTO);
  Long insert(CustomerIqBoardFileDTO customeriqboardfileDTO);

  default CustomerIqBoard convertDtoToEntity(CustomerIqBoardDTO customeriqboardDTO){

    return CustomerIqBoard.builder()
        .bno(customeriqboardDTO.getBno())
        .category(customeriqboardDTO.getCategory())
        .title(customeriqboardDTO.getTitle())
        .userEmail(customeriqboardDTO.getUserEmail())
        .content(customeriqboardDTO.getContent())
        .isSecret(customeriqboardDTO.getIsSecret())
        .comment(customeriqboardDTO.getComment())
        .commentRegDate(customeriqboardDTO.getCommentRegDate())
      .build();
  }

  default CustomerIqBoardDTO convertEntityToDto(CustomerIqBoard customeriqboard) {

    return CustomerIqBoardDTO.builder()
        .bno(customeriqboard.getBno())
        .category(customeriqboard.getCategory())
        .title(customeriqboard.getTitle())
        .userEmail(customeriqboard.getUserEmail())
        .content(customeriqboard.getContent())
        .regDate(customeriqboard.getRegDate())
        .modDate(customeriqboard.getModDate())
        .isSecret(customeriqboard.getIsSecret() == null)
        .comment(customeriqboard.getComment())
        .commentRegDate(customeriqboard.getCommentRegDate())
      .build();
  }

  List<CustomerIqBoardDTO> getlist();
}