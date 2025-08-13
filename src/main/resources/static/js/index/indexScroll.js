// 변수 선언
const sections = document.querySelectorAll('.section, footer'),
header = document.querySelector('header'),
footer = document.querySelector('footer');

let current = 0;
let isScrolling = false;

// 스크롤
window.addEventListener('wheel', e => {
  if (isScrolling) return;
  
  const searchSuggestions = document.getElementById('searchSuggestions');
  if (searchSuggestions && !searchSuggestions.classList.contains('invisible')) return;

  updateCurrent();
  if (e.deltaY > 0 && current < sections.length - 1)
    scrollToSection(current + 1);
  else if (e.deltaY < 0 && current > 0)
    scrollToSection(current - 1);
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

function updateCurrent() {
  if (footer.getBoundingClientRect().top < window.innerHeight) {
    current = sections.length - 1;
    return;
  }

  for (let i = sections.length - 1; i > 0; i--)
    if (sections[i].getBoundingClientRect().top == 61) {
      current = i;
      return;
    }

  current = 0;
}