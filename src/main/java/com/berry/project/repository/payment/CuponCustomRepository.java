package com.berry.project.repository.payment;

import com.berry.project.entity.cupon.Cupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CuponCustomRepository {
  Page<Cupon> pageCuponType(String keyword, Pageable pageable, Integer dataSet);
}
