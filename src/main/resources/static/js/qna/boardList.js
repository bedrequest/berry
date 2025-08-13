const today = new Date();
const before = new Date();
before.setDate(today.getDate() - 30);

document.addEventListener('DOMContentLoaded', () => {
    flatpickr("#start, #end", {
      // 월 셀렉터 사용 X
      monthSelectorType: "static",

      // 저장 형태 - ISO 문자열 전체 : ""
      //dateFormat: "Z",
      /*
      // 최소 선택 날짜 : 30일 전
      minDate: before,
      // 최대치 : 오늘
      maxDate: today,
      */
      // 사용자에게 표시되는 형식
      altInput: true, altFormat: "Y년 m월 d일",

      // 위치
      position: "below center",

      /** UI 언어 설정
       *
       *  > onYearChange - 년도의 숫자 뒤에 년 붙이기
       *
       *  > locale
       */
      locale:{
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
          shorthand: ["월","화","수","목","금","토","일"],
          longhand: ["월요일","화요일","수요일","목요일","금요일","토요일","일요일"]
        }
      },

      onReady: function(selectDates, dateStr, instance){
        console.log("dateFormat : ", instance.config.dateFormat);
      }
    });
});
