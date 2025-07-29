package com.berry.project.service.user;

import com.berry.project.dto.user.UserDTO;
import com.berry.project.entity.cupon.Cupon;
import com.berry.project.entity.user.AuthUser;
import com.berry.project.entity.user.User;
import com.berry.project.repository.payment.CuponRepository;
import com.berry.project.repository.user.AuthUserRepository;
import com.berry.project.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceimpl implements UserService {

  private final UserRepository userRepository;
  private final AuthUserRepository authUserRepository;
  // YSL, 쿠폰 발급을 위한 초기화
  private final CuponRepository cuponRepository;

  @Transactional
  @Override
  public UserDTO isSocialDuplicateUser(String userUid) {

    Optional<User> optional = userRepository.findByUserUid(userUid);
    if(optional.isEmpty()){
      return null;
    }

    User user = optional.get();
    List<AuthUser> authUserList = authUserRepository.findByUserId(user.getUserId());


      UserDTO userDTO = convertEntityToUserDTO(user, authUserList.stream()
          .map(this :: convertEntityToAuthDTO)
          .toList());

      return userDTO;
  }

  @Transactional
  @Override
  public void insertOauthUser(UserDTO userDTO) {

    Long userId = userRepository.save(oauthConvertUserDTOToUserEntity(userDTO)).getUserId();
    log.info("impl userId >> {}",userId);
    log.info("userDTO userID >> {}", userDTO);
    userDTO.setUserId(userId);

    if(userId > 0){
      authUserRepository.save(convertUserDTOToAuthEntity(userDTO));

      // duorpeb, 쿠폰 발급
      Cupon registerCupon
          = Cupon.builder()
          .userId(userId)
          .cuponType(0)
          .cuponEndDate(OffsetDateTime.now().plusDays(180))
          .isValid(true)
          .build();

      cuponRepository.save(registerCupon);
    }

  }

  @Transactional
  @Override
  public UserDTO selectUserEmail(String username) {

    List<User> userList = userRepository.findByUserEmail(username);
    UserDTO userDTO = new UserDTO();
    for(User user : userList){
      if(user.getProvider().equals("web")){
        List<AuthUser> authUserList = authUserRepository.findByUserId(user.getUserId());
        userDTO = convertEntityToUserDTO(user, authUserList.stream().map(this::convertEntityToAuthDTO).toList());
        return userDTO;
      }
    }
    return null;
  }

  @Transactional
  @Override
  public boolean updateLastLogin(String username) {

    List<User> userList = userRepository.findByUserEmail(username);
    for(User user : userList){
      if(user.getProvider().equals("web")){
        user.setLastLogin(LocalDateTime.now());
        log.info("updateLastLogin >>> user {}", user);
        return true;
      }
    }

    return false;
  }

  @Transactional
  @Override
  public void updateSocialLastLogin(UserDTO userDTO) {
    Optional<User> optional = userRepository.findByUserUid(userDTO.getUserUid());
    if(optional.isPresent()){
      User user = optional.get();
      user.setLastLogin(LocalDateTime.now());
    }
  }

  @Override
  public Long isDuplicateUser(String userEmail) {

    List<User> userList = userRepository.findByUserEmail(userEmail);
    log.info("isDuplicateUser userEmail >> {}", userEmail);

    for(User user : userList){
      if(user.getProvider().equals("web")){
        return user.getUserId(); // DB 찾아서 존재하면 id return
      }
    }

    return 0L; // 없으면 0 return
  }

  @Transactional
  @Override
  public Long registerUser(UserDTO userDTO) {

    Long userId = userRepository.save(oauthConvertUserDTOToUserEntity(userDTO)).getUserId();

    userDTO.setUserId(userId);
    if(userId > 0){
      authUserRepository.save(convertUserDTOToAuthEntity(userDTO));

      // duorpeb, 쿠폰 발급
      Cupon registerCupon
          = Cupon.builder()
                 .userId(userId)
                 .cuponType(0)
                 .cuponEndDate(OffsetDateTime.now().plusDays(180))
                 .isValid(true)
                 .build();

      cuponRepository.save(registerCupon);
    }

    return userId;
  }

  @Override
  public UserDTO getUserInfo(String username) {
    
    // web 인 경우
    UserDTO webUserDTO = getWebUserDTO(username);
    log.info("getUserInfo userDTO >>> {}", webUserDTO);
    Optional<User> optional = userRepository.findByUserUid(username);

    if(webUserDTO != null){
      return webUserDTO;
    }else if(optional.isPresent()){
    // oauth 인 경우
    List<AuthUser> authUserList = authUserRepository.findByUserId(optional.get().getUserId());
    UserDTO oauthUserDTO = convertEntityToUserDTO(optional.get(),
        authUserList.stream().map(this :: convertEntityToAuthDTO).toList());

      return oauthUserDTO;
    }

    return null;
  }

  @Override
  public UserDTO getUserFindById(Long userId) {

    Optional<User> optional = userRepository.findById(userId);
    if(optional.isPresent()){
      List<AuthUser> authUserList = authUserRepository.findByUserId(userId);
      UserDTO userDTO = convertEntityToUserDTO(optional.get(),
          authUserList.stream().map(this::convertEntityToAuthDTO).toList());
      return userDTO;
    }

    return null;
  }

  @Transactional
  @Override
  public void userInfoUpadate(UserDTO userDTO) {
    Optional<User> optional = userRepository.findById(userDTO.getUserId());
    if(optional.isPresent()){
      // 수정값은 이름, 휴대폰번호
      User user = optional.get();
      user.setUserName(userDTO.getUserName());

      if(!user.getUserPhone().equals(userDTO.getUserPhone())){
        // 휴대폰 번호가 변경이 되었을 경우
        user.setUserPhone(userDTO.getUserPhone());
        
        // 휴대폰 번호가 변경이 되면 휴대폰 인증 여부를 false 로 변경
        user.setMobileCertified(false);
        
      } else if (!user.getUserName().equals(userDTO.getUserName())){
        // 이름이 변경이 되었을 경우
        user.setUserName(userDTO.getUserName());
      }
    }


  }

  private UserDTO getWebUserDTO(String userEmail){
    UserDTO userDTO = new UserDTO();
    List<User> userList = userRepository.findByUserEmail((userEmail));
    for(User user : userList){
      if(user.getProvider().equals("web")){
        List<AuthUser> authUserList = authUserRepository.findByUserId(user.getUserId());
        userDTO = convertEntityToUserDTO(user, authUserList.stream().map(this::convertEntityToAuthDTO).toList());
      return userDTO;
      }
    }
    return null;
  }

}
