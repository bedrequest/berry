package com.berry.project.service.lodge;

import com.berry.project.dto.lodge.*;
import com.berry.project.entity.lodge.*;
import com.berry.project.entity.review.ReviewSummary;
import com.berry.project.handler.PagingHandler;
import com.berry.project.repository.lodge.*;
import com.berry.project.repository.payment.ReservationRepository;
import com.berry.project.repository.review.ReviewRepository;
import com.berry.project.repository.review.ReviewSummaryRepository;
import com.berry.project.repository.review.ReviewTagRepository;
import com.berry.project.repository.review.ReviewSummaryRepository;
import com.berry.project.util.FacilityMaskDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class LodgeServiceImpl implements LodgeService {

  private final LodgeRepository lodgeRepository;
  private final LodgeImgRepository lodgeImgRepository;
  private final WaysRepository waysRepository;
  private final RoomRepository roomRepository;
  private final RoomImgRepository roomImgRepository;
  private final LodgeDescriptionRepository lodgeDescriptionRepository;
  private final ReviewRepository reviewRepository;
  private final ReviewSummaryRepository reviewSummaryRepository;

  private final FacilityMaskDecoder facilityMaskDecoder;

  private final ReservationRepository reservationRepository;
  private final ObjectMapper objectMapper;

  @Override
  public LodgeDTO detail(long lodgeId, LodgeOptionDTO lodgeOptionDTO) {
    Optional<Lodge> optionalLodge = lodgeRepository.findById(lodgeId);
    if (optionalLodge.isEmpty()) return null;

    LodgeDTO lodgeDTO = convertEntityToDto(
        optionalLodge.get(),
        facilityMaskDecoder,
        lodgeDescriptionRepository.findByLodgeId(optionalLodge.get().getLodgeId()),
        reviewRepository.countByLodgeId(lodgeId),
        reviewRepository.findAverageRatingByLodgeId(lodgeId).orElse(0.0) * 2,
        null);
    fillImages(lodgeDTO);
    fillRooms(lodgeDTO, true);

    lodgeDTO.setWays(
        waysRepository.findByLodgeId(lodgeDTO.getLodgeId())
            .stream().map(Ways::getContent)
            .toList());

    return lodgeDTO;
  }

  @Override
  public PagingHandler<LodgeDTO> getLodgeList(int pageNo, ListOptionDTO listOptionDTO, LodgeOptionDTO lodgeOptionDTO) {
    Pageable pageable = PageRequest.of(pageNo - 1, 10);

    Page<LodgeDTO> result = lodgeRepository.searchLodges(listOptionDTO, lodgeOptionDTO, pageable)
        .map(this::convertEntityToDtoWithoutReview);

    for (LodgeDTO lodgeDTO : result) {
      fillImages(lodgeDTO);
      fillRooms(lodgeDTO, false);

      lodgeDTO.setAverageReviewScore(
          reviewRepository.findAverageRatingByLodgeId(lodgeDTO.getLodgeId())
              .orElse(0.0) * 2);
      lodgeDTO.setReviewCount(reviewRepository.countByLodgeId(lodgeDTO.getLodgeId()));
    }

    return new PagingHandler<>(result, listOptionDTO);
  }

  private LodgeDTO convertEntityToDtoWithoutReview(Lodge lodge) {
    return convertEntityToDto(
        lodge,
        facilityMaskDecoder,
        lodgeDescriptionRepository.findByLodgeId(lodge.getLodgeId()),
        0, null, null);
  }

  private void fillImages(LodgeDTO lodgeDTO) {
    lodgeDTO.setLodgeImages(
        lodgeImgRepository.findByLodgeId(lodgeDTO.getLodgeId())
            .stream().map(LodgeImg::getLodgeImgUrl)
            .toList());
  }

  private void fillRooms(LodgeDTO lodgeDTO, boolean withImage) {
    for (Room room : roomRepository.findByLodgeId(lodgeDTO.getLodgeId())) {
      RoomDTO roomDTO = convertEntityToDto(room);
      if (withImage) {
        roomDTO.setRoomImageUrls(
            roomImgRepository.findByRoomId(roomDTO.getRoomId())
                .stream().map(RoomImg::getRoomImgUrl)
                .toList());
        if (roomDTO.getRoomImageUrls() == null || roomDTO.getRoomImageUrls().isEmpty())
          roomDTO.setRoomImageUrls(List.of("http://image.goodchoice.kr/adimg_new/49461/673938/9e048d5653205e43f216a07978dc8321.jpg"));
      }
      lodgeDTO.getRooms().add(roomDTO);
    }
  }

  private void fillReviewStatus(LodgeDTO lodgeDTO) {
    lodgeDTO.setAverageReviewScore(
        reviewRepository.findAverageRatingByLodgeId(lodgeDTO.getLodgeId())
            .orElse(0.0)
    );
    lodgeDTO.setReviewCount(
        reviewRepository.countByLodgeId(lodgeDTO.getLodgeId())
    );
  }

  // ===== Top N 예약 숙소 집계 =====
  @Override
  public List<LodgeSummaryDTO> getTopBookedLodges(int topN) {
    // 1) 룸 단위 TopN 예약 집계 호출
    var topRooms = reservationRepository.findTopBookedRooms(PageRequest.of(0, topN));

    // 2) 룸Id → 숙소Id 매핑 후 DTO 생성
    return topRooms.stream().map(rc -> {
      Long roomId = rc.getRoomId();
      Long count = rc.getCnt();

      // a) room → lodgeId
      var room = roomRepository.findById(roomId)
          .orElseThrow(() -> new IllegalArgumentException("룸이 없습니다: " + roomId));
      Long lodgeId = room.getLodgeId();

      // b) 이하 기존 로직(숙소 조회, 이미지, 가격, 태그, AI요약)
      var lodge = lodgeRepository.findById(lodgeId)
          .orElseThrow(() -> new IllegalArgumentException("숙소가 없습니다: " + lodgeId));
      String imgUrl = lodgeImgRepository
          .findFirstByLodgeIdOrderByLodgeImgIdAsc(lodgeId)  // 수정된 메서드
          .map(LodgeImg::getLodgeImgUrl)                    // 엔티티에서 URL 추출
          .orElse("/images/default_lodge.jpg");
      Integer minPrice = roomRepository.findMinStayPriceByLodgeId(lodgeId);
      if (minPrice == null || minPrice <= 0) {
        minPrice = roomRepository.findMinRentPriceByLodgeId(lodgeId);
      }

      Map<String, Integer> stats = reviewRepository.findTagCountsByLodgeId(lodgeId)
          .stream().collect(Collectors.toMap(
              ReviewRepository.TagCount::getTagName,
              tc -> tc.getCnt().intValue()
          ));
      String statsJson = "{}";
      try {
        statsJson = objectMapper.writeValueAsString(stats);
      } catch (Exception ignored) {
      }

      String aiSum = reviewSummaryRepository.findByLodgeId(lodgeId)
              .map(ReviewSummary::getSummaryText)
              .orElseGet(() ->
                      lodgeDescriptionRepository.findByLodgeId(lodgeId)
                              .stream().map(LodgeDescription::getContent)
                              .findFirst().orElse("")
              );

      return new LodgeSummaryDTO(
          lodgeId,
          lodge.getLodgeName(),
          lodge.getLodgeAddr(),
          minPrice,
          imgUrl,
          count,
          statsJson,
          aiSum
      );
    }).collect(Collectors.toList());
  }

}
