// 확인 - 클라이언트 키, 
console.log(tossClientKey);
console.log(isBeforeInfo);
console.log(isTodayInfo);
console.log(rentNum);
console.log(userIdInfo);
console.log(roomIdInfo);
console.log(startDateInfo);
console.log(endDateInfo);



// 초기화
 // csrf Token
// const csrfToken  = document.querySelector('meta[name="_csrf"]').getAttribute('content');
// const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
 // 대실 이용 시간
let useTime = Number(rentNum);
 // 쿠폰 id 를 저장할 변수
let dataSetcuponId;
 // 이용 시작 시간
let startTimeInfo;
 // 이용 종료 시간
let endTimeInfo;
 // 결제 버튼 
const paymentButton = document.getElementById('payment-button');
 // 약관 - 전체 동의
const allAgree = document.getElementById('terms-all');
 // 약관 - 필수 약관 
const requiredAgrees = document.querySelectorAll('.terms-req');


/** DOMContentLoaded 이벤트 리스너  
 * 
 *  > 대실 운영 시간은 일괄적으로 10:00 ~ 22:00 까지 설정
 * 
 *  > 최소 결제 금액을 만족하는 쿠폰만 출력 
 *    (document.getElementById('cupon-select2').innerHTML 에 <option> 으로 삽입)
 * 
 * */
document.addEventListener('DOMContentLoaded', async () => {
  // 예약 가능 시간을 출력할 div
  const timeGrid = document.querySelector('.time-grid');

  const rsvdInfoPayload = {
    roomId : roomIdInfo,
    
    /** 이용 시작일 (reservation TABLE - stayTime) 과 이용 종료일
    * 
    *  > 선택한 타임 슬롯이 2025-08-04 의 10:00 ~ 14:00 까지 인 경우 
    *   new Date(`${startDateInfo}T${startTimeInfo}`).toISOString() 은 2025-08-04T01:00:00.000Z 로 
    *   new Date(`${endDateInfo}T${endTimeInfo}`).toISOString() 은 2025-08-04T05:00:00.000Z 로 표시 
    * 
    * */
     // 이용 시작일시 
    startDate : new Date(`${startDateInfo}T00:00:00.000Z`).toISOString(),
    
    // 이용 종료일시
    endDate : new Date(`${endDateInfo}T00:00:00.000Z`).toISOString()
  }

  // 확인
  console.log(rsvdInfoPayload);

  // Case 1) - 현재 날짜가 이용일 이전인 경우의 대실 예약
  if(isBeforeInfo){
    // Fragment 생성 const btnFragment = document.createElement('fragment');

    // for(let i = 0; i < 13; i++){
    //   // 버튼 생성
    //   const btn = document.createElement('button');
    //   btn.type = 'button';
    //   btn.classList.add('time-slot');
    //   btn.textContent = `${strTimeSlot(i)}`;
      
    //   // fragment 에 추가
    //   btnFragment.appendChild(btn);
    // }

    // 버튼 13개 (10:00 ~ 22:00) 생성 
    const btnFragment = await fragmentGenerator(0, rsvdInfoPayload);

    // fragment 를 이용해 한 번에 추가
    timeGrid.appendChild(btnFragment); 
  }


  // Case 2) - 현재 날짜가 이용일 당일인 경우의 대실 예약 
  if(isTodayInfo){
    const now = dayjs().format('HH');
    // 확인
    console.log('시간 정보 : ' + now);

    // 오전 10 시 이전 (운영 시작 시간 이전) 에 예약하는 경우 
    if(now < 10){
      // 버튼 13개 (10:00 ~ 22:00) 생성 
      const btnFragment = await fragmentGenerator(0, rsvdInfoPayload);

      // fragment 를 이용해 한 번에 추가
      timeGrid.appendChild(btnFragment); 
    }

    // 오전 10 시 이후 (운영 시작 시간 이후) 에 예약하는 경우 
    else {
      switch(now){
        case '10':
          const btnFragment = await fragmentGenerator(1, rsvdInfoPayload);
          timeGrid.appendChild(btnFragment); break;

        case '11':
          const btnFragment1 = await fragmentGenerator(2, rsvdInfoPayload);
          timeGrid.appendChild(btnFragment1); break;

        case '12':
          const btnFragment2 = await fragmentGenerator(3, rsvdInfoPayload);
          timeGrid.appendChild(btnFragment2); break;

        case '13':
          const btnFragment3 = await fragmentGenerator(4, rsvdInfoPayload);
          timeGrid.appendChild(btnFragment3); break;

        case '14':
          const btnFragment4 = await  fragmentGenerator(5, rsvdInfoPayload);
          timeGrid.appendChild(btnFragment4); break;

        case '15':
          const btnFragment5 = await fragmentGenerator(6, rsvdInfoPayload);
          timeGrid.appendChild(btnFragment5); break;

        case '16':
          const btnFragment6 = await fragmentGenerator(7, rsvdInfoPayload);
          timeGrid.appendChild(btnFragment6); break;

        case '17':
          const btnFragment7 = await fragmentGenerator(8, rsvdInfoPayload);
          timeGrid.appendChild(btnFragment7); break;

        case '18':
          const btnFragment8 = await fragmentGenerator(9, rsvdInfoPayload);
          timeGrid.appendChild(btnFragment8); break;

        case '19':
          const btnFragment9 = await fragmentGenerator(10, rsvdInfoPayload);
          timeGrid.appendChild(btnFragment9); break;

        case '20':
          const btnFragment10 = await fragmentGenerator(11, rsvdInfoPayload);
          timeGrid.appendChild(btnFragment10); break;

        case '21':
          const btnFragment11 = await fragmentGenerator(12, rsvdInfoPayload);
          timeGrid.appendChild(btnFragment11); break;

        default:
          const p = document.createElement('p');
          p.innerText = "대실 이용이 불가능합니다..!";

          timeGrid.appendChild(p); break;
      }
    }
  } 


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


/** document 클릭 이벤트 리스너
 * 
 *  > select 는 click 보단 change event listener 가 적합 
 * 
 *  > click Event Listener 를 사용하는 경우의 부작용
 *  
 *    - select 자체를 클릭해 옵션 목록을 열기만 해도 click이 발생하며 옵션 하나를 선택해 닫히는 시점에도 
 *      또 한 번 click 이 트리거되어 원하는 로직이 두 번 실행될 수 있음
 * 
 *    - 일부 브라우저에서는 option 요소 클릭이 select로 버블링되지 않는 경우도 있어, 일관성이 떨어짐
 * 
 * ￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣
 *  > change Event Listener 를 사용하는 경우의 장점
 * 
 *    - 사용자가 실제로 값을 바꿨을 때만 한 번만 호출되며 e.target.value 가 변경된 최종 값을 보장
 * 
 *    - 복수의 <select> 를 다룰 때도 동일하게 동작하므로 관리가 편함 
 */
document.addEventListener('click', (e) => {
  // 확인
  console.log(e.target);

  // 시간 선택 시  
  if(e.target.classList.contains('time-slot')){
    console.log("========================= e.target.classList.contains('time-slot') =========================")
    const rentUseTime = useTime + 1;

    // 초기화 
    const timeSlotArr = Array.from(document.querySelectorAll('.time-slot'));
    /**
     * 
     * > 클릭된 (또는 이벤트가 발생한) 요소 (e.target) 가 배열 timeSlotArr의 몇 번째 인덱스에 들어있는지 찾아서
     *   해당 숫자를 idx 라는 변수에 저장
     * */ 
    const idx = timeSlotArr.indexOf(e.target);
    // 확인 
    console.log(timeSlotArr);
    console.log(idx);

    // 기존 선택 요소들 모두 비활성화 
    timeSlotArr.forEach(e => e.classList.remove('time-slot-selected'));

    // 끝 인덱스 계산
    const endIdx = Math.min(idx + rentUseTime, timeSlotArr.length);
    console.log(`endIdx : ${endIdx}`);

    // 클릭한 버튼 부터 대실 이용 가능 시간 만큼 활성화 
    for(let i = idx; i < endIdx; i++){
      // 기존에 예약된 시간대를 제외한 부분만 활성화
      if(!timeSlotArr[i].disabled){
        timeSlotArr[i].classList.add('time-slot-selected');
      }
    }

    // 시간 정보 저장 
    startTimeInfo = strTimeSlot(idx);

    // endIdx 가 timslotArr.length 이면
    if(endIdx == 13){
      endTimeInfo = strTimeSlot((endIdx-1));

    } 
      else {
        endTimeInfo = strTimeSlot((endIdx-1));
    }
    // 확인
    console.log(`시작 시간 : ${startTimeInfo}`);
    console.log(`종료 시간 : ${endTimeInfo}`);
  } // 시간 선택 이벤트 리스너 fin 
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
    const startDate_info 
      = new Date(`${startDateInfo}T${startTimeInfo}`).toISOString();
     // 확인
    console.log(`startDate_info : ${startDate_info}`);

     // 이용 종료일시
    const endDate_info = new Date(`${endDateInfo}T${endTimeInfo}`).toISOString();
     // 확인
    console.log(`endDate_info : ${endDate_info}`);

     // 결제 금액은 위에서 사용된 총 결제 가격을 사용
  
     // 숙박 인원 (reservation TABLE - guestsAmount)
    const guestsAmount_info = parseInt(document.querySelector('.guestsAmount').textContent);
     // 예약 타입 - ReservationType 은 STAY 와 RENT 만 존재
    const reservationType_info = 'RENT';
  
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


/** fragmentGenerator(startIdx, endIdx) - 시간과 예약 정보에 따라 버튼 생성
 * 
 *  >  
 * 
*/ 
async function fragmentGenerator(startIdx, rsvdInfoPayload){ 
  try {
  
    /** await fetch(`/payment/reserve-info?${roomIdInfo}`) - roomId 로 해당 객실의 예약 가능한 시간대 가져오기
     * 
     * > 대실 예약 시 생성되는 모든 버튼은 13개 (idx 0 ~ idx 13) 로 예약 정보를 가져와 
     *   예약된 시간대의 번호에 해당하는 배열을 생성 
     * 
     * > 10:00 ~ 22:00 까지 대실 예약이 가능한 시간이기에 0 ~ 12 의 번호로 매핑
     * 
     * > 기존 예약 정보를 형식화한 배열 reservedSlots 을 반환받아 요소 생성 시 
     *    reservedSllots.includes(hour) 가 true 이면 해당 요소 disabled  
     * 
     * > e.g., 10:00 ~ 14:00 까지의 예약 내역이 존재하는 경우 
     *  const reserveSlot = [0,1,2,3,4,5];
     *  
     * */ 
    const resp = await fetch(`/payment/reserve-info`, {
      method : 'POST',
      headers : {
        'Content-Type' : 'application/json; charset=utf-8'
      },
      body : JSON.stringify(rsvdInfoPayload)
    });
    // 응답으로 예약 정보 확인
    const reservedSlots = await resp.json();


    // 확인
    console.log(`reserveSlots : ${reservedSlots}`);

    // DocumentFragment 생성 
    const frag = document.createDocumentFragment();
    
    for(let i = startIdx; i < 13; i++){
      // 버튼 생성
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.classList.add('time-slot');
      btn.textContent = `${strTimeSlot(i)}`;

      // 기존에 예약된 시간대는 비활성화
      if(reservedSlots.includes(i)){ btn.disabled = true; }
  
      frag.appendChild(btn);
    }
  
    return frag;

  } catch (error) {
    console.log(`fragmentGenerator ERROR : ${error}`);    
  }
  
}


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


