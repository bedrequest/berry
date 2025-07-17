/*<![CDATA[*/

// 1. 객체 초기화
const paymentButton = document.getElementById('payment-button');
const clientKey = 'test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq'; // 발급받은 클라이언트 키로 교체하세요.
const tossPayments = TossPayments(clientKey);

// payment_info 객체를 JavaScript에서 사용하기 쉽게 변수에 할당
const paymentInfo = /*[[${payment_info}]]*/ null;

// 2. 결제하기 버튼 클릭 이벤트 처리
paymentButton.addEventListener('click', function () {
    if (!paymentInfo) {
        alert('결제 정보가 없습니다.');
        return;
    }

    // 3. 토스페이먼츠 결제 요청
    tossPayments.requestPayment('카드', {
        amount: 50000, // 실제 결제 금액
        orderId: 'order_' + new Date().getTime(), // 주문 ID (실제로는 고유하게 생성해야 함)
        orderName: paymentInfo.lodgePayload.roomName, // 주문명
        customerName: paymentInfo.userPayload.userName, // 고객명
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
});

// 약관 동의 로직 (기존 코드 유지)
const allAgree = document.getElementById('terms-all');
const requiredAgrees = document.querySelectorAll('.terms-req');

function checkAgreements() {
    const allRequiredChecked = Array.from(requiredAgrees).every(checkbox => checkbox.checked);
    paymentButton.disabled = !allRequiredChecked;
}

allAgree.addEventListener('change', (e) => {
    requiredAgrees.forEach(checkbox => {
        checkbox.checked = e.target.checked;
    });
    checkAgreements();
});

requiredAgrees.forEach(checkbox => {
    checkbox.addEventListener('change', () => {
        const allRequiredChecked = Array.from(requiredAgrees).every(c => c.checked);
        allAgree.checked = allRequiredChecked;
        checkAgreements();
    });
});

checkAgreements();

/*]]>*/