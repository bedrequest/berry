package com.berry.project.repository.lodge;

import com.berry.project.entity.lodge.LodgeImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LodgeImgRepository extends JpaRepository<LodgeImg, Long> {
  List<LodgeImg> findByLodgeId(Long lodgeId);

  List<LodgeImg> findByLodgeIdIn(List<Long> roomIds);
}
