package com.berry.project.repository.lodge;

import com.berry.project.entity.lodge.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
  List<Room> findByLodgeId(Long lodgeId);

  List<Room> findByRoomIdIn(List<Long> roomIds);

  // ===== Top N 예약 숙소 집계 =====

  // RoomRepository
  /**
   * 해당 lodgeId 에 속한 Room 들 중 stayPrice 의 최소값을 반환
   *
   * @param lodgeId Lodge 의 PK
   * @return 최소 숙박가
   */
  @Query("""
  SELECT MIN(r.stayPrice)
  FROM Room r
  WHERE r.lodgeId = :lodgeId
    AND r.stayPrice IS NOT NULL
    AND r.stayPrice > 0
""")
  Integer findMinStayPriceByLodgeId(@Param("lodgeId") Long lodgeId);

  @Query("""
  SELECT MIN(r.rentPrice)
  FROM Room r
  WHERE r.lodgeId = :lodgeId
    AND r.rentPrice IS NOT NULL
    AND r.rentPrice > 0
""")
  Integer findMinRentPriceByLodgeId(@Param("lodgeId") Long lodgeId);

  List<Room> findByLodgeIdIn(List<Long> lodgeIds);

}
