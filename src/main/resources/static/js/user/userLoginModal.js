console.log("userLoginModal in");

// 변수
const userInputEmail = document.getElementById("userInputEmail");
const userInputPassword = document.getElementById("userInputPassword");

const inputs = [userInputEmail, userInputPassword];
// 기능 함수

// validation 함수
function isValidUserEmail(inputs) {
    // 이메일 유효성
    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    
    return regexEmail.test(inputs[0].value)
}

function isValidUserPassword(inputs) {
    // 비밀번호 유효성 (영문 대소문자, 숫자, 특수문자 포함 8자리 이상)
    const regexPassword = /^(?=.*[a-z])(?=.*[!@#$%^*=])(?=.*[0-9]).{8,}$/
    
    return regexPassword.test(inputs[1].value);
}

inputs.forEach(input => {
    input.addEventListener("input", () => {
        const valid = isValidUserEmail(inputs) && isValidUserPassword(inputs);

        document.getElementById("errorMessage").innerText = "";
        
        if(valid){
            
            document.querySelector(".login-btn").disabled = false;
            document.querySelector(".login-btn").classList.add("login-btn-hover");
        }else{
            document.querySelector(".login-btn").disabled = true;
            document.querySelector(".login-btn").classList.remove("login-btn-hover");
            
        }
        
    })
})


// web 부분
// 모달창 먼저 띄우기 위해 a 태그 막기
document.getElementById("webSignBtn").addEventListener("click", (e) => {
    
    e.preventDefault();
    
    document.querySelector(".modal").style.display = "block";
    
});

// 필수 체크요소가 비활성화 되어있으면 제출버튼 disabled
document.getElementById("agreePersonal").addEventListener("click", (e) => {
    
    if(e.target.checked){
        document.getElementById("modalSubBtn").disabled = false;
        document.getElementById("modalSubBtn").classList.add("login-btn-hover");
    }else{
        document.getElementById("modalSubBtn").disabled = true;
        document.getElementById("modalSubBtn").classList.remove("login-btn-hover");
    }
})


// 제출 버튼을 누르면 경로 이동
document.getElementById("modalSubBtn").addEventListener("click", () => {
    
    const personalChecked = document.getElementById("agreePersonal");
    const marketingChecked = document.getElementById("agreeMarketing");
    console.log(personalChecked.checked);
    console.log(marketingChecked.checked);
    if(personalChecked.checked){
        window.location.href = `/user/signup?marketing=${marketingChecked.checked}`;
    }
    
})

// 닫기
document.getElementById("closeModal").addEventListener("click", () => {
    document.querySelector(".modal").style.display = "none";
    document.getElementById("agreePersonal").checked = false;
    document.getElementById("agreeMarketing").checked = false;

})

// 비밀번호 재설정 모달

// 변수
const resetModalInputEmail = document.getElementById("resetModalInputEmail");
const resetInputs = [resetModalInputEmail];
let certifyCode = "";
let resetPasswordUserId = 0;

// 모달 띄우기
document.getElementById("resetPasswordBtn").addEventListener("click", () => {
    document.getElementById("resetPasswordModal").style.display = "block";
})

// 모달 닫기
document.getElementById("resetPasswordModalClose").addEventListener("click", () => {
    document.getElementById("resetPasswordModal").style.display = "none";
    location.reload(true);
})

resetModalInputEmail.addEventListener("input", () => {
    const valid = isValidUserEmail(resetInputs);
    
    if(valid){
        document.getElementById("emailCheckBtn").disabled = false;
        document.getElementById("emailCheckBtn").classList.add("login-btn-hover");
    }else{
        document.getElementById("emailCheckBtn").disabled = true;;
        document.getElementById("emailCheckBtn").classList.remove("login-btn-hover");
    }
})


// 재설정 중 확인 버튼
document.getElementById("emailCheckBtn").addEventListener("click", () => {

    console.log(resetModalInputEmail.value);
    checkEmail(resetModalInputEmail.value).then(result => {
        console.log(result);
        if(Number(result) > 0){
            // 성공
            alert("확인되었습니다.");
            document.getElementById("resetFirstCertified").style.display = "none";
            document.querySelector(".reset-tit-1").style.display = "none";
            document.querySelector(".reset-tit-2").style.display = "block"
            document.getElementById("verifyEmailBox").style.display = "block";
            document.getElementById("resetPasswordCertifiedUserEmailSubBtn").style.display = "inline-block";


            const userId = Number(result);
            resetPasswordUserId = userId;
            console.log("userId > ", userId);

            getCertifiedCode(userId).then(codeResult => {
                if(codeResult == "fail"){
                    alert("인증코드 받기를 실패했습니다.");
                }else{
                    certifyCode = codeResult;
                }
            })
        }else{
            // 실패
            alert("이메일이 일치하지 않습니다.");
            location.reload(true);
        }
    })

})

document.getElementById("resetPasswordCertifiedUserEmailSubBtn").addEventListener("click", () => {
    const resetCertifiedCodeInput = document.getElementById("resetCertifiedCodeInput");
    console.log(certifyCode);
    console.log(resetPasswordUserId);

    if(resetCertifiedCodeInput.value == certifyCode){
        document.getElementById("resetPasswordCertifiedUserEmailSubBtn").style.display = "none";
        resetPassword(resetPasswordUserId).then(result => {
            if(result == "ok"){
                alert("이메일로 발송된 비밀번호를 확인해주세요.");
                location.reload(true);
            }else{
                alert("비밀번호 재발급에 실패하였습니다.");
            }
        })

    }else{
        alert("인증코드가 일치하지 않습니다.");
    }
})


// 비동기

// 이메일 확인
async function checkEmail(userEmail) {    

    try {
        const url = `/user/findWebUserEmail/${encodeURIComponent(userEmail)}`
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

// 비밀번호 재발급
async function resetPassword(userId) {

    try {
        const url = `/user/resetPassword/${userId}`
        const resp = await fetch(url);
        const result = await resp.text();

        return result;
    } catch (error) {
        
    }
    
}

// 회원탈퇴시
console.log(membershipWithdrawal);
if(membershipWithdrawal == "ok"){
    alert("성공적으로 탈퇴되었습니다.");
}