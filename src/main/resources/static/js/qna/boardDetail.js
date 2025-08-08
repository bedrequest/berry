document.addEventListener('DOMContentLoaded', () => {
  const isAnsweredAttr = document.body.getAttribute('data-answered');
  const bnoValue = document.querySelector('input[name="bno"]').value;
  let isAnswered = isAnsweredAttr === 'true';

  // sessionStorage에서 답변완료 여부 가져오기
  const isSessionAnswered = sessionStorage.getItem(`answered-${bnoValue}`) === 'true';
  if (isSessionAnswered) {
    isAnswered = true;
  }

  // 버튼 및 주요 요소 선택
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
  const commentArea = document.querySelector('.comment-area'); // 댓글 영역 전체
  const charCount = document.getElementById('charCount');

  const uploadInput = document.getElementById('ex_filename');
  const uploadName = document.querySelector('.upload-name');
  const uploadLabel = document.querySelector('.file-upload label[for="ex_filename"]');
  const fileUploadDiv = document.querySelector('.file-upload');
  const previewContainer = document.getElementById('imagePreviewContainer');

  let originalState = {};

  // 본문 높이 자동조절 함수
  function autoResize(el) {
    if (!el) return;
    el.style.height = 'auto';
    el.style.height = el.scrollHeight + 'px';
  }

  function updateCharCount() {
    if (!charCount || !content) return;
    const len = content.value.length;
    charCount.innerText = `${len} / 1000`;
    charCount.classList.toggle('warning', len >= 900);
  }

  function saveOriginalState() {
    originalState = {
      title: title.value,
      content: content.value,
      comment: comment ? comment.value : '',
      category: categoryInput.value,
      files: previewContainer.innerHTML,
      charCount: charCount ? charCount.innerText : ''
    };
  }

  function restoreOriginalState() {
    // 복원 전 로그 (문제 확인용)
    console.log('Restoring state:', originalState);

    title.value = originalState.title;
    content.value = originalState.content;
    if (comment) comment.value = originalState.comment;
    categoryInput.value = originalState.category;
    previewContainer.innerHTML = originalState.files;
    if (charCount) {
      charCount.innerText = originalState.charCount;
      charCount.classList.toggle('warning', content.value.length >= 900);
    }
  }

  // 초기 본문 높이 맞춤
  autoResize(content);

  // **페이지 로드 시점에 원본 상태 저장**
  saveOriginalState();

  // readonly 상태면 버튼 숨기고, 업로드 비활성화, 댓글 숨김
  if (isAnswered) {
    [modBtn, delBtn, submitBtn, cancelBtn, completeBtn].forEach(btn => {
      if (btn) btn.style.display = "none";
    });
    if (listBtn) listBtn.style.display = "inline-block";

    title.setAttribute('readonly', true);
    content.setAttribute('readonly', true);
    if (comment) comment.setAttribute('readonly', true);

    if (uploadInput) {
      uploadInput.disabled = true;
      uploadInput.value = '';
    }
    if (uploadLabel) {
      uploadLabel.classList.add('disabled');
      uploadLabel.style.cursor = 'default';
    }
    if (fileUploadDiv) fileUploadDiv.style.display = 'none';

    if (commentArea) commentArea.style.display = 'none';

    document.querySelectorAll('.file-x').forEach(btn => {
      btn.style.visibility = 'hidden';
      btn.onclick = null;
    });
  } else {
    // 답변 완료 안된 상태일 때는 업로드 영역 기본 숨김
    if (fileUploadDiv) fileUploadDiv.style.display = 'none';

    // 댓글 영역 기본 숨김 (수정 클릭 시 보임)
    if (commentArea) commentArea.style.display = 'none';
  }

  // 중복 방지를 위한 변수
  let contentInputHandler = null;

  // 수정 버튼 클릭 이벤트
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

    // **수정 모드 진입 시에는 원본 상태 재저장하지 않음**
    // saveOriginalState();

    title.removeAttribute('readonly');
    content.removeAttribute('readonly');
    if (comment) comment.removeAttribute('readonly');

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

    // 댓글 영역 보이기
    if (commentArea) commentArea.style.display = 'block';

    // 기존 핸들러 제거 (중복방지)
    if (contentInputHandler) {
      content.removeEventListener('input', contentInputHandler);
    }
    contentInputHandler = () => {
      autoResize(content);
      updateCharCount();
    };
    content.addEventListener('input', contentInputHandler);

    autoResize(content);

    if (comment) {
      autoResize(comment);
      comment.addEventListener('input', () => {
        autoResize(comment);
      });
    }

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
        } else {
          alert("파일삭제 실패");
        }
      };
    });
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

    // 이벤트 핸들러 제거
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

  // 파일 선택시 파일명 표시 및 이미지 미리보기
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

      // category select -> input으로 값 이동
      if (categorySelect && categoryInput) {
        categoryInput.value = categorySelect.value.trim();

        categorySelect.removeAttribute('name');
        categoryInput.setAttribute('name', 'category');

        // 화면 표시용으로 대괄호 추가 (선택 사항)
        setTimeout(() => {
          categoryInput.value = `[ ${categoryInput.value} ]`;
        }, 0);

        categorySelect.style.display = 'none';
        categoryInput.style.display = 'inline-block';
      }

      // sessionStorage에 답변 완료 표시
      sessionStorage.setItem(`answered-${bnoValue}`, 'true');
      isAnswered = true;

      // 폼 제출 (서버에 저장)
      const form = document.querySelector('form');
      if (form) {
        form.submit();
      }
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

// 카테고리 input 너비 자동조절
window.addEventListener('DOMContentLoaded', () => {
  const categoryInput = document.getElementById('c');
  if (!categoryInput) return;

  function resizeInput() {
    const span = document.createElement('span');
    span.style.visibility = 'hidden';
    span.style.position = 'absolute';
    span.style.whiteSpace = 'pre';
    span.style.font = window.getComputedStyle(categoryInput).font;
    span.textContent = categoryInput.value || categoryInput.placeholder || '';
    document.body.appendChild(span);
    const width = span.offsetWidth + 20;
    document.body.removeChild(span);

    categoryInput.style.width = width + 'px';
  }

  resizeInput();
  categoryInput.addEventListener('input', resizeInput);
});

// 카테고리 input 대괄호 자동 추가 및 제거
document.addEventListener('DOMContentLoaded', () => {
  const categoryInput = document.getElementById('c');
  const form = document.querySelector('#modForm');

  if (!categoryInput) return;

  const rawValue = categoryInput.value.trim();
  const hasBrackets = /^\[\s*.*?\s*\]$/.test(rawValue);
  if (!hasBrackets && rawValue.length > 0) {
    categoryInput.value = `[ ${rawValue} ]`;
  }

  if (form) {
    form.addEventListener('submit', function () {
      categoryInput.value = categoryInput.value.replace(/^\[\s*|\s*\]$/g, '').trim();
    });
  }

  categoryInput.addEventListener('input', () => {
    const val = categoryInput.value.trim();
    const inner = val.replace(/^\[\s*|\s*\]$/g, '').trim();
    categoryInput.value = `[ ${inner} ]`;
  });
});


window.addEventListener('DOMContentLoaded', () => {
  const emailInput = document.querySelector('.input-box.email');
  if (!emailInput) return;

  function resizeEmailInput() {
    const span = document.createElement('span');
    span.style.visibility = 'hidden';
    span.style.position = 'absolute';
    span.style.whiteSpace = 'pre';
    span.style.font = window.getComputedStyle(emailInput).font;
    span.textContent = emailInput.value || emailInput.placeholder || '';
    document.body.appendChild(span);
    const width = span.offsetWidth + 20; // 여유 공간 포함
    document.body.removeChild(span);

    // 최소, 최대 너비 제한 적용
    emailInput.style.width = Math.min(Math.max(width, 50), 400) + 'px';
  }

  resizeEmailInput();

  // 값이 바뀔 때마다 크기 조절
  emailInput.addEventListener('input', resizeEmailInput);
});
