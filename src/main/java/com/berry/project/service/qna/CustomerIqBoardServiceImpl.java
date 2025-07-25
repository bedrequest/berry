package com.berry.project.service.qna;

import com.berry.project.dto.qna.CustomerIqBoardDTO;
import com.berry.project.dto.qna.CustomerIqBoardFileDTO;
import com.berry.project.entity.qna.CustomerIqBoard;
import com.berry.project.repository.qna.CustomerIqBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerIqBoardServiceImpl implements CustomerIqBoardService {

  private final CustomerIqBoardRepository customeriqboardrepository;

  @Override
  public Long insert(CustomerIqBoardDTO customeriqboardDTO) {
    return customeriqboardrepository.save(convertDtoToEntity(customeriqboardDTO)).getBno();
  }

  @Override
  public Long insert(CustomerIqBoardFileDTO customerIqboardFileDTO) {
    return 0L;
  }

  @Override
  public List<CustomerIqBoardDTO> getlist() {
    List<CustomerIqBoard> customeriqboardList = customeriqboardrepository.findAll(
        Sort.by(Sort.Direction.DESC, "bno"));

    List<CustomerIqBoardDTO> customeriqboardDTOList = customeriqboardList.stream()
        .map(customeriqboard -> convertEntityToDto(customeriqboard)).toList();

    return customeriqboardDTOList;

  }

}