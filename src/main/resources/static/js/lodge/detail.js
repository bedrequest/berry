import Swiper from 'https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.mjs';

// 0. 화면의 요소 불러오기
const [lodgeImageArea, roomArea, facility, locationArea, reviewArea] = ['lodgeImageArea', 'roomArea', 'facility', 'location', 'reviewArea'].map(e => document.getElementById(e));
const [navTop, navRoom, navFacility, navLocation, navReview] = ['navTop', 'navRoom', 'navFacility', 'navLocation', 'navReview'].map(e => document.querySelector('.' + e));
let lodgeDTO; // init을 통해 외부에서 초기화

// 1. 스크롤 대응
// 1-1. 내비게이션 바
addScroll(navTop, lodgeImageArea);
addScroll(navRoom, roomArea);
addScroll(navFacility, facility);
addScroll(navLocation, locationArea);
addScroll(navReview, reviewArea);

// 1-2. areaUnderTitle
addScroll(document.querySelector('.reviewPickUpArea'), reviewArea);
addScroll(document.querySelector('.facilityPreview'), facility);
addScroll(document.querySelector('.locationPreview'), locationArea);

// 1-3. 전역 : 화면 안에 들어온 영역 중 가장 마지막 것을 현재 영역으로 지정
// 화면 안에 들어옴 : top이 window.innerHeight 미만
let currentArea = navTop;
currentArea.classList.add('current');

let ticking = false;
function updateCurrentArea() {
    const [topRoom, topFacility, topLocation, topReview] =
        [roomArea, facility, locationArea, reviewArea].map(e =>
            e.getBoundingClientRect().top
        );

    let newArea;
    if (topReview <= 130) newArea = navReview;
    else if (topLocation <= 130) newArea = navLocation;
    else if (topFacility <= 130) newArea = navFacility;
    else if (topRoom <= 130) newArea = navRoom;
    else newArea = navTop;

    if (currentArea != newArea) {
        currentArea.classList.remove('current');
        newArea.classList.add('current');
        currentArea = newArea;
    }
    ticking = false;
}
document.addEventListener('scroll', () => {
    if (!ticking) {
        ticking = true;
        requestAnimationFrame(updateCurrentArea);
    }
});
updateCurrentArea();

// 스크롤 함수
function addScroll(click, target) {
    if (!(click instanceof HTMLElement) ||
        !(target instanceof HTMLElement)) return;

    click.addEventListener("click", () => {
        const offset = 130;
        const position = target.getBoundingClientRect().top + window.pageYOffset;
        const offsetPosition = position - offset;

        window.scrollTo({
            top: offsetPosition,
            behavior: "smooth"
        });
    });
}

// ----------------------------------------------
// 2. 모달 관리
const [modal, modalCloseBtn, modalMain, roomNavBar, imageSlide, modalFooter, modalPrevBtn, modalNextBtn, modalRoomTitle]
    = ['modal', 'modalCloseBtn', 'modalMain', 'roomNavBar', 'imageSlide', 'modalFooter', 'modalPrevBtn', 'modalNextBtn', 'modalRoomTitle'].map(e => document.getElementById(e));
let swiper = null;
let currentImageIndex = null, maxImageIndex = null;

document.addEventListener('click', e => {
    const target = e.target;

    // 모달 on/off 관리
    if (target.classList.contains('image') ||
        target.classList.contains('moreImagesBtn')) openModal(target);
    else if (!target.closest('#modalBody')) closeModal();

    // 모달 방향 버튼
    if (target.closest('.swiper-button-prev') && currentImageIndex > 0)
        select(Number(currentImageIndex) - 1);
    if (target.closest('.swiper-button-next') && currentImageIndex < maxImageIndex)
        select(Number(currentImageIndex) + 1);

    // 모달 푸터 아이템 관리
    const footerItem = target.closest('.modalFooterItem');
    if (footerItem) select(Number(footerItem.dataset.idx));
});

// 전체/객실 선택
const [showLodgeImages, showRoomImages] = ['showLodgeImages', 'showRoomImages'].map(e => document.getElementById(e));
showLodgeImages.addEventListener('click', () => {
    if (showLodgeImages.classList.contains('selected')) return;
    loadLodgeImages();
});
showRoomImages.addEventListener('click', () => {
    if (showRoomImages.classList.contains('selected')) return;
    loadRoomImages(0);
})
document.querySelectorAll('.roomNavBarItem').forEach(e => {
    e.addEventListener('click', () => {
        loadRoomImages(e.dataset.idx);
    });
});

// 모달 버튼
modalPrevBtn.addEventListener('click', () => {
    select(currentImageIndex - 1);
});
modalNextBtn.addEventListener('click', () => {
    select(currentImageIndex + 1);
});
modalCloseBtn.addEventListener('click', closeModal);

// 모달 컨트롤러
function openModal(target) {
    document.body.classList.add('modal-open');
    modal.classList.add('open');
    swiper = new Swiper('.swiper', {
        pagination: {
            el: '.swiper-pagination',
            type: 'custom',
            renderCustom: function (swiper, current, total) {
                return `<div><span>${current}</span><span class="current">/</span><span>${total}</span><div>`;
            }
        }
    });

    const room = target.closest('.room');
    if (room) loadRoomImages(Number(room.dataset.roomindex));
    else loadLodgeImages(Number(target.dataset.idx));
}

function closeModal() {
    if (swiper && !swiper.destoyed) {
        swiper.removeAllSlides();
        swiper.destroy(true, true);
    }
    modal.classList.remove('open');
    document.body.classList.remove('modal-open');
}

function loadLodgeImages(idx = 0) {
    currentImageIndex = null;
    maxImageIndex = null;
    modalRoomTitle.innerText = '';

    modalMain.classList.remove("roomModal");
    updateScrollItems(null);
    updateFooterItems(null);
    select(idx);

    showLodgeImages.classList.add('selected');
    showRoomImages.classList.remove('selected');
    roomNavBar.style.display = 'none';
}

function loadRoomImages(idx) {
    currentImageIndex = null;
    maxImageIndex = null;
    selectRoom(idx);

    modalMain.classList.add('roomModal');
    updateScrollItems(idx);
    updateFooterItems(idx);
    select(0);

    showLodgeImages.classList.remove('selected');
    showRoomImages.classList.add('selected');
    roomNavBar.style.display = 'flex';
}

function selectRoom(idx) {
    let selected;
    const rooms = document.querySelectorAll('.roomNavBarItem');
    for (let i = 0; i < rooms.length; i++) {
        if (rooms[i].dataset.idx == idx) {
            selected = rooms[i];
            modalRoomTitle.innerText = rooms[i].innerText;
            selected.classList.add('selected');
        } else rooms[i].classList.remove('selected');
    }

    scroll(selected, document.getElementById('roomNavBarContainer'));
}

// 모달 서비스
function updateScrollItems(roomIndex) {
    swiper.removeAllSlides();
    const items = createScrollItems(roomIndex);
    swiper.appendSlide(items);
}

function createScrollItems(roomIndex) {
    let items = [];
    let target = roomIndex == null ? lodgeDTO.lodgeImages : lodgeDTO.rooms[roomIndex].roomImageUrls;
    maxImageIndex = target.length - 1;

    for (const src of target)
        items.push(`<div class="swiper-slide"><img src="${src}"></div>`);
    return items;
}

function updateFooterItems(roomIndex) {
    let target = roomIndex == null ? lodgeDTO.lodgeImages : lodgeDTO.rooms[roomIndex].roomImageUrls;

    imageSlide.innerHTML = '';
    for (let i = 0; i < target.length; i++)
        imageSlide.innerHTML += `<li class="autoSlideItem modalFooterItem" data-idx="${i}"><img src="${target[i]}"></li>`;

    const placeholder = document.createElement('li');
    placeholder.classList.add('autoSlideItem', 'modalFooterItem', 'placeholder');
    imageSlide.appendChild(placeholder);
}

function select(idx) {
    if (currentImageIndex == idx) return;
    currentImageIndex = idx;

    const footerItems = document.querySelectorAll('.modalFooterItem');
    let choose;

    for (const footerItem of footerItems) {
        if (footerItem.dataset.idx == idx) {
            choose = footerItem;
            footerItem.classList.add('selected');
        } else footerItem.classList.remove('selected');
    }

    swiper.slideTo(idx);
    scroll(choose, modalFooter);

    modalPrevBtn.disabled = currentImageIndex == 0;
    modalNextBtn.disabled = currentImageIndex == maxImageIndex;
}

function scroll(target, container) {
    const itemRect = target.getBoundingClientRect();
    const containerRect = container.getBoundingClientRect();

    const offset = itemRect.left - containerRect.left;
    const scroll = offset - (containerRect.width / 2) + (itemRect.width / 2);

    container.scrollBy({
        left: scroll,
        behavior: 'smooth'
    });
}
// ----------------------------------------------

// 주소 복사
document.getElementById('addressCopy').addEventListener('click', () => {
    navigator.clipboard
        .writeText(document.getElementById('addressText').innerText)
        .then(() => {
            const addressCopySuccess = document.getElementById('address-copy-success');
            addressCopySuccess.style.opacity = 1;

            setTimeout(() => {
                addressCopySuccess.style.opacity = 0;
            }, 1500);
        });
});

// ----------------------------------------------

export default function init(data) {
    lodgeDTO = data;
};