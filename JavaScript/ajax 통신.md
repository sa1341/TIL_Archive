
ajax로 서버와 통신을 하여 request param으로 받기 위해서 아래와 같이 클라이언트 코드를 작성할 수 있습니다.
post 방식으로 데이터를 전송하면 get 방식으로 데이터를 전송할 때 인코딩을 할 필요가 없다는 장점이 있습니다.

```javascript
var Parms  = '?id=' + id;
Parms += '&code=' + code;
Parms += '&uid=' + uid;
Parms += '&pass=' + pass;
  
$.ajax({
  url: '/member/memberAction.asp',
  type: 'post', //post,get,등..전송방식
  dataType: 'text',//데이타 타입
  data: { mode : "login", code : code, uid : uid, pass : pass },
  success: function(data){  
}
  
// url에 이어서 전송
$.ajax({
  url: '/member/memberAction.asp'+Parms,
  type: 'post',
  dataType: 'text',
  success: function(data){
}
```


##### 출처: https://blog.goodkiss.co.kr/entry/jQuery-ajax-사용시-Parameter-처리방법-주의점 [ollagaza's story]
