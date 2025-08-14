package com.berry.project.service.qna;

import com.berry.project.dto.qna.CustomerIqBoardDTO;
import com.berry.project.dto.qna.CustomerIqBoardFileDTO;
import com.berry.project.dto.qna.CustomerIqFileDTO;
import com.berry.project.entity.qna.CustomerIqBoard;
import com.berry.project.entity.qna.CustomerIqFile;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

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
        .isSecret(customeriqboardDTO.getSecret())
        .comment(customeriqboardDTO.getComment())
        .commentRegDate(customeriqboardDTO.getCommentRegDate())
        .build();
  }

  default CustomerIqBoardDTO convertEntityToDto(CustomerIqBoard customeriqboard) {
    boolean isSecret = customeriqboard.getIsSecret() == null ? false : customeriqboard.getIsSecret();

    return CustomerIqBoardDTO.builder()
        .bno(customeriqboard.getBno())
        .category(customeriqboard.getCategory())
        .title(customeriqboard.getTitle())
        .userEmail(customeriqboard.getUserEmail())
        .content(customeriqboard.getContent())
        .regDate(customeriqboard.getRegDate())
        .modDate(customeriqboard.getModDate())
        .secret(isSecret)
        .comment(customeriqboard.getComment())
        .commentRegDate(customeriqboard.getCommentRegDate())
        .build();
  }

  default CustomerIqFile convertDtoToEntity(CustomerIqFileDTO customeriqfileDTO){
    return CustomerIqFile.builder()
        .uuid(customeriqfileDTO.getUuid())
        .saveDir(customeriqfileDTO.getSaveDir())
        .fileName(customeriqfileDTO.getFileName())
        .fileType(customeriqfileDTO.getFileType())
        .bno(customeriqfileDTO.getBno())
        .fileSize(customeriqfileDTO.getFileSize())
        .build();
  }

  default CustomerIqFileDTO convertEntityToDto(CustomerIqFile customeriqfile){
    return CustomerIqFileDTO.builder()
        .uuid(customeriqfile.getUuid())
        .saveDir(customeriqfile.getSaveDir())
        .fileName(customeriqfile.getFileName())
        .fileType(customeriqfile.getFileType())
        .bno(customeriqfile.getBno())
        .fileSize(customeriqfile.getFileSize())
        .regDate(customeriqfile.getRegDate())
        .modDate(customeriqfile.getModDate())
        .build();
  }

  List<CustomerIqBoardDTO> getlist();

  Map<String, Object> getList(int page, String type, String keyword, String startDate, String endDate);

  CustomerIqBoardFileDTO getDetail(Long bno);

  Long modify(CustomerIqBoardFileDTO customeriqboardfileDTO);

  void remove(Long bno);

  Page<CustomerIqBoardDTO> getPageList(int pageNo);

  long fileRemove(String uuid);

  long post(CustomerIqBoardDTO customeriqboardDTO);

}