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
  const regexPassword = /^(?=.*[a-z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,}$/
  
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

    
    const finalValid = isValidEmail && isValidPassword && isValidName && isValidPhoneNumber;
    
    // 비밀번호 확인
    document.getElementById("user_confirmPassword").disabled = !(isValidPassword);
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
          
        }else{
          console.log("중복");
          duplicate = false;
          document.getElementById("updateSubInfo").style.color = "red";
          document.getElementById("updateSubInfo").innerText = "중복된 이메일입니다.";
        }
        document.getElementById("signupButton").disabled = !(confirm && finalValid && duplicate);
      })
      
    }
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



