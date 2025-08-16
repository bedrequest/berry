console.log("boardRegister.js in");

// textarea 자동 높이 조절
const contentTextarea = document.getElementById('content');

if (contentTextarea) {
  contentTextarea.addEventListener('input', function () {
    this.style.height = 'auto';  // 높이 초기화
    this.style.height = this.scrollHeight + 'px';  // 새 높이로 설정
  });
}

const contentArea = document.getElementById('content');
const charCountDisplay = document.getElementById('charCount');

if (contentArea && charCountDisplay) {
  const updateCharCount = () => {
    const length = contentArea.value.length;
    charCountDisplay.textContent = `${length} / 1000`;

    // 경고 색 적용 (900자 이상일 때)
    if (length >= 900) {
      charCountDisplay.classList.add('warning');
    } else {
      charCountDisplay.classList.remove('warning');
    }
  };

  updateCharCount(); // 초기 로딩 시 반영
  contentArea.addEventListener('input', updateCharCount);
}




// 리스트 버튼 이동
document.getElementById('listBtn').addEventListener('click', () => {
    location.href = "/qna/list";
});

// 등록 버튼 유효성 검사
document.getElementById('regBtn').addEventListener('click', function (e) {
    const category = document.getElementById('category').value;
    const title = document.getElementById('title').value.trim();
    const content = document.getElementById('content').value.trim();

    if (category === "선택") {
        alert("카테고리를 선택해주세요.");
        e.preventDefault(); // submit 막기
        return;
    }

       // 제목 검사
        if (title === '') {
            alert('제목을 입력해주세요.');
            e.preventDefault();
            return;
        }

        // 본문 검사
        if (content === '') {
            alert('내용을 입력해주세요.');
            e.preventDefault();
            return;
        }

    console.log("등록 처리 실행");
});

// 실행파일 확장자 및 10MB 사이즈 제한
const regExp = new RegExp("\\.(exe|sh|bat|jar|dll|msi)$");
const maxSize = 1024 * 1024 * 10;

function fileValid(fileName, fileSize) {
    if (regExp.test(fileName)) return 0;
    if (fileSize > maxSize) return 0;
    return 1;
}

// 파일 선택 시 동작
document.getElementById('input-file').addEventListener('change', function () {
    const fileObject = this.files;
    console.log(fileObject);

    document.getElementById('regBtn').disabled = false;

    const div = document.getElementById('fileZone');
    if (div) div.innerHTML = "";

    let ul = `<ul class="list-group list-group-flush">`;
    let isOk = 1;
    let fileNames = [];

    // 이미지 미리보기 영역
    const previewArea = document.getElementById('imagePreviewArea');
    previewArea.innerHTML = ''; // 기존 미리보기 삭제

    for (let file of fileObject) {
        let valid = fileValid(file.name, file.size);
        isOk *= valid;
        fileNames.push(file.name);

        ul += `<li class="list-group-item">`;
        ul += `<div class="mb-3">`;
        ul += `${valid ? '<div class="fw-bold">업로드 가능</div>' : '<div class="fw-bold text-danger">업로드 불가능</div>'}`;
        ul += `${file.name}`;
        ul += `<span class="badge rounded-pill text-bg-${valid ? 'success' : 'danger'}">${file.size}Bytes</span>`;
        ul += `</div></li>`;

        // 이미지 미리보기 - textarea와 파일명 입력 사이
        if (file.type.startsWith('image/')) {
            const reader = new FileReader();
            reader.onload = function (e) {
                const imgHTML = `
                    <div class="upload-display" style="display:inline-block; margin-right:10px;">
                        <div class="upload-thumb-wrap">
                            <img src="${e.target.result}" class="upload-thumb" style="max-width:100px; max-height:100px;">
                        </div>
                    </div>`;
                previewArea.insertAdjacentHTML('beforeend', imgHTML);
            };
            reader.readAsDataURL(file);
        }
    }

    ul += `</ul>`;
    if (div) div.innerHTML = ul;

    if (isOk === 0) {
        document.getElementById('regBtn').disabled = true;
    }

    // 파일 이름 표시 (쉼표 구분)
    document.querySelector('.upload-name').value = fileNames.join(', ');
});
