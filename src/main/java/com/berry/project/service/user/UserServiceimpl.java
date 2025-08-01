package com.berry.project.service.user;

import com.berry.project.dto.user.MyPageReservationDTO;
import com.berry.project.dto.user.UserDTO;
import com.berry.project.entity.lodge.Lodge;
import com.berry.project.entity.lodge.Room;
import com.berry.project.entity.lodge.RoomImg;
import com.berry.project.entity.payment.Reservation;
import com.berry.project.entity.user.AuthUser;
import com.berry.project.entity.user.User;
import com.berry.project.repository.lodge.LodgeRepository;
import com.berry.project.repository.lodge.RoomImgRepository;
import com.berry.project.repository.lodge.RoomRepository;
import com.berry.project.repository.payment.ReservationRepository;
import com.berry.project.repository.user.AuthUserRepository;
import com.berry.project.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceimpl implements UserService {

  private final UserRepository userRepository;
  private final AuthUserRepository authUserRepository;
  private final ReservationRepository reservationRepository;
  private final RoomRepository roomRepository;
  private final RoomImgRepository roomImgRepository;
  private final LodgeRepository lodgeRepository;

  // 소셜로그인 중복검사
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
  
  // 소셜로그인 저장
  @Transactional
  @Override
  public void insertOauthUser(UserDTO userDTO) {

    Long userId = userRepository.save(oauthConvertUserDTOToUserEntity(userDTO)).getUserId();
    log.info("impl userId >> {}",userId);
    log.info("userDTO userID >> {}", userDTO);
    userDTO.setUserId(userId);

    if(userId > 0){
      authUserRepository.save(convertUserDTOToAuthEntity(userDTO));
    }

  }

  // principal username 으로 userDTO return
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

  // lastLogin 갱신
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

  // 소셜로그인 lastLogin 갱신
  @Transactional
  @Override
  public void updateSocialLastLogin(UserDTO userDTO) {
    Optional<User> optional = userRepository.findByUserUid(userDTO.getUserUid());
    if(optional.isPresent()){
      User user = optional.get();
      user.setLastLogin(LocalDateTime.now());
    }
  }

  // 이메일 중복검사
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

  // 웹 회원가입 저장
  @Transactional
  @Override
  public Long registerUser(UserDTO userDTO) {

    Long userId = userRepository.save(oauthConvertUserDTOToUserEntity(userDTO)).getUserId();

    userDTO.setUserId(userId);
    if(userId > 0){
      authUserRepository.save(convertUserDTOToAuthEntity(userDTO));
    }

    return userId;
  }

  // uid 로 유저 조회
  @Override
  public UserDTO getUserInfo(String userUid) {
    
    // web 인 경우
    UserDTO webUserDTO = getWebUserDTO(userUid);
    log.info("getUserInfo userDTO >>> {}", webUserDTO);
    Optional<User> optional = userRepository.findByUserUid(userUid);

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

  // id 로 유저 조회
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

  // 회원정보수정
  @Transactional
  @Override
  public void userInfoUpadate(UserDTO userDTO) {
    Optional<User> optional = userRepository.findById(userDTO.getUserId());
    if(optional.isPresent()){
      // 수정값은 이메일, 이름, 휴대폰번호, 선호태그
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
      } else if (!user.getUserEmail().equals(userDTO.getUserEmail())) {
        // 이메일이 변경 되었을 경우
        user.setUserEmail(userDTO.getUserEmail());

        // 이메일 변경이 되면 이메일 인증 여부를 false 로 변경
        user.setEmailCertified(false);

      } else if (user.getUserFavoriteTag() != userDTO.getUserFavoriteTag()){
        // 선호태그가 변경이 되었을 경우
        user.setUserFavoriteTag(userDTO.getUserFavoriteTag());
      }
    }


  }

  @Transactional
  @Override
  public Long updateMobileCertified(Long userId) {

    Optional<User> optional = userRepository.findById(userId);
    if(optional.isPresent()){
      User user = optional.get();
      user.setMobileCertified(true);
      return userId;
    }

    return null;
  }

  @Transactional
  @Override
  public Long updateEmailCertified(Long userId) {
    Optional<User> optional = userRepository.findById(userId);
    if(optional.isPresent()){
      User user = optional.get();
      user.setEmailCertified(true);
      return userId;
    }

    return null;
  }

  @Transactional
  @Override
  public void updatePassword(String changePassword, Long userId) {
    Optional<User> optional = userRepository.findById(userId);
    if(optional.isPresent()){
      User user = optional.get();
      user.setPassword(changePassword);
    }
  }

  // myPage 에서 출력 할 예약내역
  @Transactional
  @Override
  public List<MyPageReservationDTO> getReservationList(Long userId) {
    List<Reservation> reservationList = reservationRepository.findByUserIdOrderByReservationRegDateDesc(userId);

    // 뒤에 In 을 붙히면 JPA 에서 In 쿼리를 사용한다
    // 파라미터가 List or Collection 일 경우에 사용.

    // roomId 추출
    List<Long> roomIds = reservationList.stream().map(Reservation::getRoomId).toList();
    log.info("roomIds >>>> {}", roomIds);

    // Room 조회
    List<Room> roomList = roomRepository.findByRoomIdIn(roomIds);
    log.info("roomList >>>> {}", roomList);

    // RoomImg 조회
    List<RoomImg> roomImgList = roomImgRepository.findByRoomIdIn(roomIds);

    // lodgeId 추출
    List<Long> lodgeIds = roomList.stream().map(Room::getLodgeId).toList();

    // Lodge 조회
    List<Lodge> lodgeList = lodgeRepository.findByLodgeIdIn(lodgeIds);

    if(reservationList != null){
      List<MyPageReservationDTO> reservationDTOList = reservationList.stream().map(reservation -> {

        Room room = roomList.stream()
            .filter(r -> r.getRoomId().equals(reservation.getRoomId()))
            .findFirst()
            .orElse(null);

        Lodge lodge = (room != null) ?
            lodgeList.stream()
                .filter(l -> l.getLodgeId().equals(room.getLodgeId()))
                .findFirst()
                .orElse(null)
            : null;

        List<String> roomImageUrls = roomImgList.stream()
            .filter(img -> img.getRoomId() == reservation.getRoomId())
            .map(RoomImg::getRoomImgUrl)
            .toList();

        // MyPageReservationDTO 생성 후 반환
        return MyPageReservationDTO.builder()
            // reservation
            .reservationId(reservation.getReservationId())
            .roomId(reservation.getRoomId())
            .userId(reservation.getUserId())
            .orderId(reservation.getOrderId())
            .startDate(reservation.getStartDate())
            .bookingStatus(reservation.getBookingStatus())
            .endDate(reservation.getEndDate())
            .totalAmount(reservation.getTotalAmount())
            .guestsAmount(reservation.getGuestsAmount())
            .reservationType(reservation.getReservationType())
            .reservationRegDate(reservation.getReservationRegDate())
            // room
            .roomName(room != null ? room.getRoomName() : null)
            .rentTime(room != null ? room.getRentTime() : null)
            .stayTime(room != null ? room.getStayTime() : null)
            // lodge
            .lodgeName(lodge != null ? lodge.getLodgeName() : null)
            .lodgeAddr(lodge != null ? lodge.getLodgeAddr() : null)
            .lodgeType(lodge != null ? lodge.getLodgeType() : null)
            .businessCall(lodge != null ? lodge.getBusinessCall() : null)
            // roomImg
            .roomImageUrls(roomImageUrls)
            .build();
          }).toList();

      return reservationDTOList;
    }

//    if(reservationList != null){
//      List<MyPageReservationDTO> reservationDTOList = reservationList.stream()
//          .map(reservation -> printConvertReservationEntityToReservationDto(reservation)).toList();
//      return reservationDTOList;
//    }

    return null;
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
