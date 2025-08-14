document.addEventListener('DOMContentLoaded', () => {
  const container = document.querySelector('.container');
  const isAnsweredAttr = container ? container.dataset.answered : 'false';
  const bnoValue = document.querySelector('input[name="bno"]').value;
  let isAnswered = isAnsweredAttr === 'true';

  // 주요 요소들
  const modBtn = document.getElementById('modBtn');
  const delBtn = document.getElementById('delBtn');
  const submitBtn = document.getElementById('submitBtn');
  const completeBtn = document.getElementById('completeBtn');
  const listBtn = document.getElementById('listBtn');
  const cancelBtn = document.getElementById('CancelBtn');

  const content = document.getElementById('con');
  const title = document.getElementById('t');
  const categoryInput = document.getElementById('c');
  const categorySelect = document.getElementById('categorySelect');
  const comment = document.getElementById('comment');
  const commentArea = document.querySelector('.comment-area');
  const charCount = document.getElementById('charCount');

  const uploadInput = document.getElementById('ex_filename');
  const uploadName = document.querySelector('.upload-name');
  const uploadLabel = document.querySelector('.file-upload label[for="ex_filename"]');
  const fileUploadDiv = document.querySelector('.file-upload');
  const previewContainer = document.getElementById('imagePreviewContainer');

  let originalState = {};
  let contentInputHandler = null;

  // 자동 높이 조절 함수
  function autoResize(el) {
    if (!el) return;
    el.style.height = 'auto';
    el.style.height = el.scrollHeight + 'px';
  }

  // 글자 수 업데이트
  function updateCharCount() {
    if (!charCount || !content) return;
    const len = content.value.length;
    charCount.innerText = `${len} / 1000`;
    charCount.classList.toggle('warning', len >= 900);
  }

  // 원본 상태 저장 (수정모드 시작 시 1회만)
  function saveOriginalState() {
    originalState = {
      title: title.value,
      content: content.value,
      comment: comment ? comment.value : '',
      category: categoryInput.value,
      files: previewContainer.innerHTML,  // 현재 파일/이미지 상태 HTML 저장
      charCount: charCount ? charCount.innerText : ''
    };
    console.log('Original state saved:', originalState);
  }

  // 원본 상태 복원
  function restoreOriginalState() {
    title.value = originalState.title;
    content.value = originalState.content;
    if (comment) comment.value = originalState.comment;
    categoryInput.value = originalState.category;

    previewContainer.innerHTML = originalState.files;
    console.log('Restored files:', originalState.files);

    // 삭제 버튼 다시 붙이기
    document.querySelectorAll('.file-x').forEach(btn => {
      btn.style.visibility = 'visible';
      btn.onclick = async () => {
        const uuid = btn.dataset.uuid;
        const result = await fileRemoveToServer(uuid);
        if (result === "1") {
          alert("파일삭제 성공");
          const fileItem = btn.closest('.file-item');
          if (fileItem) fileItem.remove();
        } else {
          alert("파일삭제 실패");
        }
      };
    });

    if (uploadInput) uploadInput.value = '';
    if (charCount) {
      charCount.innerText = originalState.charCount;
      charCount.classList.toggle('warning', content.value.length >= 900);
    }
  }

  // 답변 완료 상태 처리
  if (isAnswered) {
    [modBtn, submitBtn, cancelBtn, completeBtn].forEach(btn => {
      if (btn) btn.style.display = 'none';
    });

    if (listBtn) listBtn.style.display = 'inline-block';
    if (delBtn) delBtn.style.display = 'inline-block';

    if (commentArea) commentArea.style.display = 'block';

    title.setAttribute('readonly', true);
    content.setAttribute('readonly', true);
    if (comment) {
      comment.setAttribute('readonly', true);
      // 답변 완료된 댓글 칸은 높이 고정 (자동 높이 후 높이 고정)
      autoResize(comment);
      comment.style.height = comment.scrollHeight + 'px';
    }

    if (uploadInput) {
      uploadInput.disabled = true;
      uploadInput.value = '';
    }
    if (uploadLabel) {
      uploadLabel.classList.add('disabled');
      uploadLabel.style.cursor = 'default';
    }
    if (fileUploadDiv) fileUploadDiv.style.display = 'none';

    document.querySelectorAll('.file-x').forEach(btn => {
      btn.style.visibility = 'hidden';
      btn.onclick = null;
    });

  } else {
    // 답변 완료 전 기본 상태
    if (fileUploadDiv) fileUploadDiv.style.display = 'none';
    if (commentArea) commentArea.style.display = 'none';
  }

  autoResize(content);

  // 수정 버튼 클릭 시 수정모드 전환
  modBtn.addEventListener('click', () => {
    if (isAnswered) {
      alert('답변 완료된 문의는 수정할 수 없습니다.');
      return;
    }

    // 카테고리 input → select 전환
    if (categoryInput && categorySelect) {
      categoryInput.style.display = 'none';
      categorySelect.style.display = 'inline-block';

      const rawValue = categoryInput.value.replace(/\[|\]/g, '').trim();
      categorySelect.value = rawValue;

      categoryInput.removeAttribute('name');
      categorySelect.setAttribute('name', 'category');
    }

    title.removeAttribute('readonly');
    content.removeAttribute('readonly');

    if (comment) {
      if (isAdmin) {
        comment.removeAttribute('readonly');
        if (commentArea) commentArea.style.display = 'block';

        // 댓글칸 자동 높이 조절 + input 이벤트 추가
        autoResize(comment);
        comment.addEventListener('input', () => {
          autoResize(comment);
        });

      } else {
        if (commentArea) commentArea.style.display = 'none';
      }
    }

    modBtn.style.display = 'none';
    delBtn.style.display = 'none';
    listBtn.style.display = 'none';
    submitBtn.style.display = 'inline-block';

    if (completeBtn) completeBtn.style.display = 'inline-block';
    cancelBtn.style.display = 'inline-block';

    if (uploadLabel) {
      uploadLabel.classList.remove('disabled');
      uploadLabel.style.cursor = 'pointer';
    }
    if (uploadInput) {
      uploadInput.disabled = false;
    }
    if (fileUploadDiv) {
      fileUploadDiv.style.display = 'flex';
    }

    if (charCount) {
      charCount.style.display = 'block';
      updateCharCount();
    }

    if (contentInputHandler) {
      content.removeEventListener('input', contentInputHandler);
    }

    contentInputHandler = () => {
      autoResize(content);
      updateCharCount();
    };
    content.addEventListener('input', contentInputHandler);
    autoResize(content);

    // 삭제 버튼 보이기 + 삭제 기능 활성화
    document.querySelectorAll('.file-x').forEach(btn => {
      btn.style.visibility = 'visible';
      btn.onclick = async () => {
        const uuid = btn.dataset.uuid;
        const result = await fileRemoveToServer(uuid);
        if (result === "1") {
          alert("파일삭제 성공");
          const fileItem = btn.closest('.file-item');
          if (fileItem) fileItem.remove();
        } else {
          alert("파일삭제 실패");
        }
      };
    });

    // 수정 모드 진입 시 1회만 저장
    saveOriginalState();
  });


  // 삭제 버튼 클릭
  delBtn.addEventListener('click', () => {
    if (confirm("삭제하시겠습니까?")) {
      location.href = "/qna/remove?bno=" + bnoValue;
    }
  });

  // 목록 버튼 클릭
  listBtn.addEventListener('click', () => {
    location.href = "/qna/list";
  });

  // 취소 버튼 클릭
  cancelBtn.addEventListener('click', () => {
    restoreOriginalState();

    title.setAttribute('readonly', true);
    content.setAttribute('readonly', true);
    if (comment) comment.setAttribute('readonly', true);

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
    if (fileUploadDiv) {
      fileUploadDiv.style.display = 'none';
    }

    if (charCount) {
      charCount.style.display = 'none';
    }

    if (commentArea) commentArea.style.display = 'none';

    if (contentInputHandler) {
      content.removeEventListener('input', contentInputHandler);
      contentInputHandler = null;
    }

    autoResize(content);
    if (comment) autoResize(comment);

    document.querySelectorAll('.file-x').forEach(btn => {
      btn.style.visibility = 'hidden';
      btn.onclick = null;
    });

    // 카테고리 select → input 복원
    if (categoryInput && categorySelect) {
      categorySelect.style.display = 'none';
      categoryInput.style.display = 'inline-block';

      categorySelect.removeAttribute('name');
      categoryInput.setAttribute('name', 'category');
    }
  });

  // 파일 선택 시 파일명 표시 및 이미지 미리보기
  if (uploadInput) {
    uploadInput.addEventListener('change', () => {
      const files = Array.from(uploadInput.files);
      uploadName.value = files.length ? files.map(f => f.name).join(', ') : '선택된 파일 없음';
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

  // 답변완료 버튼 클릭
  if (completeBtn) {
    completeBtn.addEventListener('click', () => {
      if (!confirm("답변을 완료하시겠습니까?")) return;

      if (categorySelect && categoryInput) {
        categoryInput.value = categorySelect.value.trim();
        categorySelect.removeAttribute('name');
        categoryInput.setAttribute('name', 'category');

        setTimeout(() => {
          categoryInput.value = `[ ${categoryInput.value} ]`;
        }, 0);

        categorySelect.style.display = 'none';
        categoryInput.style.display = 'inline-block';
      }

      sessionStorage.setItem(`answered-${bnoValue}`, 'true');
      isAnswered = true;

      if (commentArea) commentArea.style.display = 'block';

      if (comment) {
        // 답변완료 시 댓글칸 높이 자동조절 후 고정
        autoResize(comment);
        comment.style.height = comment.scrollHeight + 'px';
        comment.setAttribute('readonly', true);
      }

      const form = document.querySelector('form');
      if (form) {
        form.submit();
      }
    });
  }

  // 페이지 로드 시 저장된 댓글 높이 복원 (있으면)
  const savedCommentHeight = sessionStorage.getItem(`commentHeight-${bnoValue}`);
  if (savedCommentHeight && comment) {
    comment.style.height = savedCommentHeight;
  }

  // 댓글 높이 변화 시 세션 저장 (수정 모드에서만)
  if (comment && !comment.hasAttribute('readonly')) {
    comment.addEventListener('input', () => {
      autoResize(comment);
      sessionStorage.setItem(`commentHeight-${bnoValue}`, comment.style.height);
    });
  }
});

// 파일 삭제 요청 함수
async function fileRemoveToServer(uuid) {
  try {
    const url = `/qna/customeriqfile/${uuid}`;
    const resp = await fetch(url, { method: 'delete' });
    return await resp.text();
  } catch (error) {
    console.error(error);
    return null;
  }
}
