## css position

html을 심도있게 공부하지 않았기 때문에 이미지 간혹 웹 UI 화면을 개발할 때 이미지 파일을 배치가 필요한데... 항상 구글링을 하면서 대걍 이미지를 때려박도록 개발한 경험이 있기 때문에 오늘은 html/css에서 기본적으로 레이아웃을 배치할 때 유용한 프로퍼티인 position 프로퍼티에 대해서 살펴보았습니다.

position:static은 초기값으로 위치를 지정하지 않을 때와 같습니다.
즉, <img src="./assets/custom/btn.png" /> 이미지 파일이 있을 경우 아무것도 속성을 주지 않았지만, 기본적으로 position:static으로 프로퍼티가 잡혀있습니다.

## position:relative

position:relative은 위치 계산을 할 때 static의 원래 위치부터 계산합니다.

위(top), 아래(bottom), 좌(left), 우(right)의 위치를 같이 설정할 수도 있습니다.

기본적으로 top, bottom, left, right 프로퍼티 값을 px로 주는 경우 현재 위치를 기준으로 이동합니다. 기존에 top이 30px, left가 50px에 위치한 이미지 태그에 아래와 같이 값을 줍니다.

```html
<img src="./assets/custom/btn.png" style="position:relative; top:5px; left:5px" />
```

html 기준으로 top 35px, left 55px 만큼 이동하게 됩니다.


## position:absolute

position:absolute은 relative와 달리 문서의 원래 위치와 상관없이 위치를 지정할 수 있습니다. 하지만 가장 가까운 상위 요소를 기준으로(단, static은 제외) 위치가 결정됩니다.
상위 요소가 없으면 html을 기준으로 설정됩니다.

#### 참조 블로그: https://aboooks.tistory.com/82, https://www.zerocho.com/category/CSS/post/5864f3b59f1dc000182d3ea1
