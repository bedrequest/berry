package com.berry.project.repository.qna;

import com.berry.project.entity.qna.CustomerIqBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerIqBoardCustomRepository {

  List<CustomerIqBoard> findNoticeBoards(); // 공지글 (고정)

  Page<CustomerIqBoard> searchcoustomeriqboard(String type, String keyword,
                                        String startDate, String endDate, Pageable pageable);

  /* duorpeb, 관리자 페이지에서 검색어가 없고 미등록 답변 보기만 클릭한 경우를 위한 메서드 */
  Page<CustomerIqBoard> pageQna(String category, String keyword, Pageable pageable);

  /* duorpeb, 관리자 페이지에서 검색어만 있고 미등록 답변 보기는 클릭하지 않은 경우를 위한 메서드 */
  Page<CustomerIqBoard> pageQnaKw(String category, String keyword, Pageable pageable);

  /* duorpeb, 관리자 페이지에서 검색어와 미등록 답변 보기를 모두 적용한 경우를 위한 메서드 */
  Page<CustomerIqBoard> pageQnaKwAns(String category, String keyword, Pageable pageable);
}
