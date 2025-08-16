package com.berry.project.handler;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@ToString
public class CustomerIqPagingHandler<T> {
  private int startPage;
  private int endPage;
  private int totalPage;
  private boolean hasPrev, hasNext;
  private int pageNo;
  private long totalCount;

  private String type;
  private String keyword;
  private String tripStart;
  private String tripEnd;
  private List<T> list;

  public CustomerIqPagingHandler(Page<T> list, int pageNo) {
    this.list = list.getContent();
    this.pageNo = pageNo;
    this.totalPage = list.getTotalPages();
    this.totalCount = list.getTotalElements();
    this.endPage = (int) Math.ceil(this.pageNo / 10.0) * 10;
    this.startPage = endPage - 9;

    this.endPage = (endPage > totalPage) ? totalPage : endPage;
    this.hasPrev = this.startPage > 10;
    this.hasNext = this.endPage < this.totalPage;
  }

  public CustomerIqPagingHandler(Page<T> list, int pageNo, String type, String keyword, String tripStart, String tripEnd) {
    this(list, pageNo);
    this.type = type;
    this.keyword = keyword;
    this.tripStart = tripStart;
    this.tripEnd = tripEnd;
  }
}