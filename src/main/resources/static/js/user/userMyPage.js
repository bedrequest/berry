console.log("userMyPage in");
console.log("userId > ", myPageUserId);
console.log("checkedPassword", checkedPassword);
console.log(userTagMask);
console.log("reservationPresentList > ", reservationPresentList);
console.log("alarmList > ", alarmList);

if(checkedPassword == "fail"){
    alert("현재 비밀번호가 일치하지 않습니다.");
}

// 휴대전화 인증 버튼
const certifiedPhoneBtn = document.getElementById("certifiedPhoneBtn");
// 이메일 인증 버튼
const certifiedEmailBtn = document.getElementById("certifiedEmailBtn");
// 비밀번호 변경 버튼
const changePwModalBtn = document.getElementById("changePwModalBtn");

// 기능 =====================================================================

// 회원정보 수정 모달 변수========================================================
const inputUserEmail = document.getElementById("userEmail");
const inputUserName = document.getElementById("userName");
const inputuserPhone = document.getElementById("userPhone");
const userInfoUpdateModal = document.getElementById("userInfoUpdateModal");

const inputs = [inputUserName, inputuserPhone, inputUserEmail];
let valid = false; // validation 변수
let duplicate = true; // 중복검사 변수
let isSelectFavoritTag = false; // 선호태그 선택여부
// 기존에 입력된 이메일 값 저장
const beforeUpdateEmailInputValue = document.getElementById("userEmail").value;
// 기존에 입력된 이름 값 저장
const beforeUpdateNameInputValue = document.getElementById("userName").value;
// 기존에 입력된 번호 값 저장
const beforeUpdatePhoneInputValue = document.getElementById("userPhone").value;
// =============================================================================


// 회원정보 수정 모달============================================================

// Validation 함수

// 유효성 검사 이메일
function isValidUserEmail(inputs) {
    // 이메일 유효성
    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    
    return regexEmail.test(inputs[2].value)
}

// 유효성 검사 이름
function isValidUserName(inputs) {
    // 이름 유효성 (한글이름만)
    const regexName = /^[가-힣]+$/;
  
    return regexName.test(inputs[0].value);
}

// 유효성 검사 휴대전화
function isValidUserPhoneNumber(inputs) {
    const phoneRegex = /^01([0|1|6|7|8|9])([0-9]{3,4})([0-9]{4})$/;
    return phoneRegex.test(inputs[1].value);
}

// 가져온 선호태그 값이 있다면
function applyTagMask(userTagMask) {
    const tagButtons = document.querySelectorAll(".tag-btn");
    const viewTag = document.querySelectorAll(".user-favorite-tag");
  
    tagButtons.forEach(btn => {
      const id = Number(btn.dataset.id);
    //   const tagId = Number(viewTag.dataset.tagId);

      // 비트가 1인지 확인: tagMask & (1 << id)
      if ((userTagMask & (1 << id)) !== 0) {
        btn.classList.add("tag-btn-toggle"); // 선택된 상태로 표시
        document.getElementById(`tag${id}`).classList.add("view-tag");
        
    } else {
        btn.classList.remove("tag-btn-toggle"); // 선택 안된 상태로 초기화
        if(document.getElementById(`tag${id}`)){
            document.getElementById(`tag${id}`).classList.remove("view-tag");
        }
      }
    });
  }
  applyTagMask(userTagMask);

// 선호 태그 toggle / 비트 플래그형식
function saveTagMask(){
  const tagButtons = document.querySelectorAll(".tag-btn");
  let tagMask = 0;
  if(userTagMask != 0){
    tagMask = userTagMask;
  }

  tagButtons.forEach(btn => {
    btn.addEventListener("click", (e) => {
      const selectedTags = document.querySelectorAll(".tag-btn.tag-btn-toggle");
      const favoritTagInput = document.getElementById("userFavoritTag") || document.createElement("input");
      favoritTagInput.type = "hidden";
      favoritTagInput.name = "userFavoriteTag";
      favoritTagInput.id = "userFavoritTag"
  
      // 이미 선택된 경우는 해제 허용
      if (btn.classList.contains("tag-btn-toggle")) {
          btn.classList.remove("tag-btn-toggle");
          tagMask -= 1 << Number(e.target.dataset.id);
          isSelectFavoritTag = true;
        }
        // 새로 선택하려는 경우 제한 체크
        else if (selectedTags.length < 3) {
            
            // return 값 받고
            console.log(e.target.dataset);
            btn.classList.add("tag-btn-toggle");
            tagMask += 1 << Number(e.target.dataset.id);
            isSelectFavoritTag = true;
            
        } else {
            alert("최대 3개까지만 선택할 수 있습니다.");
        }

        console.log("tagMask > ",tagMask);
        favoritTagInput.value = tagMask;
        console.log(favoritTagInput.value);
        
        if(!document.getElementById("userFavoritTag")){

            document.getElementById("userInfoUpdateForm").appendChild(favoritTagInput);
        }

        updateSubmitButtonState();

    });

  });
  
}
saveTagMask();

function updateSubmitButtonState() {
    const isValid = isValidUserName(inputs) && isValidUserPhoneNumber(inputs) && isValidUserEmail(inputs);
  
    const isEmailChanged = beforeUpdateEmailInputValue !== inputUserEmail.value;
    const isNameChanged = beforeUpdateNameInputValue !== inputUserName.value;
    const isPhoneChanged = beforeUpdatePhoneInputValue !== inputuserPhone.value;
  
    const favoritTagInput = document.getElementById("userFavoritTag");
    const tagMask = favoritTagInput ? Number(favoritTagInput.value) : 0;
    let hasSelectedFavoriteTags = tagMask !== 0;
    if(userTagMask != 0){
        hasSelectedFavoriteTags = tagMask !== userTagMask;
    }

    const isChanged = isEmailChanged || isNameChanged || isPhoneChanged || hasSelectedFavoriteTags;
    console.log("isEmailChanged >", isEmailChanged);
    console.log("isNameChanged >", isNameChanged);
    console.log("isPhoneChanged >", isPhoneChanged);
    console.log("hasSelectedFavoriteTags >", hasSelectedFavoriteTags);
  
  
    // 버튼 상태 업데이트
    document.getElementById("modalSubBtn").disabled = !(isValid && isChanged && duplicate);
    console.log("isChanged > ", isChanged);
    console.log("isValid > ", isValid);
    console.log("duplicate > ", duplicate);
    
  }


// 회원정보 수정 input validation
inputs.forEach(input => {
    input.addEventListener("input", () => {

        updateSubmitButtonState();
        // valid = isValidUserName(inputs) && isValidUserPhoneNumber(inputs) && isValidUserEmail(inputs);
        
        // console.log("valid >", valid);
        const isEmailChanged = beforeUpdateEmailInputValue !== inputUserEmail.value;
        // const isNameChanged = beforeUpdateNameInputValue !== inputUserName.value;
        // const isPhoneChanged = beforeUpdatePhoneInputValue !== inputuserPhone.value;

        // const isChanged = isNameChanged || isPhoneChanged || isEmailChanged;
        // text 초기화
        document.getElementById("updateSubInfo").innerText = "";

        if(inputUserEmail.value != "" && input.id == "userEmail" && isValidUserEmail(inputs)){
            
            duplicateEmailCheckedToServer(input.value).then(result => {
                console.log(result);
                if(result === "ok"){
                    duplicate = true;
                    document.getElementById("updateSubInfo").style.color = "green";
                    document.getElementById("updateSubInfo").innerText = "사용 가능한 이메일입니다.";

                    updateSubmitButtonState();
                }else{

                    if(!isEmailChanged){
                        document.getElementById("updateSubInfo").innerText = "";
                        duplicate = true;
                        updateSubmitButtonState();
                    }else{
                        document.getElementById("updateSubInfo").style.color = "red";
                        document.getElementById("updateSubInfo").innerText = "중복된 이메일입니다.";
                        duplicate = false;
                        updateSubmitButtonState();
                        console.log("중복");
                    }
                }
                // document.getElementById("modalSubBtn").disabled = !(valid && isChanged && duplicate);
            })
            updateSubmitButtonState();
            
        }

    })
})

// 스크롤 막기
function openModal() {
    document.body.style.overflow = 'hidden';
  }
  
  function closeModal() {
    document.body.style.overflow = 'auto';
  }


// 회원정보수정 버튼 클릭
document.getElementById("userInfoUpdateBtn").addEventListener("click", () => {
    openModal();
    userInfoUpdateModal.style.display = "block";
})

// 회원정보수정 닫기 버튼 클릭
document.getElementById("closeModal").addEventListener("click", () => {
    closeModal();
    userInfoUpdateModal.style.display = "none";
})

// 회원탈퇴 닫기 버튼 클릭
document.getElementById("wdCloseModal").addEventListener("click", () => {
    closeModal();
    document.getElementById("withdrawMembershipModal").style.display = "none";
})

// 회원 탈퇴 버튼 클릭
document.getElementById("wmMembership").addEventListener("click", () => {
    
    const isConfirmed = confirm("정말로 탈퇴 하시겠습니까?");
    if (isConfirmed) {
        // 탈퇴 진행
        console.log("탈퇴 실행");
        document.getElementById("userInfoUpdateModal").style.display = "none";
        document.getElementById("withdrawMembershipModal").style.display = "block";
        openModal();

    } else {
        // 탈퇴 취소
        console.log("탈퇴 취소");
    }
    })

// 회원정보 수정 모달 =====================================================end

// 휴대폰 인증 모달 =========================================================

// 변수 =================
let certifiedNumber;

// 휴대폰 인증 버튼 클릭
if(certifiedPhoneBtn){
    document.getElementById("certifiedPhoneBtn").addEventListener("click", () => {
        openModal();
        document.getElementById("certifiedUserPhone").style.display = "block"
    })
}
// 휴대폰 인증 닫기 버튼 클릭
document.getElementById("certifiedUserPhoneModalClose").addEventListener("click", () => {
    closeModal();
    document.getElementById("certifiedUserPhone").style.display = "none"
})

// 인증번호 받기 버튼 클릭
document.getElementById("getCertifiedPhoneBtn").addEventListener("click", () => {

    document.getElementById("getCertifiedPhoneBtn").style.display = "none"
    document.getElementById("verifyBox").style.display = "block";
    document.getElementById("certifiedUserPhoneSubBtn").style.display = "inline-block";

    getCertifiedNumber(myPageUserId).then(result => {
        console.log(result);
        if(result == "fail"){
            alert("인증번호 받기가 실패했습니다.");
        }else{
            certifiedNumber = result;
        }
    })

})
// 인증버튼 클릭
document.getElementById("certifiedUserPhoneSubBtn").addEventListener("click", () => {
    if(certifiedNumber === document.getElementById("certifiedNumber").value){
        certifiedPhoneOk(myPageUserId).then(result => {
            if(result == "ok"){
                alert("인증되었습니다.");
                location.reload(true);
            }else{
                alert("인증에 실패했습니다.");
                document.getElementById("certifiedNumber").focus();
            }
        })
    }else {
        alert("인증번호가 일치하지 않습니다.");
    }
})

// 이메일 인증 모달 =========================================================

// 변수 =================
let certifiedCode;

// 이메일 인증 버튼 클릭
if(certifiedEmailBtn){
    document.getElementById("certifiedEmailBtn").addEventListener("click", () => {
        openModal();
        document.getElementById("certifiedUserEmail").style.display = "block"
    })
}
// 이메일 인증 닫기 버튼 클릭
document.getElementById("certifiedUserEmailModalClose").addEventListener("click", () => {
    closeModal();
    document.getElementById("certifiedUserEmail").style.display = "none"
})
// 인증코드 받기 버튼 클릭
document.getElementById("getCertifiedEmailBtn").addEventListener("click", () => {
    
    document.getElementById("getCertifiedEmailBtn").style.display = "none"
    document.getElementById("verifyEmailBox").style.display = "block";
    
    getCertifiedCode(myPageUserId).then(result => {
        console.log(result);
        if(result == "fail"){
            alert("인증코드 받기가 실패했습니다.")
        }else{
            certifiedCode = result;
        }
    })
    
})
// 인증버튼 클릭
document.getElementById("certifiedUserEmailSubBtn").addEventListener("click", () => {
    if(certifiedCode === document.getElementById("certifiedCode").value){
        certifiedEmailOk(myPageUserId).then(result => {
            if(result == "ok"){
                alert("인증되었습니다.");
                location.reload(true);
            }else{
                alert("인증에 실패했습니다.");
                document.getElementById("certifiedCode").focus();
            }
        })
    }else {
        alert("인증코드가 일치하지 않습니다.");
    }
})

// 비밀번호 변경 모달 ================================================
// 변수 =========================
const currentPw = document.getElementById("currentPw");
const changePw = document.getElementById("changePw");
const confirmPw = document.getElementById("confirmPw");
const pwdInputs = [currentPw, changePw, confirmPw];
let checkPwValid = false;
// ==============================

if(changePwModalBtn){
    document.getElementById("changePwModalBtn").addEventListener("click", () => {
        openModal();
        document.getElementById("changePwModal").style.display = "block";
    })
}
document.getElementById("closePwModal").addEventListener("click", () => {
    closeModal();
    document.getElementById("changePwModal").style.display = "none";
})

pwdInputs.forEach(input => {
    input.addEventListener("input", () => {
    
        if(input.value != "" && pwdInputs[1].value == pwdInputs[2].value && isValidUserPassword(pwdInputs)){
            checkPwValid = true;
        }else{
            checkPwValid = false;
        }
        console.log(checkPwValid);
        document.getElementById("changePwSubBtn").disabled = !checkPwValid;

    })
})

// 유효성 검사 비밀번호
function isValidUserPassword(inputs) {
    // 비밀번호 유효성 (영문 대소문자, 숫자, 특수문자 포함 8자리 이상)
    const regexPassword = /^(?=.*[a-z])(?=.*[!@#$%^*=])(?=.*[0-9]).{8,}$/
    
    return regexPassword.test(inputs[1].value);
  
  }

// 알림 리스트 출력
if(alarmList.length > 0){
    const myPageNoticeUl = document.getElementById("myPageNoticeUl");


    // code 에 따른 상태 구분
    /*
    <code>
    "s_reservation" (예약 성공문구)
    "c_reservation" (예약 취소문구)

    "s_signup" (회원가입 환영문구)

    "newSign_coupon" (신규 회원가입 쿠폰)

    "l_review" (리뷰 좋아요)
    "r_review" (리뷰 신고누적)

    "s_qna" (qna 답변 완료)
    */
    let str = "";
    let totalReviewLike = 0;
    for(let alarm of alarmList){
        const formatted = alarm.regDate.substring(0, 19).replace("T", " ");
        switch(alarm.code){
            case "s_reservation" :

                str += `<li>
                <span>숙소 예약에 성공했습니다.</span>
                <span>[결제일자 : ${formatted}]</span>
                </li>`;

                break;
            case "c_reservation" :
                

                str += `<li>
                <span>숙소 예약 취소가 되었습니다.</span>
                <span>[취소일자 : ${formatted}]</span>
                </li>`;

                break;
            case "s_signup" :

                str += `<li>
                <span>회원가입을 환영합니다.</span>
                <span>[가입일자 : ${formatted}]</span>
                </li>`;

                break;
            case "newSign_coupon" :
                str += `<li>
                <span>신규 회원 가입 쿠폰이 발급되었습니다.</span>
                <span>[발급일자 : ${formatted}]</span>
                </li>`;

                break;
            case "l_review" :
                totalReviewLike ++;

                break;
            case "r_review" :

                str += `<li>
                <span>작성한 리뷰의 신고 누적으로 인해 삭제되었습니다.</span>
                <span>[삭제일자 : ${formatted}]</span>
                </li>`;
                
                break;
            case "s_qna" :

                str += `<li>
                <span>고객문의에 남기신 글에 답변 되었습니다.</span>
                <span>[답변일자 : ${formatted}]</span>
                </li>`;
                break;
                
        }

    }
    if(totalReviewLike > 0){
        str += `<li>
        <span>작성한 리뷰를 ${totalReviewLike}명이 좋아합니다.</span>
        </li>`;
    }

    myPageNoticeUl.innerHTML = str;
}

// 세부예약 내용확인 Modal

// 세부예약정보 모달창 띄우기


document.querySelectorAll(".reservationInfoBtn").forEach(btn => {

    btn.addEventListener("click", (e) => {

        openModal();
        document.getElementById("reservationDetailInfoModal").style.display = "block";

        const index = e.target.dataset.index;
        const checkDate = e.target.dataset.customcheckdate;
        const customOrderId = e.target.dataset.customorderid;
        const orderId = e.target.dataset.orderid;
        console.log(orderId);
        console.log(checkDate);
        console.log(customOrderId);
        console.log(index);

        const ul = document.getElementById("reservationDetailUl");
        const modalActions = document.getElementById("detailInfoModalActions");
        console.log(modalActions);

        let str = `
                <li>
                    <p class="reservation-left-tit">호텔 정보</p>
                    <p class="reservation-right-tit">${reservationPresentList[index].lodgeName}</p>
                </li>
                <li>
                    <p class="reservation-left-tit">객실명</p>
                    <p class="reservation-right-tit">${reservationPresentList[index].roomName}</p>
                </li>
                <li>
                    <p class="reservation-left-tit">장소</p>
                    <p class="reservation-right-tit">${reservationPresentList[index].lodgeAddr}</p>
                </li>
                <li>
                    <p class="reservation-left-tit">숙소 유형</p>
                    <p class="reservation-right-tit">${reservationPresentList[index].lodgeType}${reservationPresentList[index].reservationType === 'STAY' ? '(숙박)' : '(대실)'}</p>
                </li>
                <li>
                    <p class="reservation-left-tit">${reservationPresentList[index].reservationType === 'STAY' ? '숙박이용시간' : '대실이용시간'}</p>
                    <p class="reservation-right-tit">${reservationPresentList[index].reservationType === 'STAY' ? reservationPresentList[index].stayTime : reservationPresentList[index].rentTime}</p>
                </li>
                <li>
                    <p class="reservation-left-tit">예약 번호</p>
                    <p class="reservation-right-tit">${customOrderId}</p>
                </li>
                <li>
                    <p class="reservation-left-tit">결제 금액</p>
                    <p class="reservation-right-tit">${reservationPresentList[index].totalAmount}원</p>
                </li>
                <li>
                    <p class="reservation-left-tit">인원</p>
                    <p class="reservation-right-tit">${reservationPresentList[index].guestsAmount}명</p>
                </li>
                <li>
                    <p class="reservation-left-tit">체크인/체크아웃</p>
                    <p class="reservation-right-tit">${checkDate}</p>
                </li>
                <li>
                    <p class="reservation-left-tit">연락처</p>
                    <p class="reservation-right-tit">${reservationPresentList[index].businessCall}</p>
                </li>
                `; 
                ul.innerHTML = str;
        let str2 = `
                <button
                type="button"
                class="cancel-reservation"
                data-index="${index}"
                data-orderid="${orderId}"
                id="cancelReservation${index}">예약취소</button>        
                `;
                // 마지막 자식요소로 추가
                modalActions.insertAdjacentHTML('afterbegin', str2);

    })
})

// 세부예약정보 모달창 닫기
document.getElementById("reservationDetailInfoCloseModal").addEventListener("click", (e) => {
    const modalActions = document.getElementById("detailInfoModalActions");
    
    if(document.querySelector(".cancel-reservation")){
        // 버튼 중복 생성 방지
        const closeBtn = document.querySelector(".cancel-reservation");
        modalActions.removeChild(closeBtn);
    }
    
    
    closeModal();
    document.getElementById("reservationDetailInfoModal").style.display = "none";
})

// 예약 취소 모달 띄우기
document.addEventListener("click", (e) => {
    if(e.target.classList.contains("cancel-reservation")){
        console.log(2);
            document.getElementById("reservationDetailInfoModal").style.display = "none";

            document.getElementById("reservationCancelModal").style.display = "block";

            const orderId = e.target.dataset.orderid;
            const index = e.target.dataset.index;
            console.log(orderId);
            console.log(index);
            document.getElementById("reservationOrderId").value = orderId;
    }
})

// 예약 취소 모달 제출버튼 클릭
// document.getElementById("subRefundBtn").addEventListener("click", () => {
//     const orderId = document.getElementById("reservationOrderId").value;
//     const otherReason = document.getElementById("otherReason");
//     console.log("orderId 최종확인용 >> ", orderId);
        
    
//     if(otherReason.value != ""){
        
//         // 기타 일 경우 가져갈 값
//         console.log("기타 사유 최종확인용 >> ", otherReason.value);
//     }else{

//         // select 가 기타가 아닐경우 가져갈 값
//         const selected = document.getElementById("refundReason");
//         console.log(selected.value);
//     }

// })

// 환불 사유가 기타 일 경우 직접 작성 란 보여주기
document.getElementById("refundReason").addEventListener("change", function () {
    const otherBox = document.getElementById("otherReasonBox");
    if (this.value === "other") {
      otherBox.style.display = "block";
    } else {
      otherBox.style.display = "none";
    }
  });


// 예약 취소 모달 닫기
document.getElementById("rcCloseModal").addEventListener("click", () => {
    const modalActions = document.getElementById("detailInfoModalActions");
    
    if(document.querySelector(".cancel-reservation")){
        // 버튼 중복 생성 방지
        const closeBtn = document.querySelector(".cancel-reservation");
        modalActions.removeChild(closeBtn);
    }
    closeModal();
    document.getElementById("reservationCancelModal").style.display = "none";

})

// 복사 버튼
document.querySelectorAll(".print-number").forEach(btn => {
    btn.addEventListener("click", () => {
        const reservationNumber = document.querySelectorAll(".reservation-right-tit");

        reservationNumber.forEach(data => {
            if(Number(data.dataset.rid) ==  Number(btn.dataset.bid)){
                let text = data.textContent.replace("복사", "").trim();
                copyToClipboard(text);
            }
        })
    })
})

// Clipboard API
async function copyToClipboard(text) {
    try {
      await navigator.clipboard.writeText(text);
      console.log('텍스트가 클립보드에 복사되었습니다.');
    } catch (err) {
      console.error('클립보드 복사 실패: ', err);
    }
  }
  


// 비동기

// 이메일 중복검사.
async function duplicateEmailCheckedToServer(userEmail) {
  
    try {
      
      const url = `/user/duplicateCheckedEmail/${encodeURIComponent(userEmail)}`;
      const resp = await fetch(url);
      const result = await resp.text();
      return result;
  
    } catch (error) {
      console.log(error);
    }
  
  }

// 휴대폰 인증번호 받기
async function getCertifiedNumber(myPageUserId) {

    try {
        const url = `/user/getCertifiedNumber/${myPageUserId}`
        const resp = await fetch(url);
        const result = await resp.text();

        return result;
    } catch (error) {
        console.log(error);
    }
    
}

// 휴대폰 인증 활성
async function certifiedPhoneOk(myPageUserId) {

    try {
        const url = `/user/certifiedPhoneOk/${myPageUserId}`
        const resp = await fetch(url);
        const result = await resp.text();

        return result;
    } catch (error) {
        console.log(error);
    }
    
}
// 이메일 인증코드 받기
async function getCertifiedCode(myPageUserId) {

    try {
        const url = `/user/getCertifiedCode/${myPageUserId}`
        const resp = await fetch(url);
        const result = await resp.text();

        return result;
    } catch (error) {
        console.log(error);
    }
    
}

// 이메일 인증 활성
async function certifiedEmailOk(myPageUserId) {

    try {
        const url = `/user/certifiedEmailOk/${myPageUserId}`
        const resp = await fetch(url);
        const result = await resp.text();

        return result;
    } catch (error) {
        console.log(error);
    }
    
}





