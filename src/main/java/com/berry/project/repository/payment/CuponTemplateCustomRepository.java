package com.berry.project.repository.payment;

import com.berry.project.entity.cupon.CuponTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CuponTemplateCustomRepository {
  Page<CuponTemplate> pageCuponTemplate(String keyword, Pageable pageable);
}
