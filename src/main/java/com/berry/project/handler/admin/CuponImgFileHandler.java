package com.berry.project.handler.admin;

import com.berry.project.dto.cupon.CuponTemplateDTO;
import com.berry.project.dto.cupon.JsCuponTemplateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;


/** CuponImgFileHandler - 쿠폰 이미지 파일 핸들러
 *
 *  > 쿠폰 생성 시 쿠폰 이미지는 쿠폰 하나당 한개
 *
 *  > 마이 페이지에서 이미지 사용 시 /upload/_cuponImg/${CuponTemplateDTO.cuponImgName}
 *
 * */
@Slf4j
@Component
public class CuponImgFileHandler {
  // 초기화
   // 파일 경로 설정
  private final String UP_DIR = "D:\\berry_file\\_cuponImg";


  /* upCuponImgFile() - 쿠폰 생성 시 이미지 파일을 폴더에 저장하는 메서드
  *
  *  > 클라이언트 (JS) 단에서 이미지 파일 외에 업로드 안되게 막아놓음
  *
  *
  * */
  public CuponTemplateDTO upCuponImgFile(MultipartFile cuponImgFile, JsCuponTemplateDTO jsCtDto, OffsetDateTime cuponEndDate) throws IOException {
    // 폴더 초기화
    File uploadDir = new File(UP_DIR);

    // 디렉터리가 없으면 하위 경로까지 생성
    if(!uploadDir.exists()){
      boolean isCreated = uploadDir.mkdirs();

      if(!isCreated){ throw new IOException("쿠폰 이미지 업로드용 폴더 생성 실패 : " + UP_DIR); }
    }

    // 파일 이름 생성
    String strUUID = UUID.randomUUID().toString();
    String cuponImgFileName = strUUID + "_" +  cuponImgFile.getOriginalFilename();

    // 파일 초기화
    File saveFile = new File(UP_DIR, cuponImgFileName);

    // 파일 저장
    cuponImgFile.transferTo(saveFile);

    return CuponTemplateDTO
      .builder()
      .cuponType(jsCtDto.getCuponType())
      .cuponTitle(jsCtDto.getCuponTitle())
      .cuponPrice(jsCtDto.getCuponPrice())
      .theMinimumAmount(jsCtDto.getTheMinimumAmount())
      .cuponImgName(cuponImgFileName)
      .cuponEndDate(cuponEndDate)
      .qty(jsCtDto.getQty())
      .build();
  }


  /** removeCuponImgFile() - 폴더에 저장한 쿠폰 이미지 파일 삭제
   *
   * */
  public boolean removeCuponImgFile(CuponTemplateDTO cuponTemplateDTO){
    // 초기화
     // return 용 변수
    boolean isOk = false;
     // 파일 초기화 - 이 객체 자체는 디스크에 어떤 작업도 하지않고 단순히 그 경로 (디렉터리나 파일) 를 가리키는 역할만 수행
    File fileDir = new File(UP_DIR, cuponTemplateDTO.getCuponImgName());

    try {
      // 파일이 존재한다면 삭제
      if(fileDir.exists()){
        isOk = fileDir.delete();

      }

    } catch (Exception e){
      e.printStackTrace();
    }

    // 확인
    log.info("쿠폰 이미지 파일 삭제 작업 : {}", isOk);

    return isOk;
  }
}
