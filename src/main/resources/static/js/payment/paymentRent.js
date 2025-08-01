// 확인 - 클라이언트 키, 
console.log(tossClientKey);
console.log(isBeforeInfo);
console.log(isTodayInfo);
console.log(rentNum);

// 초기화
 // 결제 버튼 
const paymentButton = document.getElementById('payment-button');
 // 약관 - 전체 동의
const allAgree = document.getElementById('terms-all');
 // 약관 - 필수 약관 
const requiredAgrees = document.querySelectorAll('.terms-req');
 // payment_info 객체를 JavaScript에서 사용하기 쉽게 변수에 할당
const paymentInfo = /*[[${payment_info}]]*/ null;


/** document 클릭 이벤트 리스너
 * 
 * 
 */
document.addEventListener('click', (e) => {
  // 확인
  console.log(e.target);

  // 시간 선택 시  
  if(e.target.classList.contains('time-slot')){

  }
})



/** DOMContentLoaded 이벤트 리스너  
 * 
 *  > 대실 운영 시간은 일괄적으로 11:00 ~ 22:00 까지 설정
 * 
 * */
document.addEventListener('DOMContentLoaded', () => {
  // 예약 가능 시간을 출력할 div
  const timeGrid = document.querySelectorAll('.time-gird');

  // Case 1) - 현재 날짜가 이용일 이전인 경우의 대실 예약
  if(isBeforeInfo){
    // Fragment 생성 
    const btnFragment = document.createElement('fragment');
    
    // 버튼 13개 (10:00 ~ 22:00) 생성 
    for(let i = 0; i <= 13; i++){
      // 버튼 생성
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.classList.add('time-slot');
      btn.textContent = `${strTimeSlot(i)}`;
      
      // fragment 에 추가
      btnFragment.appendChild(btn);
    }

    // fragment 를 이용해 한 번에 추가
    timeGrid.appendChild(btnFragment); 
  }


  // Case 2) - 현재 날짜가 이용일 당일인 경우의 대실 예약 
  if(isTodayInfo){
    const now = dayjs().format('HH');

    // 오전 10 시 이전 (운영 시작 시간 이전) 에 예약하는 경우 
    if(now < 10){
      // Fragment 생성 
      const btnFragment = document.createElement('fragment');
      
      // 버튼 13개 (10:00 ~ 22:00) 생성 
      for(let i = 0; i <= 13; i++){
        // 버튼 생성
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.classList.add('time-slot');
        btn.textContent = `${strTimeSlot(i)}`;
        
        // fragment 에 추가
        btnFragment.appendChild(btn);
      }

      // fragment 를 이용해 한 번에 추가
      timeGrid.appendChild(btnFragment); 
    }

    // 오전 10 시 이후 (운영 시작 시간 이후) 에 예약하는 경우 
    else {

    }
  }
})



/** 쿠폰 이벤트 리스너 - 쿠폰의 변동사항을 적용
 * 
 */
if(document.querySelector('.select-cupon')){
  document.querySelector('.select-cupon').addEventListener('click', (e) => {
    // 쿠폰 할인란 초기화, 
    document.querySelector('.usingCupon').innerHTML = '';
        
    // 쿠폰 할인에 표시
    document.querySelector('.usingCupon').innerHTML = `${e.target.value} 원`;
  
    // 할인 급액란 초기화, 
    document.querySelector('.cuponPrice').innerHTML = '';
  
    // 할인 금액에 표시
    document.querySelector('.cuponPrice').innerHTML = `${e.target.value} 원`;
  
    /** 정가 - 쿠폰 할인가를 총 결제 금액에 표시 */
     // 총 결제 금액란 초기화, 
    document.querySelector('.pbpTotalAmount').innerHTML = '';
     
    let pbpTotalAmount = Number(strikePrice) - Number(e.target.value) 
  
    document.querySelector('.pbpTotalAmount').innerHTML = `${pbpTotalAmount} 원`;
  
    // 결제하기 버튼 금액 초기화, 
    document.querySelector('.payment-button').innerHTML = ''; 
  
    // 결제하기 버튼 부분에 표시 
    document.querySelector('.payment-button').innerHTML = `${pbpTotalAmount} 원 결제하기`;
  })

}


// 본인 인증 버튼이 없는 경우에만 

/** 전체 동의 이벤트 리스너 */
allAgree.addEventListener('change', (e) => {
  // 확인,
  console.log("====================== allAgree EventListener ======================");

  requiredAgrees.forEach(checkbox => {
      checkbox.checked = e.target.checked;
  });
  checkAgreements();
});


/** 필수 약관 이벤트 리스너 */
requiredAgrees.forEach(checkbox => {
  checkbox.addEventListener('change', () => {
    console.log("====================== requiredAgrees EventListener ======================");

    const allRequiredChecked = Array.from(requiredAgrees).every(c => c.checked);
    allAgree.checked = allRequiredChecked;
    checkAgreements();
  });
});
  
checkAgreements();



/** 결제하기 버튼 이벤트 리스너 
 * 
 * */ 
paymentButton.addEventListener('click', async () => {
  try {
    console.log("====================== paymentButton EventListener ======================");

    /** 결제에 필요한 변수 초기화 */ 
     // TossPayments 초기화
    const tossPayments = TossPayments("test_ck_ZLKGPx4M3MaBdQzvKDyR3BaWypv1");
     // customerKey (임시, 원래는 user authorize 로 가져옴)
    const customerKey = `${crypto.randomUUID()}_W`;
     // tossPayment 의 결제 메서드 호출 
    const payment = tossPayments.payment({ customerKey });
    

    /** payment_before_payment TABLE 에 INSERT 할 pbpObj 속성 초기화 */
     // order_id 는 Server 에서 생성
    const orderId_info = await generateOrderId();
     // 숙소명 (pbp TABLE - orderName)
    const orderName_info = document.querySelector('.roomName').textContent;
     // cupon_id (임시 생성, 실제로는 페이지 이동 시 비동기로 로딩)
    const cuponId_info = 1;
     // 쿠폰으로 할인 받은 금액 
    const cuponPrice_info = 3000;
     // 원래 가격 
    const strikePrice_info = 5000;
     // 총 결제 가격 
    const pbpTotalAmount_info = 2000;  
     // 결제 수단 (method)
    const method_info = document.querySelector('.payment-methods input[name="payment"]:checked').value; 
  
  
    /** reservation TABLE 에 INSERT 할 reservationObj 속성 초기화 */
     // order_id 는 위에서 생성된 id 사용 
      
     // room_id (임시 생성)
    const roomId_info = 13;
     // user_id 
    const userId_info = 26;
     // 이용 시작일 (reservation TABLE - stayTime)
    const startDate_info = new Date("2025-07-21T11:00:00.000Z").toISOString();
     // 이용 종료일
    const endDate_info = new Date("2025-07-22T12:00:00.000Z").toISOString();
     // 결제 금액은 위에서 사용된 총 결제 가격을 사용
  
     // 숙박 인원 (reservation TABLE - guestsAmount)
    const guestsAmount_info = parseInt(document.querySelector('.guestsAmount').textContent);
     // 예약 타입 - ReservationType 은 STAY 와 RENT 만 존재
    const reservationType_info = 'STAY';
  
    // payment_info 가 null 인 경우 
    // if (paymentInfo == null) {
    //     alert('결제 정보가 없습니다.');
    //     return;
    // }
  
  
    /** payment_before_payment Table 에 결제 전 저장할 구매 정보의 Record Insert */
    const pbpPayload = {
      customerKey : customerKey,
      cuponId : cuponId_info,
      orderId : orderId_info,
      method : method_info,
      cuponPrice : cuponPrice_info,
      strikePrice : strikePrice_info,
      pbpTotalAmount : pbpTotalAmount_info,
      orderName : orderName_info
    }
  

    /** Reservation Table 에 예약 정보의 Record Insert */ 
    const reservePayload = {
      roomId : roomId_info,
      userId : userId_info,
      orderId : orderId_info,
      startDate : startDate_info,
      endDate : endDate_info,
      totalAmount : pbpTotalAmount_info,
      guestsAmount : guestsAmount_info,
      reservationType : reservationType_info
    }
  
    // 두 객체를 하나의 객체로 병합
    const mergePayload = {
      pbpPayload : pbpPayload,
      reservePayload : reservePayload
    }

    /** sendPaymentObjToServer(mergePayload) 
     * 
     *  > 결제하기 버튼 클릭 시 결제 전 구매 정보 (PBPDto), 예약 정보 (ReservationDTO) 를 
     *    서버로 보내고 return 으로 서버로 전송한 PBPDto 를 반환받음 
     *
     * */  
    sendPaymentObjToServer(mergePayload).then(result => {
      if(result){
        console.log("============================ sendPaymentObjToSever() ============================");
        console.log(result);

        
        // 토스페이먼츠 결제 요청
        payment.requestPayment({
          method : result.method,
          amount: {
            currency: "KRW",
            value: result.pbpTotalAmount
          }, // 실제 결제 금액
          orderId: result.orderId, // 주문 ID (실제로는 고유하게 생성해야 함)
          orderName: result.orderName, // 주문명
          successUrl: window.location.origin + '/payment/success', // 성공 시 리디렉션될 URL
          failUrl: window.location.origin + '/payment/fail',       // 실패 시 리디렉션될 URL
        })
        .catch(function (error) {
          if (error.code === 'USER_CANCEL') {
              // 결제 고객이 결제창을 닫았을 때 에러 처리
              console.log('결제가 취소되었습니다.');
          } else {
              // 그 외 에러 처리
              console.error('결제 실패:', error.message);
              alert('결제에 실패했습니다: ' + error.message);
          }
        });
      } else{
          conosole.log("sendPaymentObjToSever Error..!");
      }
    });

  } 
    catch (error) {
      console.error('orderId 생성 실패:', error);
  }
});


/** strTimeSlot(idx) - idx 에 따라 문자열을 반환하는 메서드 
 * 
 */
function strTimeSlot(idx){
  const str = ['10:00', '11:00','12:00','13:00','14:00','15:00','16:00'
    ,'17:00','18:00','19:00','20:00','21:00','22:00'];

  return str[idx];
}


/** sendPBPObjToServer(pbpObj) - 결제 전 구매 정보와 예약 정보 레코드를 컨트롤러로 보내는 메서드
 * 
 * */ 
async function sendPaymentObjToServer(mergePayload) {
  try {
    const url = `/payment/mergePayload`;
    
    const config = {
      method : 'POST',
      headers : {
        'Content-Type' : 'application/json; charset=utf-8'
      },
      body : JSON.stringify(mergePayload)
    };

    const resp = await fetch(url, config);

    const result = await resp.json();

    return result;

  } catch (error) {
    console.log('서버 전송 오류 : ', error);

    return null;
  }
}


/** generateOrderId() - orderId 생성을 위한 메서드 */ 
async function generateOrderId() {
  try {
    // fetch()
    const response = await fetch('/payment/generateOrderId', {
      method: 'POST',
      headers : {
        'Content-Type' : 'application/json'
      }
    });

    return await response.text();
    
  } catch (error) {
    console.log('generateOrderId 오류:' , error);
  }

}


/** checkAgreements() - 약관 동의에 사용되는 메서드 
 * 
 *  > Array.from() 은 유사 배열 객체 (array-like object) 혹은 이터러블 객체 (iterable) 를 
 *    진짜 배열 (Array) 로 변환해주는 메서드
 * 
 *      - requiredAgrees 는 약관 동의에 필요한 필수 체크박스들을 담은 DOM 요소 집합으로
 *        NodeList 나 HTMLCollection 형태로 반환하기에 Array.from() 으로 배열로 변환 
 * 
 * ￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣
 *  > .every()는 배열의 모든 요소가 주어진 조건을 만족하는지 검사하는 메서드
 *    
 *      - .every(checkbox => checkbox.checked) 는 변환된 배열 내의 모든 체크박스가 
 *        .checked === true (즉, 체크된 상태) 인지 확인
 * 
 *      - 모든 체크박스가 체크되어 있으면 true, 하나라도 체크되지 않았으면 false 를 반환
 * 
 * */    
function checkAgreements() {
  // 초기화
  const allRequiredChecked = Array.from(requiredAgrees).every(checkbox => checkbox.checked);
  
  if(!document.querySelector('.verify-btn')){
    paymentButton.disabled = !allRequiredChecked;

  }
    else if(document.querySelector('.verify-btn')){
      document.querySelector('.payment-button').disabled = true;
  }
}


