package com.berry.project.service.search;

import com.berry.project.dto.search.SearchDTO;
import com.berry.project.entity.search.Search;

import java.util.List;

public interface SearchService {

  default SearchDTO convertEntityToDto(Search search) {
    return SearchDTO.builder()
        .keyword(search.getKeyword())
        .detail(search.getDetail())
        .lodgeId(search.getLodgeId())
        .build();
  }

  List<SearchDTO> getKeywords(String keyword);
}
