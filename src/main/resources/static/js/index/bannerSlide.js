console.log("bannerSlide.js in");

// 기본 설정

// 배경
const bannerBackground = document.querySelector(".banner-background");

bannerBackground.style.backgroundColor = backgroundColors[0]; // 기본 색상 설정

// 이미지 슬라이드
const imgSlideContainer = document.querySelector('.img-slide-list');
const imgSlides = Array.from(document.querySelectorAll('.img-slide-resource'));

// 텍스트 슬라이드
const textSlideContainer = document.querySelector(".text-slide-list");
const textSlides = Array.from(document.querySelectorAll(".text-slide-resource"));

// 슬라이드 바
const progressBar = document.getElementById("progressBar");
const slideNumberDisplay = document.querySelector(".slide-change-number");

const imgSlideWidth = 800; // 이미지 div 길이
const textSlideWidth = 370; // 텍스트 div 길이

let currentIndex = 1;
let isSliding = false;
let autoSlide;
let progressInterval;
let startX = 0;
let isDragging = false;
let currentTranslateX = 0;

const imgTotalOriginalSlides = imgSlides.length;

// 이미지 복제 슬라이드 생성
const imgFirstClone = imgSlides[0].cloneNode(true);
const imgSecondClone = imgSlides[1].cloneNode(true);
const imgLastClone = imgSlides[imgSlides.length - 1].cloneNode(true);

imgSlideContainer.appendChild(imgFirstClone);
imgSlideContainer.appendChild(imgSecondClone);
imgSlideContainer.insertBefore(imgLastClone, imgSlides[0]);

// 텍스트 복제 슬라이드 생성
const textFirstClone = textSlides[0].cloneNode(true);
const textSecondClone = textSlides[1].cloneNode(true);
const textLastClone = textSlides[textSlides.length - 1].cloneNode(true);

textSlideContainer.appendChild(textFirstClone);
textSlideContainer.appendChild(textSecondClone);
textSlideContainer.insertBefore(textLastClone, textSlides[0]);

// 이미지 슬라이드 길이 재설정
const imgAllSlides = document.querySelectorAll('.img-slide-resource');
imgSlideContainer.style.width = `${imgSlideWidth * imgAllSlides.length}px`;

// 텍스트 슬라이드 길이 재설정
const textAllSlides = document.querySelectorAll('.text-slide-resource');
textSlideContainer.style.width = `${textSlideWidth * textAllSlides.length}px`;

// 초기 위치 설정
imgSlideContainer.style.transform = `translateX(-${imgSlideWidth * currentIndex}px)`;
textSlideContainer.style.transform = `translateX(-${textSlideWidth * currentIndex}px)`;

// 슬라이드 이동 함수
function moveToSlide(index) {
  if (isSliding) return;
  isSliding = true;
  imgSlideContainer.style.transition = 'transform 0.8s ease-in-out';
  imgSlideContainer.style.transform = `translateX(-${imgSlideWidth * index}px)`;

  textSlideContainer.style.transition = 'transform 0.8s ease-in-out';
  textSlideContainer.style.transform = `translateX(-${textSlideWidth * index}px)`;

  // 배경색 변경
  bannerBackground.style.transition = 'background-color 0.8s ease-in-out';

  const bgIndex = (index - 1 + backgroundColors.length) % backgroundColors.length;
  bannerBackground.style.backgroundColor = backgroundColors[bgIndex];

  currentIndex = index;
  updateSlideNumber(index);

  imgSlideContainer.addEventListener('transitionend', () => {
    if (currentIndex === 0) {
      imgSlideContainer.style.transition = 'none';
      textSlideContainer.style.transition = 'none';

      currentIndex = imgTotalOriginalSlides;
      imgSlideContainer.style.transform = `translateX(-${imgSlideWidth * currentIndex}px)`;
      textSlideContainer.style.transform = `translateX(-${textSlideWidth * currentIndex}px)`;

    }

    if (currentIndex === imgAllSlides.length - 2 && currentIndex === textAllSlides.length - 2) {

      imgSlideContainer.style.transition = 'none';
      textSlideContainer.style.transition = 'none';

      currentIndex = 1;
      imgSlideContainer.style.transform = `translateX(-${imgSlideWidth * currentIndex}px)`;
      textSlideContainer.style.transform = `translateX(-${textSlideWidth * currentIndex}px)`;
    }
    isSliding = false;
  }, { once: true });
}

// 슬라이드 번호 갱신
function updateSlideNumber(index) {
  let displayNum = index;
  if (index === 0) displayNum = imgTotalOriginalSlides;
  else if (index === imgAllSlides.length - 2 && index === textAllSlides.length - 2) displayNum = 1;

  slideNumberDisplay.textContent = `0${displayNum}`;
}

// 자동 슬라이드 시작
function startAutoSlide() {
  autoSlide = setInterval(() => {
    moveToSlide(currentIndex + 1);
  }, 5000);
  startProgress();
}

// 정지
function stopAutoSlide() {
  clearInterval(autoSlide);
  clearInterval(progressInterval);
}

// 재시작
function resetAutoSlide() {
  stopAutoSlide();
  startAutoSlide();
}

// 버튼 이벤트
document.getElementById('slideNextBtn').addEventListener('click', () => {
  const icon = document.getElementById("isPaused");
  
  moveToSlide(currentIndex + 1);
  clearInterval(progressInterval);
  icon.classList.remove("bi-play");
  icon.classList.add("bi-pause");
  icon.innerHTML = `<path d="M6 3.5a.5.5 0 0 1 .5.5v8a.5.5 0 0 1-1 0V4a.5.5 0 0 1 .5-.5m4 0a.5.5 0 0 1 .5.5v8a.5.5 0 0 1-1 0V4a.5.5 0 0 1 .5-.5"/>`;
  resetAutoSlide();
});
``
document.getElementById('slidePrevBtn').addEventListener('click', () => {
  const icon = document.getElementById("isPaused");

  moveToSlide(currentIndex - 1);
  clearInterval(progressInterval);
  icon.classList.remove("bi-play");
  icon.classList.add("bi-pause");
  icon.innerHTML = `<path d="M6 3.5a.5.5 0 0 1 .5.5v8a.5.5 0 0 1-1 0V4a.5.5 0 0 1 .5-.5m4 0a.5.5 0 0 1 .5.5v8a.5.5 0 0 1-1 0V4a.5.5 0 0 1 .5-.5"/>`;
  resetAutoSlide();
});

document.getElementById('slideStopBtn').addEventListener('click', (e) => {
  const icon = e.currentTarget.querySelector("svg");
  const isPaused = icon.classList.contains("bi-play");

  if (isPaused) {
    startAutoSlide();
    icon.classList.remove("bi-play");
    icon.classList.add("bi-pause");
    icon.innerHTML = `<path d="M6 3.5a.5.5 0 0 1 .5.5v8a.5.5 0 0 1-1 0V4a.5.5 0 0 1 .5-.5m4 0a.5.5 0 0 1 .5.5v8a.5.5 0 0 1-1 0V4a.5.5 0 0 1 .5-.5"/>`;
  } else {
    stopAutoSlide();
    icon.classList.remove("bi-pause");
    icon.classList.add("bi-play");
    icon.innerHTML = `<path d="M10.804 8.396 5.51 11.548A.5.5 0 0 1 5 11.096V4.904a.5.5 0 0 1 .51-.452l5.294 3.152a.5.5 0 0 1 0 .872z"/>`;
  }
});

// 진행바
function startProgress() {
  progressBar.style.width = "0%";
  let progress = 0;

  progressInterval = setInterval(() => {
    progress += 1;
    progressBar.style.width = `${progress}%`;
    if (progress >= 100) {
      clearInterval(progressInterval);
      setTimeout(startProgress, 0);
    }
  }, 50); // 50ms * 100 = 5초
}

// 초기 시작
startAutoSlide();
updateSlideNumber(currentIndex);
