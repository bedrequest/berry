package com.berry.project.repository.payment;

import com.berry.project.dto.admin.AdminReservationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationCustomRepository {
  Page<AdminReservationDTO> findAdminReservations(Pageable pageable, String keyword);
}