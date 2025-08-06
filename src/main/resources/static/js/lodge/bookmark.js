// list와 detail의 북마크 버튼 관리

let bookmarkServing = false;
let userId;

document.addEventListener('click', e => {
  const bookmark = e.target.closest('.bookmark');
  if (bookmark) {
    if (bookmarkServing) alert('북마크 처리중입니다.');
    else {
      bookmarkServing = true;
      fetch('/user/toggleBookmark', {
        method: 'post',
        body: JSON.stringify({
          userId: userId,
          lodgeId: bookmark.dataset.id
        })
      }).then(resp => resp.text())
      .then(result => {
        if (result == 0) e.classList.remove('selected');
        else if (result == 1) e.classList.add('selected');
        else alert('오류가 발생했습니다.');

        bookmarkServing = false;
      });
    }
    return;
  }
});

export default function init(id) {
  userId = id;
};