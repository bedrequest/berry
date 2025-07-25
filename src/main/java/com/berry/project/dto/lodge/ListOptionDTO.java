package com.berry.project.dto.lodge;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ListOptionDTO {

  private String keyword;
  private boolean freeForm;

  private String lodgeType;
  private Integer lowestPrice, highestPrice;
  private Integer facilityMask;

  private String sort;

  public ListOptionDTO() {
    this.freeForm = true;
    this.facilityMask = 0;
  }

  public ListOptionDTO(String keyword, Boolean freeForm) {
    this();

    this.keyword = keyword;
    if (freeForm != null) this.freeForm = freeForm;
  }

  public ListOptionDTO(String keyword, Boolean freeForm, String lodgeType, String priceRange, Integer facilityMask, String sort) {
    this(keyword, freeForm);

    this.lodgeType = lodgeType;

    if (priceRange != null) try {
      String[] split = priceRange.split("~");
      this.lowestPrice = Integer.parseInt(split[0]);
      this.highestPrice = Integer.parseInt(split[1]);
    } catch (Exception e) {
      this.lowestPrice = null;
      this.highestPrice = null;
    }

    if (facilityMask != null) this.facilityMask = facilityMask;
    this.sort = sort;
  }

}
