document.addEventListener('DOMContentLoaded', () => {
  const isAnsweredAttr = document.querySelector('.container').dataset.answered;
  const bnoValue = document.querySelector('input[name="bno"]').value;
  let isAnswered = isAnsweredAttr === 'true';

  // 주요 버튼 및 요소 선택
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

  // 본문 높이 자동 조절 함수
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

  // 원본 상태 저장
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

  // 원본 상태 복원
  // 복원 전 로그 (문제 확인용)
  console.log('Restoring state:', originalState);


  function restoreOriginalState() {
      title.value = originalState.title;
      content.value = originalState.content;
      if (comment) comment.value = originalState.comment;
      categoryInput.value = originalState.category;

      previewContainer.innerHTML = originalState.files;

      // 파일 input 초기화 (선택된 파일 초기화)
      if (uploadInput) {
        uploadInput.value = '';
      }

      if (charCount) {
        charCount.innerText = originalState.charCount;
        charCount.classList.toggle('warning', content.value.length >= 900);
      }

      // 복원된 이미지 미리보기 내 삭제 버튼 이벤트 다시 연결
      document.querySelectorAll('.file-x').forEach(btn => {
        btn.style.visibility = 'hidden'; // 수정모드 아니면 숨김
        btn.onclick = null;
      });
  }

  // 초기 본문 높이 맞춤
  autoResize(content);

  // 원본 상태 저장
  saveOriginalState();

  // 답변완료 상태 처리
  if (isAnswered) {
  console.log("답변완료된 게시글");
    // 수정, 제출, 답변완료, 취소 버튼 숨기기
    [modBtn, submitBtn, cancelBtn, completeBtn].forEach(btn => {
      if (btn) btn.style.display = 'none';
    });

    // List 버튼은 보이기
    if (listBtn) listBtn.style.display = 'inline-block';
    if (delBtn) listBtn.style.display = 'inline-block';

    // 댓글칸 항상 보이기 (관리자/일반 모두)
    if (commentArea) commentArea.style.display = 'block';

    // 본문, 제목, 댓글 읽기 전용 유지
    title.setAttribute('readonly', true);
    content.setAttribute('readonly', true);
    if (comment) comment.setAttribute('readonly', true);

    // 파일 업로드 비활성화 및 숨김
    if (uploadInput) {
      uploadInput.disabled = true;
      uploadInput.value = '';
    }
    if (uploadLabel) {
      uploadLabel.classList.add('disabled');
      uploadLabel.style.cursor = 'default';
    }
    if (fileUploadDiv) fileUploadDiv.style.display = 'none';

    // 파일 삭제 버튼 숨김
    document.querySelectorAll('.file-x').forEach(btn => {
      btn.style.visibility = 'hidden';
      btn.onclick = null;
    });
  } else {
    // 답변 완료 전 상태

    // 업로드 영역 기본 숨김
    if (fileUploadDiv) fileUploadDiv.style.display = 'none';

    // 댓글 영역 기본 숨김 (수정 모드에서 보임)
    if (commentArea) commentArea.style.display = 'none';
  }

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

   title.removeAttribute('readonly');
   content.removeAttribute('readonly');

   if (comment) {
     if (isAdmin) {
       comment.removeAttribute('readonly');
       if (commentArea) commentArea.style.display = 'block';
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

   // 기존 핸들러 제거 (중복 방지)
   if (contentInputHandler) {
     content.removeEventListener('input', contentInputHandler);
   }

   // 새 input 이벤트 핸들러 붙이기 (본문 높이 자동조절 + 글자수 카운트)
   contentInputHandler = () => {
     autoResize(content);
     updateCharCount();
   };
   content.addEventListener('input', contentInputHandler);

   autoResize(content);

   if (comment) {
     autoResize(comment);
     // 댓글도 높이 자동조절 (필요시)
     comment.addEventListener('input', () => {
       autoResize(comment);
     });
   }

   // 파일 삭제 버튼 보이기 및 삭제 기능 활성화
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

      console.log('답변완료 버튼 클릭 - 댓글 영역:', commentArea);
      if (commentArea) {
        commentArea.style.display = 'block';
        console.log('댓글 영역 보이도록 처리 완료');
      } else {
        console.log('댓글 영역을 찾을 수 없습니다.');
      }

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

// 카테고리 input 너비 자동 조절
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

// 이메일 input 너비 자동 조절
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
/* 이미지 작업전 js */