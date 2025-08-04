document.addEventListener('DOMContentLoaded', () => {
  const modBtn = document.getElementById('modBtn');        // 수정 버튼
  const delBtn = document.getElementById('delBtn');        // 삭제 버튼
  const submitBtn = document.getElementById('submitBtn');  // 제출 버튼
  const completeBtn = document.getElementById('completeBtn');  // 답변완료 버튼
  const listBtn = document.getElementById('listBtn');      // 리스트 이동 버튼
  const content = document.getElementById('con');          // 내용 입력칸 (textarea)
  const title = document.getElementById('t');              // 제목 입력칸
  const categoryInput = document.getElementById('c');      // 카테고리 input
  const categorySelect = document.getElementById('categorySelect'); // 카테고리 select
  const userEmail = document.getElementById('w');

  const uploadInput = document.getElementById('ex_filename');      // 파일 선택 input
  const uploadName = document.querySelector('.upload-name');       // 파일 이름 표시 input
  const uploadLabel = document.querySelector('.file-upload label[for="ex_filename"]');
  const previewContainer = document.getElementById('imagePreviewContainer');  // 이미지 미리보기

  const bnoValue = document.querySelector('input[name="bno"]').value;

  console.log("boardDetail.js in");
  console.log(bnoValue);

  // Modify 버튼 클릭 시 처리
  modBtn.addEventListener('click', () => {
    // 제목, 내용, 작성자 이메일 readonly 해제
    title.removeAttribute('readonly');
    content.removeAttribute('readonly');

    // 카테고리 input 숨기기
    categoryInput.style.display = 'none';

    // 카테고리 select 보이기 및 값 설정
    categorySelect.style.display = 'inline-block';
    categorySelect.value = categoryInput.value;
    categorySelect.disabled = false;

    // Modify & Delete 버튼 숨김
    modBtn.style.display = 'none';
    delBtn.style.display = 'none';

    // Submit 버튼 보이기
    submitBtn.style.display = 'inline-block';

    // ✅ 답변완료 버튼 보이기
    if (completeBtn) {
      completeBtn.style.display = 'inline-block';
    }

    // 업로드 버튼 활성화
    if (uploadLabel) {
      uploadLabel.classList.remove('disabled');
      uploadLabel.style.cursor = 'pointer';
    }
    if (uploadInput) {
      uploadInput.disabled = false;
    }

    // 글자 수 표시 보이기 및 초기화
    if (charCount) {
      charCount.style.display = 'block';
      updateCharCount();
    }

    // textarea 입력시 자동 높이 조절 및 글자수 업데이트
    content.style.height = 'auto';
    content.style.height = content.scrollHeight + 'px';

    content.addEventListener('input', () => {
      content.style.height = 'auto';
      content.style.height = content.scrollHeight + 'px';
      updateCharCount();
    });

    // file-x 버튼들 보이게 변경 및 삭제 기능 연결
    let fileDelBtns = document.querySelectorAll(".file-x");
    fileDelBtns.forEach(btn => {
      btn.style.visibility = "visible";

      btn.addEventListener('click', (e) => {
        let uuid = btn.dataset.uuid;
        fileRemoveToServer(uuid).then(result => {
          if (result == "1") {
            alert("파일삭제 성공");
            const fileXElement = btn.previousElementSibling || btn.parentElement.querySelector('.fileX');
            if (fileXElement) fileXElement.remove();
            btn.remove();
          }
        });
      });
    });
  });

  delBtn.addEventListener('click', () => {
    location.href = "/qna/remove?bno=" + bnoValue;
  });

  listBtn.addEventListener('click', () => {
    location.href = "/qna/list";
  });

  if (uploadInput) {
    uploadInput.addEventListener('change', function () {
      const files = Array.from(this.files);

      if (files.length > 0) {
        const names = files.map(file => file.name).join(', ');
        uploadName.value = names;
      } else {
        uploadName.value = '선택된 파일 없음';
      }

      previewContainer.innerHTML = '';

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

  function updateCharCount() {
    if (!charCount || !content) return;
    const len = content.value.length;
    charCount.innerText = `${len} / 1000`;

    if (len >= 900) {
      charCount.classList.add('warning');
    } else {
      charCount.classList.remove('warning');
    }
  }

  // 답변완료 버튼 기능 (옵션 - 필요 시 서버 연동 가능)
//  if (completeBtn) {
//    completeBtn.addEventListener('click', () => {
//      const confirmResult = confirm("답변완료로 처리하시겠습니까?");
//      if (!confirmResult) return;
//
//      fetch(`/qna/complete/${bnoValue}`, {
//        method: 'POST'
//      })
//        .then(res => res.text())
//        .then(result => {
//          if (result === 'success') {
//            alert('답변 완료 처리되었습니다.');
//            location.reload();
//          } else {
//            alert('처리에 실패했습니다.');
//          }
//        })
//        .catch(err => {
//          console.error(err);
//          alert('오류 발생');
//        });
//    });
//  }
});

// 서버에 파일 삭제 요청
async function fileRemoveToServer(uuid) {
  try {
    const url = `/qna/customeriqfile/${uuid}`;
    const config = {
      method: 'delete',
    }
    const resp = await fetch(url, config);
    const result = await resp.text();
    return result;
  } catch (error) {
    console.error(error);
  }
}

document.addEventListener('DOMContentLoaded', () => {
  const content = document.getElementById('con');

  if (content) {
    content.style.height = 'auto';
    content.style.height = content.scrollHeight + 'px';
  }
});
