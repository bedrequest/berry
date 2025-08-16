package com.berry.project.service.user;

import com.berry.project.dto.user.DeactivatedUserDTO;
import com.berry.project.entity.user.DeactivatedUser;

public interface DeactivatedUserService {

  default DeactivatedUser convertDTOToEntity(DeactivatedUserDTO deactivatedUserDTO) {
    return DeactivatedUser.builder()
        .userId(deactivatedUserDTO.getUserId())
        .dReason(deactivatedUserDTO.getDReason())
        .dUserName(deactivatedUserDTO.getDUserName())
        .dUserEmail(deactivatedUserDTO.getDUserEmail())
        .dUserPhone(deactivatedUserDTO.getDUserPhone())
        .build();
  }

  default DeactivatedUserDTO convertEntityToDTO(DeactivatedUser deactivatedUser) {
    return DeactivatedUserDTO.builder()
        .dUserId(deactivatedUser.getDUserId())
        .userId(deactivatedUser.getUserId())
        .dUserEmail(deactivatedUser.getDUserEmail())
        .dUserName(deactivatedUser.getDUserName())
        .dUserPhone(deactivatedUser.getDUserPhone())
        .dReason(deactivatedUser.getDReason())
        .regDate(deactivatedUser.getRegDate())
        .modDate(deactivatedUser.getModDate())
        .build();
  }

  void registerDeactivatedUser(DeactivatedUserDTO deactivatedUserDTO);
}
