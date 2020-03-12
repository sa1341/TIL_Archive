# Selector 종류


`$("셀렉터").html()`
- 셀렉터태그내에 존재하는 자식태그을 통째로 읽어올때 사용되는 함수
※ 태그 동적추가할때 주로 사용되는 함수

```javascript
ex) html()
<div id="getTag">
    <h3>11111111<br/><span>22222</span>333333333</h3>
</div>

$(function(){
    var getTag = $("#getTag").html();
    alert(getTag);
});
```
즉, 자식 태그의 <h3>11111111<br/><span>22222</span>333333333</h3> 까지 모두 출력됩니다.


`$("셀렉터").text()`
- 셀렉터태그내에 존재하는 자식태그들 중에 html태그는 모두 제외 한 채 문자열만 출력하고자 할때 사용되는 함수
※ html태그까지 모두 문자로 인식시켜주는 함수


```javascript
ex) text()
<div id="getTag">
    <h3>11111111<br/><span>22222</span>333333333</h3>
</div>

$(function(){
       var getText = $("#getTag").text();
      alert(getText);
});

// 출력 결과: 1111111122222333333333  <- html 태그를 제외한 value만 뽑아옵니다.
```

`$("셀렉터").val()`
- INPUT 태그에 정의된 value속성의 값을 확인하고자 할때 사용되는 함수


이미지 경로 불러올 때 주의사항
target/ 디렉토리 밑으로 이미지 파일이 실제로 존재하는지 항상 확인하기. 빌드가 제대로 안되서 업로드가 안될 수도 있기 때문입니다.


#### 출처: https://hellogk.tistory.com/88 [IT Code Storage]
