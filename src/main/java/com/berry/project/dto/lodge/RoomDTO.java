package com.berry.project.dto.lodge;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {
  private Long roomId;
  private long lodgeId;
  private String roomName;
  private String info;
  private Integer rentPrice;
  private String rentTime;
  private int stayPrice;
  private String stayOption;
  private String stayTime;
  private int stockCount;
  private int standardCount;
  private int maxCount;
  private List<String> roomImageUrls;

  public int getRentTimeInt() {
    if (rentTime == null || rentTime.isEmpty()) return 0;

    int result = rentTime.charAt(0) - '0';

    if (rentTime.charAt(1) >= '0' &&
        rentTime.charAt(1) <= '9') {
      result *= 10;
      result += rentTime.charAt(1) - '0';
    }

    return result;
  }

  public String getStayCheckIn() {
    if (stayTime == null || stayTime.isEmpty()) return null;
    return stayTime.split("·")[0];
  }

  public String getStayCheckOut() {
    if (stayTime == null || stayTime.isEmpty()) return null;
    return stayTime.split("·")[1];
  }
}
