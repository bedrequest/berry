package com.berry.project.repository.qna;

import com.berry.project.entity.qna.CustomerIqBoard;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerIqBoardRepository extends JpaRepository<CustomerIqBoard, Long>, CustomerIqBoardCustomRepository{
}
