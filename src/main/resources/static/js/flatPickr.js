// 초기화
const today = new Date();
// 선택 시 시간 부분 무시
today.setHours(0, 0, 0, 0);
// 최대 지정 날짜
const maxDate = new Date();
// 현재 날짜로부터 90 일 뒤까지만 선택 가능
maxDate.setDate(maxDate.getDate() + 90);
maxDate.setHours(23, 59, 59, 999);

// 년도, const nextYear = today.getFullYear() + (today.getMonth() >= 9 ? 1 : 0);

// 달력 라이브러리

flatpickr = flatpickr("#checkInInput, #checkOutInput", {
  // 월 셀렉터 사용 X
  monthSelectorType: "static",

  // 저장 형태 - ISO 문자열 전체 : ""
  //dateFormat: "Z",
  // 최소 선택 날짜
  minDate: today,
  // maxDate: new Date(nextYear, maxDate_info.getMonth(), maxDate_info.getDate()),
  maxDate: maxDate,
  // 사용자에게 표시되는 형식
  altInput: true, altFormat: "Y년 m월 d일",

  // 초기 선택 날짜를 오늘 ~ 오늘로 지정
  defaultDate: today,

  yearRange: [today.getFullYear(), maxDate.getFullYear()],

  // 위치
  position: "below center",


  /** UI 언어 설정
   *
   *  > onYearChange - 년도의 숫자 뒤에 년 붙이기
   *
   *  > locale
   */
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
  },

  onReady: function (selectDates, dateStr, instance) {
    console.log("dateFormat : ", instance.config.dateFormat);
  }
});
