// 확인
console.log("========================= adminMain.js in =========================");

// 초기화
 // csrf
// const csrfToken  = document.querySelector('meta[name="_csrf"]').getAttribute('content');
// const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
 // 검색 정렬 디브와 카테고리 별 컨텐츠 출력 디브 모두 감싸는 컨테이너
const listContainer = document.querySelector('.list-container');
 // 카테고리 별 컨텐츠 출력 디브, const printContent = document.getElementById('print-content');
  // 페이지네이션의 frag 를 전달하기 위한 hidden input, <input id="frag-input" type="hidden" name="frag"> 
const fragInput = document.getElementById('frag-input');
 // 생성한 쿠폰 별 사용 내역을 조회하기 위한 전역젹 변수 초기화
let dataSetCuponType = 0;
 // 페이지네이션의 filterType 을 전달하기 위한 hidden input <input id="filterType-input" type="hidden" name="filterType">
const filterTypeInput = document.getElementById('filterType-input');
 /** keyword 를 위한 초기화
  * 
  *   > 프래그먼트에 검색바가 포함되어 있기에 검색된 페이지네이션의 페이지 번호를 누르면 
  *     기존 페이지네이션 상태로 돌아감
  * 
  *   > <input id="frag-input" type="hidden" name="frag">, <input id="filterType-input" type="hidden" name="filterType">
  *    과 같이 사용하며 해당 요소 아래에 추가 
  *  
  * */ 
const keywordInput = document.getElementById('keyword');

/** fragInput Change 이벤트 리스너 
 * 
 *  > 각 카테고리 별로 보여지는 정렬 기준을 다르게 하기 위한 이벤트 리스너 
 * 
 *  > 멤버 관리의 전체 유저 보기 - 최신 가입 순 (default), 최신 로그인 순 
 *    고객 문의 관리 - 최신 등록 순 (default)
 *    리뷰 관리 - 최신 신고 순 (default), 신고 많은 순 
 *    예약 관리 - 최신 예약 순 (default)
 *    쿠폰 발급 및 삭제 - 최신 생성 순 (default)
 * 
 *  > 옵션 여러 개를 한 번에 변경 시에는 select.replaceChildren(...) 를 사용해 변경이 가능
 * 
 * */
fragInput.addEventListener('change', function () {

  if(document.querySelector('.sort-detail')){
    const select = document.querySelector('.sort-detail');
    // 변화 시 마다 초기화 
    select.options.length = 0;
    
    // switch는 엄격 비교 (===) 를 하기 때문에 숫자 1과 문자 1을 구분하여 사용해야 함
    switch (fragInput.value) {
      // default 정렬
      case "cupon-delete": case "cupon-manage": case "qna-payment": case "qna-cancel": 
      case "qna-facilities": case "qna-service": case "qna-others": case "payment-completed":
      case "payment-wait": case "payment-cancel": case "reservation-order": case "reservation-lodge": 
      // 최신 순 정렬
      select.add(new Option('최신 순', 'LATEST'));
      break;

      /** 전체 유저 보기가 클릭된 경우
       *
       *  > 최신 가입 순과 최신 로그인 순의 정렬 기준 2개 생성
       * */
      case "all-user":
        // 최신 가입 순 - 변경 전 코드  
        // const sortOption1 = document.createElement('option');
        
        // sortOption1.value="LATEST-SIGN-UP";
        // sortOption1.innerText="최신 가입 순";
        // document.querySelector('.sort-detail').appendChild(sortOption1);

        // 최신 가입 순 - 변경 후 코드 
        select.add(new Option('최신 가입 순', 'LATEST-SIGN-UP'));


        // 최신 로그인 순 - 변경 전 코드
        // const sortOption2 = document.createElement('option');

        // sortOption2.value="LATEST-LOGIN";
        // sortOption2.innerText="최신 로그인 순";
        // document.querySelector('.sort-detail').appendChild(sortOption2);

        // 최신 가입 순 - 변경 후 코드 
        select.add(new Option('최신 로그인 순', 'LATEST-LOGIN'))
        break;
      

      /** 리뷰 관리의 신고 내역이 클릭된 경우
       * 
       *  > 최신 신고 순과 신고 많은 순의 정렬 기준 2개가 생성 
       * */ 
      case "review-report":
        // 최신 신고 순
        select.add(new Option('최신 순','LATEST'));
        // 신고 많은 순
        select.add(new Option('신고 많은 순', 'MOST-REPORTED'));
        break;
     
      default:
        break;
    }
  }
}) 


/** document - DOMContentLoaded */
document.addEventListener('DOMContentLoaded', async () => {
  // 초기화
   // h2 이벤트 리스너 달기 위한 초기화 
  const sidebarH2 = document.querySelectorAll('.tit-txt');


  /** h2 클릭 이벤트 리스너 */ 
  sidebarH2.forEach(e => {
    e.addEventListener('click', () => {
      // 클릭된 h2 의 부모 div (각 카테고리 div) 안에서 <ul class="category-subtopic"> 찾기
      const subList = e.parentElement.querySelector('.category-subtopic');


      /** classList.add() / classList.remove() / classList.toggle() 
       * 
       *  > classList.add() 는 항상 해당 요소에 () 의 클래스를 추가하고 
       *    clasList.toggle() 은 () 의 클래스가 클래스가 없으면 추가 있으면 제거
       * 
       *  >  드롭다운 메뉴를 열기/닫기 용도로 쓰려면 toggle 이 적합하고 단순히 
       *     여는 작업만 하고 싶을 때 (닫을 필요가 없을 때) 는 add 를 사용
       * 
       *  > 기존 코드, subList.classList.toggle('invisible');
       * 
       *  > 접고 펼치는 동작을 조금 더 부드럽게 구현하기 위해 아래와 같이 변경 
       * 
       */
      if(!subList.classList.contains('invisible')){
        fold(subList);
      
        subList.classList.add('invisible');

      } else if(subList.classList.contains('invisible')) { 
          unfold(subList);
          subList.classList.remove('invisible');
      }
      
    })
  })


  /* 생성된 쿠폰만 <li> 로 출력 하기 */
  fetch('/admin/cupon-templates')
  .then(res => { return res.json(); })
  .then(result => {
    // 확인
    console.log("/admin/cupon-templates==========");
    console.log(result);

    if(result.length === 0){ return; }

    // 초기화
    // cupon-ul
    const cuponUl = document.querySelector('.cupon-ul');
    
    result.forEach(e => {
      const li = document.createElement('li'); 
      // cupon-"쿠폰타입" 으로 class 명 추가
      li.classList.add('subtopic-li',`cupon-${e.cuponType}`, 'cupon-manage');
      // data-속성 추가, data-cupon-type="1"
      li.dataset.cuponType = String(e.cuponType);
      li.innerText = e.cuponTitle;

      cuponUl.appendChild(li);
    })
  })
})



/** 문서 클릭 이벤트 리스너 */ 
document.addEventListener('click', async (e) => {
  // 확인
  console.log(`e.target : ${e.target}`);

  // 카테고리 클릭 시
  if(e.target.classList.contains('subtopic-li')){
    // subtopic-li 이벤트 리스너를 위한 초기화
    const subtopicLi = document.querySelectorAll('.subtopic-li');

    // 기존 선택 요소 비활성화
    subtopicLi.forEach(e => e.classList.remove('selected-el'));

    e.target.classList.toggle('selected-el');
  }

  
  /** 쿠폰 생성 (class="cupon-generate") 클릭 이벤트 리스너
   * 
   * 
   */
  if(e.target.matches('li#cupon-generate')){
    try {
      // frag 초기화 (페이지네이션이 필요 없기에 리셋)
      fragInput.value = "";

      // keyword 초기화 
      keywordInput.value = "";

      // filterType 초기화 
      filterTypeInput.value ='';

      // dataSet 초기화 
      dataSetCuponType = 0;

      // 검색 정렬 컨테이너 숨기기, document.querySelector('.search-sort-container').classList.add('display-hidden');

      const frag = await getSectionFragment('cupon-generate'); 

      listContainer.innerHTML = frag;

    } catch (error) {
        console.log(error); 
    }
  }


  /** 이미지 파일 선택 버튼 (cupon-file-btn) 이벤트 리스너 */
  if(e.target.matches('button#cupon-file-btn')){
    // 초기화
    const previewImg = document.querySelector('.cupon-img-preview')

    // 숨겨진 input 클릭
    document.getElementById('cupon-img-input').click();

    // input 에 change Event Listener add
    document.getElementById('cupon-img-input').addEventListener('change', (e) => {
      // 초기화
      const file = e.target.files[0];
       // 파일크기 제한, 20MB
      const maxSize = 1024 * 1024 * 10;

      // 선택된 사항이 없으면 기존의 선택된 이미지로 복원
      if(!e.target.files || e.target.files.length == 0) { return; }

      // 파일 타입 검사 - MIME 타입으로 이미지 파일인지 검사 (image/ 로 시작하면 이미지)
      if(!file.type.startsWith('image/')){

        alert('⚠️ 이미지 파일만 업로드할 수 있습니다.');
        
        previewImg.value = ''; // 선택된 파일 초기화

        previewImg.innerHTML = ''; // 미리보기 초기화

        return;
      }

      // 파일 크기 검사 - 10MB 초과시 경고 
      if(file.size > maxSize){
        alert(`⚠️ ${maxSize}MB 이하의 이미지 파일만 업로드 가능합니다.`);

        previewImg.value = '';

        previewImg.innerHTML = '';

        return;
      }

      /** FileReader API 
       * 
       *  > JS 에서 파일을 읽는 객체로 파일을 읽고 클라이언트 컴퓨터에 파일 저장 가능 
       * 
       *  > 유효한 이미지만 미리보기 로드
       * */  
      const reader = new FileReader();


      /** reader.readAsDataURL(file) 
       * 
       *  > 매개변수에 작성된 파일 (cupon-img-input 의 img 파일) 을 읽어 저장한 뒤 
       *    파일을 나타내는 URL 을 result 속성으로 얻어올 수 있게 하는 메서드
       * 
       */
      reader.readAsDataURL(file);


      // 파일을 다 읽으면
      reader.onload = (e) => {
        // 확인
        console.log(e.target.result);

        // previewImg 의 img src 속성에 저장할 url
        const url = e.target.result;
        
        // previewImg.setAttribute("src", url);
        previewImg.innerHTML = `<img src='${url}' class='preview-img'>`;  
      }
    })
  }


  /** 쿠폰 생성 버튼 (id="cupon-generate-btn") 클릭 이벤트 리스너 
   * 
   *  > JS 로는 에러만 체크하고 <form> 으로 전송
   * 
   * */ 
  if(e.target.matches('button#cupon-generate-btn')){
    // 쿠폰타입이 음수면 리턴
    if(Number(document.getElementById('cuponType').value) <= 0){
      alert('쿠폰 타입은 1 이상이어야 합니다');

      return;
    }

    // 할인 금액이 음수면 리턴
    if(document.getElementById('cuponPrice').value < 0){ 
      alert('금액은 0 이상이어야 합니다..!');

      return; 
    } 

    // 최소 결제 금액이 0 미만이면 리턴 
    if(document.getElementById('theMinimumAmount').value < 0){
      alert('최소 결제 금액은 0 이상이어야 합니다..!');

      return;
    }
    
    // 유효 기간이 오늘날짜보다 이전이면 리턴
    if(new Date(document.getElementById('cuponEndDate').value) < new Date()){
      alert('유효 기간 설정이 잘못되었습니다..!');

      return;
    }

    // 이미지 파일이 선택되지 않았으면 리턴 
    if(!document.getElementById('cupon-img-input').files 
      || document.getElementById('cupon-img-input').files.length === 0){
      alert('이미지 파일을 반드시 추가 해야 합니다..!');

      return;
    }

    // sumbit 버튼을 동적으로 생성 
    const btn = document.createElement('button');
    btn.type = 'submit';
    // <form> 태그의 가장 마지막에 추가
    document.querySelector('.create-form').appendChild(btn);

    btn.click();
  }


  /** 쿠폰 발급 및 삭제 <li> (class="cupon-delete") 클릭 이벤트 리스너 */
  if(e.target.matches('li#cupon-delete')){
    try {
      // frag 초기화 
      fragInput.value = "";

      // keyword 초기화 
      keywordInput.value = "";

      // filterType 초기화 
      filterTypeInput.value ='';

      // dataSet 초기화 
      dataSetCuponType = 0;
      
      const frag = await getPagingSectionFragment('cupon-delete'); 

      // 프래그먼트 교체
      listContainer.innerHTML = frag;

      // frag 값 초기화
      fragInput.value = "cupon-delete";

      // 수동으로 document.getElementById('frag-input') 의 change 이벤트 발생
      fragInput.dispatchEvent(new Event('change'));

    } catch (error) {
        console.log(error); 
    }
  } 


  /** 쿠폰 삭제 카테고리에서의 쿠폰 삭제 버튼 클릭 이벤트 리스너 */ 
  if(e.target.matches('button#cupon-delete-btn')){
    console.log("============== e.target.matches('button#cupon-delete-btn') ==============");
    
    // th:data-ct-id 에서 ctId 가져오기
    const ctId = e.target.dataset.ctId;

    /** 삭제 버튼 클릭 시 가장 가까운 tr 삭제
     * 
     *  > ?. 는 옵셔널 체이닝(optional chaining) 연산자로 예를 들어 설명하녀 tr?.remove(); 가 있을 때, 
     *    tr?.remove() 의 뜻은 tr가 null 또는 undefined가 아니면 remove()를 호출하고
     *    그렇지 않으면 아무 것도 하지 않고 undefined 를 돌려 에러를 막는다는 의미
     * */ 
    e.target.closest('td').closest('tr').remove();
    
    // 확인
    console.log(`ctId : ${ctId}`);
    
    try {
      console.log("============== e.target.matches('button#cupon-delete-btn')'s try ==============");
      
      // CuponTemplate TALBE 에서 해당 쿠폰 유형 삭제 
      fetch(`/admin/ct-del?ct-id=${ctId}`,{
        method : 'DELETE'
        // csrf 미사용 + 인증 쿠키가 없다면 method:delete 만으로 동작 
        // headers : {
        //   [csrfHeader] : csrfToken
        // }
      })
      .then(resp => { return resp.text(); } )
      .then(result => {
        if(result == 1){


          alert('삭제가 성공적으로 완료되었습니다..!');
        } 
      })

    } catch (error) {
        console.log("============== e.target.matches('button.cupon-delete-btn')'s catch ==============");
        console.log(error);
    }
  }


  /** 페이지네이션 버튼 클릭 이벤트 리스너 */
  if(e.target.matches('a.page-link')){
    // 초기화
     // th:data-page 에서 pageNo 가져오기
    const pageNo = e.target.dataset.page;
     // frag 초기화
    const frag = fragInput.value;
     // qty
    let qty = 10;
     // sortType
    const sortTypeValue = document.querySelector('.sort-detail').value || '';
     /** keyword 
      * 
      *   > 기존의 const keywordValue = document.getElementById('keyword').value || ''; 는 요소가 아예 없는 경우 
      *     null 로 인해 에러가 발생 (아예 없을 일은 없긴 함) 
      * 
      *   > ?.(옵셔널 체이닝): 요소가 없으면 undefined 를 반환해서 에러를 막음
      * 
      *   > ??(널 병합): null/undefined일 때만 우측 값을 사용 (빈 문자열은 그대로 유지)
      * */ 
    const keywordValue = keywordInput?.value ?? '';
     // dataSet
    const dataSet = dataSetCuponType || 0;
     // filterType
    const filterType = filterTypeInput.value || '';

    // 필수 파라미터 에러 처리
    if(!frag){ return; }

    try {
      // 확인
      console.log("========== a.page-link clicked ==========");

      /** getPagingSectionFragment() 의 파라미터와 defaultValue
       * 
       *  > frag, pageNo = 1, qty = 10, sortTypeValue='', keywordValue='', dataSet=0, filterType=''
       * 
       *  > 변화가 있을 때 마다, frag 는 항상 전달 
       *  
       * */  
      const result = await getPagingSectionFragment(frag, pageNo, qty, sortTypeValue, keywordValue, dataSet, filterType);
      
      listContainer.innerHTML = result;

      // 수동으로 document.getElementById('frag-input') 의 change 이벤트 발생
      fragInput.dispatchEvent(new Event('change'));

    } catch (error) {
      console.log(`===== e.target.matches('a.page-link') 의 ERROR =====`);
      console.log(error);
    }
  } 


  /** 검색 버튼 (search-btn) 클릭 이벤트 리스너 */
  if(e.target.matches('button#search-btn')){
    // 호출
    try {
      // keyword 초기화 
      keywordInput.value = "";

      // dataSet 초기화 
      dataSetCuponType = 0;
      keywordInput.value = document.getElementById('keyword-send-input').value;
      
      // frag 가져오기
      const frag = fragInput.value;

      const result = await getPagingSectionFragment(frag);

      listContainer.innerHTML = result;

      // 수동으로 document.getElementById('frag-input') 의 change 이벤트 발생
      fragInput.dispatchEvent(new Event('change'));

    } catch (error) {
        console.log("============= e.target.matches('button#search-btn) =============");
        console.log(error);
    } 
  }


  /** 생성한 쿠폰의 <li> (class="cupon-manage", data-cupon-type="쿠폰타입") 클릭 이벤트 리스너 */
  if(e.target.matches('li.cupon-manage')){
    try {
      // frag 초기화 
      fragInput.value = "";

      // keyword 초기화 
      keywordInput.value = "";

      // filterType 초기화 
      filterTypeInput.value ='';

      // dataset 가져오기
      const dataSet = e.target.dataset.cuponType;
       // 전역적 설정
      dataSetCuponType = dataSet;

      const result = await getPagingSectionFragment('cupon-manage', 1, 10, '', '', dataSet, '');
      
      listContainer.innerHTML = result;

      fragInput.value="cupon-manage"

      // 수동으로 document.getElementById('frag-input') 의 change 이벤트 발생
      fragInput.dispatchEvent(new Event('change'));

    } catch (error) {
        console.log("=============== e.target.matches('li.cupon-manage') ===============");
        console.log(error);
    }
  }


  /* 일괄 발급 (class="all-issuance") 클릭 이벤트 리스너 */
  if(e.target.matches('button.all-issuance')){
    try {
      console.log("================== e.target.matches('button.all-issuance')'s try ==================");

      // dataset 으로 가져온 ctId 를 서버에 전송
      const dataSetCtId = e.target.dataset.ctId;
    
      fetch(`/admin/cupon-all-gen?ct-id=${dataSetCtId}`,{
        method: 'POST'
        
        /** Content-Type 
         * 
         *  > Content-Type 은 내가 보내는 바디의 타입을 의미 (Reqeuest Body's Type) 하기에 쿼리스트링 
         *    사용 시 Cotent-Type 불필요 
         * 
         *  > CSRF 미사용 + 인증/쿠키도 없는 경우 method: 'POST' 만으로 충분
         * 
         *  > CSRF 사용 시 csrfHeader 에 csrfToken 을 담아 전송하면 됨  
         * */ 
        // 'Content-Type' : 
        // [csrfHeader] : csrfToken
        
      })
      .then(resp => { return resp.text(); })
      .then(result => {
        if(result == "1"){ 
          alert('쿠폰 발급 완료..!');
        }
      })
    } 
      catch (error) {
        console.log("================== e.target.matches('button.all-issuance')'s catch ==================");
        console.log(error);

    }
  } 

  
  /** 전체 유저 보기 <li> (id="all-user") 클릭 이벤트 리스너 */
  if(e.target.matches('li#all-user')){
    // 
    try {
      console.log("====================== e.target.matches('li#all-user')'s try ======================");
      
      // frag 초기화 
      fragInput.value = "";

      // keyword 초기화 
      keywordInput.value = "";

      // filterType 초기화 
      filterTypeInput.value ='';

      // dataSet 초기화 
      dataSetCuponType = 0;

      // sortType 초기화 - 정렬 기준이 default 와 다른 <li> 는 이 작업이 꼭 필요 안 그럼 두번 눌러야됨
      if(document.querySelector('.sort-detail')){
        document.querySelector('.sort-detail').value='';
      }

      const result = await getPagingSectionFragment("all-user");
      
      listContainer.innerHTML = result;
      
      fragInput.value = "all-user";

      // 수동으로 document.getElementById('frag-input') 의 change 이벤트 발생
      fragInput.dispatchEvent(new Event('change'));

    } 
      catch (error) {
        console.log("====================== e.target.matches('li#all-user')'s catch ======================");
        console.log(error);
    }
  }


  /* 고객 문의 관리 - 결제 (<li class="qna-payment">) 클릭 이벤트 리스너 */
  if(e.target.matches('li.qna-payment')){
    try {
      console.log("====================== e.target.matches('li.qna-payment')'s try ======================");
      
      // frag 초기화 
      fragInput.value = "";

      // keyword 초기화 
      keywordInput.value = "";

      // filterType 초기화 
      filterTypeInput.value ='';

      // dataSet 초기화 
      dataSetCuponType = 0;

      const result = await getPagingSectionFragment("qna-payment");

      listContainer.innerHTML = result;

      fragInput.value = "qna-payment";

      // 수동으로 document.getElementById('frag-input') 의 change 이벤트 발생
      fragInput.dispatchEvent(new Event('change'));

    } 
      catch (error) {
        console.log("====================== e.target.matches('li.qna-payment')'s catch ======================");
        console.log(error);
    }
  }


  /* 고객 문의 관리 - 환불 (<li class="qna-cancel">) 클릭 이벤트 리스너 */
  if(e.target.matches('li.qna-cancel')){
    try {
      console.log("====================== e.target.matches('li.qna-cancel')'s try ======================");
      
      // frag 초기화 
      fragInput.value = "";

      // keyword 초기화 
      keywordInput.value = "";

      // filterType 초기화 
      filterTypeInput.value ='';

      // dataSet 초기화 
      dataSetCuponType = 0;

      const result = await getPagingSectionFragment("qna-cancel");

      listContainer.innerHTML = result;

      fragInput.value = "qna-cancel";

      // 수동으로 document.getElementById('frag-input') 의 change 이벤트 발생
      fragInput.dispatchEvent(new Event('change'));

    } 
      catch (error) {
        console.log("====================== e.target.matches('li.qna-cancel')'s catch ======================");
        console.log(error);
    }
  } 


  /* 고객 문의 관리 - 시설 (<li class="qna-facilities">) 클릭 이벤트 리스너 */
  if(e.target.matches('li.qna-facilities')){
    try {
      console.log("====================== e.target.matches('li.qna-facilities')'s try ======================");
      
      // frag 초기화 
      fragInput.value = "";

      // keyword 초기화 
      keywordInput.value = "";

      // filterType 초기화 
      filterTypeInput.value ='';

      // dataSet 초기화 
      dataSetCuponType = 0;

      const result = await getPagingSectionFragment("qna-facilities");

      listContainer.innerHTML = result;

      fragInput.value = "qna-facilities";

      // 수동으로 document.getElementById('frag-input') 의 change 이벤트 발생
      fragInput.dispatchEvent(new Event('change'));

    } 
      catch (error) {
        console.log("====================== e.target.matches('li.qna-facilities')'s catch ======================");
        console.log(error);
    }
  } 


  /* 고객 문의 관리 - 서비스 (<li class="qna-service">) 클릭 이벤트 리스너 */
  if(e.target.matches('li.qna-service')){
    try {
      console.log("====================== e.target.matches('li.qna-service')'s try ======================");
      
      // frag 초기화 
      fragInput.value = "";

      // keyword 초기화 
      keywordInput.value = "";

      // filterType 초기화 
      filterTypeInput.value ='';

      // dataSet 초기화 
      dataSetCuponType = 0;

      const result = await getPagingSectionFragment("qna-service");

      listContainer.innerHTML = result;

      fragInput.value = "qna-service";

      // 수동으로 document.getElementById('frag-input') 의 change 이벤트 발생
      fragInput.dispatchEvent(new Event('change'));

    } 
      catch (error) {
        console.log("====================== e.target.matches('li.qna-service')'s catch ======================");
        console.log(error);
    }
  } 


  /* 고객 문의 관리 - 기타 (<li class="qna-others">) 클릭 이벤트 리스너 */
  if(e.target.matches('li.qna-others')){
    try {
      console.log("====================== e.target.matches('li.qna-others')'s try ======================");
      
      // frag 초기화 
      fragInput.value = "";

      // keyword 초기화 
      keywordInput.value = "";

      // filterType 초기화 
      filterTypeInput.value ='';

      // dataSet 초기화 
      dataSetCuponType = 0;

      const result = await getPagingSectionFragment("qna-others");

      listContainer.innerHTML = result;

      fragInput.value = "qna-others";

      // 수동으로 document.getElementById('frag-input') 의 change 이벤트 발생
      fragInput.dispatchEvent(new Event('change'));

    } 
      catch (error) {
        console.log("====================== e.target.matches('li.qna-others')'s catch ======================");
        console.log(error);
    }
  } 

  
  /* 고객 문의 관리 - 미등록된 답변 보기 (<button class="incompleted-qna">) 클릭 이벤트 리스너 */
  if(e.target.matches('button.incompleted-qna')){
    try {
      console.log("====================== e.target.matches('button.incompleted-qna')'s try ======================");
      // 초기화
      filterTypeInput.value = ''; filterTypeInput.value = "incompleted-qna";
       // frag
      const frag = fragInput.value;
       // filterType
      const filterType = filterTypeInput.value;

      if(document.querySelectorAll('.sort-detail')){
        document.querySelectorAll('.sort-detail').value ='';
      }

      dataSetCuponType = 0;

      const result = await getPagingSectionFragment(frag, 1, 10, '', '', 0, filterType);

      listContainer.innerHTML = result;

      // 수동으로 document.getElementById('frag-input') 의 change 이벤트 발생
      fragInput.dispatchEvent(new Event('change'));

    } 
      catch (error) {
        console.log("====================== e.target.matches('button.incompleted-qna')'s catch ======================");
        console.log(error);
    }
  }


  /* 고객 문의 관리 - 삭제 버튼 (<button id="qna-delete-btn">) 클릭 이벤트 리스너 */
  if(e.target.matches('button#qna-delete-btn')){
    try{
      // bno 가져오기
      const bno = e.target.dataset.bno;
      // 확인
      console.log(`bno : ${bno}`);

      fetch(`/admin/qna-del?bno=${bno}`,{
        method : 'DELETE'
      })
      .then(resp => { return resp.text(); })
      .then(result => {
        if(result == "1"){
          /** 삭제 버튼 클릭 시 가장 가까운 tr 삭제
           * 
           *  > ?. 는 옵셔널 체이닝(optional chaining) 연산자로 예를 들어 설명하녀 tr?.remove(); 가 있을 때, 
           *    tr?.remove() 의 뜻은 tr가 null 또는 undefined가 아니면 remove()를 호출하고
           *    그렇지 않으면 아무 것도 하지 않고 undefined 를 돌려 에러를 막는다는 의미
           * */ 
          e.target.closest('td').closest('tr').remove();

          alert('삭제가 성공적으로 완료되었습니다..!');
        }
      })
    }
      catch(error){
        console.log(error);
    }
  } 


  /* 결제 관리 - 사이드바 결제 완료 내역 (<li class="payment-completed">) 클릭 이벤트 리스너 */
  if(e.target.matches('li.payment-completed')){
    try {
      console.log("====================== e.target.matches('li.payment-completed')'s try ======================");

      // frag 초기화 
      fragInput.value = "";

      // keyword 초기화 
      keywordInput.value = "";

      // filterType 초기화 
      filterTypeInput.value ='';

      // dataSet 초기화 
      dataSetCuponType = 0;

      const result = await getPagingSectionFragment('payment-completed');

      listContainer.innerHTML = result;

      // frag 해당 요소로 초기화
      fragInput.value = 'payment-completed';

      // select 초기화 
      fragInput.dispatchEvent(new Event('change'));

    } 
      catch (error) {
        console.log("====================== e.target.matches('li.payment-completed')'s catch ======================");
        console.log(error);
    }
  } 


  /* 결제 관리 - 사이드바 환불 완료 내역 (<li class="payment-cancel">) 클릭 이벤트 리스너 */
  if(e.target.matches('li.payment-cancel')){
    try {
      console.log("====================== e.target.matches('li.payment-cancel')'s try ======================");

      // frag 초기화 
      fragInput.value = "";

      // keyword 초기화 
      keywordInput.value = "";

      // filterType 초기화 
      filterTypeInput.value ='';

      // dataSet 초기화 
      dataSetCuponType = 0;

      const result = await getPagingSectionFragment('payment-cancel');

      listContainer.innerHTML = result;

      // frag 해당 요소로 초기화
      fragInput.value = 'payment-cancel';

      // select 초기화 
      fragInput.dispatchEvent(new Event('change'));

    } 
      catch (error) {
        console.log("====================== e.target.matches('li.payment-cancel')'s catch ======================");
        console.log(error);
    }
  } 


  /* 리뷰 관리 - 사이드바 신고 내역 (<li class="review-report">) 클릭 이벤트 리스너 */
  if(e.target.matches('li.review-report')){
    try {
      console.log("====================== e.target.matches('review-report')'s try ======================");

      // frag 초기화 
      fragInput.value = "";

      // keyword 초기화 
      keywordInput.value = "";

      // filterType 초기화 
      filterTypeInput.value ='';

      // dataSet 초기화 
      dataSetCuponType = 0;

      // sortType 초기화 - 정렬 기준이 default 와 다른 <li> 는 이 작업이 꼭 필요 안 그럼 두번 눌러야됨
      if(document.querySelector('.sort-detail')){
        document.querySelector('.sort-detail').value='';
      }

      const result = await getPagingSectionFragment('review-report');

      listContainer.innerHTML = result;

      // frag 해당 요소로 초기화
      fragInput.value = 'review-report';

      // select 초기화 
      fragInput.dispatchEvent(new Event('change'));

    } 
      catch (error) {
        console.log("====================== e.target.matches('li.review-report')'s catch ======================");
        console.log(error);
    }
  } 
  

  /* 리뷰 관리 - 삭제 버튼 (<button id="review-delete-btn">) 클릭 이벤트 리스너 */
  if(e.target.matches('button#review-delete-btn')){
    try{
      // bno 가져오기
      const reviewId = e.target.dataset.reviewId;
      // 확인
      console.log(`bno : ${reviewId}`);

      fetch(`/admin/review-del?r-no=${reviewId}`,{
        method : 'DELETE'
      })
      .then(resp => { return resp.text(); })
      .then(result => {
        if(result == "1"){
          /** 삭제 버튼 클릭 시 가장 가까운 tr 삭제
           * 
           *  > ?. 는 옵셔널 체이닝(optional chaining) 연산자로 예를 들어 설명하녀 tr?.remove(); 가 있을 때, 
           *    tr?.remove() 의 뜻은 tr가 null 또는 undefined가 아니면 remove()를 호출하고
           *    그렇지 않으면 아무 것도 하지 않고 undefined 를 돌려 에러를 막는다는 의미
           * */ 
          e.target.closest('td').closest('tr').remove();

          alert('삭제가 성공적으로 완료되었습니다..!');
        }
      })
    }
      catch(error){
        console.log(error);
    }
  } 


  /* 예약 내역 관리 - 예약 내역 클릭 이벤트 리스너 */
  if(e.target.matches('li.reservation-order')){
    try {
      console.log("====================== e.target.matches('reservation-order')'s try ======================");

      // frag 초기화 
      fragInput.value = "";

      // keyword 초기화 
      keywordInput.value = "";

      // filterType 초기화 
      filterTypeInput.value ='';

      // dataSet 초기화 
      dataSetCuponType = 0;

      const result = await getPagingSectionFragment('reservation-order');

      listContainer.innerHTML = result;

      // frag 해당 요소로 초기화
      fragInput.value = 'reservation-order';

      // select 초기화 
      fragInput.dispatchEvent(new Event('change'));

    } 
      catch (error) {
        console.log("====================== e.target.matches('li.reservation-order')'s catch ======================");
        console.log(error);
    }
  }  


  /* 예약 내역 관리 - 숙소 별 정보 클릭 이벤트 리스너 */
  if(e.target.matches('li.reservation-lodge')){
    try {
      console.log("====================== e.target.matches('reservation-lodge')'s try ======================");

      // frag 초기화 
      fragInput.value = "";

      // keyword 초기화 
      keywordInput.value = "";

      // filterType 초기화 
      filterTypeInput.value ='';

      // dataSet 초기화 
      dataSetCuponType = 0;

      const result = await getPagingSectionFragment('reservation-lodge');

      listContainer.innerHTML = result;

      // frag 해당 요소로 초기화
      fragInput.value = 'reservation-lodge';

      // select 초기화 
      fragInput.dispatchEvent(new Event('change'));

    } 
      catch (error) {
        console.log("====================== e.target.matches('li.reservation-lodge')'s catch ======================");
        console.log(error);
    }
  }   
});



/** getPagingSectionFragment() - 페이지네이션이 필요한 HTML 프래그먼트 가져오기 
 * 
 *  > Controller 의 페이지네이션 매핑 형식 
 *  (/admin/list?frag=value&pageNo=value&qty=value&sortType=value&keyword=value&dataset=value&filterType=value) 에
 *  맞게 파라미터 전달
 * 
 *  > 별도의 페이지 이동 없이 해당 페이지에서 이동하기 위해 파라미터 초기화
 * */
async function getPagingSectionFragment(frag, pageNo = 1, qty = 10, sortTypeValue='', keywordValue='', dataSet=0, filterType='') {
  // 초기화
  let result; 

  // 
  if(document.getElementById('keyword-send-input') && document.querySelector('.sort-detail')){
    // 변경 사항이 있는 파라미터 가져오기
     // 검색창의 키워드
    keywordValue = document.getElementById('keyword').value;
     // 정렬 기준 
    sortTypeValue = document.querySelector('.sort-detail').value;


    // 쿠폰 요소 클릭 시 
    if(dataSetCuponType != 0){
       // 변경 사항이 있는 파라미터 가져오기
        // 검색창의 키워드
        keywordValue = document.getElementById('keyword').value;

         // 정렬 기준 
        sortTypeValue = document.querySelector('.sort-detail').value;

      // 확인
      console.log("============= getPagingSectionFragment() 의 결과 (dataSet 의 값이 있는 경우) =============");

      const resp = await fetch(`/admin/list?frag=${frag}&pageNo=${pageNo}&qty=${qty}&sortType=${sortTypeValue}&keyword=${keywordValue}&dataSet=${dataSetCuponType}&filterType=${filterType}`);
      // 확인
      console.log(resp);

      result = await resp.text();
    } 
      else {
        const resp = await fetch(`/admin/list?frag=${frag}&pageNo=${pageNo}&qty=${qty}&sortType=${sortTypeValue}&keyword=${keywordValue}&dataSet=${dataSet}&filterType=${filterType}`);
        // 확인
        console.log(resp);
    
        result = await resp.text();
    
        // 확인
        console.log("============= getPagingSectionFragment() 의 결과 (검색창과 정렬 셀렉트 박스가 있는 경우) =============");
        // console.log(result);
    }
  }  
    else {
      // 확인
      console.log("============= getPagingSectionFragment() 의 결과 (검색창과 정렬 셀렉트 박스가 없는 경우) =============");

      // const resp = await fetch(`/admin/list?frag=${frag}`);
      const resp = await fetch(`/admin/list?frag=${frag}&pageNo=${pageNo}&qty=${qty}&sortType=${sortTypeValue}&keyword=${keywordValue}&dataSet=${dataSet}&filterType=${filterType}`);

      // 확인
      console.log(resp);

      result = await resp.text();
  }
   
  return result;
}


/** getSectionFragment() - 페이지네이션이 필요 없는 HTML 프래그먼트 가져오기 */
async function getSectionFragment(name){
  // 기존에 있는 CuponTemplate 의 Record 가져오기 
  const resp = await fetch(`/admin/fragments/${name}`);

  const result = await resp.text();

  return result;
}


/** unfold() - 드롭다운 구현 함수로 펼칠 때 사용하는 함수  */ 
function unfold(el){
  el.style.height = '0px';

  /** 현재 높이 (0) 를 실제 컨텐츠 높이로 변환 
   * 
   *  > element.scrollHeight 는 인터페이스의 읽기 전용 속성으로 오버플로로 인해 표시되지 않는 콘텐츠를 포함하여 
   *    요소 콘텐츠의 높이를 반환하며 해당 값은 요소가 세로 스크롤바 없이 모든 콘텐츠를 표시하는 데 
   *    필요한 최소 높이를 나타냄
   * 
   * */ 
  
  const targetHeight = el.scrollHeight;


  // transition 이 끝나면 height 제거 (auto 상태로 변환)
  el.addEventListener('transitionend', function te() {
    el.style.height = 'auto';
    el.removeEventListener('transitionend', te);
  })

  
 /** requestAnimationFrame()
   * 
   *  > requestAnimationFrame()은 브라우저가 다음 화면 그리기 (리페인트) 를 준비할 때 콜백 함수를 실행하도록 
   *    예약해 주는 Web API 함수
   * 
   *  > requestAnimationFrame(callback) 이 호출되면 브라우저는 다음 프레임을 그리기 직전에 callback 을 호출
   * 
   *  > 브라우저 리페인트 타이밍에 맞춰서 실행되므로 setTimeout 이나 setInterval 보다 더 매끄러운 움직임을 
   *    얻을 수 있음
   * 
   *  > 화면 갱신 주기(보통 초당 60프레임)에 맞춰 호출되므로 불필요한 레이아웃 계산이나 리페인트를 줄여줌
   * 
   *  > requestAnimationFrame(() => { el.style.height = min_height + 'px'; })
   * */ 
  requestAnimationFrame(() => {
    // inline 스타일로 height 설정 
    el.style.height = targetHeight + 'px';
  })
}


/** fold() - 드롭다운 구현 함수로 접을 때 사용하는 함수 */
function fold(el){
  // 시작 높이 (만약, auto 상태라면 먼저 scrollHeight 를 픽셀값으로 고정)
  el.style.height = el.scrollHeight + 'px';
 
  requestAnimationFrame(() => { el.style.height = '0px'; })
  
  /** min-height 값을 숫자로 파싱하는 방법
   * 
   *  > 다음과 같은 방법은 CSS 에 min-height 를 지정 (고정값 사용) 한 뒤 사용하는 방법 
   * 
   *  > e.g., const min_Height = parseFloat(getComputedStyle(el).minHeight);
   * 
   * */ 
}