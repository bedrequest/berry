package com.berry.project.repository.payment;

import com.berry.project.entity.payment.PaymentReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface PaymentReceiptRepository extends JpaRepository<PaymentReceipt, String>, PaymentReceiptCustomRepository {
    
    /** findByOrderId(String orderId) - orderId로 결제 영수증 조회
     *
     */
    Optional<PaymentReceipt> findByOrderId(String orderId);


    /** List"<"PaymentReceipt> findByStatusOrderByApprovedAtDesc(String status)
     *  - 결제 상태별 조회
     *
     */
    List<PaymentReceipt> findByStatusOrderByApprovedAtDesc(String status);


    /** List"<"PaymentReceipt> findByMethodOrderByApprovedAtDesc(String method) - 결제 방법별 조회
     *
     */
    List<PaymentReceipt> findByMethodOrderByApprovedAtDesc(String method);


    /** List"<"PaymentReceipt> findByDateRange(LocalDateTime start, LocalDateTime end)
     *  - 특정 기간 내 결제 내역 조회
     *
     */
    @Query("SELECT p FROM PaymentReceipt p WHERE p.approvedAt >= :startDate AND p.approvedAt <= :endDate")
    List<PaymentReceipt> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );


    /** List"<"PaymentReceipt> findByMinAmount(@Param("minAmount") int minAmount)
     *  - 특정 금액 이상의 결제 내역 조회
     */
    @Query("SELECT p FROM PaymentReceipt p WHERE p.totalAmount >= :minAmount")
    List<PaymentReceipt> findByMinAmount(@Param("minAmount") int minAmount);


    /** List"<"PaymentReceipt> findByOrderNameContainingOrderByApprovedAtDesc(String orderName)
     *  - 주문명으로 검색
     */
    List<PaymentReceipt> findByOrderNameContainingOrderByApprovedAtDesc(String orderName);


    /** List"<"PaymentReceipt> findByTypeOrderByApprovedAtDesc(String type)
     *  - 결제 타입별 조회
     */
    List<PaymentReceipt> findByTypeOrderByApprovedAtDesc(String type);


}
