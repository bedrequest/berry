// src/main/resources/static/js/review/reviews.js

(function(window, document) {
  // 리뷰 관련 UI 인터랙션 초기화
  function initReviewScripts() {
    // 리뷰 작성 폼 토글
    const toggleBtn     = document.getElementById('toggleReviewFormBtn');
    const formContainer = document.getElementById('reviewFormContainer');
    if (toggleBtn && formContainer) {
      toggleBtn.addEventListener('click', () => {
        formContainer.classList.toggle('d-none');
      });
    }

    // 정렬 드롭다운 토글
    const sortToggle = document.getElementById('sortToggle');
    const sortMenu   = document.getElementById('sortMenu');
    const sortWrap   = sortToggle?.closest('.sort-dropdown');
    if (sortToggle && sortMenu && sortWrap) {
      // 초기엔 항상 닫힌 상태
      sortWrap.classList.remove('open');

      sortToggle.addEventListener('click', e => {
        e.stopPropagation();
        sortWrap.classList.toggle('open');
      });
      // 바깥 클릭 시 메뉴 닫기
      document.addEventListener('click', () => {
        sortWrap.classList.remove('open');
      });
    }

    // 리뷰 내용 자동 접기 + 더보기
    document.querySelectorAll('.review-content').forEach(content => {
      content.classList.add('collapsed');
      const btn = content.parentElement.querySelector('.toggle-more');
      if (btn) {
        if (content.scrollHeight > content.clientHeight) {
          btn.style.display = 'inline-block';
          btn.innerText   = '더보기';
        } else {
          btn.style.display = 'none';
        }
      }
    });

    // 더보기/접기
    document.querySelectorAll('.toggle-more').forEach(btn => {
      btn.addEventListener('click', () => {
        const content = btn.closest('.review-content-container')
                          .querySelector('.review-content');
        if (content.classList.toggle('collapsed')) {
          btn.innerText = '더보기';
        } else {
          btn.innerText = '접기';
        }
      });
    });

    

//  도넛 차트 초기화
const canvas   = document.getElementById('tagChart');
const dataElem = document.getElementById('reviewChartData');
if (canvas && dataElem && typeof Chart !== 'undefined') {
  // 1) 원본 배열 파싱
  const rawLabels = JSON.parse(dataElem.dataset.labels || '[]');
  const rawCounts = JSON.parse(dataElem.dataset.counts || '[]');

  // 2) 라벨–카운트 쌍으로 묶어서 내림차순 정렬
  const items = rawLabels.map((label, i) => ({
    label,
    count: rawCounts[i] || 0
  }));
  items.sort((a, b) => b.count - a.count);

  // 3) 상위 4개 뽑고 나머지는 기타로 합산
  const topItems = items.slice(0, 4);
  const otherCount = items.slice(4).reduce((sum, it) => sum + it.count, 0);

  // 4) 차트용 배열 재생성
  const topLabels = topItems.map(it => it.label);
  const topCounts = topItems.map(it => it.count);
  if (otherCount > 0) {
    topLabels.push('기타');
    topCounts.push(otherCount);
  }

  // 5) 차트 생성
  new Chart(canvas.getContext('2d'), {
    type: 'doughnut',
    data: {
      labels: topLabels,
      datasets: [{
        data: topCounts,
        backgroundColor: [
          '#ff6c80',  // 밝은 에르메스 코랄 오렌지 (부드럽고 따뜻함)
          '#A3D9A5',  // 보테가 무드의 밝은 세이지 민트 (깔끔하고 신선함)
          '#EAC8A0',  // 로에베 감성의 연한 누드 베이지 (따뜻하고 화사)
          '#C2B5E3',  // 발렌시아가의 연보라 블루 (세련되고 시크하게)
          '#C7D3D4'   // 기존 실버그레이 유지 (밸런스용)
        ]
        ,
        hoverOffset: 6
      }]
    },
    options: {
      plugins: {
        legend: { position: 'right' },
        tooltip: {
          callbacks: {
            label: ctx => `${ctx.label}: ${ctx.parsed}개`
          }
        }
      }
    }
  });
  // Swiper 슬라이더 초기화
  document.querySelectorAll('.swiper-container').forEach(container => {
    if (container.swiper) container.swiper.destroy(true, true);
    new Swiper(container, {
      slidesPerView: 5,    // 필요에 따라 숫자 or 'auto'
      spaceBetween: 2,
      freeMode: false,
      navigation: {
        prevEl: container.querySelector('.swiper-button-prev'),
        nextEl: container.querySelector('.swiper-button-next'),
      },
      // breakpoints: {
      //   576:  { slidesPerView: 1, spaceBetween: 8 },
      //   768:  { slidesPerView: 2, spaceBetween: 12 },
      //   992:  { slidesPerView: 3, spaceBetween: 16 },
      //   1200: { slidesPerView: 4, spaceBetween: 16 },
      // }
    });
  });


}

  }

  // 페이징 링크 바인딩
  function bindPaginationLinks() {
    const reviewArea = document.getElementById('reviewArea');
    if (!reviewArea) return;
    reviewArea.querySelectorAll('.page-link[data-page]').forEach(link => {
      link.addEventListener('click', e => {
        e.preventDefault();
        loadReviewFragment(link.dataset.page);
      });
    });
  }

  // 리뷰 프래그먼트 비동기 로드
  // 이제 page, sortKey 두 인자를 받습니다.
  function loadReviewFragment(page = 1, sortKey) {
    const reviewArea = document.getElementById('reviewArea');
    if (!reviewArea) return;

    let baseUrl = reviewArea.dataset.url;
    // page 파라미터 교체
    baseUrl = baseUrl.replace(/(page=)\d+/, `$1${page}`);
    // sort가 넘어왔다면 sort 파라미터도 교체 or 추가
    if (sortKey) {
      if (baseUrl.match(/(sort=)[^&]*/)) {
        baseUrl = baseUrl.replace(/(sort=)[^&]*/, `$1${sortKey}`);
      } else {
        baseUrl += `&sort=${sortKey}`;
      }
    }

    fetch(baseUrl)
      .then(res => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.text();
      })
      .then(html => {
        reviewArea.innerHTML = html;
        initReviewScripts();
        bindPaginationLinks();
        if (window.location.hash) {
        const target = document.querySelector(window.location.hash);
        if (target) target.scrollIntoView({ behavior: 'auto' });
        }
      })
      .catch(err => {
        console.error('Fetch error:', err);
        reviewArea.innerHTML = '<p class="text-center text-danger">리뷰 로딩에 실패했습니다.</p>';
      });
  }

  // 전역 노출 & 초기 실행
  window.loadReviewFragment = loadReviewFragment;
  window.addEventListener('DOMContentLoaded', () => {
    if (typeof Chart === 'undefined') {
      const s = document.createElement('script');
      s.src = 'https://cdn.jsdelivr.net/npm/chart.js';
      s.onload = () => { loadReviewFragment(); };
      document.head.appendChild(s);
    } else {
      loadReviewFragment();
    }
  });
})(window, document);
