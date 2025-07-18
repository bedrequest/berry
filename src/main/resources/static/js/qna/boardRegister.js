console.log("boardRegister.js in");

// 리스트 버튼 이동
document.getElementById('listBtn').addEventListener('click', () => {
    location.href = "/board/list";
});

// 등록 버튼 유효성 검사
document.getElementById('regBtn').addEventListener('click', function (e) {
    const category = document.getElementById('category').value;

    if (category === "--선택--") {
        alert("카테고리를 선택해주세요.");
        e.preventDefault(); // submit 막기
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
    if (div) div.innerHTML = ""; // 파일존이 있으면 초기화

    let ul = `<ul class="list-group list-group-flush">`;
    let isOk = 1;

    for (let file of fileObject) {
        let valid = fileValid(file.name, file.size);
        isOk *= valid;

        ul += `<li class="list-group-item">`;
        ul += `<div class="mb-3">`;
        ul += `${valid ? '<div class="fw-bold">업로드 가능</div>' : '<div class="fw-bold text-danger">업로드 불가능</div>'}`;
        ul += `${file.name}`;
        ul += `<span class="badge rounded-pill text-bg-${valid ? 'success' : 'danger'}">${file.size}Bytes</span>`;
        ul += `</div></li>`;
    }

    ul += `</ul>`;
    if (div) div.innerHTML = ul;

    if (isOk === 0) {
        document.getElementById('regBtn').disabled = true;
    }

    // 파일 이름 표시
    const filename = fileObject[0]?.name || '파일선택';
    document.querySelector('.upload-name').value = filename;

    // 이미지 미리보기
    if (fileObject[0]?.type.startsWith('image/')) {
        const reader = new FileReader();
        reader.onload = function (e) {
            const preview = document.querySelector('.preview-image');
            if (!preview) return;

            // 기존 미리보기 삭제
            const oldDisplay = preview.querySelector('.upload-display');
            if (oldDisplay) oldDisplay.remove();

            const imgHTML = `
                <div class="upload-display">
                    <div class="upload-thumb-wrap">
                        <img src="${e.target.result}" class="upload-thumb">
                    </div>
                </div>`;
            preview.insertAdjacentHTML('afterbegin', imgHTML);
        };
        reader.readAsDataURL(fileObject[0]);
    }
});
