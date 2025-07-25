package com.berry.project.service.user;

import com.berry.project.dto.user.AuthUserDTO;
import com.berry.project.dto.user.UserDTO;
import com.berry.project.entity.user.AuthUser;
import com.berry.project.entity.user.User;

import java.util.List;

public interface UserService {

  default User oauthConvertUserDTOToUserEntity(UserDTO userDTO) {
    return User.builder()
        .userId(userDTO.getUserId())
        .userEmail(userDTO.getUserEmail())
        .password(userDTO.getPassword())
        .userPhone(userDTO.getUserPhone())
        .userName(userDTO.getUserName())
        .userUid(userDTO.getUserUid())
        .provider(userDTO.getProvider())
        .birthday(userDTO.getBirthday())
        .customerKey(userDTO.getCustomerKey())
        .isAdult(userDTO.isAdult())
        .lastLogin(userDTO.getLastLogin())
        .isEmailCertified(userDTO.isEmailCertified())
        .isMobileCertified(userDTO.isMobileCertified())
        .userTermOption(userDTO.isUserTermOption())
        .userGrade("silver")
        .build();
  }

  default AuthUser convertUserDTOToAuthEntity(UserDTO userDTO) {
    return AuthUser.builder()
        .userId(userDTO.getUserId())
        .authRole("USER_ROLE")
        .build();
  }

  default AuthUserDTO convertEntityToAuthDTO(AuthUser authUser) {
    return AuthUserDTO.builder()
        .authId(authUser.getAuthId())
        .userId(authUser.getUserId())
        .authRole(authUser.getAuthRole())
        .build();
  }

  default UserDTO convertEntityToUserDTO(User user, List<AuthUserDTO> authUserDTOList) {
    return UserDTO.builder()
        .userId(user.getUserId())
        .userEmail(user.getUserEmail())
        .password(user.getPassword())
        .userUid(user.getUserUid())
        .userGrade(user.getUserGrade())
        .userName(user.getUserName())
        .userFavoriteTag(user.getUserFavoriteTag())
        .userPhone(user.getUserPhone())
        .userTermOption(user.isUserTermOption())
        .regDate(user.getRegDate())
        .lastLogin(user.getLastLogin())
        .modDate(user.getModDate())
        .provider(user.getProvider())
        .birthday(user.getBirthday())
        .authList(authUserDTOList)
        .isAdult(user.isAdult())
        .isMobileCertified(user.isMobileCertified())
        .customerKey(user.getCustomerKey())
        .isEmailCertified(user.isEmailCertified())
        .build();
  }


  UserDTO isSocialDuplicateUser(String userUid);

  void insertOauthUser(UserDTO userDTO);

  UserDTO selectUserEmail(String username);

  boolean updateLastLogin(String name);

  void updateSocialLastLogin(UserDTO userDTO);

  Long registerUser(UserDTO userDTO);

  Long isDuplicateUser(String userEmail);

  UserDTO getUserInfo(String username);

  UserDTO getUserFindById(Long userId);

  void userInfoUpadate(UserDTO userDTO);
}
