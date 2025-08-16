package com.berry.project.repository.user;

import com.berry.project.entity.user.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

  List<AuthUser> findByUserId(Long userId);

  void deleteByUserId(Long userid);

  /* duorpeb, findAuthRoleByUserId() - userId 에 맞는 권한 정보 한번에 가져오기 */
  List<AuthUser> findByUserIdIn(Collection<Long> allIds);
}
