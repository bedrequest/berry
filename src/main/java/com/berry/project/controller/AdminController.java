package com.berry.project.controller;

import com.berry.project.dto.cupon.CuponTemplateDTO;
import com.berry.project.dto.cupon.JsCuponTemplateDTO;
import com.berry.project.handler.admin.AdminPagingHandler;
import com.berry.project.handler.admin.CuponImgFileHandler;
import com.berry.project.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;


/**
 *
 *  > 데이터 전달 시 각 카테고리를 구분할 수 있는 수를 함께 전달
 *
 * */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/*")
public class AdminController {
  // 초기화
  private final AdminService adminService;
  private final CuponImgFileHandler cuponImgFileHandler;

  /** adminMain() - 관리자 페이지 */
  @GetMapping("/admin-main")
  public void adminMain(){

  }


  /** cuponTemplate() - CuponTemplate TABLE 의 Record 가져오기 */
  @GetMapping("/cupon-templates")
  @ResponseBody
  public List<CuponTemplateDTO> cuponTemplate(){
    List<CuponTemplateDTO> cuponTemplateDTOList = adminService.getCuponTemplateDTOList();

    return cuponTemplateDTOList;
  }


  /** cuponGenerate(@RequestBody CuponTemplateDTO ctDto) - 쿠폰 생성
   * */
  @PostMapping("/cupon-generate")
  public String cuponGenerate(@RequestParam(name="cuponImg")MultipartFile cuponImgFile, JsCuponTemplateDTO jsCtDto) throws IOException {
    // 초기화
    OffsetDateTime cuponEndDate = null;

    /** input type="datetime-local" 로 받은 LocalDateTime 을 OffsetDateTime 으로 변환
     * */
    if(jsCtDto.getCuponEndDate() != null){
      cuponEndDate = jsCtDto.getCuponEndDate().atOffset(ZoneOffset.ofHours(9));
    }

    // 파일 저장
    CuponTemplateDTO cuponTemplateDTO = cuponImgFileHandler.upCuponImgFile(cuponImgFile, jsCtDto, cuponEndDate);

    adminService.insertCuponTemplate(cuponTemplateDTO);

    return "redirect:/admin/admin-main";
  }


  /** fragments() - JS 의 요청을 받아 HTML Fragment 를 반환
   *
   *  > 각 th:fragment 안에서 데이터가 없는 경우를 처리
   *
   *  > 해당 매핑은 페이지네이션이 필요 없는 HTML 과 매핑하는 컨트롤러
   *
   *  > fragments 로 할당하는 경우, class="list-up-container".innerHTML 에 할당
   * */
  @GetMapping("/fragments/{name}")
  public String fragments(@PathVariable String name, Model m){
    String targetUrl = "";

    switch(name){
      // 쿠폰 생성 클릭 시 반환하는 th:fragment
      case "cupon-generate":
         targetUrl = "admin/admin-fragments :: " + name; break;

      default:
        break;
    }

    /* 문자열 앞에 슬래시 붙이면 안됨 */
    return targetUrl;
  }


  /** list() - 페이지네이션이 적용된 프래그먼트를 반환
   *
   *  > 단일 진입점 (/admin/list) 에서 요소 별로 다르게 라우팅 (adminService.getPagingFragments) 하고
   *    항상 AdminPagingHandler 를 반환해서 템플릿은 한 타입만 바라보게 함
   *
   *  > list() 로 할당하는 fragment 의 경우 class="sort-detail".innerHTML 의 <option> 을 frag 에 맞게
   *    변경해야 함
   *
   * */
  @GetMapping("/list")
  public String list(Model m, @RequestParam(name="frag") String frag, @RequestParam(name="pageNo", defaultValue = "1") int pageNo
  , @RequestParam(name="qty", defaultValue = "10") int qty, @RequestParam(name="sortType", required = false) String sortType
  , @RequestParam(name="keyword", required=false) String keyword, @RequestParam(name="dataSet", required = false, defaultValue = "0") Integer dataSet
  , @RequestParam(name="filterType", required = false) String filterType){
    // 초기화
    String targetUrl = "";

    // 페이지네이션
    AdminPagingHandler<?> ph = adminService.getPagingFragments(frag, pageNo, qty, sortType, keyword, dataSet, filterType);
    m.addAttribute("ph", ph);
    
    // frag 별로 분기
    switch(frag){
      // 쿠폰 관리 - 쿠폰 발급 및 삭제 클릭 시 반환하는 th:fragment=cupon-delete
      case "cupon-delete":
        targetUrl = "admin/admin-fragments-paging :: " + frag;
        break;

      // 쿠폰 관리 - 생성한 쿠폰 클릭 시 반환하는 th:fragment=cupon-manage
      case "cupon-manage":
        targetUrl = "admin/admin-fragments-paging :: " + frag;

      // 멤버 관리 - 전체 유저 보기 클릭 시 반환하는 th:fragment=all-user
      case "all-user":
        targetUrl = "admin/admin-fragments-paging :: " + frag;
        break;

      // 고객 문의 관리 클릭 시 반환하는 th:fragment=qna
      case "qna-payment", "qna-cancel", "qna-facilities", "qna-service", "qna-others" :
        targetUrl = "admin/admin-fragments-paging :: qna";
        break;

      // 결제 관리 - 결제 완료 내역 클릭 시 반환하는 th:fragment=payment-completed
      case "payment-completed":
        targetUrl = "admin/admin-fragments-paging :: " + frag;

      // 결제 관리 - 환불 완료 내역 클릭 시 반환하는 th:fragment=payment-cancel
      case "payment-cancel":
        targetUrl = "admin/admin-fragments-paging :: " + frag;

      // 리뷰 관리 - 신고 내역 클릭 시 반환하는 th:fragment=review-report
      case "review-report":
        targetUrl = "admin/admin-fragments-paging :: " + frag;

      // 예약 내역 관리 - 예약 내역 클릭 시 반환하는 th:fragment=reservation-order
      case "reservation-order":
        targetUrl = "admin/admin-fragments-paging :: " + frag;

      // 예약 내역 관리 - 숙소 별 정보 조회 클릭 시 반환하는 th:fragment=reservation-lodge
      case "reservation-lodge":
        targetUrl = "admin/admin-fragments-paging :: " + frag;

      default :
        break;
    }

    return targetUrl;
  }


  /** ctDel(@RequestParam(name="ct-id") String ctId) - 쿠폰 삭제 */
  @DeleteMapping("/ct-del")
  @ResponseBody
  public String ctDel(@RequestParam(name="ct-id") String ctId){
    String isOk = "-1";

    if(adminService.deleteCuponTemplate(ctId)){
      isOk = "1";
    }

    return isOk;
  }


  /* cuponAllGen(@RequestParam(name="ct-id") String ctId) - 쿠폰 발급 */
  @PostMapping("/cupon-all-gen")
  @ResponseBody
  public String cuponAllGen(@RequestParam(name="ct-id") String ctId){
    String isOk = "-1";

    if(adminService.insertCuponAllUser(ctId)){ isOk = "1"; }

    return isOk;
  }


  /* qnaDeleteElem() - 관리자 페이지에서 해당 고객문의 글 삭제 */
  @DeleteMapping("/qna-del")
  @ResponseBody
  public String qnaDel(@RequestParam(name="bno") long bno){
    String isOk = "-1";

    if(adminService.deleteQnaElem(bno)){
      isOk = "1";
    }

    return isOk;
  }


  /* reviewDeleteElem() - 관리자 페이지에서 해당 리뷰 삭제 */
  @DeleteMapping("/review-del")
  @ResponseBody
  public String reviewDel(@RequestParam(name="r-no") long reviewId){
    String isOk = "-1";

    if(adminService.deleteReviewElem(reviewId)){
      isOk = "1";
    }

    return isOk;
  }

}
