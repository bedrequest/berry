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

  // 🟡 [Modify] 버튼 클릭 시
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
  });

  // 🔵 [List] 버튼 클릭 시 목록 페이지로 이동
  listBtn.addEventListener('click', () => {
    location.href = "/qna/list";
  });

  // 🟢 파일 선택 시 이미지 미리보기 및 파일명 출력
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
