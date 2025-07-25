package com.berry.project.service.search;

import com.berry.project.dto.search.SearchDTO;
import com.berry.project.repository.search.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

  private final SearchRepository searchRepository;

  @Override
  public List<SearchDTO> getKeywords(String keyword) {
    if (keyword == null) return null;

    return searchRepository.findThatContains(keyword)
        .stream()
        .map(this::convertEntityToDto).toList();
  }
}
