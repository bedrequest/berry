package com.berry.project.repository.qna;

import com.berry.project.entity.qna.CustomerIqBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardCustomerIqRepository {

  List<CustomerIqBoard> findNoticeBoards(); // 공지글 (고정)

  Page<CustomerIqBoard> searchcoustomeriqboard(String type, String keyword,
                                               String startDate, String endDate, Pageable pageable);
}
