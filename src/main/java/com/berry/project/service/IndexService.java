package com.berry.project.service;

import com.berry.project.dto.ReviewLodgeDTO;
import com.berry.project.dto.lodge.LodgeDTO;
import com.berry.project.handler.PagingHandler;

import java.util.List;
import java.util.NoSuchElementException;

public interface IndexService {
  PagingHandler<LodgeDTO> getLodgeListByTag(int pageNo, int tagId);

  List<ReviewLodgeDTO> getRecentReviews() throws NoSuchElementException;
}
