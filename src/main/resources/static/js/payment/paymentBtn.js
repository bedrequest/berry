// 초기화
 // 대실 예약 버튼
const paymentBtn_rent = document.querySelector('.paymentBtn_rent');
 // 숙박 예약 버튼
const paymentBtn_stay = document.querySelector('.paymentBtn_stay');


/** 대실 예약 버튼 이벤트 리스너 */
paymentBtn_rent.addEventListener('click', () => {
  // 초기화
  
  
  // user_id 와 customerKey 가져오기  
   // customerKey 임의 설정
  const customerKey = `${crypto.randomUUID()}_W`;
   // 객체 초기화
  const userPayload = {
    // 임의 설정
    userId : 23,
    customerKey : customerKey,
    userName : 'Lee',
    userPhone : '010-1111-1111'
  }
  
  const lodgePayload = {
    // 임의 설정
    roomId : 23,
    roomName : '디럭스 룸',
    stayTime : '11:00 ~ 23:00'
  };

  
  /** 아래의 코드를 다음과 같이 표현 가능
   * 
   *  > 코드 (ES6 프로퍼티 축약 표현 방식 )
   *    const mergePayload = { userPayload, lodgePayload };
   * 
   *  > 내가 작성한 방식은 명시적으로 키와 값을 작성한 것 
   */
  const mergePayload = {
    userPayload : userPayload,
    lodgePayload : lodgePayload
  }

  getPaymentRentPageFromServer(mergePayload);

})


/** getPaymentRentPageFromServer() - 비동기 요청으로 HTML을 받아 페이지를 교체 */
async function getPaymentRentPageFromServer(mergePayload) {
  try {
    // 1. 컨트롤러의 @PostMapping("/paymentRent")에 요청
    const url = `/payment/paymentRent`;

    const config = {
      method : 'POST',
      headers : {
        'Content-Type' : 'application/json; charset=utf-8' 
      },
      body : JSON.stringify(mergePayload)
    }

    const resp = await fetch(url, config);
    
    // 2. 응답으로 받은 HTML 문자열을 변수에 저장
    const html = await resp.text();

    // 3. 응답이 성공적이면, 현재 문서의 내용을 받은 HTML로 완전히 교체
    if (resp.ok) {
        document.open();
        document.write(html);
        document.close();
    } else {
        console.error('페이지 로딩에 실패했습니다.', html);
    }

  } catch (error) {
    console.log(error);
  }
}