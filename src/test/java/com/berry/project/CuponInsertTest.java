package com.berry.project;

import com.berry.project.entity.cupon.Cupon;
import com.berry.project.repository.payment.CuponRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.stream.IntStream;

@SpringBootTest
public class CuponInsertTest {
  // Repository 초기화
  @Autowired
  private CuponRepository cuponRepository;

  @Test
  void insertCuponType1Records(){
    IntStream.rangeClosed(10, 160).forEach(i -> {
      Cupon cupon = new Cupon();

      cupon.setUserId(i);
      cupon.setCuponType(1);
      cupon.setCuponRegDate(OffsetDateTime.now());
      cupon.setCuponEndDate(OffsetDateTime.now().plusDays(90));
      cupon.setValid(true);

      cuponRepository.save(cupon);
    });
  }
}
