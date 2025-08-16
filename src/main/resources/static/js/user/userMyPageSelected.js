console.log("selected in");
// 토글
document.querySelectorAll(".myPage-select-btn").forEach(btn => {
    btn.addEventListener("click", () => {
        document.querySelectorAll(".myPage-select-btn").forEach(otherBtn => {
            otherBtn.classList.remove("myPage-selected-btn");
        });

        btn.classList.add("myPage-selected-btn");

        const selectedNum = btn.dataset.number;
        const alarm = document.getElementById("myPageDisplay1");
        const bookmark = document.getElementById("myPageDisplay2");
        const reservation = document.getElementById("myPageDisplay3");
        const reservationList = document.getElementById("myPageDisplay4");

        const displays = [alarm, bookmark, reservation, reservationList];

        switch(selectedNum){

            case "1":
                // 알림
                selectedDisplay(alarm, displays);
                break;
            case "2":
                // 북마크
                selectedDisplay(bookmark, displays);
                break;
            case "3":
                // 예약정보
                selectedDisplay(reservation, displays);
                break;
            case "4":
                // 예약내역
                selectedDisplay(reservationList, displays);
                break;
            default :
                // 전체
                displays.forEach(display => {
                    display.style.display = "block";
                })
                break;

        }
    });
});

function selectedDisplay(display, displays){
    displays.forEach(dis => {
        if(display != dis) {
            dis.style.display = "none";
        }
        display.style.display = "block"
    })   
}