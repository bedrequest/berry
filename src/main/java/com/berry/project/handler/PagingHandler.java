package com.berry.project.handler;

import com.berry.project.dto.cupon.CuponDTO;
import com.berry.project.dto.lodge.ListOptionDTO;
import com.berry.project.dto.payment.PaymentReceiptDTO;
import com.berry.project.dto.payment.ReservationDTO;
import com.berry.project.dto.qna.CustomerIqBoardDTO;
import com.berry.project.dto.review.ReviewResponseDTO;
import com.berry.project.dto.user.UserDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;

@ToString
@Getter
@RequiredArgsConstructor
public class PagingHandler<T> {
  private final int startPage;
  private final int totalPage;
  private final long totalCount;
  private final boolean hasPrev;
  private final boolean hasNext;
  private final int pageNo;
  List<T> list;
  private int endPage;
  private ListOptionDTO listOptionDTO;

  public PagingHandler(Page<T> page) {
    int groupSize = 5;

    this.pageNo = page.getNumber() + 1;
    this.totalPage = page.getTotalPages();
    this.totalCount = page.getTotalElements();

    this.startPage = page.getNumber() / groupSize * groupSize + 1;
    this.endPage = this.startPage + groupSize - 1;
    if (this.endPage > this.totalPage) this.endPage = this.totalPage;

    hasPrev = startPage > 1;
    hasNext = endPage < totalPage;

    list = page.toList();
  }

  /**
   * Creates a paging handler with custom group size.
   */
  public PagingHandler(Page<T> page, ListOptionDTO listOptionDTO) {
    this(page);

    this.listOptionDTO = listOptionDTO;
  }


}
