/* Lodge List js 
작성자 : 이유현
목차
0. HTML 요소 불러오기, 변수 선언
*/

// 0. HTML 요소 불러오기, 변수 선언
const listOption = pagingHandler.listOptionDTO;

// 1. 메인 : 검색 조건 변경을 감지
document.addEventListener('click', e => {
  let isOptionChanged = false;
  
  // 1) 숙소 유형
  if (e.target.classList.contains('customRadioBtn')) {
    if (e.target.classList.contains('selected')) return;

    const lodgeType = e.target.closest('.lodgeType').querySelector('span').innerText;
    listOption.lodgeType = lodgeType == '전체' ? null : lodgeType;

    pagingHandler.pageNo = 1;
    isOptionChanged = true;
  }

  // 2) 가격 : TODO
  // 3) 태그 : TODO

  // 4) 시설
  const facilityBtn = e.target.closest('.facilityBtn');
  if (facilityBtn) {
    let idx = Number(facilityBtn.dataset.index);
    if (facilityBtn.classList.contains('facility2')) idx += publicFacilityCount;
    else if (facilityBtn.classList.contains('facility3')) idx += publicFacilityCount + innerFacilityCount;

    const mask = 1 << idx;
    if (listOption.facilityMask == null) listOption.facilityMask = mask;
    else if ((listOption.facilityMask & mask) == 0) listOption.facilityMask += mask;
    else listOption.facilityMask -= mask;

    pagingHandler.pageNo = 1;
    isOptionChanged = true;
  }

  // 5) 페이징
  const page = e.target.closest('.page-item');
  if (page) {
    if (page.classList.contains('disabled') || page.classList.contains('active')) return;
    pagingHandler.pageNo = page.dataset.pageno;
    isOptionChanged = true;
  }

  if (isOptionChanged) reload();
});

function reload() {
  let address = location.origin + '/lodge/list?'
  + `keyword=${listOption.keyword}&`
  + `checkIn=${lodgeOption.checkIn}&`
  + `checkOut=${lodgeOption.checkOut}&`
  + `adult=${lodgeOption.adult}&`
  + `child=${lodgeOption.child}&`
  + `freeForm=${listOption.freeForm}&`
  + `pageNo=${pagingHandler.pageNo}&`;

  if (listOption.lodgeType != null)
    address += `lodgeType=${listOption.lodgeType}&`;

  if (listOption.lowestPrice != null)
    address += `lowestPrice=${listOption.lowestPrice}&`;
  if (listOption.highestPrice != null)
    address += `highestPrice=${listOption.highestPrice}&`;

  if (listOption.facilityMask != 0)
    address += `facilityMask=${listOption.facilityMask}&`;

  if (address.endsWith('&')) address = address.slice(0, -1);
  location.href = address;
}