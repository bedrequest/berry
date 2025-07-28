package com.berry.project.repository.qna;

import com.berry.project.entity.qna.CustomerIqFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerIqFileRepository extends JpaRepository<CustomerIqFile, String> {

  List<CustomerIqFile> findByBno(long bno);

}
