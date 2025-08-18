console.log("============================== cuponList.js in ==============================");


/** 최소 결제 금액을 만족하는 쿠폰만 출력 
   *  
   *  > document.getElementById('cupon-select2').innerHTML 에 <option> 으로 삽입)
   * 
   *  */ 
cuponList.forEach(v => {
  // CuponTemplate TABLE 에서 CuponType 으로 쿠폰 정보 조회 
  fetch(`/payment/cupon-info?cupon-type=${v.cuponType}`)
  
  /** 최소 결제 금액을 만족하는 경우 셀렉트에 출력 
   * 
   *  > `<option value=${result.cuponPrice} data-cupon-id="v.cuponId">result.cuponTitle</option>`
   * */ 
  .then(resp => { 
    if(resp.ok){
      return resp.json(); 
    } 

    return;
  })

  .then(result => {
    // 확인 
    console.log(`========================= fetch(/payment/cupon-info?cupon-type=${v.cuponType}) =========================`);
    console.log(result);

    // 동적으로 <li> 생성
    const li = document.createElement('li');
   
    li.textContent = `${result.cuponTitle}`;
    
    document.querySelector('.cuponList-ul').appendChild(li);
  })
})