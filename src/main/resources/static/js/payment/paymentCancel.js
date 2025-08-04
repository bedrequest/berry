// 확인
console.log("======================== paymentCacncel.js in ========================");

// 초기화
 // 환불 버튼
const paymentCancel = document.getElementById('subRefundBtn');  
 // 환불 사유 
const cancelReason = document.querySelector('.cancelReason');

/** 환불 버튼 이벤트 리스너 */
paymentCancel.addEventListener('click', async () => {
  try {
    // 초기화
    let cancelReason_info;
    
    // orderId 가져오기 
    const orderId_info = document.getElementById("reservationOrderId").value;
    // 환불 사유 적는 요소
    const otherReason = document.getElementById("otherReason");
    // 확인
        
    
    if(otherReason.value != ""){
      // 환불 사유 초기화
      cancelReason_info = otherReason.value;

      // 기타 일 경우 가져갈 값

    } else{
        // select 가 기타가 아닐경우 가져갈 값
        const selected = document.getElementById("refundReason");
        // 환불 사유 초기화
        cancelReason_info = selected.value;

    }
    
    // payload
    const cancelPayload = {
      orderId : orderId_info,
      cancelReason : cancelReason_info
    }
    console.log("cancelPayload >>");
    console.log(cancelPayload);

    postPaymentCancelToServer(cancelPayload).then(result => {
      if(result == '0'){ 
        alert('결제 취소가 성공적으로 완료되었습니다!');
        location.reload(true);
      } 
        else if(result == '-1'){ 
          alert('결제 취소가 성공적으로 완료되지 못했습니다!');
        } else if(result == '-2'){
          alert('환불이 불가능합니다..!');
        }
    })

  } catch (error) {
    console.log(`환불 중 ERROR 발생 ! (에러 내용 : ${error})`);
  }
   
})


/** postPaymentCancelToServer(cancelPayload) - 결제 취소 API 호출에 필요한 정보를 서버에 전송 */
async function postPaymentCancelToServer(cancelPayload){
  try {
    const url = `/payment/cancel`;

    const res = await fetch(url, {
      method : 'POST',
      headers : {
        'Content-Type' : 'application/json; charset=utf-8'
      },
      body : JSON.stringify(cancelPayload)
    });

    const result = await res.text();

    return result;

  } catch (error) {
    console.log(error);
  }
}