package com.berry.project.repository.user;

import com.berry.project.entity.cupon.CuponTemplate;
import com.berry.project.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCustomRepository {
  Page<User> pageUser(String keyword, Pageable pageable);
}
