/*
 * Aside menu control 
 */
$(document).ready(function () { // 이벤트 등록
  $('#stateMsgBtn').click(function (e) { // 버튼 클릭 시 발생하는 것
    $('#stateMsgInput').attr({ 'class': 'mt-2' });  // mt-2 done을 제거해 입력창이 보이게 함 
    $('#stateInput').val($('#stateMsg').text()); // 입력창에 stateMsg 내용이 보이게 함 
  });
  // 이벤트 등록-stateMsgSubmit 등에 클릭이 일어나면 ChangeStateMsg 등 작동
  $('#stateMsgSubmit').click(ChangeStateMsg);
  $('#getWeatherButton').click(getWeather);
});

function ChangeStateMsg() {
  let stateInputVal = $('#stateInput').val(); // 사용자가 수정한 글 읽기
  $('#stateMsgInput').attr({ 'class': 'mt-2 d-none' }); // 입력창 감추기

  // AJAX(Asynchronous JS and XML): 화면의 일부분만 바꿀 때 주로 사용 
  $.ajax({
    type: 'GET',        // 서버에 GET 방식으로 보냄
    url: '/abbs/aside/stateMsg',  // 서버 주소 
    data: { stateMsg: stateInputVal }, // 어떤 데이터를 서버로 보낼 것인지 
    success: function (result) { // 재대로 처리 o 
      // 왔는지 안왔는지 확인
      console.log('state message:', stateInputVal, result);
      $('#stateMsg').html(stateInputVal); // stateMsg 바꾸기
    }
  });
}

function getWeather() {
  $.ajax({
    type: 'GET',
    url: '/abbs/aside/weather', // GET으로 서버에서 찾아와라 
    // 서버에서 받아온 result를 화면에 보여줌
    success: function (result) {
      $('#weather').html(result);
    }
  });
}