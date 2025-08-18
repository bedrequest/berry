package com.berry.project.repository.payment;

import com.berry.project.entity.payment.Reservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;


public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationCustomRepository {
    
    /** findByOrderId(String orderId) - orderId 로 예약 정보 조회
     *
     */
    Optional<Reservation> findByOrderId(String orderId);


    /** existsByOrderId(String orderId) - orderId 존재 여부 확인 (중복 방지용)
     *
     */
    boolean existsByOrderId(String orderId);


    /** findByUserIdOrderByReservationRegDateDesc(Long userId) - 사용자 ID로 예약 목록 조회
     *
     */
    List<Reservation> findByUserIdOrderByReservationRegDateDesc(Long userId);


    /** findConflictingReservations(Long roomId, OffsetDateTime startDate, OffsetDateTime endDate,)
     *  - 객실 ID와 날짜 범위로 예약 조회 (중복 예약 방지)
     *
     */
    @Query("SELECT r FROM Reservation r WHERE r.roomId = :roomId " +
           "AND ((r.startDate <= :endDate AND r.endDate >= :startDate)) " +
           "AND r.bookingStatus != 'CANCELLED'")
    List<Reservation> findConflictingReservations(
        @Param("roomId") Long roomId,
        @Param("startDate") OffsetDateTime startDate,
        @Param("endDate") OffsetDateTime endDate
    );


    /** findByBookingStatusOrderByReservationRegDateDesc(String bookingStatus) - 예약 상태별 조회
     *
     */
    List<Reservation> findByBookingStatusOrderByReservationRegDateDesc(String bookingStatus);


    /** List"<"Reservation> findByReservationTypeOrderByReservationRegDateDesc(String reservationType)
     *
     * - 예약 타입별 조회 (숙박/대실)
     */
    List<Reservation> findByReservationTypeOrderByReservationRegDateDesc(String reservationType);


    /** findByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) - 특정 기간 내 예약 조회
     *
     */
    @Query("SELECT r FROM Reservation r WHERE r.startDate >= :startDate AND r.endDate <= :endDate")
    List<Reservation> findByDateRange(
        @Param("startDate") OffsetDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );


    /** findByRoomId(long roomId) - roomId 의 예약내역 조회
     *
     * */
    @Query("SELECT r FROM Reservation r WHERE r.roomId = :roomId and r.bookingStatus = 'DONE'")
    List<Reservation> findByRoomId(@Param("roomId") long roomId);


    /** deleteByBookingStatusAndReservationRegDateBefore(String bookingStatus, OffsetDateTime currentDate)
     *  - 특정 시간 이전의 PENDING 상태 레코드 삭제
     *
     */
    void deleteByBookingStatusAndReservationRegDateBefore(String bookingStatus, java.time.OffsetDateTime currentDate);

  // ===== Top N 예약 숙소 집계 =====
  // ================================================================

  /**
   * Projection: roomId 별 예약 건수
   */
  interface RoomBookingCount {
    Long getRoomId();
    Long getCnt();
  }

  /**
   * roomId 별 예약 건수를 집계하여 내림차순으로 반환.
   * - 취소된 예약(CANCELLED) 제외
   * - Pageable 로 Top N 개만 조회 가능
   */
  @Query("""
        SELECT r.roomId    AS roomId,
               COUNT(r)    AS cnt
          FROM Reservation r
         WHERE r.bookingStatus <> 'CANCELLED'
         GROUP BY r.roomId
         ORDER BY COUNT(r) DESC
    """)
  List<RoomBookingCount> findTopBookedRooms(Pageable pageable);
}
