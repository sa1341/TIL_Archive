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

```javascript
const routes: Routes = [ 
    { path: '', component: MainComponent }, 
    { path: 'product-list', component: ProductListComponent },
    { path: '**', component: ErrorComponent }
]
```

위에서 설정한 routes를 RouterModule에 인자로 전달하면 등록이 됩니다.

```javascript
@NgModule({
  imports: [ BrowserModule,
      RouterModule.forRoot(routes)
  ],
  ...
})
export class AppModule { }
```

아래의 템플릿에서는 모듈에 등록한 라우터를 사용하고 있습니다.
nav 태그를 보면 안에 a 태그가 존재하고 routerLink 속성에 이동할 경로가 설정된 것을 확인할 수 있습니다. 
이동할 a태그를 선택하면 routerLink에 설정된 경로에 해당하는 컴포넌트의 뷰가 router-outlet 태그 위치에 변환되어 노출됩니다.

```javascript
<h1>Angular Router</h1>
<nav>
    <a routerLink="">main</a>
    <a routerLink="/product-list">product list</a>
</nav>
<router-outlet></router-outlet>
```

## Router 데이터 전달

라우터 설정시 이동할 대상 컴포넌트에 파라미터 인자나 데이터를 전달할 수 있습니다.


## 라우터 인자 추출

아래의 설정을 보면 ProductDetailComponent에 맵핑된 URL에 :/productId와 같이 동적인 path를 가진 파라미터가 존재한다.


```javascript
RouterModule.forRoot([
	{ path : '', component : HomeComponent },
	{ path : 'products/:productId', component : ProductDetailComponent }
])]
```

ProductDetailComponent에서는 전달된 productId에 따라 노출되는 상품정보가 변한다고 가정하고 이 데이터를 추출하는 방법을 알아보겠습니다.

첫번째로 아래와 같이 ActivatedRoute 객체를 의존성 주입을 받아야 합니다. 

```javascript
export class ProductDetailComponent implements OnDestroy {
	
	productId: string;

	constructor (route : ActivatedRoute) {
		this.productId = route.snapshot.params['productId'];
	}
}
```


그리고 snapshot.params 속성에서 추출할 인자를 설정하면 URL에서 인자를 추출할 수 있습니다. 
추출한 productId를 이용하여 서버에 상품정보를 요청하는 코드를 다음에 작성할 수 있습니다.

```javascript
export class ProductDetailComponent implements OnDestroy {
	
	productId: string;

	constructor (route : ActivatedRoute) {
		this.productId = route.snapshot.params['productId'];
	}
}
```

## 라우터 데이터 추출

라우터에는 라우터를 설정하는 시점에 컴포넌트에 데이터를 전달할 수 있습니다. data 프로퍼티를 이용하여 가능합니다.

data 프로퍼티:
- 키-값 형태로 이루어진 배열

- 라우터를 설정하는 시점에 컴포넌트로 데이터를 전달

- 해당 컴포넌트에 접근시 ActivatedRoute 객체에 저장

```javascript
RouterModule.forRoot([
	{ path : '', component : HomeComponent },
	{ path : 'products/:productId', component : ProductDetailComponent, data : [{ isProd : true}] }
])]

```

라우터 인자와 같이 ActivatedRoute 객체에 저장되며 data 속성에서 추출이 가능합니다.


```javascript
export class ProductDetailComponent implements OnDestroy {
	
	productId: string;
	isProdEnvironment : string;

	constructor (route : ActivatedRoute) {
		this.productId = route.snapshot.params['id'];
		this.isProdEnvironment = route.snapshot.data[0]['isProd'];
	}
}
```

## 자식 라우팅

Angular 애플리케이션은 부모-자식 관계로 구성된 컴포넌트 집합입니다.

자식 컴포넌트는 부모 컴포넌트와 상관없이 독자적인 의존성을 주입 받을 수 있으며 자식 컴포넌트 밖에서 이루어지는 라우팅과 별개로 동작하도록 자식 컴포넌트에 대한 라우팅 설정이 가능합니다.

아래 예제 코드를 보면 기본 path에 HomeComponent와 ProductDetailComponent가 존재하고 ProductDetailComponent에 자식으로 childeren 속성이 존재하는 것을 확인할 수 있습니다.


```javascript
const routes: Routes = [ 
    { path: '', component: MainComponent }, 
    { path: 'product-list', component: ProductListComponent, 
      children: [
                    { path: '', component: ProductDescriptionComponent },
                    { path: 'seller/:id', component: SellerInfoComponent }
      ] 
    }
```

children 속성에는 디폴트 path 상품 설명을 나타내는 ProductDescriptonComponent가 존재하고 seller/:id path에 SellerInfoComponent가 존재합니다.

이 경우 ProductDetailComponent가 렌더링 될 때 기본적으로 상품 설명 컴포넌트가 렌더링이 같이 되고 그 안에서 특정 버튼 혹은 이벤트가 발생하여 path가 seller로 변경되면 그 위치에 판매자 정보 컴포넌트가 노출될 것이라고 생각할 수 있습니다.


![Untitled Diagram](https://user-images.githubusercontent.com/22395934/80908044-77cfce00-8d57-11ea-8c51-44f1bad23762.png)

## Route Guard

Angular에서는 Servlet에서의 filter와 유사한 개념의 Route Guard를 제공합니다. Route Guard를 이용하면 다음과 같은 기능을 제공할 수 있습니다.

- 라우터를 통해 들어오거나 나갈 때 유효성을 검증합니다.

- 사용자 인증 여부를 확인하고 인증하지 않았다면 인증하게 합니다.

- 라우터에서 나갈 때 저장되지 않은 정보를 사용자에게 알립니다.

Route Guard를 설정하기 위해서는 @angular/router 패키지의 CanActivate, CanDeActivate 인터페이스를 구현해야 합니다.

각각의 인터페이스에는 canActivate() 함수와 canDeactivate() 함수가 존재하여 true/false 값을 반환합니다.

canActivate와 canDeactivate는 배열 형태로 받을 수 있어 단계별로 유효성을 검증하는 것이 가능합니다.

다음은 ProductDetailComponent 진입시 로그인 가드를 설정한 예입니다. 로그인 가드에서는 로그인 유/무를 체크하는 로직이 있다고 가정합니다.



```javascript
const routes : Routes = [
	{ path : '', component : HomeComponent },

	{ path : 'products/:productId', component : ProductDetailComponent,
	  
                       canActivate : [LoginGuard], 

	  children : [
	  	{ path : '', component : ProductDescriptionComponent },

	  	{ path : 'seller/:id', component : SellerInfoComponent },
	  ] 

	}
]
```

## 라우팅 영역 여러 개 만들기

Angular에서는 기본 라우팅 영역 외에 추가 라우팅 영역을 만들 수 있습니다.
추가 라우팅 영역은 name 속성을 지정하여 구분할 수 있으며 URL 상에서는 괄호로 둘러싸여 표현됩니다.

```javascript
<router-outlet></router-outlet>
<router-outlet name="chat"></router-outlet>
```

```javascript
http://localhost:8080/#/home/aux:chat)
```

URL 경로에서 home은 기본 라우팅이고, 괄호안에 있는 chat은 추가 라우팅을 의미합니다.


출처: https://lucaskim.tistory.com/45 [Lucas Kim]
