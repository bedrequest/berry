import debounce from '../util/debounce.js';
import flatpickr from "https://esm.sh/flatpickr";

/* 목차
0. HTML 요소 불러오기
1. Main
2. 검색어 입력 부분
3. 여행 첫날, 마지막날
4. 인원 입력
5. 검색 버튼
6. 기타 유틸리티
7. Export : 초깃값 세팅
*/

// 0. HTML 요소 불러오기
const [
  searchBox,
  searchKeywordArea, noKeywordWarning, searchKeywordInput, searchSuggestions,
  checkInInput, checkOutInput,
  peopleCountArea, peopleCountInputArea, adultInput, childInput,
  adultMinusBtn, adultPlusBtn, childMinusBtn, childPlusBtn,
  searchBtn, freeForm]
  = [
    'searchBox',
    'searchKeywordArea', 'noKeywordWarning', 'searchKeywordInput', 'searchSuggestions',
    'checkInInput', 'checkOutInput',
    'peopleCountArea', 'peopleCountInputArea', 'adultInput', 'childInput',
    'adultMinusBtn', 'adultPlusBtn', 'childMinusBtn', 'childPlusBtn',
    'searchBtn', 'freeForm'].map(e => document.getElementById(e));

const [adultCount, childCount] = ['adultCount', 'childCount'].map(e => document.querySelectorAll("." + e));

let suggestions = [];
let lodgeId = null;
let adult = 2, child = 0;
/* ------------------------------------------- */

// 1. Main : 컨트롤
document.addEventListener('click', e => {
  // 1. 검색어 입력창이 선택됐는가
  if (e.target == searchKeywordArea || e.target == searchKeywordInput) {
    searchSuggestions.classList.remove('invisible');
    return;
  }
  searchSuggestions.classList.add('invisible');

  // 2. 추천 검색어를 클릭했는가
  let idx = undefined;
  if (e.target.closest('.keywordSuggestion') != null)
    idx = e.target.closest('.keywordSuggestion').dataset.no;
  if (idx != undefined) {
    searchKeywordInput.value = suggestions[idx].keyword;
    lodgeId = suggestions[idx].lodgeId;
    freeForm.value = "false";

    checkInInput.select();
    return;
  }

  // 3. 인원 입력창 컨트롤 : 인원 수 칸을 누르면 인원 입력창 부분을 토글하되, 인원 입력창 부분은 눌러도 그대로 유지
  if (!peopleCountArea.contains(e.target))
    peopleCountInputArea.classList.add('invisible');
  else if (!peopleCountInputArea.contains(e.target))
    peopleCountInputArea.classList.toggle('invisible');
});

// 1. 검색어 입력 부분(#searchKeywordArea)
searchKeywordArea.addEventListener('click', () => {
  searchKeywordInput.click();
});
searchKeywordInput.addEventListener('input', debounce(getKeywords, 400));

if (searchKeywordInput.value != '') getKeywords();

function getKeywords() {
  lodgeId = null;
  freeForm.value = "true";
  const keyword = searchKeywordInput.value;
  if (keyword == '') {
    searchSuggestions.innerHTML = '';
    return;
  }

  fetch('/search/' + keyword)
    .then(resp => resp.json())
    .then(results => {
      suggestions = results;
      searchSuggestions.innerHTML = '';
      if (results.length == 0) searchSuggestions.innerHTML = `
    <div id="noSearchResult">
      <span>검색 결과가 없어요</span>
      <span>목적지 이름 또는 띄어쓰기를 다시 확인해주세요.</span>
    </div>
    `;

      for (let i = 0; i < results.length; i++) {
        let html = `<div class="keywordSuggestion" data-no="${i}">`;
        html += results[i].lodgeId == '0' || !results[i].lodgeId ?
          iconGeoAlt() : iconBuilding();
        html += '<div class="suggestionTextArea">';
        html += `<span class="keyword">${results[i].keyword}</span><br>`;
        html += `<span class="detail">${results[i].detail}</span>`;
        html += '</div><div>';
        searchSuggestions.innerHTML += html;
      }
    });
}
/* ------------------------------------------- */

// 2. 여행 첫날, 마지막날
// flatpickr 적용
const today = new Date();
today.setHours(0, 0, 0, 0);

const maxDate = new Date();
maxDate.setDate(today.getDate() + 90);
maxDate.setHours(23, 59, 59, 999);

const calendars = flatpickr("#checkInInput, #checkOutInput", {
  monthSelectorType: "static",

  minDate: today,
  maxDate: maxDate,

  altInput: true,
  altFormat: "Y년 m월 d일",

  defaultDate: today,

  yearRange: [today.getFullYear(), maxDate.getFullYear()],

  position: "below center",
  locale: {
    months: {
      longhand: [
        "01", "02", "03", "04", "05", "06",
        "07", "08", "09", "10", "11", "12"
      ],
      shorthand: [
        "01", "02", "03", "04", "05", "06",
        "07", "08", "09", "10", "11", "12"
      ]
    },
    weekdays: {
      shorthand: ["월", "화", "수", "목", "금", "토", "일"],
      longhand: ["월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"]
    }
  }
});

/* ------------------------------------------- */

// 3. 인원 입력
// 3-1. 인원을 받으면 화면과 input에 반영하는 hook
function updateHeadCount(adult, child) {
  updateAdultCount(adult);
  updateChildCount(child);
}
function updateAdultCount(adultNew) {
  adult = adultNew;
  adultInput.value = adultNew;
  adultCount.forEach(e => {
    e.innerHTML = adultNew;
  });
}
function updateChildCount(childNew) {
  child = childNew;
  childInput.value = childNew;
  childCount.forEach(e => {
    e.innerHTML = childNew;
  });
}

// 3-2. 버튼에 입력 기능 추가
adultMinusBtn.addEventListener('click', () => {
  if (adult > 2) updateAdultCount(adult - 1);
  else updateAdultCount(2);
});
adultPlusBtn.addEventListener('click', () => {
  updateAdultCount(adult + 1);
});
childMinusBtn.addEventListener('click', () => {
  if (child > 0) updateChildCount(child - 1);
  else updateChildCount(0);
})
childPlusBtn.addEventListener('click', () => {
  updateChildCount(child + 1);
})
/* ------------------------------------------- */

// 4. 검색 버튼
// 검색어가 비어있으면 경고문 출력(timeout을 이용한 비동기), 아니라면 searchOption을 통해 검색
let noKeywordWarningTimeout = null;

searchBtn.addEventListener('click', () => {
  if (searchKeywordInput.value.length == 0) {
    noKeywordWarning.classList.remove('invisible');
    noKeywordWarning.style.opacity = "0.8";

    if (noKeywordWarningTimeout) clearTimeout(noKeywordWarningTimeout);

    noKeywordWarningTimeout = setTimeout(() => {
      noKeywordWarning.style.opacity = "0";

      setTimeout(() => {
        noKeywordWarning.classList.add('invisible');
        noKeywordWarningTimeout = null;
      }, 300);
    }, 3000);
    return;
  }

  if (lodgeId) {
    searchKeywordInput.disabled = true;
    freeForm.disabled = true;
    searchBox.action = "/lodge/detail/" + lodgeId;
  } else {
    searchBox.action = "/lodge/list";
    searchKeywordInput.disabled = false;
    freeForm.disabled = false;
  }
  searchBox.submit();

  searchKeywordInput.disabled = false;
  freeForm.disabled = false;
});
/* ------------------------------------------- */

// 5. 기타 유틸리티
/* width와 height를 받아서 그 사이즈의 부트스트랩 아이콘을 리턴하는 함수들
 * width만 주면 height는 width와 같은 값으로 지정됨
 */
function iconGeoAlt(width = 16, height = width) {
  if (width == undefined) return;
  return `<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}" fill="currentColor" class="bi bi-geo-alt" viewBox="0 0 16 16">
  <path d="M12.166 8.94c-.524 1.062-1.234 2.12-1.96 3.07A32 32 0 0 1 8 14.58a32 32 0 0 1-2.206-2.57c-.726-.95-1.436-2.008-1.96-3.07C3.304 7.867 3 6.862 3 6a5 5 0 0 1 10 0c0 .862-.305 1.867-.834 2.94M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10"/>
  <path d="M8 8a2 2 0 1 1 0-4 2 2 0 0 1 0 4m0 1a3 3 0 1 0 0-6 3 3 0 0 0 0 6"/>
</svg>`;
}
function iconBuilding(width = 16, height = width) {
  if (width == undefined) return;
  return `<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}" fill="currentColor" class="bi bi-building" viewBox="0 0 16 16">
  <path d="M4 2.5a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5zm3 0a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5zm3.5-.5a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5zM4 5.5a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5zM7.5 5a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5zm2.5.5a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5zM4.5 8a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5zm2.5.5a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5zm3.5-.5a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5z"/>
  <path d="M2 1a1 1 0 0 1 1-1h10a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1zm11 0H3v14h3v-2.5a.5.5 0 0 1 .5-.5h3a.5.5 0 0 1 .5.5V15h3z"/>
</svg>`;
}

// 6. Export : 초깃값 세팅
export default function init(lodgeOption, listOption, lodgeDTO) {
  try {
    if (lodgeOption) {
      if (lodgeDTO) {
        searchKeywordInput.value = lodgeDTO.lodgeName;
        lodgeId = lodgeDTO.lodgeId;
      }
      calendars[0].setDate(lodgeOption.checkIn);
      calendars[1].setDate(lodgeOption.checkOut);
      adult = lodgeOption.adult;
      child = lodgeOption.child;
    } else throw Error("insert defalut value");
    if (listOption) {
      searchKeywordInput.value = listOption.keyword;
      freeForm.value = listOption.freeForm;
    }
  } catch (insertDefaultValueHere) {
    const today = new Date(), tomorrow = new Date();
    tomorrow.setDate(today.getDate() + 1);
    calendars[0].setDate(today);
    calendars[1].setDate(tomorrow);
  }
  updateHeadCount(adult, child);
  getKeywords();
}