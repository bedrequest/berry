package com.berry.project.dto.lodge;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class LodgeOptionDTO {

  private LocalDate checkIn, checkOut;
  private int adult, child;

  public LodgeOptionDTO() {
    setCheckIn(null);
    setCheckOut(null);
    setAdult(2);
    setChild(0);
  }

  public void setCheckIn(LocalDate checkIn) {
    LocalDate today = LocalDate.now();
    if (checkIn == null ||
        checkIn.isBefore(today) ||
        checkIn.isAfter(today.plusDays(89))) this.checkIn = today;
    else this.checkIn = checkIn;
  }

  public void setCheckOut(LocalDate checkOut) {
    setCheckIn(checkIn);
    LocalDate tomorrow = LocalDate.now().plusDays(1);

    if (checkOut == null ||
        checkOut.isBefore(checkIn.plusDays(1)) ||
        checkOut.isAfter(tomorrow.plusDays(89)))
      checkOut = checkIn.plusDays(1);
    this.checkOut = checkOut;
  }

  public void setAdult(Integer adult) {
    if (adult == null) adult = 2;
    this.adult = Math.max(adult, 2);
  }

  public void setChild(Integer child) {
    if (child == null) child = 0;
    this.child = Math.max(child, 0);
  }
}
