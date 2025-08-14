package com.berry.project.repository.lodge;

import com.berry.project.entity.lodge.Lodge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LodgeRepository extends JpaRepository<Lodge, Long>, LodgeCustomRepository {

  List<Lodge> findByLodgeIdIn(List<Long> lodgeIds);

}
