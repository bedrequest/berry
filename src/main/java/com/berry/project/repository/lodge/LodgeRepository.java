package com.berry.project.repository.lodge;

import com.berry.project.entity.lodge.Lodge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LodgeRepository extends JpaRepository<Lodge, Long>, LodgeCustomRepository {
}
