package com.berry.project.repository.lodge;

import com.berry.project.entity.lodge.LodgeImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LodgeImgRepository extends JpaRepository<LodgeImg, Long> {
  List<LodgeImg> findByLodgeId(Long lodgeId);

  @Query("SELECT i.lodgeImgUrl FROM LodgeImg i WHERE i.lodgeId = :lodgeId ORDER BY i.lodgeImgId limit 1")
  String findFirstLodgeImage(@Param("lodgeId") long lodgeId);
}
