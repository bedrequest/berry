package com.berry.project.repository.payment;

import com.berry.project.entity.cupon.Cupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;


public interface CuponRepository extends JpaRepository<Cupon, Long>, CuponCustomRepository{
    
    /**  List"<"Cupon> findByUserIdOrderByCuponRegDateDesc(Long userId) - 사용자 ID로 쿠폰 목록 조회
     *
     */
    List<Cupon> findByUserIdOrderByCuponRegDateDesc(Long userId);


    /** findValidCuponsByUserId(Long userId, LocalDateTime currentDate) - 유효한 쿠폰만 조회
     *
     */
    @Query("SELECT c FROM Cupon c WHERE c.userId = :userId AND c.isValid = true")
    List<Cupon> findValidCuponsByUserId(@Param("userId") Long userId);


    /** List"<"Cupon> findByCuponTypeOrderByCuponRegDateDesc(Integer cuponType) - 쿠폰 타입별 조회
     *
     */
    Page<Cupon> findByCuponType(Integer cuponType, Pageable pageable);


    /** List"<"Cupon> findExpiredCupons(LocalDateTime currentDate) - 만료된 쿠폰 조회
     *
     */
    @Query("SELECT c FROM Cupon c WHERE c.cuponEndDate < :currentDate")
    List<Cupon> findExpiredCupons(@Param("currentDate") OffsetDateTime currentDate);


    /** List"<"Cupon> findByIssueDateRange(LocalDateTime start, LocalDateTime end) - 특정 기간에 발급된 쿠폰 조회
     *
     */
    @Query("SELECT c FROM Cupon c WHERE c.cuponRegDate >= :startDate AND c.cuponRegDate <= :endDate")
    List<Cupon> findByIssueDateRange(
        @Param("startDate") OffsetDateTime startDate,
        @Param("endDate") OffsetDateTime endDate
    );


    /** long countValidCuponsByUserId(Long userId, LocalDateTime currentDate) - 사용자별 유효한 쿠폰 개수 조회
     *
     */
    @Query("SELECT COUNT(c) FROM Cupon c WHERE c.userId = :userId AND c.isValid = true AND c.cuponEndDate > :currentDate")
    int countValidCuponsByUserId(
        @Param("userId") Long userId,
        @Param("currentDate") OffsetDateTime currentDate
    );


    /** deleteByUserId(@Param("userid") Long Userid) - userId 와 일치하는 모든 쿠폰을 삭제
     *
     * > @Param userId 삭제할 사용자의 ID
     *
     * > @Modifying 은 Spring Data JPA 에서 @Query 어노테이션과 함께 데이터 변경 (INSERT/UPDATE/DELETE)
     *   쿼리를 사용할 때 반드시 붙여야 하는 애노테이션
     *   
     * > @Modifying 메서드는 반드시 트랜잭션 경계 안에서 실행되어야 하기에 @Service 레이어의 메서드에 @Transactional
     *   을 걸거나 Repository 에서 메서드 위에 @Transactional을 붙여야 함
     * */
    @Modifying
    @Query("DELETE FROM Cupon c WHERE c.userId = :userid")
    void deleteByUserId(@Param("userid") Long userid);


    /** deleteByCuponEndDateAtBefore() - 유효 기간이 지난 쿠폰 삭제
     *
     * */
    @Modifying
    @Query("DELETE FROM Cupon c WHERE c.cuponEndDate < :currentDate")
    void deleteByCuponEndDateBefore(@Param("currentDate") OffsetDateTime currentDate);


    /** deleteByCuponType() */
    void deleteByCuponType(int cuponType);

}
