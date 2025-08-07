console.log("header");
console.log(notificationUserId);
// 알림 버튼이 있는 경우에만 (로그인이 되었을 경우)

console.log(document.getElementById("notificationBtn"));

getAlarmList(notificationUserId).then(result => {
    const ul = document.getElementById("notificationUl");
    let str = "";
    let i = 0;
    
    if(result.length > 0){
        // 헤더에서는 5개만 출력
        for(let alarmObj of result){
            i++;

            const date = new Date(...alarmObj.regDate);
            const formatted = date.toISOString().substring(0, 19).replace("T", " ");


            if(i > 4){

                return;
            }

            switch(alarmObj.code){
                case "s_reservation" :
    
                    str += `<li>
                    <p class="notification-main-tit">숙소 예약에 성공했습니다.</p>
                    <p class="notification-under-tit">${formatted}</p>
                    </li>`;
    
                    break;
                case "c_reservation" :
                    
    
                    str += `<li>
                    <p class="notification-main-tit">숙소 예약 취소가 되었습니다.</p>
                    <p class="notification-under-tit">${formatted}</p>
                    </li>`;
    
                    break;
                case "s_signup" :
    
                    str += `<li>
                    <p class="notification-main-tit">회원가입을 환영합니다.</p>
                    <p class="notification-under-tit">${formatted}</p>
                    </li>`;
    
                    break;
                case "newSign_coupon" :
                    str += `<li>
                    <p class="notification-main-tit">신규 회원 가입 쿠폰이 발급되었습니다.</p>
                    <p class="notification-under-tit">${formatted}</p>
                    </li>`;
    
                    break;
                case "l_review" :
                    str += `<li>
                    <p class="notification-main-tit">작성한 리뷰에 좋아요가 추가되었습니다.</p>
                    <p class="notification-under-tit">${formatted}</p>
                    </li>`
    
                    break;
                case "r_review" :
    
                    str += `<li>
                    <p class="notification-main-tit">작성한 리뷰의 신고 누적으로 인해 삭제되었습니다.</p>
                    <p class="notification-under-tit">${formatted}</p>
                    </li>`;
                    
                    break;
                case "s_qna" :
    
                    str += `<li>
                    <p class="notification-main-tit">고객문의에 남기신 글에 답변 되었습니다.</p>
                    <p class="notification-under-tit">${formatted}</p>
                    </li>`;
                    break;
                    
            }
        }
        if(i > 4){
            str += `
            <li class="notification-more">
            <a href="/user/myPage">더보기</a>
            </li>
            `;
        }

    }else{
        str = `<li class="none-notification">현재 알림이 없습니다.</li>`
    }

    ul.innerHTML = str;
    

})

if(document.getElementById("notificationBtn")){
    document.getElementById("notificationBtn").addEventListener("click", (e) => {
        
      document.getElementById("notification").classList.toggle("toggle-display");
        
        
    })
}

// 알림창을 띄우고 다른곳을 클릭 했을 경우
document.addEventListener("click", (e) => {

    // 안열려있으면 무시
    if(document.getElementById("notification").classList.contains("toggle-display")){
        return;
    }

    // 열려있는경우
    if(
        !document.getElementById("notificationBtn").contains(e.target) && // 더보기버튼
        !document.getElementById("notification").contains(e.target) // 알림 div 둘다 아닐경우
    
    ){
        document.getElementById("notification").classList.add("toggle-display"); // 알림 창 닫기
    }
})

async function getAlarmList(userId) {    

    try {
        
        const url = `/user/getAlarmList/${userId}`
        const resp = await fetch(url);
        const result = await resp.json();

        return result;

    } catch (error) {
        console.log(error);
    }


}


