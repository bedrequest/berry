console.log(redirectTo);

document.addEventListener('click', (e) => {
  const socialBtn = e.target.closest('.social-btn');

  if(socialBtn){
    // 기존 링크 동작 차단
    e.preventDefault();

    // 카카오 로그인 버튼을 클릭한 경우
    if(socialBtn.classList.contains('kakao-btn')){
      const form = document.createElement('form');
      form.method = 'GET';
      form.action = '/oauth2/authorization/kakao';
      form.style.display = 'none';

      const inputRedirectTo = document.createElement('input');
      inputRedirectTo.type = 'hidden';
      inputRedirectTo.name = 'redirectTo';
      inputRedirectTo.value = redirectTo;
      
      form.appendChild(inputRedirectTo);

      document.body.appendChild(form);
      form.submit();
    }

    // 네이버 로그인을 클릭하는 경우 
    if(socialBtn.classList.contains('naver-btn')){
      const form = document.createElement('form');
      form.method = 'GET';
      form.action = '/oauth2/authorization/naver';
      form.style.display = 'none';

      const inputRedirectTo = document.createElement('input');
      inputRedirectTo.type = 'hidden';
      inputRedirectTo.name = 'redirectTo';
      inputRedirectTo.value = redirectTo;
      
      form.appendChild(inputRedirectTo);

      document.body.appendChild(form);
      form.submit();
    }

    // 구글 로그인을 클릭하는 경우
    if(socialBtn.classList.contains('google-btn')){
      const form = document.createElement('form');
      form.method = 'GET';
      form.action = '/oauth2/authorization/google';
      form.style.display = 'none';

      const inputRedirectTo = document.createElement('input');
      inputRedirectTo.type = 'hidden';
      inputRedirectTo.name = 'redirectTo';
      inputRedirectTo.value = redirectTo;
      
      form.appendChild(inputRedirectTo);

      document.body.appendChild(form);
      form.submit();
    }
  }
})
