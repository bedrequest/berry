package com.berry.project.dto.lodge;

import com.berry.project.data.LodgeData;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class ListOptionDTO {

  @Setter
  private String keyword;
  private boolean freeForm;

  @Setter
  private String lodgeType;
  private int lowestPrice, highestPrice;
  private Integer facilityMask;

  @Setter
  private int favoriteMask;

  private String sort;

  public ListOptionDTO() {
    int[] priceTable = new LodgeData().getPriceTable();

    this.freeForm = true;
    this.facilityMask = 0;
    this.sort = "추천순";
    this.lowestPrice = priceTable[0];
    this.highestPrice = priceTable[priceTable.length - 1];
  }

  public ListOptionDTO(String keyword, Boolean freeForm) {
    this();

    setKeyword(keyword);
    setFreeForm(freeForm);
  }

  public ListOptionDTO(String keyword, Boolean freeForm, String lodgeType, Integer lowestPrice, Integer highestPrice, Integer facilityMask, String sort) {
    this(keyword, freeForm);

    setLodgeType(lodgeType);
    setFacilityMask(facilityMask);
    setLowestPrice(lowestPrice);
    setHighestPrice(highestPrice);
    setSort(sort);
  }

  public void setFreeForm(Boolean freeForm) {
    if (freeForm != null) this.freeForm = freeForm;
  }

  public void setLowestPrice(Integer lowestPrice) {
    int[] priceTable = new LodgeData().getPriceTable();

    if (lowestPrice == null || lowestPrice >= this.highestPrice) {
      this.lowestPrice = priceTable[0];
      return;
    }

    int tableIndex = 0;
    for (int i = 0; i < priceTable.length - 1; i++)
      if (priceTable[i] == lowestPrice) {
        tableIndex = i;
        break;
      }
    this.lowestPrice = priceTable[tableIndex];
  }

  public void setHighestPrice(Integer highestPrice) {
    int[] priceTable = new LodgeData().getPriceTable();

    if (highestPrice == null || highestPrice <= this.lowestPrice) {
      this.highestPrice = priceTable[priceTable.length - 1];
      return;
    }

    int tableIndex = priceTable.length - 1;
    for (int i = 1; i < priceTable.length - 1; i++)
      if (priceTable[i] == highestPrice) {
        tableIndex = i;
        break;
      }

    this.highestPrice = priceTable[tableIndex];
  }

  public void setFacilityMask(Integer facilityMask) {
    if (facilityMask != null) this.facilityMask = facilityMask;
  }

  public void setSort(String sort) {
    if (sort != null) this.sort = sort;
  }

  public boolean isDefaultOption() {
    int[] priceTable = new LodgeData().getPriceTable();

    if (lodgeType != null) return false;
    if (getLowestPrice() != priceTable[0]) return false;
    if (getHighestPrice() != priceTable[priceTable.length - 1]) return false;
    if (facilityMask != 0) return false;
    return favoriteMask == 0;
  }
}
