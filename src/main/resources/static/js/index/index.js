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

// 3. 검색 API
const [outerSearchInput, outerSearchButton, outerSearchResultArea]
  = ['outerSearchInput', 'outerSearchButton', 'outerSearchResultArea'].map(e => document.getElementById(e));

outerSearchButton.addEventListener('click', () => {
  const keyword = outerSearchInput.value;
  if (keyword == '') return;

  fetch('/outerSearch/' + keyword)
  .then(resp => resp.json())
    .then(result => {
      outerSearchResultArea.innerHTML = '';
      for (const blog of result.items) 
        outerSearchResultArea.innerHTML += `
      <a href="${blog.link}" class="naver-search-item" target="_blank">
        <div class="naver-search-header">
          <div class="naver-search-writer">${blog.bloggername}</div>
          <div class="naver-search-postdate">${blog.postdate}</div>
        </div>
        <div class="naver-search-title">${blog.title}</div>
        <div class="naver-search-description">${blog.description}</div>
      </a>
      `;
    }).catch(error => {
      alert('검색에 실패했습니다. 자세한 내용은 콘솔을 확인해주세요.');
      console.log(error);
    });
});