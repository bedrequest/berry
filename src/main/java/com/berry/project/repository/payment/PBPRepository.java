package com.berry.project.repository.payment;

import com.berry.project.entity.payment.PaymentBeforePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface PBPRepository extends JpaRepository<PaymentBeforePayment, Long> {
  /**
   * findByOrderId(String orderId) - orderId로 결제 전 정보 조회
   *
   */
  Optional<PaymentBeforePayment> findByOrderId(String orderId);


  /**
   * findByCustomerKeyOrderByOrderRegDateDesc(String customerKey) - customerKey로 결제 전 정보 조회
   *
   */
  List<PaymentBeforePayment> findByCustomerKeyOrderByOrderRegDateDesc(String customerKey);


  /**
   * existsByOrderId(String orderId) - orderId 존재 여부 확인
   *
   */
  boolean existsByOrderId(String orderId);


  /**
   * findByDateRange (@Param("startDate") LocalDateTime startDate,
   * "@Param("endDate") LocalDateTime endDate) - 특정 기간 내 결제 전 정보 조회
   *
   *
   */
  @Query("SELECT p FROM PaymentBeforePayment p WHERE p.orderRegDate >= :startDate AND p.orderRegDate <= :endDate")
  List<PaymentBeforePayment> findByDateRange(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate
  );


  /**
   * findByCuponIdOrderByOrderRegDateDesc(Long cuponId) - 쿠폰 ID로 결제 전 정보 조회
   *
   *
   */
  List<PaymentBeforePayment> findByCuponIdOrderByOrderRegDateDesc(Long cuponId);


  /**
   * findByMinAmount(@Param("minAmount") int minAmount) - 특정 금액 이상의 결제 전 정보 조회
   *
   */
  @Query("SELECT p FROM PaymentBeforePayment p WHERE p.strikePrice >= :minAmount")
  List<PaymentBeforePayment> findByMinAmount(@Param("minAmount") int minAmount);


  /**
   * findByOrderNameContainingOrderByOrderRegDateDesc(String orderName) - 주문명으로 검색
   *
   */
  List<PaymentBeforePayment> findByOrderNameContainingOrderByOrderRegDateDesc(String orderName);


  /**
   * deleteByOrderRegDateBefore(OffsetDateTime oneHourAgo) - 특정 시간 이전의 레코드 삭제
   *
   */
  void deleteByOrderRegDateBefore(java.time.OffsetDateTime oneHourAgo);
}
