
## JQuery on("click")과 click()의 차이점


on("click") 과 click()의 가장 큰 차이점은 동적으로 이벤트를 바인딩할 수 있는지의 차이입니다.
on("click")은 동적으로 가능하고 click()은 최초에 선언된 element에만 동작합니다.

아래 예제 코드를 살펴보면서 차이점을 알아보겠습니다.


```html
<div class="page">
  <ul class="pagination">
    <li>1번</li>
    <li>2번</li>
    <li>3번</li>
  </ul>
</div>
```

## click() 이벤트

위 예제 소스에서 ul 태그의 하위에 있는 li 태그들을 클릭하면 아래 함수가 실행됩니다.


```javascript
$('#pagination').children().click(function () { 
  $(this).remove(); 
}); 
```

그러면 클릭한 li 태그에 바인딩된 click 이벤트가 실행되어 해당 li 태그가 remove() 될 것입니다.


여기서 아래 소스 처럼 동적으로 새로운 li 태그를 추가한 뒤,

```javascript
$('#pagination').append('<li> New li tag </li>'); 
```

새로 추가된 `<li> New li tag </li>` 를 클릭하면 click() 메소드는 동작하지 않습니다.

왜냐하면 click() 이벤트는 최초에 페이지를 로딩할 때 선언되어 있던
element에 이벤트를 바인딩하고 나서는 더이상 동적으로 바인딩을 하지 않기 때문입니다.



## on("click") 이벤트

이럴 경우에는 on("click") 이벤트를 사용하면 동적으로 이벤트를 바인딩 시킬 수 있습니다.

아래 예제 코드 처럼 부모 태그인 'pagination'의 이벤트를 자식 태그인 li 들에게
delegate 시키는 방식으로 메소드를 구현하면 동적으로 추가된 태그에게 이벤트를 줄수 있다.

```javascript
$('#pagination').on('click', 'li', function(event) { 
  $(event.target).remove() 
}); 
```


즉, pagination 아래에 추가되는 태그는 모두 부모의 on("click")이벤트를 물려 받게 되는 것입니다.
자바로 빗대어 표현하면 상속과 비슷한 개념입니다.


여기서 아래 소스 처럼 동적으로 새로운 li 태그를 추가한 뒤,

```javascript
$('#pagination').append('<li> New li tag </li>'); 
```

새로 추가된 `<li> New li tag </li>` 를 클릭하면 .on ("click") 이벤트가 동작하여 해당 태그는 remove() 됩니다.



#### 참조: https://lookingfor.tistory.com/entry/JQuery-%ED%81%B4%EB%A6%AD-%EC%9D%B4%EB%B2%A4%ED%8A%B8-onclick-%EA%B3%BC-click-%EC%9D%98-%EC%B0%A8%EC%9D%B4
