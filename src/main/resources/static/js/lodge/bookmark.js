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
        headers: {
          'content-type': 'application/json; charset=utf-8'
        },
        body: JSON.stringify({
          userId: userId,
          lodgeId: bookmark.dataset.id
        })
      }).then(resp => resp.text())
      .then(result => {
        if (result == 0) removeBookmark(bookmark.dataset.id);
        else if (result == 1) addBookmark(bookmark.dataset.id);
        else alert('오류가 발생했습니다.');

        bookmarkServing = false;
      });
    }
    return;
  }
});

function addBookmark(lodgeId) {
  for (const bookmark of document.querySelectorAll('.bookmark'))
    if (bookmark.dataset.id == lodgeId)
      bookmark.classList.add('selected');
}
function removeBookmark(lodgeId) {
  for (const bookmark of document.querySelectorAll('.bookmark'))
    if (bookmark.dataset.id == lodgeId)
      bookmark.classList.remove('selected');
}

export default function init(id) {
  userId = id;
};