package com.berry.project.service.user;

import com.berry.project.dto.alarm.AlarmDTO;
import com.berry.project.dto.lodge.RoomDTO;
import com.berry.project.dto.user.*;
import com.berry.project.entity.alarm.Alarm;
import com.berry.project.entity.lodge.Room;
import com.berry.project.entity.user.AuthUser;
import com.berry.project.entity.user.User;
import com.berry.project.entity.user.UserBookmark;

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
        .userGrade("SILVER")
        .build();
  }

  default AuthUser convertUserDTOToAuthEntity(UserDTO userDTO) {
    return AuthUser.builder()
        .userId(userDTO.getUserId())
        .authRole("ROLE_USER")
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

  default UserBookmarkDTO convertUserBookmarkEntityToUserBookmarkDTO(UserBookmark userBookmark) {
    return UserBookmarkDTO.builder()
        .userBookmarkId(userBookmark.getUserBookmarkId())
        .userId(userBookmark.getUserId())
        .lodgeId(userBookmark.getLodgeId())
        .regDate(userBookmark.getRegDate())
        .modDate(userBookmark.getModDate())
        .build();
  }

  default UserBookmark convertUserBookmarkDTOToUserBookmarkEntity(UserBookmarkDTO userBookmarkDTO) {
    return UserBookmark.builder()
        .userId(userBookmarkDTO.getUserId())
        .lodgeId(userBookmarkDTO.getLodgeId())
        .build();
  }

  default Alarm convertAlarmDTOToAlarmEntity(AlarmDTO alarmDTO) {
    return Alarm.builder()
        .userId(alarmDTO.getUserId())
        .targetId(alarmDTO.getTargetId())
        .code(alarmDTO.getCode())
        .build();
  }

  default AlarmDTO convertAlarmEntityToAlarmDTO(Alarm alarm) {
    return AlarmDTO.builder()
        .alarmId(alarm.getAlarmId())
        .userId(alarm.getUserId())
        .targetId(alarm.getTargetId())
        .code(alarm.getCode())
        .regDate(alarm.getRegDate())
        .modDate(alarm.getModDate())
        .build();
  }

  // lodgeService 에서 가져온 convert
  default RoomDTO convertEntityToDto(Room room) {
    return RoomDTO.builder()
        .roomId(room.getRoomId())
        .lodgeId(room.getLodgeId())
        .roomName(room.getRoomName())
        .info(room.getInfo())
        .rentPrice(room.getRentPrice())
        .rentTime(room.getRentTime())
        .stayPrice(room.getStayPrice())
        .stayOption(room.getStayOption())
        .stayTime(room.getStayTime())
        .stockCount(room.getStockCount())
        .standardCount(room.getStandardCount())
        .maxCount(room.getMaxCount())
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

  String userInfoUpdate(UserDTO userDTO);

  Long updateMobileCertified(Long userId);

  Long updateEmailCertified(Long userId);

  void updatePassword(String changePassword, Long userId);

  List<MyPageReservationDTO> getReservationList(Long userId);

  Long toggleBookmark(UserBookmarkDTO userBookmarkDTO);

  List<AlarmDTO> getAlarmList(Long userId);

  Long findWebUserEmail(String userEmail);

  List<BookmarkLodgeDTO> getBookmarkLodgeList(Long userId);
}
