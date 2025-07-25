package com.berry.project.repository.user;

import com.berry.project.entity.user.DeactivatedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeactivatedUserRepository extends JpaRepository<DeactivatedUser, Long> {
}
