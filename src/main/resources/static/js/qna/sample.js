
document.addEventListener('DOMContentLoaded', () => {
  const isAnsweredAttr = document.body.getAttribute('data-answered');
  const bnoValue = document.querySelector('input[name="bno"]').value;
  let isAnswered = isAnsweredAttr === 'true';

  const isSessionAnswered = sessionStorage.getItem(`answered-${bnoValue}`) === 'true';
  if (isSessionAnswered) {
    isAnswered = true;
  }

  const modBtn = document.getElementById('modBtn');
  const delBtn = document.getElementById('delBtn');
  const submitBtn = document.getElementById('submitBtn');
  const completeBtn = document.getElementById('completeBtn');
  const listBtn = document.getElementById('listBtn');
  const cancelBtn = document.getElementById('CancelBtn');

  const content = document.getElementById('con');
  const title = document.getElementById('t');
  const categoryInput = document.getElementById('c');
  const comment = document.getElementById('comment');
  const charCount = document.getElementById('charCount');

  const uploadInput = document.getElementById('ex_filename');
  const uploadName = document.querySelector('.upload-name');
  const uploadLabel = document.querySelector('.file-upload label[for="ex_filename"]');
  const previewContainer = document.getElementById('imagePreviewContainer');

  const modForm = document.getElementById('modForm');

  let originalState = {};

  function saveOriginalState() {
    originalState = {
      title: title.value,
      content: content.value,
      comment: comment ? comment.value : '',
      category: categoryInput.value,
      files: previewContainer.innerHTML,
      charCount: charCount.innerText
    };
  }

  function restoreOriginalState() {
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

  if (comment) autoResize(comment);
  if (content) autoResize(content);

  if (isAnswered) {
    [modBtn, delBtn, submitBtn, cancelBtn, completeBtn].forEach(btn => {
      if (btn) btn.style.display = "none";
    });

    if (listBtn) listBtn.style.display = "inline-block";
    if (title) title.setAttribute('readonly', true);
    if (content) content.setAttribute('readonly', true);
    if (comment) comment.setAttribute('readonly', true);

    if (uploadInput) {
      uploadInput.disabled = true;
      uploadInput.value = '';
    }
    if (uploadLabel) {
      uploadLabel.classList.add('disabled');
      uploadLabel.style.cursor = 'default';
    }

    document.querySelectorAll('.file-x').forEach(btn => {
      btn.style.visibility = 'hidden';
      btn.onclick = null;
    });
  }

  if (modBtn) {
    modBtn.addEventListener('click', () => {
      if (isAnswered) {
        alert('답변 완료된 문의는 수정할 수 없습니다.');
        return;
      }

      saveOriginalState();

      title.removeAttribute('readonly');
      content.removeAttribute('readonly');
      if (comment) comment.removeAttribute('readonly');

      modBtn.style.display = 'none';
      delBtn.style.display = 'none';
      listBtn.style.display = 'none';
      submitBtn.style.display = 'inline-block';
      completeBtn.style.display = 'inline-block';
      cancelBtn.style.display = 'inline-block';

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
      content.addEventListener('input', () => {
        autoResize(content);
        updateCharCount();
      });

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
  }

  if (delBtn) {
    delBtn.addEventListener('click', () => {
      if (confirm("삭제하시겠습니까?")) {
        location.href = "/qna/remove?bno=" + bnoValue;
      }
    });
  }

  if (listBtn) {
    listBtn.addEventListener('click', () => {
      location.href = "/qna/list";
    });
  }

  if (cancelBtn) {
    cancelBtn.addEventListener('click', () => {
      restoreOriginalState();

      title.setAttribute('readonly', true);
      content.setAttribute('readonly', true);
      if (comment) comment.setAttribute('readonly', true);

      modBtn.style.display = 'inline-block';
      delBtn.style.display = 'inline-block';
      listBtn.style.display = 'inline-block';

      submitBtn.style.display = 'none';
      completeBtn.style.display = 'none';
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

      autoResize(content);
      if (comment) autoResize(comment);

      document.querySelectorAll('.file-x').forEach(btn => {
        btn.style.visibility = 'hidden';
        btn.onclick = null;
      });
    });
  }

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

  if (completeBtn) {
    completeBtn.addEventListener('click', () => {
      if (!confirm("답변을 완료하시겠습니까?")) return;

      sessionStorage.setItem(`answered-${bnoValue}`, 'true');
      isAnswered = true;

      [modBtn, delBtn, submitBtn, cancelBtn, completeBtn].forEach(btn => {
        if (btn) btn.style.display = "none";
      });

      if (listBtn) listBtn.style.display = "inline-block";
      if (title) title.setAttribute('readonly', true);
      if (content) content.setAttribute('readonly', true);
      if (comment) comment.setAttribute('readonly', true);

      if (uploadInput) {
        uploadInput.disabled = true;
        uploadInput.value = '';
      }
      if (uploadLabel) {
        uploadLabel.classList.add('disabled');
        uploadLabel.style.cursor = 'default';
      }

      document.querySelectorAll('.file-x').forEach(btn => {
        btn.style.visibility = 'hidden';
        btn.onclick = null;
      });
    });
  }
});

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
