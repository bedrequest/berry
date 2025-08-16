package com.berry.project.repository.lodge;

import com.berry.project.dto.lodge.ListOptionDTO;
import com.berry.project.dto.lodge.LodgeOptionDTO;
import com.berry.project.entity.lodge.Lodge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LodgeCustomRepository {

  Page<Lodge> searchLodges(ListOptionDTO listOptionDTO, LodgeOptionDTO lodgeOptionDTO, Pageable pageable);

  Page<Lodge> pageLodge(Pageable pageable, String keyword);
}
