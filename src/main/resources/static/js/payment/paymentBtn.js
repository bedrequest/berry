// 확인, user 정보
console.log(user);

// 초기화
 // csrf Token
// const csrfToken  = document.querySelector('meta[name="_csrf"]').getAttribute('content');
// const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
 // 인원수

/** 전체 이벤트 리스너 */
document.addEventListener('click', (e) => {
  console.log(e.target);

  const reserveBtn = e.target.closest('.reserveBtn');

  if(reserveBtn){
    /** 대실 예약 이벤트 리스너
     * 
     *  > 결제 페이지로 가져가야 할 정보
     * 
     *    - userId, roomId, guests_amount (adultCount 와 childCount), start_date, end_date
     * 
     * */  
    if(reserveBtn.classList.contains('paymentBtn_rent')){
      // 확인
      console.log("================ e.target.classList.contain(대실버튼) ================");
      // 비로그인 상태라면
      if(!user){
        const ret = encodeURIComponent(window.location.href);
        window.location.href = `/user/login?redirectTo=${ret}`;
        return;
      }

      // 초기화
       // 인원수
      let cnt = Number(document.getElementById('adultInput').value)
        + Number(document.getElementById('childInput').value);
       // 시작일
      const startDate_info = document.getElementById('checkInInput').value;
       // 종료일
      const endDate_info = document.getElementById('checkOutInput').value;
       // 확인
      console.log(startDate_info);
      console.log(endDate_info);

      // 객체 파싱
      const orderPayload = {
        userId : user,
        roomId : reserveBtn.dataset.roomid,
        guestsAmount : cnt,
        startDate : startDate_info,
        endDate : endDate_info
      }
      // 확인
      console.log(orderPayload);

    
      /** 동적으로 <form> 태그 생성하여 제출 */
      dynamicPostForPRG('/payment/moveRent', orderPayload);

      // else {
      //     alert('로그인이 필요한 작업입니다!');
      // }    
      
    } // 대실 예약 이벤트 리스너 fin
  
  
    /** 숙박 예약 버튼 이벤트 리스너
     * 
     * */
    if(reserveBtn.classList.contains('paymentBtn_stay')){
      // 확인
      console.log("================ e.target.classList.contain(예약 버튼) ================");
  
      // 비로그인 상태라면
      if(!user){
        const ret = encodeURIComponent(window.location.href);
        window.location.href = `/user/login?redirectTo=${ret}`;
        return;
      }

      // 초기화
       // 인원수
      let cnt = Number(document.getElementById('adultInput').value)
        + Number(document.getElementById('childInput').value);
       // 시작일
      const startDate_info = document.getElementById('checkInInput').value;
       // 종료일
      const endDate_info = document.getElementById('checkOutInput').value;
       // 확인
      console.log(startDate_info);
      console.log(endDate_info);

      // 객체 파싱
      const orderPayload = {
        userId : user,
        roomId : reserveBtn.dataset.roomid,
        guestsAmount : cnt,
        startDate : startDate_info,
        endDate : endDate_info
      }

      // 확인
      console.log(orderPayload);

      /** 동적으로 <form> 태그 생성하여 제출 */
      dynamicPostForPRG('/payment/moveStay', orderPayload);

      // else {
      //     alert('로그인이 필요한 작업입니다!');
      // }    
    } // 숙박 예약 이벤트 리스너 fin 

  } // reserveBtn 이벤트 리스너 fin

}) // document 이벤트 리스너 fin 



/** dynamicPostForPRG(url, orderPayload) - 결제 페이지로 이동하기 위한 함수 
 * 
 * 
 */
function dynamicPostForPRG(url, orderPayload){
  // <form> 생성
  const form = document.createElement('form');
  form.method = 'POST';
  form.action = url;
  form.style.display = 'none';

  // 객체를 분해해 각각의 input hidden 으로 전송
  Object.entries(orderPayload).forEach(([key, value]) => {
    const input = document.createElement('input');
    input.type = 'hidden';
    input.name = key;
    input.value = value;
    form.appendChild(input)
  });

  // 문서에 붙이고 제출 
  document.querySelector('.prg').appendChild(form);
  form.submit();
}
