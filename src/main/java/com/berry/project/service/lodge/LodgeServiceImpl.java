package com.berry.project.service.lodge;

import com.berry.project.dto.lodge.ListOptionDTO;
import com.berry.project.dto.lodge.LodgeDTO;
import com.berry.project.dto.lodge.LodgeOptionDTO;
import com.berry.project.dto.lodge.RoomDTO;
import com.berry.project.entity.lodge.*;
import com.berry.project.handler.PagingHandler;
import com.berry.project.repository.lodge.*;
import com.berry.project.repository.review.ReviewRepository;
import com.berry.project.util.FacilityMaskDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

  private final FacilityMaskDecoder facilityMaskDecoder;

  @Override
  public LodgeDTO detail(long lodgeId, LodgeOptionDTO lodgeOptionDTO) {
    Optional<Lodge> optionalLodge = lodgeRepository.findById(lodgeId);
    if (optionalLodge.isEmpty()) return null;

    LodgeDTO lodgeDTO = convertEntityToDto(
        optionalLodge.get(),
        facilityMaskDecoder,
        lodgeDescriptionRepository.findByLodgeId(optionalLodge.get().getLodgeId()),
        reviewRepository.countByLodgeId(lodgeId),
        reviewRepository.findAverageRatingByLodgeId(lodgeId).orElse(0.0),
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
      if (withImage)
        roomDTO.setRoomImageUrls(
            roomImgRepository.findByRoomId(roomDTO.getRoomId())
                .stream().map(RoomImg::getRoomImgUrl)
                .toList());
      lodgeDTO.getRooms().add(roomDTO);
    }
  }
}
