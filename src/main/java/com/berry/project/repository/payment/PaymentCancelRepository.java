package com.berry.project.repository.payment;

import com.berry.project.entity.payment.PaymentCancel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface PaymentCancelRepository extends JpaRepository<PaymentCancel, Long>, PaymentCancelCustomRepository{
    
    /** List"<"PaymentCancel> findByPaymentKeyOrderByCanceledAtDesc(String paymentKey) - paymentKey 로
     *  취소 내역 조회
     *
     */
    List<PaymentCancel> findByPaymentKeyOrderByCanceledAtDesc(String paymentKey);


    /** Optional"<"PaymentCancel> findByTransactionKey(String transactionKey) - transactionKey로 취소 내역 조회
     *
     */
    Optional<PaymentCancel> findByTransactionKey(String transactionKey);


    /** List"<"PaymentCancel> findByCancelReasonContainingOrderByCanceledAtDesc(String cancelReason)
     *  - 취소 사유별 조회
     *
     */
    List<PaymentCancel> findByCancelReasonContainingOrderByCanceledAtDesc(String cancelReason);


    /** List"<"PaymentCancel> findByDateRange(LocalDateTime start, LocalDateTime end)
     *  - 특정 기간 내 취소 내역 조회
     */
    @Query("SELECT p FROM PaymentCancel p WHERE p.canceledAt >= :startDate AND p.canceledAt <= :endDate")
    List<PaymentCancel> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    
    /** List"<"PaymentCancel> findByMinCancelAmount(@Param("minAmount") int minAmount)
     *  - 특정 금액 이상의 취소 내역 조회
     */
    @Query("SELECT p FROM PaymentCancel p WHERE p.cancelAmount >= :minAmount")
    List<PaymentCancel> findByMinCancelAmount(@Param("minAmount") int minAmount);


    /** boolean existsByPaymentKey(String paymentKey) - paymentKey 존재 여부 확인
     *
     */
    boolean existsByPaymentKey(String paymentKey);
}
