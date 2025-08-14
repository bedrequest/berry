package com.berry.project.dto.lodge;

import com.berry.project.dto.review.ReviewResponseDTO;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LodgeDTO {
  private Long lodgeId;
  private String lodgeType;
  private String lodgeName;
  private String lodgeAddr;
  private List<String> facilities;
  private String intro;
  private List<LodgeDescriptionDTO> description;
  private String businessCall;
  private double latitude;
  private double longitude;
  private List<RoomDTO> rooms;
  private List<String> lodgeImages;
  private List<String> ways;
  private Double averageReviewScore;
  private long reviewCount;
  private ReviewResponseDTO bestReview;

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
