package com.berry.project.handler;

import com.berry.project.dto.lodge.ListOptionDTO;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;

@ToString
@Getter
public class PagingHandler<T> {
  private final int startPage;
  private int endPage;
  private final int totalPage;
  private final long totalCount;
  private final boolean hasPrev;
  private final boolean hasNext;
  private final int pageNo;
  List<T> list;

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
