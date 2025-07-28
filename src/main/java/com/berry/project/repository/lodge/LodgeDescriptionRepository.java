package com.berry.project.repository.lodge;

import com.berry.project.entity.lodge.LodgeDescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LodgeDescriptionRepository extends JpaRepository<LodgeDescription, Long> {
  List<LodgeDescription> findByLodgeId(long lodgeId);
}
