package com.berry.project.repository.search;

import com.berry.project.entity.search.Search;

import java.util.List;

public interface SearchCustomRepository {

  List<Search> findThatContains(String keyword);

}
