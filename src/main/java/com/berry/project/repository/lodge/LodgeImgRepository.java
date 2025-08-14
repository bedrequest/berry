package com.berry.project.repository.lodge;

import com.berry.project.entity.lodge.LodgeImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LodgeImgRepository extends JpaRepository<LodgeImg, Long> {
  List<LodgeImg> findByLodgeId(Long lodgeId);

  // ===== Top N 예약 숙소 집계 =====
  Optional<LodgeImg> findFirstByLodgeIdOrderByLodgeImgIdAsc(Long lodgeId);

  List<LodgeImg> findByLodgeIdIn(List<Long> roomIds);
}
