## Angular  개념정리

## Angular는 세가지 지시자(directive)가 있습니다.

- 컴포넌트 - 템플릿을 가지는 지시자

- 구조 지시자 - DOM 요소를 동적으로 추가하거나 제거하는 지시자

- 속성 지시자 - 요소, 컴포넌트, 다른 지시자의 행동이나 모양을 변경하는 지시자

## 구조 지시자
: DOM 요소를 추가 하거나 제거하는 등 DOM의 레이아웃을 동적으로 변경할 때 사용하는 지시자 입니다.

### NgIf 지시자
- boolean 표현식을 가지며 값에 따라 DOM 요소가 나타나거나 사라집니다.

```javascript
<div *ngIf="hero">{{ hero.name }}</div>
```

위의 예에서는 hero 값이 존재하면 div 요소가 출력되고 값이 null 혹은 undefined라면 요소가 나타나지 않습니다.

### NgSwitch 지시자
: 조건에 따라 DOM 요소를 추가하거나 제거하는 기능을 갖춘 지시자로 switch 문을 이용하여 요소를 조작할 수 있습니다.

```javascript
<div [ngSwitch]='product'>
    <span *ngSwitchCase="'computer'">computer</span>
    <span *ngSwitchCase="'TV'">TV</span>
    <span *ngSwitchCase="'Phone'">Phone</span>
    <span *ngSwitchDefault>default</span>
</div>
```
 
 위의 예에서 ngSwitch의 product 값에 따라 span 태그의 computer, TV, phone 요소가 나타나며 일반적인 프로그래밍 언어에서와 같이 어떤 조건에도 만족하지 않을 경우 ngSwitchDefault문을 이용하여 디폴트인 경우를 처리할 수 있습니다.


### NgFor 지시자
: 배열 형태의 요소를 let {요소} of {배열} 형태의 문법을 이용하여 반복해서 나타나게 하고 싶을 경우 사용할 수 있습니다.

```javascript
<div *ngFor="let hero of heroes; let i=index>
    ({{i}}) {{ hero.name }}
</div>
```

위의 예에서는 ngFor안에 heores 배열에서 요소를 hero 변수에 할당하고 있으며 해당 인덱스 i 변수에 저장하고 있습니다.
heroes 배열의 사이즈 만큼 요소의 인덱스와 값을 출력하는 코드입니다.

## 속성 지시자

: 속성 지시자는 @directive 어노테이션이 붙은 컨트롤러 클래스를 만들어 작성하며 속성을 조작하는데 사용됩니다.

ngClass, ngStyle 지시자가 존재하며 각각 클래스나 스타일 요소를 동적으로 변경하는데 사용할 수 있습니다.


> Angular는 Module 단위로 구성되어 있습니다. 어플리케이션에서 사용하는 모듈은 app.module.ts에 정의합니다. 그리고 가져와서 사용합니다.

## Angular 기본 명령어 모음
Angular build  명령어 

```javascript
npm run build 
```

Angular server 기동 명령어
```javascript
ng serve --open
```


Angular로 만든 프로젝트 구조를 먼저 파악해야 합니다.
가장 처음에 Visual Studio로 프로젝트를 열었을때 나오는 여러 페이지가 있습니다. index.html 파일은 대문같은 역할을 하는 페이지입니다. 이곳에서 app 디렉토리에 있는 app.component.ts에 정의되어 있는 로직을 view에 적용할 수 가 있습니다. 

Anugular에서 Component 생성하는 명령어

```javascript
ng generate component home
```

Angular에서 class 파일을 만드는 명령어

```javascript
ng generate class classes/item // classes 디렉토리 밑에 item  클래스를 생성합니다.
```

Angular service 파일을 만드는 명령어

```javascript
ng generage service services/item
```

이렇게 명령어를 주게 되면 app/ 디렉토리 밑에 생성됩니다.


이렇게 Componet를 생성하면 html, css, spec.ts, component.ts 파일 총 4개의 파일이 생성되는 것을 확인할 수가 있습니다. html 파일은 사용자에게 보여주는 정적인 view 파일이고, css는 이 html을 꾸며주는 파일이라고 생각하면 됩니다. spec.ts 파일은 테스트 파일이고 component.ts에는  해당 Component의 name이 정의되어 있습니다. 이곳에 로직과 데이터를 정의하여 view에 바인딩을 할 수가 있습니다.



## Router의 개념
Router는 navigation의 역할을 하는 녀석이라고 생각하면 편합니다. 길을 안내해주는 것처럼 해당 페이지 간의 이동을 설정할 때 사용하는 요소입니다. 기본적으로 애플리케이션은 화면 하나로만 구성되지 않습니다. 여러 페이지들이 존재하게 되는데 해당 페이지들이 이동하기 위해서는 Router를 사용하면 쉽게 해당 페이지로 이동이 가능합니다. 

라우터는 URL을 사용하여 특정 영역에 어떤 뷰를 보여 줄지 결정하는 기능을 제공합니다.
전통적인 서버사이드 렌더링을 하는 웹 사이트는 주소가 바뀔 때마다 서버에 전체 페이지를 요청하고 전체 페이지를 화면에 렌더링합니다. 매 요청시 전체 페이지를 새로 랜더링하는 것은 비효율적이기 때문에 라우터를 이용하면 필요한 부분만 랜더링을 한다면 효율적일 것입니다.

라우터는 한마디로 URL에 해당하는 컴포넌트를 화면에 노출하고 네비게이션을 할 수 있는 기능을 가지고 있습니다.



## Router 구성 요소

Router - 라우터를 구현하는 객체입니다. Navigate() 함수와 navigateByUrl() 함수를 사용하여 경로를 이동할 수 있습니다.

RouterOulet - 라우터가 컴포넌트를 <router-outlet> 태그에 렌더링 하는 영역을 구현하는 Directive 입니다.

Routes - 특정 URL에 연결되는 컴포넌트를 지정하는 배열입니다.

RouterLink - HTML의 앵커 태그는 브라우저의 URL 주소를 변경하는 것입니다. Angular에서는 RouterLink를 사용하면 라우터를 통해 렌더링 할 컴포넌트를 변경할 수 있습니다.

ActivatedRoute - 현재 동작하는 라우터 인스턴스 객체입니다.


### Routing 테이블 설정

Routing 테이블은 Angular 프로젝트를 생성하게 되면 app 디렉토리 밑에 angular-routing.modules.ts 파일이 생성되는데 이 파일에서 라우팅 정보를 설정할 수 있습니다.

라우터를 사용하기 위해서는 먼저 Router 모듈을 import 해야합니다.
```javascript
import { Routes, RouterModule } from '@angular/router';
```

라우터에서 컴포넌트는 고유의 URL과 매칭됩니다.
URL과 컴포넌트는 아래와 같이 Routes 객체를 설정하여 지정할 수 있습니다.

```javascript
// 가장 먼저 .component.ts에 정의되어 있는 class 명으로 import를 해야합니다.
import { HomeComponent } from './home/home.component';
import { ItemComponent } from './item/item.component';

// const는 변수 타입으로 immutable하고 선언과 동시에 값을 넣어줘야합니다.

const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  { path: 'home', component: HomeComponent},
  { path: 'item', component: ItemComponent}
];
```

현재 path 경로가 존재하지 않으면 디폴트 path에서는 home Component로 노출 되도록 설정되어 있습니다. path가 `item` 일 경우에는 ItemComponent가 노출이 되도록 설정이 되었기 때문에 `item.component.html` 파일의 내용을 보여주게 됩니다.

nav 태그를 보면 a 태그가 존재하고 routerLink 속성에 이동할 경로가 설정된 것을 확인할 수가 있습니다.
이동할 a 태그를 선택하면 routerLink에 설정된 경로에 해당하는 컴포넌트의 뷰가 router-outlet 태그 위치에 변환되어 노출됩니다.

```javascript
<div style="text-align:center">
  <h1>
    Really Welcome to {{ title }}
  </h1>
  <nav>
    <a routerLink='/home'>List</a>
    <a routerLink='/item'>NEW</a>
  </nav>
  <router-outlet>이곳에 Component 내용이 출력!</router-outlet>
</div>
```

## 의존성 주입

Angular 프로젝트에서 view 템플릿과 로직이 명확하게 분리되어 있습니다. 예를들어서 아래와 같은 소스 코드에서 템플릿에 입력 태그를 정의했습니다.

```javascript
<div>
    <label>ID
        <input placeholder="ID"/>
    </label>
</div>
<div>
    <label>Name
        <input placeholder="Name"/>
    </label>
</div> 
<button (click)="saveItem()">Save</button>
```

button 태그를 정의하였고, 해당 버튼을 클릭했을 때 saveItem() 메소드가 호출되도록 하였습니다. 바로 이 saveItem() 로직을 정의하는 곳은 해당 템플릿을 관리하는 Component.ts에서 정의를 하게 됩니다.

```javascript
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router'

export class ItemComponent implements OnInit {
  private editable: boolean = false;

  constructor(private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    let id = this.route.snapshot.paramMap.get('id');

    if(id){
      //update Loginc
      this.editable = true;
    }
  }

  saveItem(){
    if(this.editable){
        // Update

    }else{
      // Save

    }

    this.router.navigateByUrl('/home');
  }
}
```

여기서 @angular/router 모듈로부터 ActivatedRoute, Router 객체를 import 하였습니다. 해당 로직에서 이 객체의 인스턴스를 사용하기 위해서는 import가 선행되어야 합니다.

ActivedRoute는 위에서 설명했듯이 현재 사용중인 Router 객체의 인스턴스입니다. 라우터로 노출할 페이지를 변경했을 때 변수에 따라 화면을 동적으로 처리하고 싶을때 AcitvatedRoute 인스턴스를 사용하여 파라미터 값을 받을 수 있습니다. 현재 위의 로직은 파라미터 id값에 따라 if ~ else로 분기처리를 하였고, 처리가 되었다면 다시 `/home` 경로에 매핑되는 컴포넌트 템플릿을 노출하도록 구현한 코드입니다. 여기서 이 `/home` 경로로 이동하기 위해서 import를 한 Router 객체가 필요하게 됩니다. 

의존성 주입을 설명하기 위해서 쓰잘대없는 서론까지 말하게 되었지만 여기서 정말 말하고 싶은 것은 import를 했다고 해당 객체를 사용할 수 없습니다. `의존성 주입(Dependency Injection)`을 해줘야 사용할 수 있는데 이 부분을 constructor(생성자)를 통해서 의존성  주입을 받을 수 있습니다. 


## 클래스
만약 URL 변수가 여러개이면 어떻게 관리할까요? 하나 하나 각각 받아도 상관없겠지만, 해당 Component로 넘어오는 데이터가 많을 경우 Java처럼 객체로 바인딩하면 더 편리하게 URL 변수 값을 받을 수가 있습니다. 먼저 간단하게 cmd 창에서 아래 명령어로 Item이라는 클래스 파일을 만들어 보겠습니다.

```javascript
ng generate class classes/item
```

실행이 완료되면 app/classes/item.ts 파일이 생성된 것을 확인할 수가 있습니다.

```javascript
export class Item {
    id: string;
    name: string;
    available: boolean;
}
```

타입 스크립트는 자바스크립트 언어로 해석되지만 변수를 선언하는 방식에서 약간 다른부분이 있습니다. `변수명: 타입 정보` 방식으로 변수만 선언한 형태입니다.

item.component.ts 파일에서 item 클래스 파일을 사용하기 위해서 import를 해줘야 합니다.

```javascript
import { Item } from '../classes/item'
private item: Item;
private editable: boolean = false;

constructor(private route: ActivatedRoute, 
              private router: Router) { 
                this.item = {
                  id: '',
                  name: '',
                  available: true
                }
}
```

Item 객체의 id, name은 form으로 부터 값을 받기 때문에 비워두었습니다. 

### Item View
![angular_form](https://user-images.githubusercontent.com/22395934/72679944-cb474480-3af7-11ea-960b-8eabdbd16bb2.PNG)

### item.component.html 코드
```javascript
<div>
    <label>ID
        <input [(ngModel)]="item.id" placeholder="ID"/>
    </label>
</div>
<div>
    <label>Name
        <input [(ngModel)]="item.name" placeholder="Name"/>
    </label>
</div> 
<div>
    <label>Available
        <input [(ngModel)]="item.available" type="checkbox"/>
    </label>
</div> 
<button (click)="saveItem()">Save</button>
<button (click)="deleteItem()">delete</button>
```

이제 item.component.html 파일에서 폼 화면에 값을 입력하고 save 버튼을 클릭하면 item.component.ts의 클래스에서 saveItem() 메소드가 호출되어 자동으로 Item 객체와 바인딩이 됩니다. 물론 ActivatedRoute 인스턴스가 있어야 가능한 일입니다.


## 모듈
Angular는 모듈 단위의 집합으로 구성 된 애플리케이션입니다. 
NgModule는 최상위 모듈로써 모든 모듈을 관리하는 모듈 관리자 같은 역할을 합니다. 그렇기 때문에 필요한 모듈마다 import를 하고 @NgModule 클래스 안에 사용할 모듈을 넣어줘야 합니다.

> 참고로 html 파일에서 ngModel 프로퍼티는 form 모듈에 정의되어 있기 때문에 form 모듈을 정의하지 않고 사용하면 에러가 발생하는 것을 확인 할 수 있습니다. 그렇기 때문에 app.module.ts 파일에 꼭 import를 해줘야 합니다.



## 서비스
서비스는 말 그대로 서비스를 제공합니다. Angular에서는 분업화가 잘 되어 있습니다. 비즈니스 로직을 Component에서 구현할 수 도 있지만 유지보수성과 확장성을 위해서 별도의 서비스 부분을 나눠서 Component에 주입해서 사용할 수 있습니다. 가장 간단하게 Item Service 파일을 만들고 item 타입만 넣을 수 있는 배열 객체를 생성해서 브라우저에서 제공하는 LocalStorage에 넣고 빼는 서비스를 구현해 보겠습니다.

Angular service 파일을 만드는 명령어

```javascript
ng generage service services/item
```

마찬가지로 app/services/ 밑에 item.service.ts 파일이 생성됩니다.

```javascript
// 사용할 모듈들을 import 합니다.
import { Injectable } from '@angular/core';
import { LocalStorageService } from 'ngx-webstorage';
import { Item } from '../classes/item';

// @Injectable 어노테이션으로 인해서 해당 서비스를 주입하여 사용할 수 있습니다.
@Injectable({
  providedIn: 'root'
})
export class ItemService {

  // item 객체를 담을 배열 객체 items 선언
  public items : Item[] = [];

  constructor(private storage: LocalStorageService) { }

    getItems(){
      if(localStorage.getItem('items')){
        this.items = JSON.parse(localStorage.getItem('items'));
      }

    }

    saveItems(){
      localStorage.setItem('items', JSON.stringify(this.items));
    }

    getItem(id: string){
      return this.items.find(item => item.id == id);
    }


    addItem(item: Item){
      this.items.push(item);
      this.saveItems();
    }

    deleteItem(item: Item){
      this.items.splice(this.items.indexOf(item), 1);
      this.saveItems();
    }
  
}
```


index.html은 외부에서 사용하는 웹을 가정해서 만들어진 샘플 코드 입니다.
여기서 app-root라는 태그를 썼는데 이 태그는 app component에 내장된 템플릿입니다.
그럼 우리는 app-test라는 태그를 소유한 test component를 만들었는데 이 녀석을 호출할 수 없을까요?
`결론은 불가능 하다는 것입니다.`

그 이유는 한 모듈에 bootstrap으로 선언한 템플릿만 외부에서 직접적으로 사용할 수 있습니다.
그럼 bootstrap에 여러 컴포넌트를 선언할 수 있을까요?
그렇지도 않습니다. bootstrap에 선언가능한것은 오직 하나뿐입니다.

즉 하나의 컴포넌트만이 bootstrap될 수 있습니다.

나머지 템플릿들은 우리가 루트 컴포넌트를 통해서만 표현할 수 있는 것입니다.



#### 참조: https://lucaskim.tistory.com/45, https://kamang-it.tistory.com/entry/Angular-08angular-modulecomponent-%EB%B0%8F-module-%EB%A7%8C%EB%93%A4%EA%B8%B0

