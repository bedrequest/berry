package com.berry.project.repository.payment;

import com.berry.project.entity.cupon.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface CuponRepository extends JpaRepository<Cupon, Long> {
    
    /**
     * 사용자 ID로 쿠폰 목록 조회
     */
    List<Cupon> findByUserIdOrderByCuponRegDateDesc(Long userId);
    
    /**
     * 유효한 쿠폰만 조회
     */
    @Query("SELECT c FROM Cupon c WHERE c.userId = :userId AND c.isValid = true AND c.cuponEndDate > :currentDate")
    List<Cupon> findValidCuponsByUserId(
        @Param("userId") Long userId,
        @Param("currentDate") LocalDateTime currentDate
    );
    
    /**
     * 쿠폰 타입별 조회
     */
    List<Cupon> findByCuponTypeOrderByCuponRegDateDesc(Integer cuponType);
    
    /**
     * 만료된 쿠폰 조회
     */
    @Query("SELECT c FROM Cupon c WHERE c.cuponEndDate < :currentDate")
    List<Cupon> findExpiredCupons(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * 특정 기간에 발급된 쿠폰 조회
     */
    @Query("SELECT c FROM Cupon c WHERE c.cuponRegDate >= :startDate AND c.cuponRegDate <= :endDate")
    List<Cupon> findByIssueDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * 사용자별 유효한 쿠폰 개수 조회
     */
    @Query("SELECT COUNT(c) FROM Cupon c WHERE c.userId = :userId AND c.isValid = true AND c.cuponEndDate > :currentDate")
    long countValidCuponsByUserId(
        @Param("userId") Long userId,
        @Param("currentDate") LocalDateTime currentDate
    );
}
