document.addEventListener('DOMContentLoaded', () => {
  let isAnswered = false;  // 답변완료 상태 플래그

  const modBtn = document.getElementById('modBtn');        // 수정 버튼
  const delBtn = document.getElementById('delBtn');        // 삭제 버튼
  const submitBtn = document.getElementById('submitBtn');  // 제출 버튼
  const completeBtn = document.getElementById('completeBtn');  // 답변완료 버튼
  const listBtn = document.getElementById('listBtn');      // 리스트 버튼
  const cancelBtn = document.getElementById('CancelBtn');  // 취소 버튼

  const content = document.getElementById('con');          // 본문 textarea
  const title = document.getElementById('t');              // 제목 input
  const categoryInput = document.getElementById('c');      // 카테고리 input
  const categorySelect = document.getElementById('categorySelect'); // 카테고리 select
  const comment = document.getElementById('comment');      // 댓글 textarea
  const charCount = document.getElementById('charCount');

  const uploadInput = document.getElementById('ex_filename');      // 파일 선택 input
  const uploadName = document.querySelector('.upload-name');       // 파일 이름 표시 input
  const uploadLabel = document.querySelector('.file-upload label[for="ex_filename"]');
  const previewContainer = document.getElementById('imagePreviewContainer');  // 이미지 미리보기

  const modForm = document.getElementById('modForm');
  const bnoValue = document.querySelector('input[name="bno"]').value;

  // 자동 높이 조절 함수
  function autoResize(el) {
    if (!el) return;
    el.style.height = 'auto';
    el.style.height = el.scrollHeight + 'px';
  }

  // 글자수 업데이트 함수
  function updateCharCount() {
    if (!charCount || !content) return;
    const len = content.value.length;
    charCount.innerText = `${len} / 1000`;
    if (len >= 900) charCount.classList.add('warning');
    else charCount.classList.remove('warning');
  }

  // 초기 textarea 높이 조절
  if (comment) autoResize(comment);
  if (content) autoResize(content);

  // 수정 버튼 클릭
  modBtn.addEventListener('click', () => {
    if (isAnswered) {
      alert('답변 완료된 문의는 수정할 수 없습니다.');
      return;
    }

    title.removeAttribute('readonly');
    content.removeAttribute('readonly');
    if (comment) comment.removeAttribute('readonly');

    categoryInput.style.display = 'none';
    categorySelect.style.display = 'inline-block';
    categorySelect.value = categoryInput.value;
    categorySelect.disabled = false;

    modBtn.style.display = 'none';
    delBtn.style.display = 'none';
    listBtn.style.display = 'none';

    submitBtn.style.display = 'inline-block';
    if (completeBtn) completeBtn.style.display = 'inline-block';
    if (cancelBtn) cancelBtn.style.display = 'inline-block';

    if (uploadLabel) {
      uploadLabel.classList.remove('disabled');
      uploadLabel.style.cursor = 'pointer';
    }
    if (uploadInput) uploadInput.disabled = false;

    if (charCount) {
      charCount.style.display = 'block';
      updateCharCount();
    }

    autoResize(content);
    content.oninput = null;
    content.addEventListener('input', () => {
      autoResize(content);
      updateCharCount();
    });

    if (comment) {
      autoResize(comment);
      comment.oninput = null;
      comment.addEventListener('input', () => {
        autoResize(comment);
      });
    }

    // 파일 삭제 버튼 처리
    document.querySelectorAll('.file-x').forEach(btn => {
      btn.style.visibility = 'visible';
      btn.onclick = async () => {
        const uuid = btn.dataset.uuid;
        const result = await fileRemoveToServer(uuid);
        if (result == "1") {
          alert("파일삭제 성공");
          const fileElement = btn.previousElementSibling || btn.parentElement.querySelector('.fileX');
          if (fileElement) fileElement.remove();
          btn.remove();
        }
      };
    });
  });

  // 삭제 버튼 클릭
  delBtn.addEventListener('click', () => {
    location.href = "/qna/remove?bno=" + bnoValue;
  });

  // 리스트 버튼 클릭
  listBtn.addEventListener('click', () => {
    location.href = "/qna/list";
  });

  // 취소 버튼 클릭 (수정 취소)
  if (cancelBtn) {
    cancelBtn.addEventListener('click', () => {
      title.setAttribute('readonly', true);
      content.setAttribute('readonly', true);
      if (comment) comment.setAttribute('readonly', true);

      categoryInput.style.display = 'inline-block';
      categorySelect.style.display = 'none';
      categorySelect.disabled = true;

      modBtn.style.display = 'inline-block';
      delBtn.style.display = 'inline-block';
      listBtn.style.display = 'inline-block';

      submitBtn.style.display = 'none';
      if (completeBtn) completeBtn.style.display = 'none';

      cancelBtn.style.display = 'none';

      if (uploadLabel) {
        uploadLabel.classList.add('disabled');
        uploadLabel.style.cursor = 'default';
      }
      if (uploadInput) {
        uploadInput.disabled = true;
        uploadInput.value = '';
      }

      if (charCount) {
        charCount.style.display = 'none';
      }

      if (previewContainer) previewContainer.innerHTML = '';

      autoResize(content);
      if (comment) autoResize(comment);
    });
  }

  // 파일 선택 시 미리보기
  if (uploadInput) {
    uploadInput.addEventListener('change', () => {
      const files = Array.from(uploadInput.files);
      if (files.length > 0) {
        uploadName.value = files.map(f => f.name).join(', ');
      } else {
        uploadName.value = '선택된 파일 없음';
      }
      previewContainer.innerHTML = '';
      files.forEach(file => {
        if (file.type.startsWith('image/')) {
          const reader = new FileReader();
          reader.onload = e => {
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

  // 답변완료 버튼 클릭 (수정 저장 + 답변완료 상태)
  if (completeBtn) {
    completeBtn.addEventListener('click', (event) => {
      event.preventDefault();
      if (!confirm('답변완료로 처리하시겠습니까?')) return;

      console.log('답변완료 버튼 클릭');
      console.log('modBtn:', modBtn);
      console.log('delBtn:', delBtn);

      isAnswered = true;  // 답변완료 상태로 변경

      if (modBtn) {
        modBtn.style.setProperty('display', 'none', 'important');
        console.log('modBtn 숨김 처리 완료');
      }
      if (delBtn) {
        delBtn.style.setProperty('display', 'none', 'important');
        console.log('delBtn 숨김 처리 완료');
      }
      if (listBtn) listBtn.style.display = 'none';      // 리스트 버튼도 숨김
      if (submitBtn) submitBtn.style.display = 'none';
      if (completeBtn) completeBtn.style.display = 'none';
      if (cancelBtn) cancelBtn.style.display = 'none';

      // 제목, 내용, 댓글 모두 readonly 처리
      if (title) title.setAttribute('readonly', true);
      if (content) content.setAttribute('readonly', true);
      if (comment) comment.setAttribute('readonly', true);

      // 카테고리 셀렉트 숨기고, input 보여주기
      if (categorySelect) {
        categorySelect.style.display = 'none';
        categorySelect.disabled = true;
      }
      if (categoryInput) categoryInput.style.display = 'inline-block';

      // 파일 업로드 비활성화 및 미리보기 초기화
      if (uploadInput) {
        uploadInput.disabled = true;
        uploadInput.value = '';
      }
      if (uploadLabel) {
        uploadLabel.classList.add('disabled');
        uploadLabel.style.cursor = 'default';
      }
      if (previewContainer) previewContainer.innerHTML = '';

      // 폼 제출
      modForm.submit();
    });

    if (isAnswered) {
      [modBtn, delBtn, listBtn, submitBtn, completeBtn, cancelBtn].forEach(btn => {
        if (btn) btn.style.display = 'none';
      });
      // 기타 readonly 처리도 추가 가능
    }

  }

});

// 서버에 파일 삭제 요청 함수
async function fileRemoveToServer(uuid) {
  try {
    const url = `/qna/customeriqfile/${uuid}`;
    const resp = await fetch(url, { method: 'delete' });
    const result = await resp.text();
    return result;
  } catch (error) {
    console.error(error);
  }
}
