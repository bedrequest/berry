package com.berry.project.repository.lodge;

import com.berry.project.entity.lodge.Ways;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaysRepository extends JpaRepository<Ways, Long> {
  List<Ways> findByLodgeId(Long lodgeId);
}
