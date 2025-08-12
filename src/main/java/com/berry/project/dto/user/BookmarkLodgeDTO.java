package com.berry.project.dto.user;

import com.berry.project.dto.lodge.RoomDTO;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookmarkLodgeDTO {

  private Long userId;
  private Long bookmarkId;
  private Long lodgeId;
  private String lodgeType;
  private String lodgeAddr;
  private String lodgeName;
  private Double averageReviewScore;
  private long reviewCount;
  private List<String> lodgeImages;
  private List<RoomDTO> rooms;

  public String getShortAddress() {
    return String.join(" ",
        Arrays.copyOf(
            lodgeAddr.split(" "), 3));
  }

  public RoomDTO getCheapestRent() {
    RoomDTO result = null;
    for (RoomDTO room : rooms) {
      if (room.getRentPrice() == null || room.getRentPrice() == 0) continue;

      if (result == null || result.getRentPrice() > room.getRentPrice())
        result = room;
    }

    return result;
  }

  public RoomDTO getCheapestStay() {
    RoomDTO result = null;
    for (RoomDTO room : rooms) {
      if (room.getStayPrice() == 0) continue;

      if (result == null || result.getStayPrice() > room.getStayPrice())
        result = room;
    }

    return result;
  }

  public String getCheapestPrice() {
    RoomDTO stay = getCheapestStay(), rent = getCheapestRent();
    if (stay == null && rent == null) return "";

    if (stay == null) return rent.getRentPrice() + "원~";
    if (rent == null) return stay.getStayPrice() + "원~";

    int price = stay.getStayPrice();
    return (price > rent.getRentPrice() ? rent.getRentPrice() : price) + "원~";
  }

}
