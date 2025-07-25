window.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.review-content').forEach(content => {
    const maxLines = 3;
    const lineHeight = parseFloat(getComputedStyle(content).lineHeight);
    const maxHeight = lineHeight * maxLines;

    // 기준 높이보다 콘텐츠가 크면 접고 버튼 표시
    if (content.scrollHeight > maxHeight + 5) {
      content.classList.add('collapsed');
      const moreBtn = content.nextElementSibling;
      if (moreBtn && moreBtn.classList.contains('toggle-more')) {
        moreBtn.style.display = 'inline';
        moreBtn.innerText = '더보기';
      }
    } else {
      // 짧으면 버튼 숨김
      const moreBtn = content.nextElementSibling;
      if (moreBtn && moreBtn.classList.contains('toggle-more')) {
        moreBtn.style.display = 'none';
      }
    }
  });
});

function toggleContent(btn) {
  const content = btn.previousElementSibling;
  if (content.classList.contains('collapsed')) {
    content.classList.remove('collapsed');
    btn.innerText = '접기';
  } else {
    content.classList.add('collapsed');
    btn.innerText = '더보기';
  }
}
