package com.berry.project.service.user;

import com.berry.project.dto.alarm.AlarmDTO;
import com.berry.project.dto.lodge.RoomDTO;
import com.berry.project.dto.user.BookmarkLodgeDTO;
import com.berry.project.dto.user.MyPageReservationDTO;
import com.berry.project.dto.user.UserBookmarkDTO;
import com.berry.project.dto.user.UserDTO;
import com.berry.project.entity.alarm.Alarm;
import com.berry.project.entity.cupon.Cupon;
import com.berry.project.handler.payment.CuponHandler;
import com.berry.project.repository.payment.CuponRepository;
import com.berry.project.entity.lodge.Lodge;
import com.berry.project.entity.lodge.LodgeImg;
import com.berry.project.entity.lodge.Room;
import com.berry.project.entity.payment.Reservation;
import com.berry.project.entity.user.AuthUser;
import com.berry.project.entity.user.User;
import com.berry.project.entity.user.UserBookmark;
import com.berry.project.repository.lodge.LodgeImgRepository;
import com.berry.project.repository.lodge.LodgeRepository;
import com.berry.project.repository.lodge.RoomRepository;
import com.berry.project.repository.payment.ReservationRepository;
import com.berry.project.repository.review.ReviewRepository;
import com.berry.project.repository.user.AlarmRepository;
import com.berry.project.repository.user.AuthUserRepository;
import com.berry.project.repository.user.UserBookmarkRepository;
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
  private final UserBookmarkRepository userBookmarkRepository;
  private final AlarmRepository alarmRepository;
  private final ReviewRepository reviewRepository;
  // YSL, 쿠폰 발급을 위한 초기화
  private final CuponRepository cuponRepository;
  private final ReservationRepository reservationRepository;
  private final RoomRepository roomRepository;
  private final LodgeImgRepository lodgeImgRepository;
  private final LodgeRepository lodgeRepository;
  private final CuponHandler cuponHandler;

  // 소셜로그인 중복검사
  @Transactional
  @Override
  public UserDTO isSocialDuplicateUser(String userUid) {

    Optional<User> optional = userRepository.findByUserUid(userUid);
    if (optional.isEmpty()) {
      return null;
    }

    User user = optional.get();
    List<AuthUser> authUserList = authUserRepository.findByUserId(user.getUserId());


    UserDTO userDTO = convertEntityToUserDTO(user, authUserList.stream()
        .map(this::convertEntityToAuthDTO)
        .toList());

    return userDTO;
  }

  // 소셜로그인 저장
  @Transactional
  @Override
  public void insertOauthUser(UserDTO userDTO) {

    Long userId = userRepository.save(oauthConvertUserDTOToUserEntity(userDTO)).getUserId();
    log.info("impl userId >> {}", userId);
    log.info("userDTO userID >> {}", userDTO);
    userDTO.setUserId(userId);

    if (userId > 0) {
      authUserRepository.save(convertUserDTOToAuthEntity(userDTO));

      // 회원가입 알림 저장
      Alarm alarm = Alarm.builder()
          .userId(userId) // 로그인한 유저 id
          .targetId(userId) // 타겟 테이블 id
          .code("s_signup") // 알림 코드 (자기가 직접 작성하거나 만들어놓은 예시 참고)
          .build();

      alarmRepository.save(alarm);

      // duorpeb, 쿠폰 발급
      Cupon registerCupon = cuponHandler.callCuponGenerate(1, userId);

      if(registerCupon != null) {
        Long cuponId = cuponRepository.save(registerCupon).getCuponId();

        alarm = Alarm.builder()
            .userId(userId)
            .targetId(cuponId)
            .code("newSign_coupon")
            .build();
        alarmRepository.save(alarm);
      }
    }

  }

  // principal username 으로 userDTO return
  @Transactional
  @Override
  public UserDTO selectUserEmail(String username) {

    List<User> userList = userRepository.findByUserEmail(username);
    UserDTO userDTO = new UserDTO();
    for (User user : userList) {
      if (user.getProvider().equals("web")) {
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
    for (User user : userList) {
      if (user.getProvider().equals("web")) {
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

    for (User user : userList) {
      if (user.getProvider().equals("web")) {
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
    if (userId > 0) {
      authUserRepository.save(convertUserDTOToAuthEntity(userDTO));

      // duorpeb, 쿠폰 발급 - 변경 전
//      Cupon registerCupon
//          = Cupon.builder()
//                 .userId(userId)
//                 .cuponType(1)
//                 .cuponEndDate(OffsetDateTime.now().plusDays(180))
//                 .isValid(true)
//                 .build();

      // duorpeb, 쿠폰 발급 - 변경 후
      Cupon registerCupon = cuponHandler.callCuponGenerate(1, userId);

      // 회원가입 알림 저장
      Alarm alarm = Alarm.builder()
          .userId(userId)
          .targetId(userId)
          .code("s_signup")
          .build();

      alarmRepository.save(alarm);
      
      // registerCupon 이 null 이 아닌 경우에만 쿠폰 발급
      if(registerCupon != null){
        Long cuponId = cuponRepository.save(registerCupon).getCuponId();

        alarm = Alarm.builder()
            .userId(userId)
            .targetId(cuponId)
            .code("newSign_coupon")
            .build();

        alarmRepository.save(alarm);
      }
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

    if (webUserDTO != null) {
      return webUserDTO;
    } else if (optional.isPresent()) {
      // oauth 인 경우
      List<AuthUser> authUserList = authUserRepository.findByUserId(optional.get().getUserId());
      UserDTO oauthUserDTO = convertEntityToUserDTO(optional.get(),
          authUserList.stream().map(this::convertEntityToAuthDTO).toList());

      return oauthUserDTO;
    }

    return null;
  }

  // id 로 유저 조회
  @Override
  public UserDTO getUserFindById(Long userId) {

    Optional<User> optional = userRepository.findById(userId);
    if (optional.isPresent()) {
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
  public String userInfoUpdate(UserDTO userDTO) {
    Optional<User> optional = userRepository.findById(userDTO.getUserId());
    if (optional.isPresent()) {
      String userEmail= optional.get().getUserEmail();
      // 수정값은 이메일, 이름, 휴대폰번호, 선호태그
      User user = optional.get();
      user.setUserName(userDTO.getUserName());

      if (!user.getUserPhone().equals(userDTO.getUserPhone())) {
        // 휴대폰 번호가 변경이 되었을 경우
        user.setUserPhone(userDTO.getUserPhone());
        log.info("휴대폰 번호 변경");

        // 휴대폰 번호가 변경이 되면 휴대폰 인증 여부를 false 로 변경
        user.setMobileCertified(false);
      }
      if (!user.getUserName().equals(userDTO.getUserName())) {
        // 이름이 변경이 되었을 경우
        user.setUserName(userDTO.getUserName());
      }
      if (!user.getUserEmail().equals(userDTO.getUserEmail())) {
        // 이메일이 변경 되었을 경우
        user.setUserEmail(userDTO.getUserEmail());
        log.info("이메일 변경");

        // 이메일 변경이 되면 이메일 인증 여부를 false 로 변경
        user.setEmailCertified(false);

      }
      if (user.getUserFavoriteTag() != userDTO.getUserFavoriteTag()) {
        log.info("선호태그 변경");
        // 선호태그가 변경이 되었을 경우
        user.setUserFavoriteTag(userDTO.getUserFavoriteTag());
      }
      return userEmail;
    }
    return null;
  }

  @Transactional
  @Override
  public Long updateMobileCertified(Long userId) {

    Optional<User> optional = userRepository.findById(userId);
    if (optional.isPresent()) {
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
    if (optional.isPresent()) {
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
    if (optional.isPresent()){
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

    // lodgeId 추출
    List<Long> lodgeIds = roomList.stream().map(Room::getLodgeId).toList();

    // LodgeImg 조회
    List<LodgeImg> lodgeImgList = lodgeImgRepository.findByLodgeIdIn(lodgeIds);

    // Lodge 조회
    List<Lodge> lodgeList = lodgeRepository.findByLodgeIdIn(lodgeIds);

    if (reservationList != null) {
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

        List<String> lodgeImageUrls = lodgeImgList.stream()
            .filter(img -> img.getLodgeId() == room.getLodgeId())
            .map(LodgeImg::getLodgeImgUrl)
            .toList();

        // MyPageReservationDTO 생성 후 반환
        return MyPageReservationDTO.builder()
            // reservation
            .reservationId(reservation.getReservationId())
            .roomId(reservation.getRoomId())
            .userId(reservation.getUserId())
            .orderId(reservation.getOrderId())
            .startDate(reservation.getStartDate().toLocalDateTime())
            .bookingStatus(reservation.getBookingStatus())
            .endDate(reservation.getEndDate().toLocalDateTime())
            .totalAmount(reservation.getTotalAmount())
            .guestsAmount(reservation.getGuestsAmount())
            .reservationType(reservation.getReservationType())
            .reservationRegDate(reservation.getReservationRegDate().toLocalDateTime())
            // room
            .roomName(room != null ? room.getRoomName() : null)
            .rentTime(room != null ? room.getRentTime() : null)
            .stayTime(room != null ? room.getStayTime() : null)
            // lodge
            .lodgeId(lodge != null ? lodge.getLodgeId() : null)
            .lodgeName(lodge != null ? lodge.getLodgeName() : null)
            .lodgeAddr(lodge != null ? lodge.getLodgeAddr() : null)
            .lodgeType(lodge != null ? lodge.getLodgeType() : null)
            .businessCall(lodge != null ? lodge.getBusinessCall() : null)
            // lodgeImg
            .lodgeImageUrls(lodgeImageUrls)
            .build();
      }).toList();

      return reservationDTOList;
    }

    return null;
  }

  @Transactional
  @Override
  public List<BookmarkLodgeDTO> getBookmarkLodgeList(Long userId) {

    // 1. userId 를 통해 bookmarkId 조회
    List<UserBookmark> userBookmarkList = userBookmarkRepository.findByUserIdOrderByRegDateDesc(userId);
    log.info("impl userBookmarkList >> {}", userBookmarkList);

    // 2. 조회한 bookmarkId 를 통해 lodgeId 조회
    List<Long> lodgeIds = userBookmarkList.stream().map(UserBookmark::getLodgeId).toList();
    log.info("impl lodgeIds >> {}", lodgeIds);

    // 3. lodgeId 를 통해 lodge 조회
    List<Lodge> lodgeList = lodgeRepository.findByLodgeIdIn(lodgeIds);
    log.info("impl lodgeList >> {}", lodgeList);

    // 4. lodgeImg 조회
    List<LodgeImg> lodgeImgList = lodgeImgRepository.findByLodgeIdIn(lodgeIds);
    log.info("impl lodgeImgList >> {}", lodgeImgList);

    // 5. room 조회
    List<Room> roomList = roomRepository.findByLodgeIdIn(lodgeIds);
    log.info("impl roomList >> {}", roomList);

    if (userBookmarkList != null) {
      List<BookmarkLodgeDTO> bookmarkLodgeDTOList = userBookmarkList.stream().map(userBookmark -> {
        Room room = roomList.stream()
            .filter(r -> r.getLodgeId() == userBookmark.getLodgeId())
            .findFirst()
            .orElse(null);

        Lodge lodge = (userBookmark.getLodgeId() != null) ?
            lodgeList.stream()
                .filter(l -> l.getLodgeId().equals(room.getLodgeId()))
                .findFirst()
                .orElse(null)
            : null;

        List<String> lodgeImageUrls = lodgeImgList.stream()
            .filter(img -> img.getLodgeId() == room.getLodgeId())
            .map(LodgeImg::getLodgeImgUrl)
            .toList();

        // roomDTO
        List<RoomDTO> rooms = roomList.stream()
            .filter(room1 -> room1.getLodgeId() == lodge.getLodgeId())
            .map(this::convertEntityToDto).toList();
        log.info("impl rooms >> {}", rooms);


        // bookmarkLodgeDTO builder
        return BookmarkLodgeDTO.builder()
            .userId(userBookmark.getUserId())
            .bookmarkId(userBookmark.getUserBookmarkId())
            .lodgeId(lodge != null ? lodge.getLodgeId() : null)
            .lodgeType(lodge != null ? lodge.getLodgeType() : null)
            .lodgeAddr(lodge != null ? lodge.getLodgeAddr() : null)
            .lodgeName(lodge != null ? lodge.getLodgeName() : null)
            .lodgeImages(lodgeImageUrls)
            .rooms(rooms)
            .averageReviewScore(reviewRepository.findAverageRatingByLodgeId(lodge.getLodgeId()).orElse(0.0))
            .reviewCount(reviewRepository.countByLodgeId(lodge.getLodgeId()))
            .build();
      }).toList();

      return bookmarkLodgeDTOList;
    }

    return null;
  }

  @Transactional
  @Override
  public Long toggleBookmark(UserBookmarkDTO userBookmarkDTO) {
    log.info(">> User Bookmark Input > {}", userBookmarkDTO);

    Optional<UserBookmark> optional = userBookmarkRepository.findByUserIdAndLodgeId(userBookmarkDTO.getUserId(), userBookmarkDTO.getLodgeId());
    log.info(">> search result > {}", optional);

    if (optional.isPresent()) {
      // 있다면 지우고 0 return
      userBookmarkRepository.delete(optional.get());

      return 0L;
    } else {
      // 없으면 DB 에 저장 후 Id return
      Long isOk = userBookmarkRepository.save(convertUserBookmarkDTOToUserBookmarkEntity(userBookmarkDTO)).getUserId();

      return isOk;
    }

  }

  @Override
  public List<AlarmDTO> getAlarmList(Long userId) {
    List<Alarm> alarmList = alarmRepository.findByUserIdOrderByRegDateDesc(userId);

    List<AlarmDTO> alarmDTOList = alarmList.stream().map(this::convertAlarmEntityToAlarmDTO).toList();

    return alarmDTOList;
  }

  // 이메일로 유저 존재 여부 찾기 (비밀번호 재설정)
  @Override
  public Long findWebUserEmail(String userEmail) {

    UserDTO userDTO = getWebUserDTO(userEmail);

    if (userDTO == null) {
      return 0L;
    }

    return userDTO.getUserId();
  }

  private UserDTO getWebUserDTO(String userEmail) {
    UserDTO userDTO = new UserDTO();
    List<User> userList = userRepository.findByUserEmail((userEmail));
    for (User user : userList) {
      if (user.getProvider().equals("web")) {
        List<AuthUser> authUserList = authUserRepository.findByUserId(user.getUserId());
        userDTO = convertEntityToUserDTO(user, authUserList.stream().map(this::convertEntityToAuthDTO).toList());
        return userDTO;
      }
    }
    return null;
  }


}
