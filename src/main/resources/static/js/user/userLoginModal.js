console.log("userLoginModal in");

// 기능 함수

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
    }else{
        document.getElementById("modalSubBtn").disabled = true;
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

document.getElementById("closeModal").addEventListener("click", () => {
    document.querySelector(".modal").style.display = "none";
})

