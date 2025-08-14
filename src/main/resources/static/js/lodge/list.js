/* Lodge List js 
작성자 : 이유현
목차
0. HTML 요소 불러오기, 변수 선언
*/

// 0. HTML 요소 불러오기, 변수 선언
const listOption = pagingHandler.listOptionDTO;
const [lowestPriceArea,
  lowestPriceDown, lowestPriceText, lowestPriceUp,
  highestPriceDown, highestPriceText, highestPriceUp]
  = ['lowestPriceArea',
    'lowestPriceDown', 'lowestPriceText', 'lowestPriceUp',
    'highestPriceDown', 'highestPriceText', 'highestPriceUp'].map(e => document.getElementById(e));
let lowestPriceIndex = priceTable.indexOf(listOption.lowestPrice),
highestPriceIndex = priceTable.indexOf(listOption.highestPrice);

let bookmarkServing = false;

// 1. 메인 : 검색 조건 변경을 감지
document.addEventListener('click', e => {
  // 검색 조건 변경 감지
  let isOptionChanged = false;

  // 1) 숙소 유형
  if (e.target.classList.contains('customRadioBtn')) {
    if (e.target.classList.contains('selected')) return;

    const lodgeType = e.target.closest('.lodgeType').querySelector('span').innerText;
    listOption.lodgeType = lodgeType == '전체' ? null : lodgeType;

    pagingHandler.pageNo = 1;
    isOptionChanged = true;
  }

  // 2) 가격 : 별도로 분리

  // 3) 태그
  const favoriteBtn = e.target.closest('.favorite');
  if (favoriteBtn) {
    const idx = Number(favoriteBtn.dataset.index);
    const mask = 1 << idx;

    if (listOption.favoriteMask == null) listOption.favoriteMask = mask;
    else if ((listOption.favoriteMask & mask) == 0) listOption.favoriteMask += mask;
    else listOption.favoriteMask -= mask;

    pagingHandler.pageNo = 1;
    isOptionChanged = true;
  }

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

  // 6) 정렬 조건
  const sortOption = e.target.closest('.sortOption');
  if (sortOption && !sortOption.classList.contains('selected')) {
    listOption.sort = sortOption.querySelector('span').innerText;
    isOptionChanged = true;
  }

  if (isOptionChanged) reload();
});

// 2. 필터 초기화 버튼의 이벤트
document.getElementById('filterResetBtn').addEventListener('click', () => {
  location.href = getHrefWithDefaultListOption();
});

// 3. 가격 관련
// 3-1. 버튼 초기 설정
lowestPriceDown.disabled = (lowestPriceIndex == 0);
lowestPriceUp.disabled = (lowestPriceIndex >= priceTable.length - 2);
highestPriceDown.disabled = (highestPriceIndex - lowestPriceIndex <= 1);
highestPriceUp.disabled = (highestPriceIndex == priceTable.length - 1);

// 3-2. 버튼 클릭 대응
lowestPriceDown.addEventListener('click', () => {
  lowestPriceIndex--;
  reload();
});
lowestPriceUp.addEventListener('click', () => {
  lowestPriceIndex++;
  reload();
});
highestPriceDown.addEventListener('click', () => {
  highestPriceIndex--;
  reload();
})
highestPriceUp.addEventListener('click', () => {
  highestPriceIndex++;
  reload();
})

function reload() {
  let address = getHrefWithDefaultListOption();

  if (listOption.lodgeType != null)
    address += '&lodgeType=' + listOption.lodgeType;
  address += '&facilityMask=' + listOption.facilityMask;
  address += '&favoriteMask=' + listOption.favoriteMask;
  address += '&lowestPrice=' + priceTable[lowestPriceIndex];
  address += '&highestPrice=' + priceTable[highestPriceIndex];
  address += '&sort=' + listOption.sort;
  location.href = address;
}

function getHrefWithDefaultListOption() {
  return location.origin + '/lodge/list?'
    + 'keyword=' + listOption.keyword
    + '&checkIn=' + lodgeOption.checkIn
    + '&checkOut=' + lodgeOption.checkOut
    + '&adult=' + lodgeOption.adult
    + '&child=' + lodgeOption.child
    + '&freeForm=' + listOption.freeForm
    + '&pageNo=' + pagingHandler.pageNo;
}

// 3. 정렬 조건 버튼
const lodgeSortBtn = document.getElementById('lodgeSortBtn');
lodgeSortBtn.addEventListener('click', () => {
  lodgeSortBtn.classList.toggle('active');
});