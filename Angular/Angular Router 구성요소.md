
# Router 구성 요소


라우터는 URL을 사용하여 특정 영역에 어떤 뷰를 보여 줄지 결정하는 기능을 제공합니다.
전통적인 서버사이드 렌더링을 하는 웹 사이트는 주소가 바뀔 때마다 서버에 전체 페이지를 요청하고 전체 페이지를 화면에 렌더링합니다.


매 요청시 전체 페이지를 새로 랜더링하는 것은 비효율적이기 때문에 라우터를 이용하여 필요한 부분만 랜더링을 한다면 효율적일 것입니다. 
라우터는 URL에 해당하는 컴포넌트를 화면에 노출하고 네비게이션을 할 수 있는 기능을 가지고 있다.


- Router – 라우터를 구현하는 객체이다. Navigate() 함수와 navigateByUrl() 함수를 사용하여 경로를 이동할 수 있습니다.

- RouterOulet – 라우터가 컴포넌트를 <router-outlet>태그에 렌더링하는 영역을 구현하는 Directive 입니다.

- Routes – 특정 URL에 연결되는 컴포넌트를 지정하는 배열입니다.

- RouterLink – HTML의 앵커 태그는 브라우저의 URL 주소를 변경하는 것입니다. 앵귤러에서 RouterLink를 사용하면 라우터를 통해 렌더링할 컴포넌트를 변경할 수 있습니다.

- ActivatedRoute – 현재 동작하는 라우터 인스턴스 객체입니다.

## Router 설정

라우터를 사용하기 위해서는 먼저 Router 모듈을 import 해야 합니다.

```javascript
import { RouterModule, Routes } from '@angular/router';
```

라우터에서 컴포넌트는 고유의 URL과 매칭됩니다.
URL과 컴포넌트는 아래와 같이 Routes 객체를 설정하여 지정할 수 있습니다.



아래의 예에서는 디폴트 path에서는 MainComponent가 노출이 되고 product-list path에서는 ProductListComponent가 노출이 되도록 설정을 한 것을 볼 수 있습니다.

출처: https://lucaskim.tistory.com/45 [Lucas Kim]
