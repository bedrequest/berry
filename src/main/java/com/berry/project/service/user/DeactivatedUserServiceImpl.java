package com.berry.project.service.user;

import com.berry.project.dto.user.DeactivatedUserDTO;
import com.berry.project.repository.payment.CuponRepository;
import com.berry.project.repository.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeactivatedUserServiceImpl implements DeactivatedUserService {

  private final UserRepository userRepository;
  private final DeactivatedUserRepository deactivatedUserRepository;
  private final AuthUserRepository authUserRepository;
  private final AlarmRepository alarmRepository;
  private final UserBookmarkRepository userBookmarkRepository;
  // YSL, Cupon TABLE 을 사용하기 위한 초기화
  private final CuponRepository cuponRepository;

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

    // alarm 삭제
    alarmRepository.deleteByUserId(userid);
    
    // 북마크 삭제
    userBookmarkRepository.deleteByUserId(userid);

    // duorpeb, Cupon Table 에서 해당 User 의 Cupon 삭제
    cuponRepository.deleteByUserId(userid);
  }
}
