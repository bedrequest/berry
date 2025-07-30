console.log("boardDetail.js in");

//<script>
//  const realUpload = document.getElementById('ex_filename');
//  const uploadName = document.querySelector('.upload-name');
//
//  realUpload.addEventListener('change', function () {
//    const files = Array.from(this.files);  // 파일 목록을 배열로 변환
//
//    if (files.length > 0) {
//      const names = files.map(file => file.name).join(', ');
//      uploadName.value = names;
//    } else {
//      uploadName.value = '선택된 파일 없음';
//    }
//  });
//</script>

// bnoValue 변수 선언 (폼 내 hidden input에서 값 읽기)
const bnoValue = document.querySelector('input[name="bno"]').value;

console.log("boardDetail.js in");
console.log(bnoValue);

// 파일 업로드 후 선택된 파일명 표시
const realUpload = document.getElementById('ex_filename');
const uploadName = document.querySelector('.upload-name');

realUpload.addEventListener('change', function () {
  const files = Array.from(this.files);  // 파일 목록 배열로 변환

  if (files.length > 0) {
    const names = files.map(file => file.name).join(', ');
    uploadName.value = names;
  } else {
    uploadName.value = '선택된 파일 없음';
  }
});

// listBtn 클릭 시 /board/list로 이동
document.getElementById('listBtn').addEventListener('click', () => {
  location.href = "/qna/list";
});

// delBtn 클릭 시 /board/remove로 이동 (bno 파라미터 포함)
document.getElementById('delBtn').addEventListener('click', () => {
  location.href = "/qna/remove?bno=" + bnoValue;
});

// modBtn 클릭 시 제목, 카테고리 readOnly 해제 및 수정 submit 버튼 생성
document.getElementById('modBtn').addEventListener('click', () => {
  document.getElementById('t').readOnly = false; // 제목 input
  document.getElementById('c').readOnly = false; // 카테고리 input

  // submit 버튼 생성
  let modBtn = document.createElement("button");
  modBtn.setAttribute('type', 'submit');
  modBtn.setAttribute('id', 'regBtn');
  modBtn.classList.add('btn', 'btn-warning');
  modBtn.innerText = "Submit";

  // 폼에 submit 버튼 추가 (form에 id="modForm" 있어야 함)
  document.getElementById('modForm').appendChild(modBtn);

  // 기존 Modify, Delete 버튼 제거
  document.getElementById('modBtn').remove();
  document.getElementById('delBtn').remove();

  // 업로드 버튼 활성화 (id="trigger" 엘리먼트가 있어야 함)
  const triggerBtn = document.getElementById('trigger');
  if(triggerBtn) {
    triggerBtn.disabled = false;
  }

  // file-x 버튼들 보이게 하고 클릭 시 비동기 파일 삭제 기능 추가
  let fileDelBtn = document.querySelectorAll(".file-x");
  console.log(fileDelBtn);
  fileDelBtn.forEach(btn => {
    btn.style.visibility = "visible";

    btn.addEventListener('click', (e) => {
      let uuid = btn.dataset.uuid;
      fileRemoveToServer(uuid).then(result => {
        if(result === "1"){
          alert("파일삭제 성공");
          e.target.closest('li').remove();
        } else {
          alert("파일삭제 실패");
        }
      });
    });
  });
});

// 비동기 파일 삭제 함수 (CSRF 토큰, 헤더 변수 선언 필요)
async function fileRemoveToServer(uuid) {
  try {
    const url = `/board/file/${uuid}`;
    const config = {
      method: 'DELETE',
      headers: {
        [csrfHeader]: csrfToken
      }
    };
    const resp = await fetch(url, config);
    const result = await resp.text();
    return result;
  } catch (error) {
    console.error("파일 삭제 중 오류:", error);
  }
}
