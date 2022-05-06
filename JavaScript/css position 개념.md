## css 위치 정렬

html을 심도있게 공부하지 않았기 때문에 이미지 간혹 웹 UI 화면을 개발할 때 이미지 파일을 배치가 필요한데... 항상 구글링을 하면서 대걍 이미지를 때려박도록 개발한 경험이 있기 때문에 오늘은 html/css에서 기본적으로 레이아웃을 배치할 때 유용한 프로퍼티인 position 프로퍼티에 대해서 살펴보았습니다.

position:static은 초기값으로 위치를 지정하지 않을 때와 같습니다.
즉, 이미지 파일이 있을 경우 아무것도 속성을 주지 않았지만, 기본적으로 position:static으로 프로퍼티가 잡혀있습니다.

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

## margin, padding

padding: border의 안쪽에 대한 여백

- px, pt, cm, % -  지정 단위, pt, px는 고정 값
- padding-bottom - 아래쪽에 대한 여백
- padding-left - 왼쪽에 대한 여백
- padding-right - 오른쪽에 대한 여백
- padding-top - 위쪽에 대한 여백


margin: border의 바깥쪽에 대한 여백

- auto, px, pt, cm, % - 지정단위. auto는 브라우저가 자동으로 계산하는 값
- margin-bottom - 아래쪽 여백
- margin-left - 왼쪽 여백
- margin-right - 오른쪽 여백
- margin-top - 위쪽 여백
- margin:0 auto - 중앙정렬


#### border, padding, margin은 width에 영향을 줍니다.
#### margin은 상하좌우 여백이 겹쳐질 경우 가장 큰 값으로 정해집니다.
#### block, inline에 따라 정렬방법이 다릅니다.


## [block / inline 정렬의 차이점]

ex) 박스 가운데 데이터를 정렬 시키려면

1) inline - inline 요소는 부모에 정렬 값을 준다.

    부모 박스의 height값과 동일한 line-height 값을 지정해주면
    line-height의 속성으로 가운데 위치하게 된다.

2) block - 부모 박스의 line-height 값을 글자 크기만큼 지정해준 후,
   
    div 값에는 박스의 height값에서 line-height값을 뺀 후
    가운데 위치해야 하니 2로 나눈 후 그 값을 마진 값으로 설정해주고
    margin:0 auto를 선언.



## [border / padding / margin 값 설정]

border:10px                    - 상/하/좌/우 10px 적용
border: 10px, 30px              - 상하 10px, 좌우30px 적용
border: 10px, 30px, 20px       - 상10px, 좌우30px, 하20px 적용 
border: 10px, 20px, 5px, 30px  -  상10px, 우20px, 하5px, 좌30px 적용






#### 참조 블로그: https://aboooks.tistory.com/82, https://www.zerocho.com/category/CSS/post/5864f3b59f1dc000182d3ea1
