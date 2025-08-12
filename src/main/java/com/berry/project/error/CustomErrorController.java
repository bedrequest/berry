package com.berry.project.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class CustomErrorController implements ErrorController {

  private final ErrorAttributes errorAttributes;

  @RequestMapping("/error")
  public String handleError(WebRequest webRequest, Model model) {
    String title = null, content = null;

    // 에러 속성들 가져오기
    Map<String, Object> errorDetails = errorAttributes.getErrorAttributes(
        webRequest, ErrorAttributeOptions.of(
            ErrorAttributeOptions.Include.MESSAGE,
            ErrorAttributeOptions.Include.EXCEPTION,
            ErrorAttributeOptions.Include.STACK_TRACE,
            ErrorAttributeOptions.Include.STATUS
        )
    );

    log.info(">>>> error trace");
    log.info(errorDetails.get("trace").toString());

    String code = errorDetails.get("status").toString();

    switch (code) {
      case "404" -> {
        title = "찾을 수 없는 페이지에요";
        content = "찾고 있는 페이지 주소가 변경 또는 삭제되었을 수 있어요.";
      }
      case "500" -> {
        title = "기술 문제로 연결이 끊어졌어요";
        content = "페이지를 새로 고침 해주세요.<br>문제가 계속되면 고객센터로 문의해주세요.";
      }
      default -> {
        title = "오류가 발생했어요";
        content = "다시 시도해주세요.<br>문제가 계속되면 고객센터로 문의해주세요.";
      }
    }

    content += "<br>[에러코드:" + code + "]";
    model.addAttribute("title", title);
    model.addAttribute("content", content);
    return "error";
  }
}
