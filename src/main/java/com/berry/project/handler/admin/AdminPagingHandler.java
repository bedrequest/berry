package com.berry.project.handler.admin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.List;

@Slf4j
@Getter
@Setter
@ToString
public class AdminPagingHandler<T> {
   // CuponTemplateDTO, UserDTO, CustomerIqBoardDTO, PaymentReceiptDTO, ReservationDTO, CuponDTO, ReviewResponseDTO
  private List<T> pgList;
  // 초기화
   // 시작/종료 넘버
  private int startIdx;
  private int endIdx;
  // 전체 페이지 수
  private int ttcPage;
   // 전체 데이터 개수
  private long ttc;
  private boolean hasPrev, hasNext;
  private int pageNo;
   // 검색을 위한 초기화
  private String sortType;
  private String keyword;
   // Paging Size
  private int qty;
   // fragmnet 를 위한 초기화
  private String frag;

  public AdminPagingHandler(Page<T> pgList, int pageNo, String sortType, String keyword, int qty, String frag){
    // 초기화
    if(qty <= 0){ qty = 10; }
    this.qty = qty;
     // 시작 번지
    this.pageNo = pageNo;

     // 전체 데이터 개수
    this.ttc = pgList.getTotalElements();

     // 전체 페이지 개수
    this.ttcPage = pgList.getTotalPages();

     // 페이지네이션 페이징 단위 정의 (e.g., 페이징 단위가 10일 경우 (qty=10) 1~10, 11~20, ...)
    this.endIdx = (int)Math.ceil(this.pageNo/(double) qty) * qty;
    this.startIdx = endIdx - (qty-1);

     // 전체 데이터 개수만큼 마지막 페이지 설정
    if(endIdx > ttcPage) { endIdx = ttcPage; }

     // 이전, 다음 초기화
    this.hasPrev = this.startIdx > 1;
    this.hasNext = this.endIdx < this.ttcPage;

     // 검색 변수 초기화
    this.sortType = sortType;
    this.keyword = keyword;

    /** 리스트 초기화
     *
     *   > getContent() 는 현재 요청한 페이지 안에 담긴 실제 엔티티 (또는 DTO) 목록을
     *     List<CommentDTO> 형태로 꺼내주는 메서드
     *
     *   > Page 객체는 내부적으로 콘텐츠(Content) + 페이징 메타데이터 (총 요소 수, 총 페이지 수, 현재 페이지 번호 등)
     *     를 갖고 있는데 getContent() 만 호출하면 메타데이터를 제외한 “지금 이 페이지에서 보여줄 순수한 댓글 리스트”만
     *     추출해서 return
     *
     * */
    this.pgList = pgList.getContent();

    this.frag = frag;
  }
}
