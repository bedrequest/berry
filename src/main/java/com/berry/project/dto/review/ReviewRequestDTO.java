package com.berry.project.dto.review;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 리뷰 생성 및 수정 요청용 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDTO {
  @NotNull(message = "아이디는 필수입니다.")
  private Long userId;

  @NotNull(message = "숙소이름은 필수입니다.")
  private Long lodgeId;

  private Long reservationId;

  @NotNull(message = "별점은 필수입니다.")
  @Min(value = 1, message = "최소 별점은 1 이상이어야 합니다.")
  @Max(value = 5, message = "최대 별점은 5 이하이어야 합니다.")
  private Integer rating;

  @NotBlank(message = "리뷰를 적어주세요.")
  private String content;

  @Size(max = 3, message = "태그는 최대 3개까지 선택 가능합니다.")
  private List<String> tagNames;

  private Long reviewId;


  private MultipartFile[] files;
  private String userEmail;
  private List<String> deleteImageUuids;
}
