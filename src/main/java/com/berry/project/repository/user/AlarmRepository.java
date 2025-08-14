package com.berry.project.repository.user;

import com.berry.project.entity.alarm.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
  List<Alarm> findByUserIdOrderByRegDateDesc(Long userId);

  void deleteByUserId(Long userid);
}
