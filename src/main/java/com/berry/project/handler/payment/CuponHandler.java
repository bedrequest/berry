package com.berry.project.handler.payment;

import com.berry.project.entity.cupon.Cupon;
import com.berry.project.entity.cupon.CuponTemplate;
import com.berry.project.entity.user.User;
import com.berry.project.repository.payment.CuponRepository;
import com.berry.project.repository.payment.CuponTemplateRepository;
import com.berry.project.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** CuponHandler
 *
 *  > 현재 쿠폰 유형
 *    1,
 *
 * */
@Component
@RequiredArgsConstructor
public class CuponHandler {
  // 초기화
   // CuponTemplate TABLE
  private final CuponTemplateRepository cuponTemplateRepository;
   // User TABLE
  private final UserRepository userRepository;
   // Cupon Repository
  private final CuponRepository cuponRepository;

  /** callCuponGenerate() - cuponType 을 파라미터로 받아 new Cupon() 을 return 하는 메서드
   *
   *  > ServiceImpl 에서 bulid().bulider() 를 사용하여 save() 하는 것과 handler 를 통해 save() 하는 것의
   *    차이점은 없고 JPA save() 관점에선 두 방식 모두 INSERT/UPDATE 동작이 똑같이 이루어짐
   *
   * */
  public Cupon callCuponGenerate(int cuponType, Long userId){
    // 초기화
    // 쿠폰 생성
    Cupon cupon = new Cupon();

    // cuponType 으로 CuponTemplate TABLE 에서 찾고 없으면 예외 던지기
    Optional<CuponTemplate> ct = cuponTemplateRepository.findByCuponType(cuponType);

    // 레코드가 있는 경우
    if(ct.isPresent()){
      // cupon 초기화
      // userId
      cupon.setUserId(userId);
      // 쿠폰 타입
      cupon.setCuponType(cuponType);
      // 쿠폰 유효기간 null 이면 현재 날짜로부터 +180
      if(ct.get().getCuponEndDate() == null){
        cupon.setCuponEndDate(OffsetDateTime.now().plusDays(180));

      }
        // 쿠폰 유효기간이 있으면 해당 유효기간으로 설정
        else {
          cupon.setCuponEndDate(ct.get().getCuponEndDate());
      }
      // 쿠폰의 유효성
      cupon.setValid(true);

      return cupon;
    }

    return null;
  }


  /** callIssuanceCuponAllUser()
   *  - 관리자 페이지에서 일괄 발급 버튼 클릭 시 모든 유저에게 쿠폰 발급하는 메서드
   *
   *  */
  public void callIssuanceCuponAllUser(CuponTemplate cuponTemplate){
    // 초기화
    OffsetDateTime cuponEndDateTimeInfo;

    // 모든 사용자 조회
    List<User> allUser = userRepository.findAll();

    // 조건 초기화
    if(cuponTemplate.getCuponEndDate() == null){
       cuponEndDateTimeInfo = OffsetDateTime.now().plusDays(180);
    }
      else{
        cuponEndDateTimeInfo = cuponTemplate.getCuponEndDate();
    }

    // 각 사용자에게 발급될 쿠폰 엔티티 생성
    List<Cupon> cuponsIssuance = allUser
        .stream().map(
            user -> Cupon.builder()
                               .userId(user.getUserId())
                               .cuponType(cuponTemplate.getCuponType())
                               .cuponRegDate(cuponEndDateTimeInfo)
                               .isValid(true)
                               .build()
        ).collect(Collectors.toList());

    // 한 번에 저장
    cuponRepository.saveAll(cuponsIssuance);
  }

}
