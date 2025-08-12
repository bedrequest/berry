import Swiper from 'https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.mjs';

// 1. 리뷰 스와이퍼
const reviewSwiper = new Swiper('#reviewPickUpSection .swiper', {
  slidesPerView: 2,
  spaceBetween: 20, 
  navigation: {
    nextEl: '#reviewPickUpSection .swiper-button-next',
    prevEl: '#reviewPickUpSection .swiper-button-prev'
  }
});

// 2. 태그 추천 스와이퍼
if (document.querySelectorAll('.tag>.swiper').length > 0) {
  const suggestSwipers = new Swiper('.tag>.swiper', {
    slidesPerView: 4,
    spaceBetween: 12
  }),
  suggestPrevBtns = document.querySelectorAll('.tag>.swiper-button-prev'),
  suggestNextBtns = document.querySelectorAll('.tag>.swiper-button-next');
  
  for (let i = 0; i < suggestSwipers.length; i++)
    setNavBtns(suggestSwipers[i], suggestPrevBtns[i], suggestNextBtns[i]);
}

/** prev와 next에 swiper의 navigation 효과를 주는 함수 */
function setNavBtns(swiper, prev, next) {
  prev.addEventListener('click', () => {
    if (prev.classList.contains('disabled')) return;
    swiper.slidePrev();
    checkButtons(swiper, prev, next);
  });
  next.addEventListener('click', () => {
    if (next.classList.contains('disabled')) return;
    swiper.slideNext();
    checkButtons(swiper, prev, next);
  });

  checkButtons(swiper, prev, next);
}

/** prev와 next의 class disabled를 관리하는 함수 */
function checkButtons(swiper, prev, next) {
  const current = swiper.realIndex, length = swiper.slides.length;

  if (current == 0) prev.classList.add('disabled');
  else prev.classList.remove('disabled');

  if (current + swiper.slidesPerView == length) next.classList.add('disabled');
  else next.classList.remove('disabled');
}