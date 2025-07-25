package com.berry.project.service.lodge;

import com.berry.project.dto.lodge.LodgeDTO;
import com.berry.project.dto.lodge.RoomDTO;
import com.berry.project.dto.lodge.ListOptionDTO;
import com.berry.project.dto.lodge.LodgeOptionDTO;
import com.berry.project.entity.lodge.Lodge;
import com.berry.project.entity.lodge.Room;
import com.berry.project.handler.PagingHandler;
import com.berry.project.util.FacilityMaskDecoder;

import java.util.ArrayList;

public interface LodgeService {
  LodgeDTO detail(long lodgeId, LodgeOptionDTO lodgeOptionDTO);

  default LodgeDTO convertEntityToDto(Lodge lodge, FacilityMaskDecoder facilityMaskDecoder) {
    return LodgeDTO.builder()
        .lodgeId(lodge.getLodgeId())
        .lodgeName(lodge.getLodgeName())
        .lodgeType(lodge.getLodgeType())
        .lodgeAddr(lodge.getLodgeAddr())
        .facilities(facilityMaskDecoder.decode(lodge.getFacility()))
        .intro(lodge.getIntro())
        .description(lodge.getDescription())
        .businessCall(lodge.getBusinessCall())
        .latitude(lodge.getLatitude())
        .longitude(lodge.getLongitude())
        .rooms(new ArrayList<>())
        .build();
  }

  default RoomDTO convertEntityToDto(Room room) {
    return RoomDTO.builder()
        .roomId(room.getRoomId())
        .lodgeId(room.getLodgeId())
        .roomName(room.getRoomName())
        .info(room.getInfo())
        .rentPrice(room.getRentPrice())
        .rentTime(room.getRentTime())
        .stayPrice(room.getStayPrice())
        .stayOption(room.getStayOption())
        .stayTime(room.getStayTime())
        .stockCount(room.getStockCount())
        .standardCount(room.getStandardCount())
        .maxCount(room.getMaxCount())
        .build();
  }

  PagingHandler<LodgeDTO> getLodgeList(int pageNo, ListOptionDTO listOptionDTO, LodgeOptionDTO lodgeOptionDTO);
}
