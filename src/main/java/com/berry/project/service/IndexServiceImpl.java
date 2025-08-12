package com.berry.project.service;

import com.berry.project.dto.ReviewLodgeDTO;
import com.berry.project.dto.lodge.LodgeDTO;
import com.berry.project.dto.lodge.LodgeWithTagCountDTO;
import com.berry.project.dto.lodge.RoomDTO;
import com.berry.project.entity.lodge.LodgeImg;
import com.berry.project.handler.PagingHandler;
import com.berry.project.repository.lodge.LodgeImgRepository;
import com.berry.project.repository.lodge.LodgeRepository;
import com.berry.project.repository.lodge.RoomRepository;
import com.berry.project.repository.review.ReviewRepository;
import com.berry.project.repository.review.ReviewTagMappingRepository;
import com.berry.project.util.TagMaskDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class IndexServiceImpl implements IndexService {

  private final LodgeRepository lodgeRepository;
  private final ReviewRepository reviewRepository;
  private final ReviewTagMappingRepository reviewTagMappingRepository;
  private final LodgeImgRepository lodgeImgRepository;
  private final RoomRepository roomRepository;

  private final TagMaskDecoder tagMaskDecoder;

  @Override
  public PagingHandler<LodgeWithTagCountDTO> getLodgeListByTag(int pageNo, int tagId) {
    Pageable pageable = PageRequest.of(pageNo - 1, 10);

    Page<LodgeWithTagCountDTO> result = lodgeRepository.searchByTag(tagId, pageable)
        .map(entry -> {
          List<String> lodgeImages = lodgeImgRepository.findByLodgeId(entry.getLodgeId())
              .stream().map(LodgeImg::getLodgeImgUrl)
              .toList();
          List<RoomDTO> roomDTOList = roomRepository.findByLodgeId(entry.getLodgeId())
              .stream().map(room -> RoomDTO.builder()
                  .roomId(room.getRoomId())
                  .lodgeId(room.getLodgeId())
                  .rentPrice(room.getRentPrice())
                  .rentTime(room.getRentTime())
                  .stayPrice(room.getStayPrice())
                  .stayTime(room.getStayTime())
                  .build()).toList();

          return new LodgeWithTagCountDTO(LodgeDTO.builder()
            .lodgeId(entry.getLodgeId())
            .lodgeName(entry.getLodgeName())
            .lodgeAddr(entry.getLodgeAddr())
            .lodgeImages(lodgeImages)
            .rooms(roomDTOList)
            .averageReviewScore(reviewRepository.findAverageRatingByLodgeId(entry.getLodgeId()).orElse(0.0))
            .reviewCount(reviewRepository.countByLodgeId(entry.getLodgeId()))
            .build(), reviewRepository.countReviewByLodgeIdAndTagId(entry.getLodgeId(), tagId));
        });
    return new PagingHandler<>(result);
  }

  @Override
  public List<ReviewLodgeDTO> getRecentReviews() throws NoSuchElementException {
    return reviewRepository.findTop10ByOrderByCreatedAtDesc()
        .stream().map(entry -> {
          Long reviewId = entry.getReviewId();
          List<String> tags = reviewTagMappingRepository.findByReviewId(reviewId)
              .stream()
              .map(tagMapping -> tagMaskDecoder.get(tagMapping.getTagId().intValue() - 1))
              .toList();
          Long lodgeId = entry.getLodgeId();

          return ReviewLodgeDTO.builder()
              .reviewId(entry.getReviewId())
              .userId(entry.getUserId())
              .lodgeId(entry.getLodgeId())
              .userEmail(entry.getUserEmail())
              .rating(entry.getRating())
              .content(entry.getContent())
              .createdAt(entry.getCreatedAt())
              .tags(tags)
              .lodgeName(lodgeRepository.findById(lodgeId).orElseThrow().getLodgeName())
              .lodgeImage(lodgeImgRepository.findFirstLodgeImage(lodgeId))
              .build();
        }).toList();
  }
}
