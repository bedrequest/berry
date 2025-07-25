// 확인
console.log("======================== paymentCacncel.js in ========================");

// 초기화
 // 환불 버튼
const paymentCancel = document.querySelector('.payment-cancel');  
 // 환불 사유 
const cancelReason = document.querySelector('.cancelReason');

/** 환불 버튼 이벤트 리스너 */
paymentCancel.addEventListener('click', async () => {
  try {
    // orderId 가져오기 (추후 수정)
    const orderId_info = "order_1753423933297_1_54f9d3f4";

    // cancelReason 가져오기 
    const cancelReason_info = "단순 변심"; ;
    
    // payload
    const cancelPayload = {
      orderId : orderId_info,
      cancelReason : cancelReason_info
    }

    postPaymentCancelToServer(cancelPayload).then(result => {
      if(result == '0'){ 
        alert('결제 취소가 성공적으로 완료되었습니다!');
      } 
        else { alert('결제 취소가 성공적으로 완료되지 못했습니다!');}
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