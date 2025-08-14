
// 기본 설정

// 이미지 슬라이드
const lodgeReviewSlide = document.querySelector('.ms2__slide-wrapper');
const lodgeSlides = Array.from(document.querySelectorAll('.ms2__slide'));

const lodgeReviewSlideWidth = document.querySelector('.ms2__slide-container').clientWidth;

let lodgeReviewCurrentIndex = 1;
let lodgeReviewAnimating = false;
let islodgeReviewSliding = false;

const lodgeTotalOriginalSlides = lodgeSlides.length;

// 이미지 복제 슬라이드 생성
const lodgeReviewFirstClone = lodgeSlides[0].cloneNode(true);
const lodgeReviewLastClone = lodgeSlides[lodgeSlides.length - 1].cloneNode(true);

lodgeReviewSlide.appendChild(lodgeReviewFirstClone);
lodgeReviewSlide.insertBefore(lodgeReviewLastClone, lodgeSlides[0]);

// 이미지 슬라이드 길이 재설정
const lodgeReviewAllSlides = document.querySelectorAll('.ms2__slide');
lodgeReviewSlide.style.width = `${lodgeReviewSlideWidth * lodgeReviewAllSlides.length}px`;


// 초기 위치 설정
lodgeReviewSlide.style.transform = `translateX(-${lodgeReviewSlideWidth * lodgeReviewCurrentIndex}px)`;

// 슬라이드 이동 함수
function lodgeReviewMoveToSlide(index) {
  if(islodgeReviewSliding) return;

  lodgeReviewSlide.style.transition = 'transform 0.5s ease-in-out';
  lodgeReviewSlide.style.transform = `translateX(-${lodgeReviewSlideWidth * index}px)`;

  lodgeReviewCurrentIndex = index;

  lodgeReviewSlide.addEventListener('transitionend', () => {
    if (lodgeReviewCurrentIndex === 0) {
      lodgeReviewSlide.style.transition = 'none';

      lodgeReviewCurrentIndex = lodgeTotalOriginalSlides;
      lodgeReviewSlide.style.transform = `translateX(-${lodgeReviewSlideWidth * lodgeReviewCurrentIndex}px)`;

    }
    if (lodgeReviewCurrentIndex === lodgeReviewAllSlides.length - 1) {
      lodgeReviewSlide.style.transition = 'none';

      lodgeReviewCurrentIndex = 1;
      lodgeReviewSlide.style.transform = `translateX(-${lodgeReviewSlideWidth * lodgeReviewCurrentIndex}px)`;
    }
    islodgeReviewSliding = false;
    requestAnimationFrame(() => { lodgeReviewAnimating = false; });
  }, { once: true });
}

// 버튼 이벤트
document.getElementById('lodgeReviewNextBtn').addEventListener('click', () => {
  lodgeReviewMoveToSlide(lodgeReviewCurrentIndex + 1); // 이전
});

document.getElementById('lodgeReviewPrevBtn').addEventListener('click', () => {
  lodgeReviewMoveToSlide(lodgeReviewCurrentIndex - 1); // 다음
});


// 차트 
if (window.Chart) {
    Chart.defaults.elements.arc.borderColor = 'transparent';
    Chart.defaults.elements.arc.borderWidth = 0;
  }

  const PALETTE = ['#FF6B8A', '#6AD0FF', '#FFD166', '#7AE582', '#B39DDB', '#FFA7C4'];
  const OTHER_COLOR = 'rgba(31,41,55,0.12)';

  document.querySelectorAll('#best-lodges-section .chart').forEach(box => {
    try {
      const raw = box.getAttribute('data-stats') || '{}';
      const stats = JSON.parse(raw);
      const labels = Object.keys(stats || {});
      const values = Object.values(stats || {}).map(v => Number(v) || 0);

      if (!labels.length || values.every(v => v === 0)) { box.style.display = 'none'; return; }

      const pairs = labels.map((l,i)=>({ l, v: values[i] })).sort((a,b)=>b.v-a.v);
      const top = pairs.slice(0,4);
      const others = pairs.slice(4).reduce((s,p)=>s+p.v,0);
      const chartLabels = top.map(p=>p.l).concat(others>0?['기타']:[]);
      const chartValues = top.map(p=>p.v).concat(others>0?[others]:[]);

      const canvas = box.querySelector('canvas'); if (!canvas) return;
      const bg = chartLabels.map((label, i)=> label==='기타' ? OTHER_COLOR : PALETTE[i % PALETTE.length]);

      new Chart(canvas.getContext('2d'), {
        type: 'doughnut',
        data: { labels: chartLabels, datasets: [{
          data: chartValues,
          backgroundColor: bg,
          borderColor: 'transparent',
          borderWidth: 0,
          spacing: 2,
          hoverOffset: 4
        }]},
        options: {
          cutout: '62%',
          responsive: false,
          maintainAspectRatio: true,
          plugins: {
            title: { display: false},
            legend: { position: 'right', labels: { boxWidth: 18 } },
            tooltip: { enabled: true }
          }
        }
      });
    } catch(e) {
      console.warn('top5 chart error', e);
      box.style.display = 'none';
    }
  });