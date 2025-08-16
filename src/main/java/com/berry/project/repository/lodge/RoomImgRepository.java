package com.berry.project.repository.lodge;

import com.berry.project.entity.lodge.RoomImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomImgRepository extends JpaRepository<RoomImg, Long> {
  List<RoomImg> findByRoomId(Long roomId);

}
