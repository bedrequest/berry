package com.berry.project.repository.lodge;

import com.berry.project.entity.lodge.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
  List<Room> findByLodgeId(Long lodgeId);

  List<Room> findByRoomIdIn(List<Long> roomIds);
}
