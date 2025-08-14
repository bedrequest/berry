package com.berry.project.repository.qna;

import com.berry.project.entity.qna.CustomerIqBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface CustomerIqBoardRepository extends JpaRepository<CustomerIqBoard, Long>, CustomerIqBoardCustomRepository{
  /* duropeb, 카테고리 별 필터 */
  @Query("SELECT c FROM CustomerIqBoard c WHERE c.category = :category ")
  Page<CustomerIqBoard> findByCategory(@Param("category") String category, Pageable pageable);
}
