package com.berry.project.repository.user;

import com.berry.project.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {
  Optional<User> findByUserUid(String userUid);

  List<User> findByUserEmail(String username);
}
