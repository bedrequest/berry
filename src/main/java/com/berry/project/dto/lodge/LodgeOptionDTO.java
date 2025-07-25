package com.berry.project.dto.lodge;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class LodgeOptionDTO {

  private LocalDate checkIn, checkOut;

  private int adult, child;

  public LodgeOptionDTO() {
    LocalDate today = LocalDate.now();
    this.checkIn = today;
    this.checkOut = today.plusDays(1);

    adult = 2;
  }

  public LodgeOptionDTO(LocalDate checkIn, LocalDate checkOut,
                        Integer adult, Integer child) {
    this();
    LocalDate today = LocalDate.now();

    if (checkIn != null &&
        !checkIn.isBefore(today) &&
        !checkIn.isAfter(today.plusDays(89))) this.checkIn = checkIn;
    this.checkOut = this.checkIn.plusDays(1);
    if (checkOut != null &&
        !checkOut.isBefore(this.checkIn.plusDays(1)) &&
        !checkOut.isAfter(today.plusDays(90))) this.checkOut = checkOut;

    if (adult != null && adult > 2) this.adult = adult;
    if (child != null) this.child = child;
  }

}
