package com.berry.project.repository.user;

import com.berry.project.entity.user.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

  List<AuthUser> findByUserId(Long userId);

  void deleteByUserId(Long userid);
}
