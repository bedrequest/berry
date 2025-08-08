import Swiper from 'swiper';
import { Chart } from 'chart.js';

function initMainSectionTwo() {
  // 1) Swiper 풀스크린 슬라이더
  const swiper = new Swiper('.full-screen-slider', {
    slidesPerView: 1,
    loop: true,
    effect: 'fade',
    fadeEffect: { crossFade: true },
    navigation: {
      prevEl: '.swiper-button-prev',
      nextEl: '.swiper-button-next'
    },
    pagination: {
      el: '.swiper-pagination',
      clickable: true
    }
  });

  // 2) Chart.js 도넛 차트
  document.querySelectorAll('.chart').forEach(el => {
    const stats = JSON.parse(el.dataset.stats);
    new Chart(el.querySelector('canvas').getContext('2d'), {
      type: 'doughnut',
      data: {
        labels: Object.keys(stats),
        datasets: [{ data: Object.values(stats) }]
      },
      options: {
        cutout: '60%',
        maintainAspectRatio: false,
        plugins: { legend: { position: 'right' } }
      }
    });
  });
}

document.addEventListener('DOMContentLoaded', initMainSectionTwo);
