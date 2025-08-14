package com.berry.project.repository.payment;

import com.berry.project.entity.cupon.CuponTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuponTemplateRepository extends JpaRepository<CuponTemplate, Long>, CuponTemplateCustomRepository {
  // findByCuponType() - 쿠폰 타입으로 엔티티 조회
  Optional<CuponTemplate> findByCuponType(int cuponType);

}
