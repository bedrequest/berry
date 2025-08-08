// 변수 선언
const sections = document.querySelectorAll('.section, footer'),
header = document.querySelector('header');

let current = 0;
let isScrolling = false;

window.addEventListener('wheel', e => {
  if (isScrolling) return;

  if (e.deltaY > 0 && current < sections.length - 1) {
    current++;
    scrollToSection(current);
  } else if (e.deltaY < 0 && current > 0) {
    current--;
    scrollToSection(current);
  }
});

function scrollToSection(index) {
  isScrolling = true;
  window.scrollTo({
    top: sections[index].offsetTop - 61,
    behavior: "smooth"
  });

  setTimeout(() => {
    isScrolling = false;
  }, 800);
}