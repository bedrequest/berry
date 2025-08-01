// src/main/resources/static/js/review/reviews.js

(function(window, document) {
  // 리뷰 관련 UI 인터랙션 초기화
  function initReviewScripts() {
    // 1) 리뷰 작성 폼 토글
    const toggleBtn     = document.getElementById('toggleReviewFormBtn');
    const formContainer = document.getElementById('reviewFormContainer');
    if (toggleBtn && formContainer) {
      toggleBtn.addEventListener('click', () => {
        formContainer.classList.toggle('d-none');
      });
    }

    // 2) 정렬 드롭다운 토글
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

    // 3) 리뷰 내용 자동 접기 + 더보기
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

    // 4) 더보기/접기
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

    // 5) 좋아요·신고 
    document.querySelectorAll('button[data-like], button[data-report]').forEach(btn => {
      const isLike = btn.hasAttribute('data-like');
      btn.addEventListener('click', () => {
        const reviewId  = btn.dataset.reviewId;
        const pageParam = btn.dataset.pageParam;
        const url       = isLike
          ? `/reviews/${reviewId}/like?pageParam=${pageParam}`
          : `/reviews/${reviewId}/report?pageParam=${pageParam}`;
        if (!isLike && !confirm('정말 이 리뷰를 신고하시겠습니까?')) return;

        fetch(url, {
          method: 'POST',
          headers: { 'X-Requested-With': 'XMLHttpRequest' }
        })
        .then(res => {
          if (!res.ok) throw new Error(`HTTP ${res.status}`);
          return res.json();
        })
        .then(data => {
          if (isLike) {
            btn.classList.toggle('active');
            btn.querySelector('.like-count').innerText = data.likeCount;
          } else {
            alert(data.message || '신고가 접수되었습니다.');
          }
        })
        .catch(err => {
          console.error(err);
          alert((isLike ? '좋아요' : '신고') + ' 중 오류가 발생했습니다.');
        });
      });
    });

// 6) 도넛 차트 초기화
const canvas   = document.getElementById('tagChart');
const dataElem = document.getElementById('reviewChartData');
if (canvas && dataElem && typeof Chart !== 'undefined') {
  // 원본 레이블·카운트 파싱
  const labels = JSON.parse(dataElem.dataset.labels || '[]');
  const counts = JSON.parse(dataElem.dataset.counts || '[]');

  // 상위 3개만 추출
  const topLabels = labels.slice(0, 4);
  const topCounts = counts.slice(0, 4);

  // 나머지 합산해서 "기타"로 추가
  const otherCount = counts.slice(4).reduce((sum, v) => sum + v, 0);
  if (otherCount > 0) {
    topLabels.push('기타');
    topCounts.push(otherCount);
  }

  // 차트 생성
  new Chart(canvas.getContext('2d'), {
    type: 'doughnut',
    data: {
      labels: topLabels,
      datasets: [{
        data: topCounts,
        // backgroundColor: ['#E74B60', '#F39C12', '#2ECC71', '	#3498DB','	#BDC3C7'],
        backgroundColor: [
          '#E74B60',  // 메인 강조 색
          '#50E3C2',  // 밝고 생기 있는 민트
          '#F5A623',  // 따뜻한 보완 색
          '#4A90E2',  // 깔끔한 블루톤
          '#BDC3C7'   // 기타 회색 (중립 영역)
        ],
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
