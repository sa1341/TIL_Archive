## Angular


## Angular에서 사용하는 용어 정리

Webpack은 응용 프로그램 소스 코드를 chunk로 묶어 브라우저에서 로드하도록 하는 도구이며 인기있는 모듈 번들러입니다.

웹펙은 강력한 모듈 번들러이고, 번들은 함께 포함된 Asset을 통합하는 JavaScript 파일입니다. 그리고 하나의 요청으로 클라이언트에 제공되어야 합니다. Bundle에는 JavaScript, CSS style, HTML 및 거의 모든 종류의 파일이 포함될 수 있습니다.

WebPack은 응용 프로그램 소스 코드를 훑어서, import 문을 찾아 의존성 그래프를 작성하고, 하나 이상의 Bundle을 제공합니다. Webpack은 플러그인과 Rule을 통해 TypeScript, SASS, LESS 파일과 같은 JavaScript 파일이 아닌 파일을 사전 처리 및 축약이 가능합니다. 

자바 스크립트 엔진: 자바스크립트 코드를 실행하는 프로그램 또는 인터프리터입니다. 하지만 인터프리터로만 구현되지 않고 JIT 컴파일러로도 구현할 수도 있습니다. 대표적으로 구글이 C++ 언어로 만든 V8 엔진이 있습니다. 여러 목적으로 자바스크립트 엔진을 사용하지만, 대체적으로 웹 브라우저에서 사용됩니다.

타입 스크립트 : 타입 스크립트는 자바스크립트의 상위 확장입니다. TypeScript를 사용하는 가장 큰 이유는 `정적타입을 지원합니다.`, `객체지향 프로그래밍, 도구의 지원(IDE), ES6 지원`등 많은 것을 지원받을 수 있는 언어 입니다.
`타입 스크립트 컴파일러(tsc) 파일(.ts)를 자바스크립트 파일로 트랜스 파일링 합니다.`
컴파일은 일반적으로 소스코드를 바이트 코드로 변환하는 것을 의미하고, 트랜스 파일링은 TypeScript 파일을 자바스크립트 파일로 변환하므로 컴파일러보다는 트랜스파일링이 보다 적절한 표현입니다.


## Angular 컴포넌트

Angular CLI는 컴포넌트를 생성하는 명령어가 있습니다.-*

```java
ng genrate component home
```

기본적으로 위 명령어를 수행하면 home이라는 폴더를 생성합니다. 컴포넌트는 URL의 경로의 단위가 될수 있기 때문에 폴더로 구분됩니다. 

- src/app/home 폴더에 4개의 파일이 생성합니다.

  - home.component.html: 컴포넌트 템플릿을 위한 HTML 파일

  - home.component.css: 컴포넌트 템플릿의 스타일링을 위한 CSS 파일
  
  - home.component.ts: 컴포넌트 클래스 파일

  - home.component.spec.ts: 컴포넌트 유닛 테스트를 위한 스펙 파일

- 루트 모듈 src/app/app.module.ts에 새롭개 생성된 컴포넌트를 등록합니다.
컴포넌트 클래스를 import하고 @ngModule 데코레이터 declarations 프로퍼티에 컴포넌트 클래스를 등록합니다.


### 명령어 정리

NodeJS 버전 확인 : node -v
npm 버전 확인 : npm -v
AngularCLI로 프로젝트 만드는 명령어 : npm new 프로젝트명(angularPetlist)
Angular 프로젝트 실행시키는 명령어 : ng serve --open

Angular의 장점은 반응형 웹이라는 장점입니다. 스마트폰이나 데스크탑과 같이 화면 크기에 영향을 받지 않고 스스로 반응하여 화면에 맞게 데이터를 적절하게 보여줍니다. 
