package com.berry.project.service.qna;

import com.berry.project.dto.qna.CustomerIqBoardDTO;
import com.berry.project.dto.qna.CustomerIqBoardFileDTO;
import com.berry.project.dto.qna.CustomerIqFileDTO;
import com.berry.project.entity.qna.CustomerIqBoard;
import com.berry.project.entity.qna.CustomerIqFile;
import com.berry.project.repository.qna.CustomerIqBoardRepository;
import com.berry.project.repository.qna.CustomerIqFileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerIqBoardServiceImpl implements CustomerIqBoardService {

  private final CustomerIqBoardRepository customeriqboardrepository;
  private final CustomerIqFileRepository customeriqfilerepository;

  @Transactional
  @Override
  public Long insert(CustomerIqBoardFileDTO customeriqboardfileDTO) {
    CustomerIqBoardDTO customeriqboardDTO = customeriqboardfileDTO.getBoardDTO();
    Long bno = customeriqboardrepository.save(convertDtoToEntity(customeriqboardDTO)).getBno();
    bno = fileSave(customeriqboardfileDTO.getFileList(), bno);
    return bno;
  }

  @Override
  public Long insert(CustomerIqBoardDTO customeriqboardDTO) {
    return customeriqboardrepository.save(convertDtoToEntity(customeriqboardDTO)).getBno();
  }

  private Long fileSave(List<CustomerIqFileDTO> fileList, Long bno) {
    if(bno > 0 && fileList != null){
      for(CustomerIqFileDTO customeriqfileDTO : fileList){
        customeriqfileDTO.setBno(bno);
        bno = customeriqfilerepository.save(convertDtoToEntity(customeriqfileDTO)).getBno();
      }
    }
    return bno;
  }

  @Override
  public List<CustomerIqBoardDTO> getlist() {
    List<CustomerIqBoard> customeriqboardList = customeriqboardrepository.findAll(
        Sort.by(Sort.Direction.DESC, "bno"));

    List<CustomerIqBoardDTO> customeriqboardDTOList = customeriqboardList.stream()
        .map(customeriqboard -> convertEntityToDto(customeriqboard)).toList();

    return customeriqboardDTOList;

  }

  @Override
  public Page<CustomerIqBoardDTO> getList(int page, String type, String keyword, String  startDate, String endDate) {

    Pageable pageable = PageRequest.of(page, 10,
        Sort.by("bno").descending());

    Page<CustomerIqBoard> list = customeriqboardrepository.searchcoustomeriqboard(type,keyword,startDate,endDate,pageable);
    log.info(">>> list serviceImpl >> {}", list.getContent());
    Page<CustomerIqBoardDTO> customeriqboardDTOList = list.map(this::convertEntityToDto);

    return customeriqboardDTOList;
  }


}