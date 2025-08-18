// 확인 - 클라이언트 키, 
console.log(tossClientKey);
console.log(userIdInfo);
console.log(roomIdInfo);
console.log(startDateInfo);
console.log(endDateInfo);



// 초기화
 // csrf Token
// const csrfToken  = document.querySelector('meta[name="_csrf"]').getAttribute('content');
// const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
 // 쿠폰 id 를 저장할 변수
let dataSetcuponId;
 // 결제 버튼 
const paymentButton = document.getElementById('payment-button');
 // 약관 - 전체 동의
const allAgree = document.getElementById('terms-all');
 // 약관 - 필수 약관 
const requiredAgrees = document.querySelectorAll('.terms-req');


/** 문서 로드 시 이벤트 리스너 */
document.addEventListener('DOMContentLoaded', () => {
  checkAgreements();

  /** 최소 결제 금액을 만족하는 쿠폰만 출력 
   *  
   *  > document.getElementById('cupon-select2').innerHTML 에 <option> 으로 삽입)
   * 
   *  */ 
  cuponList.forEach(v => {
    // CuponTemplate TABLE 에서 CuponType 으로 쿠폰 정보 조회 
    fetch(`/payment/cupon-info?cupon-type=${v.cuponType}`)
    
    /** 최소 결제 금액을 만족하는 경우 셀렉트에 출력 
     * 
     *  > `<option value=${result.cuponPrice} data-cupon-id="v.cuponId">result.cuponTitle</option>`
     * */ 
    .then(resp => { 
      if(resp.ok){
        return resp.json(); 
      } 

      return;
    })

    .then(result => {
      // 확인 
      console.log(`========================= fetch(/payment/cupon-info?cupon-type=${v.cuponType}) =========================`);
      console.log(result);

      if(result && (Number(strikePrice) < Number(result.theMinimumAmount))){
        return; 
      }

      // 숫자로 변환
      else if(result && (Number(strikePrice) >= Number(result.theMinimumAmount))){
        // 동적으로 <option> 생성
        const option = document.createElement('option');
         // dataset 으로 cupon-id 초기화 
        option.dataset.cuponId = v.cuponId;
         // value 초기화
        option.value = result.cuponPrice;
         // 쿠폰 이름 넣기
        option.innerText = result.cuponTitle;

        document.getElementById('cupon-select2').appendChild(option);

      }
     
    })
  })
}) 


/** 쿠폰 이벤트 리스너 - 쿠폰의 변동사항을 적용
 * 
 */
 // 쿠폰이 있는 경우에만 
if(document.querySelector('.select-cupon')){
  const selectedCp = document.querySelector('.select-cupon');

  selectedCp.addEventListener('change', (e) => {
    // 선택된 옵션 가져오기 
    const selectedOption = e.target.selectedOptions[0];

    // data-cupon-id 와 value 읽기 
    dataSetcuponId = selectedOption.dataset.cuponId;
    const value = Number(selectedOption.value);

    // 확인
    console.log(`선택된 쿠폰 ID : ${dataSetcuponId}`);
    console.log(`할인 금액 : ${value}`);

    // 쿠폰 할인에 표시
    document.querySelector('.usingCupon').textContent = `${value} 원`;

    // 할인 금액에 표시
    document.querySelector('.cuponPrice').textContent = `${e.target.value}`;

    /** 정가 - 쿠폰 할인가를 총 결제 금액에 표시 */
    let pbpTotalAmount = Number(strikePrice) - Number(e.target.value) 
  
    document.querySelector('.pbpTotalAmount').textContent = `${pbpTotalAmount}`;

    // 결제하기 버튼 부분에 표시 
    document.querySelector('.payment-button').textContent = `${pbpTotalAmount} 원 결제하기`;
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
    const customerKey = await getUserCustomerKey(userIdInfo);
     // tossPayment 의 결제 메서드 호출 
    const payment = tossPayments.payment({ customerKey });
    

    /** payment_before_payment TABLE 에 INSERT 할 pbpObj 속성 초기화 */
     // order_id 는 Server 에서 생성
    const orderId_info = await generateOrderId();
     // 숙소명 (pbp TABLE - orderName)
    const orderName_info = document.querySelector('.roomName').textContent;
     // cupon_id (임시 생성, 실제로는 페이지 이동 시 비동기로 로딩)
    const cuponId_info = dataSetcuponId;
     // 쿠폰으로 할인 받은 금액 
    const cuponPrice_info = Number(document.querySelector('.cuponPrice').textContent);
     // 원래 가격 
    const strikePrice_info = Number(document.querySelector('.strikePrice').textContent);
     // 총 결제 가격 
    const pbpTotalAmount_info = Number(document.querySelector('.pbpTotalAmount').textContent);  
     // 결제 수단 (method)
    const method_info = document.querySelector('.payment-methods input[name="payment"]:checked').value; 
  
  
    /** reservation TABLE 에 INSERT 할 reservationObj 속성 초기화 */
     // order_id 는 위에서 생성된 id 사용 
      
     // room_id (임시 생성)
    const roomId_info = roomIdInfo;
     // user_id 
    const userId_info = userIdInfo;
    
     /** 이용 시작일 (reservation TABLE - stayTime) 과 이용 종료일
      * 
      *  > 선택한 타임 슬롯이 2025-08-04 의 10:00 ~ 14:00 까지 인 경우 
      *   new Date(`${startDateInfo}T${startTimeInfo}`).toISOString() 은 2025-08-04T01:00:00.000Z 로 
      *   new Date(`${endDateInfo}T${endTimeInfo}`).toISOString() 은 2025-08-04T05:00:00.000Z 로 표시 
      * 
      * */ 
    const startDate_info = new Date(startDateInfo).toISOString();
     // 확인
    console.log(`startDate_info : ${startDate_info}`);

     // 이용 종료일시
    const endDate_info = new Date(endDateInfo).toISOString();
     // 확인
    console.log(`endDate_info : ${endDate_info}`);

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

  } // try{} fin
    catch (error) {
      console.error('orderId 생성 실패:', error);
  }
});



/** getUserCustomerKey(userId) - userId 로 customerKey 가져오기
 * 
 * 
 */
async function getUserCustomerKey(userId) {
  try{
    const resp = await fetch(`/payment/reserve-ck?ck=${userId}`);

    const result = await resp.text();

    return result;

  } catch(error){

    console.log(`getUserCustomerKey ERROR : ${error}`);
  }
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


