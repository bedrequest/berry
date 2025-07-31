document.addEventListener('DOMContentLoaded', () => {
  const modBtn = document.getElementById('modBtn');        // 수정 버튼
  const delBtn = document.getElementById('delBtn');        // 삭제 버튼
  const submitBtn = document.getElementById('submitBtn');  // 제출 버튼
  const listBtn = document.getElementById('listBtn');      // 리스트 이동 버튼
  const content = document.getElementById('con');          // 내용 입력칸
  const title = document.getElementById('t');              // 제목 입력칸
  const categoryInput = document.getElementById('c');      // 카테고리 input
  const categorySelect = document.getElementById('categorySelect'); // 카테고리 select
  const userEmail = document.getElementById('w');

  const uploadInput = document.getElementById('ex_filename');      // 파일 선택 input
  const uploadName = document.querySelector('.upload-name');       // 파일 이름 표시 input
  const uploadLabel = document.querySelector('.file-upload label[for="ex_filename"]');
  const previewContainer = document.getElementById('imagePreviewContainer');  // 이미지 미리보기

  const bnoValue = document.querySelector('input[name="bno"]').value;

  // [Modify] 버튼 클릭 시
  modBtn.addEventListener('click', () => {
    // 제목, 내용, 작성자 이메일 readonly 해제
    title.removeAttribute('readonly');
    content.removeAttribute('readonly');
    userEmail.removeAttribute('readonly');

    // 카테고리 input 숨기기
    categoryInput.style.display = 'none';

    // 카테고리 select 보이기 및 값 설정
    categorySelect.style.display = 'inline-block';
    categorySelect.value = categoryInput.value;

    // Modify & Delete 숨김
    modBtn.style.display = 'none';
    delBtn.style.display = 'none';

    // Submit 보이기
    submitBtn.style.display = 'inline-block';

    // 업로드 버튼 활성화: label 클래스 제거, input disabled 해제
    if (uploadLabel) {
      uploadLabel.classList.remove('disabled');
    }
    if (uploadInput) {
      uploadInput.disabled = false;
    }


//file-x (class) 버튼을 보이게 설정 : style="visibility: hidden => visible로 변환"
    let fileDelBtn = document.querySelectorAll(".file-x");
    console.log(fileDelBtn);
    fileDelBtn.forEach(btn =>{
        btn.style.visibility = "visible";
        // file-x 버튼을 클릭하면 비동기로 uuid를 보내서 DB상에서 파일 삭제
        btn.addEventListener('click',(e)=>{
            let uuid = btn.dataset.uuid;
            // 비동기 전송
            fileRemoveToServer(uuid).then(result =>{
                if(result == "1"){
                    alert("파일삭제 성공");
                    let fileX = document.querySelector('.fileX');
                    console.log(fileX);
                    fileX.remove();
                    btn.remove();

                }
            })
        })
    });
  });

  delBtn.addEventListener('click',()=>{
      location.href="/qna/remove?bno="+bnoValue;
  });

  // [List] 버튼 클릭 시 목록 페이지로 이동
  listBtn.addEventListener('click', () => {
  console.log("asdf");
    location.href = "/qna/list";
  });

  // 파일 선택 시 이미지 미리보기 및 파일명 출력
  if (uploadInput) {
    uploadInput.addEventListener('change', function () {
      const files = Array.from(this.files);

      // 파일 이름 표시
      if (files.length > 0) {
        const names = files.map(file => file.name).join(', ');
        uploadName.value = names;
      } else {
        uploadName.value = '선택된 파일 없음';
      }

      // 기존 미리보기 제거
      previewContainer.innerHTML = '';

      // 이미지 미리보기 생성
      files.forEach(file => {
        if (file.type.startsWith('image/')) {
          const reader = new FileReader();
          reader.onload = function (e) {
            const img = document.createElement('img');
            img.src = e.target.result;
            img.classList.add('preview-image');
            previewContainer.appendChild(img);
          };
          reader.readAsDataURL(file);
        }
      });
    });
  }

});




async function fileRemoveToServer(uuid) {
    try {
        const url = `/qna/customeriqfile/${uuid}`;
        const config = {
            method:'delete',

        }
        const resp = await fetch(url, config);
        const result = await resp.text();
        return result;
    } catch (error) {
        console.log(error);
    }
}
