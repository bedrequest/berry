console.log("signup in");

// 달력 라이브러리
flatpickr("#user_birthday", {
    dateFormat: "Ymd", // 현재 저장하고있는 생일정보는 yyyymmdd 형태이다
    maxDate: "today",
    altInput: true,
    altFormat: "Y년 m월 d일",
    yearRange: [1900, new Date().getFullYear()],
    defaultDate: "1990-01-01",
    position: "above",
    locale:{
      months: {
        longhand: [
          "01", "02", "03", "04", "05", "06",
          "07", "08", "09", "10", "11", "12"
        ],
        shorthand: [
          "01", "02", "03", "04", "05", "06",
          "07", "08", "09", "10", "11", "12"
        ]
      }
    }
  });

// validation

// id 목록 >>
/*
<input>
user_email
user_name
user_password
user_confirmPassword
user_birthday

<button>
signupButton
*/

const inputUserEmail = document.getElementById("user_email");
const inputUserName = document.getElementById("user_name");
const inputUserPassword = document.getElementById("user_password");
const inputUserConfirmPassword = document.getElementById("user_confirmPassword");
const inputUserPhoneNumber = document.getElementById("user_phone");
let duplicate = false; // 중복검사 확인용
let emailCertified = false; // 이메일 인증 확인용
let phoneCertified = false; // 모바일 인증 확인용

const inputs = [inputUserEmail, inputUserName, inputUserPassword, inputUserConfirmPassword, inputUserPhoneNumber];

// 기능 함수 =====================================================================
// 1. 유효성 검사 이메일
function isValidUserEmail(inputs) {
  // 이메일 유효성
  const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  
  return regexEmail.test(inputs[0].value)

}
// 2. 유효성 검사 비밀번호
function isValidUserPassword(inputs) {
  // 비밀번호 유효성 (영문 대소문자, 숫자, 특수문자 포함 8자리 이상)
  const regexPassword = /^(?=.*[a-z])(?=.*[!@#$%^*=])(?=.*[0-9]).{8,}$/
  
  return regexPassword.test(inputs[2].value);

}
// 3. 유효성 검사 이름
function isValidUserName(inputs) {
  // 이름 유효성 (한글이름만)
  const regexName = /^[가-힣]+$/;

  return regexName.test(inputs[1].value);
}
// 4. 유효성 검사 휴대전화
function isValidUserPhoneNumber(inputs) {
  const phoneRegex = /^01([0|1|6|7|8|9])([0-9]{3,4})([0-9]{4})$/;
  return phoneRegex.test(inputs[4].value);
}


inputs.forEach(input => {
  input.addEventListener("input", () => {

    let confirm = false;
    const isValidEmail = isValidUserEmail(inputs);
    const isValidPassword = isValidUserPassword(inputs);
    const isValidName = isValidUserName(inputs);
    const isValidPhoneNumber = isValidUserPhoneNumber(inputs);
    document.getElementById("certifiedPhoneBtn").disabled = !isValidPhoneNumber;
    document.getElementById("certifiedEmailBtn").disabled = !isValidEmail;

    
    const finalValid = isValidEmail && isValidPassword && isValidName && isValidPhoneNumber;
    
    // 비밀번호 확인
    document.getElementById("user_confirmPassword").disabled = !(isValidPassword);

    if(inputUserPassword.value === inputUserConfirmPassword.value && inputUserPassword.value !== ""){
      document.getElementById("pwdSubInfo").style.color = "green";
      document.getElementById("pwdSubInfo").innerText = "비밀번호가 일치합니다.";
    }else if(inputUserConfirmPassword.value.trim().length > 8) {
      document.getElementById("pwdSubInfo").style.color = "red";
      document.getElementById("pwdSubInfo").innerText = "비밀번호를 확인해주세요.";
    }

    if(finalValid && (inputUserPassword.value === inputUserConfirmPassword.value)){
      confirm = true;
    }
    
    // text 초기화
    document.getElementById("updateSubInfo").innerText = "";
    // 이메일 중복검사 결과값 반영
    if(isValidEmail && input.id == "user_email" && input != ""){
      
      duplicateEmailCheckedToServer(input.value).then(result => {
        console.log(result);
        if(result === "ok"){
          duplicate = true;
          document.getElementById("updateSubInfo").style.color = "green";
          document.getElementById("updateSubInfo").innerText = "사용 가능한 이메일입니다.";
          document.getElementById("certifiedEmailBtn").disabled = false;
          
        }else{
          console.log("중복");
          duplicate = false;
          document.getElementById("updateSubInfo").style.color = "red";
          document.getElementById("updateSubInfo").innerText = "중복된 이메일입니다.";
          document.getElementById("certifiedEmailBtn").disabled = true;
        }
        document.getElementById("signupButton").disabled = !(confirm && finalValid && duplicate && emailCertified && phoneCertified);
      })
      
    }
    document.getElementById("signupButton").disabled = !(confirm && finalValid && duplicate && emailCertified && phoneCertified);
  })
})




// 비동기 처리 함수======================================================================
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

// 모달 인증
// 휴대전화 인증 버튼
const certifiedPhoneBtn = document.getElementById("certifiedPhoneBtn");
// 이메일 인증 버튼
const certifiedEmailBtn = document.getElementById("certifiedEmailBtn");

// 스크롤 막기
function openModal() {
  document.body.style.overflow = 'hidden';
}

function closeModal() {
  document.body.style.overflow = 'auto';
}

// 휴대폰 인증 모달 =========================================================

// 변수 =================
let certifiedNumber;

// 휴대폰 인증 버튼 클릭
if(certifiedPhoneBtn){
    document.getElementById("certifiedPhoneBtn").addEventListener("click", () => {
        openModal();
        document.getElementById("certifiedUserPhone").style.display = "block";
        document.getElementById("certifiedPhoneBtn").style.display = "none";
        document.getElementById("pwdTit").innerText = inputUserPhoneNumber.value;
    })
}
// 휴대폰 인증 닫기 버튼 클릭
document.getElementById("certifiedUserPhoneModalClose").addEventListener("click", () => {
    closeModal();
    document.getElementById("certifiedUserPhone").style.display = "none";
    document.getElementById("certifiedPhoneBtn").style.display = "block";
})

// 인증번호 받기 버튼 클릭
document.getElementById("getCertifiedPhoneBtn").addEventListener("click", () => {
    console.log(inputUserPhoneNumber.value);

    document.getElementById("getCertifiedPhoneBtn").style.display = "none";

    document.getElementById("verifyBox").style.display = "block";
    document.getElementById("certifiedUserPhoneSubBtn").style.display = "inline-block";

    getSignInCertifiedNumber(inputUserPhoneNumber.value).then(result => {
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
      // 인증 완료 표시
      document.getElementById("phoneCertifiedOk").style.display = "block";
      // 여기에 input hidden 으로 들고가는 값 채워주기
      document.getElementById("isMobileCertified").value = true;
      // 확인용 변수 활성화
      phoneCertified = true;
      // input readOnly 로 변경
      inputUserPhoneNumber.readOnly = true;



      // 여기서 유효성 검사 한번 더
      let confirm = false;
      const isValidEmail = isValidUserEmail(inputs);
      const isValidPassword = isValidUserPassword(inputs);
      const isValidName = isValidUserName(inputs);
      const isValidPhoneNumber = isValidUserPhoneNumber(inputs);
      document.getElementById("certifiedPhoneBtn").disabled = !isValidPhoneNumber;

      
      const finalValid = isValidEmail && isValidPassword && isValidName && isValidPhoneNumber;
      
      // 비밀번호 확인
      document.getElementById("user_confirmPassword").disabled = !(isValidPassword);
      if(finalValid && (inputUserPassword.value === inputUserConfirmPassword.value)){
        confirm = true;
      }

      document.getElementById("signupButton").disabled = !(confirm && finalValid && emailCertified && phoneCertified);
      // 모달창 닫기
      document.getElementById("certifiedUserPhone").style.display = "none";

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
        document.getElementById("certifiedEmailBtn").style.display = "none"
        document.getElementById("emailTit").innerText = inputUserEmail.value;
    })
}
// 이메일 인증 닫기 버튼 클릭
document.getElementById("certifiedUserEmailModalClose").addEventListener("click", () => {
    closeModal();
    document.getElementById("certifiedUserEmail").style.display = "none";
    document.getElementById("certifiedEmailBtn").style.display = "block";
})
// 인증코드 받기 버튼 클릭
document.getElementById("getCertifiedEmailBtn").addEventListener("click", () => {

    document.getElementById("getCertifiedEmailBtn").style.display = "none";
    document.getElementById("verifyEmailBox").style.display = "block";

    getSignInCertifiedCode(inputUserEmail.value).then(result => {
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
      // 인증 완료 표시
      document.getElementById("emailCertifiedOk").style.display = "block";
      // 여기에 input hidden 으로 들고가는 값 채워주기
      document.getElementById("isEmailCertified").value = true;
      // 확인용 변수 활성화
      emailCertified = true;
      // input readOnly 로 변경
      inputUserEmail.readOnly = true;


      // 여기서 유효성 검사 한번 더
      let confirm = false;
      const isValidEmail = isValidUserEmail(inputs);
      const isValidPassword = isValidUserPassword(inputs);
      const isValidName = isValidUserName(inputs);
      const isValidPhoneNumber = isValidUserPhoneNumber(inputs);
      document.getElementById("certifiedPhoneBtn").disabled = !isValidPhoneNumber;

      
      const finalValid = isValidEmail && isValidPassword && isValidName && isValidPhoneNumber;
      
      // 비밀번호 확인
      document.getElementById("user_confirmPassword").disabled = !(isValidPassword);
      if(finalValid && (inputUserPassword.value === inputUserConfirmPassword.value)){
        confirm = true;
      }

      document.getElementById("signupButton").disabled = !(confirm && finalValid && emailCertified && phoneCertified);

      // 모달창 닫기
      document.getElementById("certifiedUserEmail").style.display = "none";

    } else{
      alert("인증코드가 일치하지 않습니다.");
    }
})


// 비동기
// 휴대폰 인증번호 받기
async function getSignInCertifiedNumber(phoneNumber) {

  try {
      const url = `/user/getSignInCertifiedNumber/${phoneNumber}`
      const resp = await fetch(url);
      const result = await resp.text();

      return result;
  } catch (error) {
      console.log(error);
  }
  
}

// 이메일 인증코드 받기
async function getSignInCertifiedCode(email) {

  try {
      const url = `/user/getSignInCertifiedCode/${encodeURIComponent(email)}`
      const resp = await fetch(url);
      const result = await resp.text();

      return result;
  } catch (error) {
      console.log(error);
  }
  
}