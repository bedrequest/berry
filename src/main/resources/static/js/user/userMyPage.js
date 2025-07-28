console.log("userMyPage in");
console.log(myPageUserId);


// 기능 =====================================================================

// 회원정보 수정 모달 ========================================================
const inputUserName = document.getElementById("userName");
const inputuserPhone = document.getElementById("userPhone");

const inputs = [inputUserName, inputuserPhone];
let valid = false; // validation 변수

// 기존에 입력된 이름 값 저장
const beforeUpdateNameInputValue = document.getElementById("userName").value;
// 기존에 입력된 번호 값 저장
const beforeUpdatePhoneInputValue = document.getElementById("userPhone").value;
console.log(beforeUpdateNameInputValue);


// Validation 함수
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

inputs.forEach(input => {
    input.addEventListener("input", () => {

        valid = isValidUserName(inputs) && isValidUserPhoneNumber(inputs);
        
        console.log("valid >", valid);
        const isNameChanged = beforeUpdateNameInputValue !== inputUserName.value;
        const isPhoneChanged = beforeUpdatePhoneInputValue !== inputuserPhone.value;

        const isChanged = isNameChanged || isPhoneChanged;


        document.getElementById("modalSubBtn").disabled = !(valid && isChanged);

    })
})


// 회원정보 수정 모달
const userInfoUpdateModal = document.getElementById("userInfoUpdateModal");

// 회원정보수정 버튼 클릭
document.getElementById("userInfoUpdateBtn").addEventListener("click", () => {

    userInfoUpdateModal.style.display = "block";
})

// 회원정보수정 닫기 버튼 클릭
document.getElementById("closeModal").addEventListener("click", () => {
    userInfoUpdateModal.style.display = "none";
})

// 회원탈퇴 닫기 버튼 클릭
document.getElementById("wdCloseModal").addEventListener("click", () => {
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

    } else {
        // 탈퇴 취소
        console.log("탈퇴 취소");
    }
    })

// 회원정보 수정 모달 =====================================================end

// 휴대폰 인증 모달 =========================================================

// 휴대폰 인증 버튼 클릭
document.getElementById("certifiedPhoneBtn").addEventListener("click", () => {
    document.getElementById("certifiedUserPhone").style.display = "block"
})
// 휴대폰 인증 닫기 버튼 클릭
document.getElementById("certifiedUserPhoneModalClose").addEventListener("click", () => {
    document.getElementById("certifiedUserPhone").style.display = "none"
})



