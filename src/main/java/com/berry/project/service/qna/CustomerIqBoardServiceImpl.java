package com.berry.project.service.qna;

import com.berry.project.dto.qna.CustomerIqBoardDTO;
import com.berry.project.dto.qna.CustomerIqBoardFileDTO;
import com.berry.project.dto.qna.CustomerIqFileDTO;
import com.berry.project.entity.qna.CustomerIqBoard;
import com.berry.project.entity.qna.CustomerIqFile;
import com.berry.project.repository.qna.CustomerIqBoardRepository;
import com.berry.project.repository.qna.CustomerIqFileRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
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
    if (bno > 0 && fileList != null) {
      for (CustomerIqFileDTO customeriqfileDTO : fileList) {
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
  public Page<CustomerIqBoardDTO> getPageList(int pageNo) {
    Pageable pageable = PageRequest.of(pageNo, 10,
        Sort.by("bno").descending());
    Page<CustomerIqBoard> list = customeriqboardrepository.findAll(pageable);
    Page<CustomerIqBoardDTO> customeriqboardDTOPageList = list.map(this::convertEntityToDto);
    return customeriqboardDTOPageList;
  }

  @Override
  public Map<String, Object> getList(int page, String type, String keyword, String startDate, String endDate) {

    Pageable pageable = PageRequest.of(page, 15,
        Sort.by("bno").descending());

    // 1. 공지글은 항상 고정
    List<CustomerIqBoard> noticeList = customeriqboardrepository.findNoticeBoards();
    List<CustomerIqBoardDTO> noticeDTOList = noticeList
        .stream()
        .map(this::convertEntityToDto)
        .toList();

    Page<CustomerIqBoard> list = customeriqboardrepository.searchcoustomeriqboard(type, keyword, startDate, endDate, pageable);
    log.info(">>> list serviceImpl >> {}", list.getContent());
    Page<CustomerIqBoardDTO> customeriqboardDTOList = list.map(this::convertEntityToDto);

    // 4. 반환 형태
    Map<String, Object> result = new HashMap<>();
    result.put("noticeList", noticeDTOList);   // 공지글 따로
    result.put("list", customeriqboardDTOList);

    return result;
  }

  @Transactional
  @Override
  public CustomerIqBoardFileDTO getDetail(Long bno) {
    Optional<CustomerIqBoard> optional = customeriqboardrepository.findById(bno);
    if (optional.isPresent()) {
      CustomerIqBoard customeriqboard = optional.get();

      customeriqboardrepository.save(customeriqboard);

      CustomerIqBoardDTO customeriqboardDTO = convertEntityToDto(optional.get());

      List<CustomerIqFile> fileList = customeriqfilerepository.findByBno(bno);
      List<CustomerIqFileDTO> fileDTOList = fileList.stream()
          .map(this::convertEntityToDto)
          .toList();
      CustomerIqBoardFileDTO customeriqboardfileDTO = new CustomerIqBoardFileDTO(customeriqboardDTO, fileDTOList);
      return customeriqboardfileDTO;
    }
    return null;
  }

  @Transactional
  @Override
  public Long modify(CustomerIqBoardFileDTO customeriqboardfileDTO) {
    CustomerIqBoard customeriqboard = customeriqboardrepository.findById(customeriqboardfileDTO.getBoardDTO().getBno())
        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글"));

    customeriqboard.setTitle(customeriqboardfileDTO.getBoardDTO().getTitle());
    customeriqboard.setCategory(customeriqboardfileDTO.getBoardDTO().getCategory());
    customeriqboard.setContent(customeriqboardfileDTO.getBoardDTO().getContent());

    long bno = fileSave(customeriqboardfileDTO.getFileList(), customeriqboard.getBno());

    return bno;
  }

  @Override
  public void remove(Long bno) {
    customeriqboardrepository.deleteById(bno);
  }

  @Transactional
  @Override
  public long fileRemove(String uuid) {
    Optional<CustomerIqFile> customeriqfile = customeriqfilerepository.findById(uuid);
    if (customeriqfile.isPresent()) {
      Optional<CustomerIqBoard> optional = customeriqboardrepository.findById(customeriqfile.get().getBno());
      if (optional.isPresent()) {
        CustomerIqBoard customerIqBoard = optional.get();
      }
      customeriqfilerepository.deleteById(uuid);
    }
    return customeriqfile.get().getBno();
  }

  @Transactional
  @Override
  public long post(CustomerIqBoardDTO customeriqboardDTO) {
    Optional<CustomerIqBoard> optional = customeriqboardrepository.findById(customeriqboardDTO.getBno());
    log.info("optional CustomerIqBoard >> {}", optional.get());
    if (optional.isPresent()) {
      CustomerIqBoard customeriqboard = optional.get();
      customeriqboard.setComment(customeriqboardDTO.getComment());
      customeriqboard.setCommentRegDate(LocalDateTime.now());

      return customeriqboardrepository.save(customeriqboard).getBno();
    }
    return 0L;
  }
}