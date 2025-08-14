package com.berry.project.handler;

import com.berry.project.dto.review.ReviewImageDTO;
import com.berry.project.entity.review.ReviewImage;
import com.berry.project.repository.review.ReviewImageRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReviewFileHandler {
  private final ReviewImageRepository reviewImageRepository;
  // 실제 파일이 저장될 최상위 디렉토리 (프로젝트 환경에 맞게 수정)
  private final String UPLOAD_DIR = "D:/berry_file";

  //리뷰 ID와 업로드 파일 배열을 받아, 물리 저장 및 DB 저장 후 DTO 리스트 반환
  public List<ReviewImageDTO> storeReviewFiles(Long reviewId, MultipartFile[] files) {
    // 1) 오늘 날짜 폴더 생성
    String datePath = LocalDate.now().toString().replace("-", File.separator);
    File folder = new File(UPLOAD_DIR, datePath);
    if (!folder.exists() && !folder.mkdirs()) {
      throw new RuntimeException("업로드 디렉토리 생성 실패: " + folder.getAbsolutePath());
    }

    List<ReviewImageDTO> result = new ArrayList<>();
    // 2) 각 파일 처리
    for (MultipartFile file : files) {
      if (file.isEmpty()) continue;
      try {
        // UUID 생성 및 저장 파일명
        String uuid = UUID.randomUUID().toString();
        String original = file.getOriginalFilename();
        String saveName = uuid + "_" + original;
        String thumbName = uuid + "_th_" + original;

        // 물리 저장
        File dest = new File(folder, saveName);
        file.transferTo(dest);

        // 이미지 3× 해상도 썸네일 생성 (630×630)
        if (file.getContentType() != null && file.getContentType().startsWith("image")) {
          Thumbnails.of(dest)
              .size(630, 630)            // 3배 크기로 설정
              .outputQuality(0.9f)        // 품질 90%
              .toFile(new File(folder, thumbName));
        }

        // 엔티티 생성 & 저장
        ReviewImage img = ReviewImage.builder()
            .reviewUuid(uuid)
            .reviewId(reviewId)
            .reviewSaveDir(datePath)
            .reviewFileName(original)
            .reviewFileSize(file.getSize())
            .build();
        ReviewImage saved = reviewImageRepository.save(img);

        // DTO 생성 및 저장
        result.add(ReviewImageDTO.builder()
            .reviewUuid(saved.getReviewUuid())
            .reviewId(saved.getReviewId())
            .reviewSaveDir(saved.getReviewSaveDir())
            .reviewFileName(saved.getReviewFileName())
            .reviewFileSize(saved.getReviewFileSize())
            .regDate(saved.getRegDate())
            .modDate(saved.getModDate())
            .build());
      } catch (Exception e) {
        throw new RuntimeException("리뷰 파일 처리 중 오류 발생", e);
      }
    }

    return result;
  }

  public void deleteFile(ReviewImage img) {
    // 저장된 폴더 경로
    File folder = new File(UPLOAD_DIR, img.getReviewSaveDir());
    // 원본
    File original = new File(folder, img.getReviewUuid() + "_" + img.getReviewFileName());
    if (original.exists()) {
      original.delete();
    }
    // 썸네일
    File thumb = new File(folder, img.getReviewUuid() + "_th_" + img.getReviewFileName());
    if (thumb.exists()) {
      thumb.delete();
    }
  }

}

