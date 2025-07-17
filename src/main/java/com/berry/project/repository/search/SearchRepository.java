package com.berry.project.repository.search;

import com.berry.project.entity.search.Search;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRepository extends JpaRepository<Search, Long>, SearchCustomRepository {

}
