package com.berry.project.service.user;

import com.berry.project.dto.user.DeactivatedUserDTO;
import com.berry.project.repository.user.AuthUserRepository;
import com.berry.project.repository.user.DeactivatedUserRepository;
import com.berry.project.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeactivatedUserServiceImpl implements DeactivatedUserService{

  private final UserRepository userRepository;
  private final DeactivatedUserRepository deactivatedUserRepository;
  private final AuthUserRepository authUserRepository;

  @Transactional
  @Override
  public void registerDeactivatedUser(DeactivatedUserDTO deactivatedUserDTO) {
    // 비활성 계정 테이블에 추가
    Long userid = deactivatedUserRepository.save(convertDTOToEntity(deactivatedUserDTO)).getUserId();
    log.info("registerDeactivatedUser userId >> {}", userid);

    // User Table 에서 삭제.
    userRepository.deleteById(userid);

    // AuthUser Table 에서 권한 삭제
    authUserRepository.deleteByUserId(userid);


  }
}
