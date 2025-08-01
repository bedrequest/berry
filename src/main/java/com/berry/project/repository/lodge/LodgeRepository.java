package com.berry.project.repository.lodge;

import com.berry.project.entity.lodge.Lodge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LodgeRepository extends JpaRepository<Lodge, Long>, LodgeCustomRepository {

  List<Lodge> findByLodgeIdIn(List<Long> lodgeIds);
}
