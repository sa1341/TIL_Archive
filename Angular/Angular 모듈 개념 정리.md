# Angular 모듈

Angular 모듈에 대해서 알기전에 간단하게 사전지식으로 Annotation이라는 것을 알아봤습니다. Java 언어에서 사용해봤던 어노테이션과 마찬가지로 Angular에서도 어노테이션은 중요한 개념입니다. Annotation의 용도는 Class를 어떤 용도로 사용할 것인지를 정해주는 용도입니다.

Annotation은 class위에 @를 사용해서 정의합니다.

- class에 @Component를 사용하면 해당 class는 component가 됩니다.
- class에 @NgModule를 사용하면 해당 class는 Module이 됩니다.
- class에 @Directive를 사용하면 해당 class는 Directive가 됩니다.

- Annotation은 class 용도를 좀 더 잘 설명하기 위해서 metadata를 사용하여 좀 더 상세하게 정의합니다.


## Angular에서 Module이란?

1. 특정 기능에 대하여 관련된 component나 service, directive 등을 편하게 사용하기 위해 하나로 모은 것입니다.

즉, 특정한 기능을 사용하기 위해 관련된 모든 것을 모아 놓은 것이라고 볼 수 있습니다.
예를 들면 배송 모듈이나 결제 모듈을 대표적입니다.

2. 생성방법은 @NgModule 어노테이션을 class에 선언함으로써 해당 class를 모듈로 만들 수 있습니다.

```javascript
@NgModule({
    imports : [BrowserModule],
    declarations : [AngularPetComponent],
    bootstrap: [AngularPetComponent]
})
export class AppModule {}
```

3. @Module Annotaion에서 사용 가능한 메타데이터 구성 요소

- declarations
    - 현재 모듈에 포함시킬 view class들을 지정합니다.
    (view class 종류로는 component, directive, pipe가 존재)

- exports
    - declarations에서 정의한 현재 모듈의 구성요소를 다른 모듈의 템플릿에서 사용할 수 있도록 노출시킬 view class들을 지정(이 곳에서 지정안한 뷰 클래스들은 사용 불가)

- imports
    - 다른 모듈에서 노출(exports)한 view class들을 사용하기 위해 해당 module을 import 합니다.

 - providers
    - 공통 서비스의 등록, 등록된 서비스는 앱의 모든 부분에서 사용 가능합니다.

- bootstrap
    - 메인 애플리케이션 view로 사용될 root component를 지정합니다. root module에서만 지정 가능 합니다.



## Angular에서 component란?

1.정의
- Angular 애플리케이션을 구성하는 기본 요소입니다.
- Angular 애플리케이션은 모듈 안에 Component로 계층을 구성해서 만들기 때문에 모든 앱은 최소 하나의 root module과 하나의 root Component를 가집니다.
- 모듈의 bootstrap 항목으로 지정된 component가 root component 입니다.
위 코드에서 보면 AngularPetComponent가 root component 입니다.

2.구성
- view: 화면을 구성하는 부분
- class: component의 동작을 구성하는 부분

3. 생성 방법
- @Component 어노테이션을 사용하여 class를 component로 만듭니다.
- annotation안에는 화면 관련 부분을 구현합니다.
- class 부분에는 component의 동작 관련 부분을 구현 합니다.

```java
// 컴포넌트 선언
@Component({ // view 부분
    selector : 'hello-world',
    template : '<h1>Hello {{ name }}!</h1>'.
styles : ['h1 {background-color: red;}']
})
class AngularComponent { // 동작을 구성하는 class 부분
    name : string;

    constructor () {
        this.name = 'Angular';
    }
}
```

4. @Component Annotation에서 사용 가능한 메타데이터 구성 요소

- selector
    - HTML에서 어느 곳을 Component로 대체 할지 구분하기 위한 용도로 사용합니다.
    - CSS의 selector와 유사합니다.
    - selector가 '<pet-world>'일 경우 다음 HTML 태그를 안다면 해당 부분을 Component로 교체합니다.

    ```javascript
    <pet-world></pet-world>
    ```
    - Angular라는 다른 애플리케이션의 selector 또는 HTML 요소와 충돌을 방지하기 위해 접두사(prefix)를 추가하여 케밥 표기법(하이폰으로 단어를 연결하는 표기법)으로 selector를 명명하도록 권장하고 있습니다.


- template
    - selector로 교체된 부분에 보여질 HTML 코드를 입력합니다.

- templateUrl
    -  selector로 교체된 부분에 보여질 HTML 파일 경로를 입력합니다.
- style
    - selector로 교체된 부분에 스타일을 적용합니다.

- styleUrls
    - selector로 교체된 부분에 스타일 코드의 파일 경로를 입력합니다.


## Angular에서 디렉티브(Directive)란?

1. 정의
- HTML 엘리먼트에 사용자가 원하는 동작을 추가하기 위해 사용합니다.
- view가 빠진 component라고 이해하면 편합니다.

2. 생성방법
- @Directive 어노테이션을 class에 붙여서 사용합니다.

```javascript
@Directive({
    selector: 'input[log-directive]',
    host: {
        'input' : 'onInput($event)'
    }
})
class LogDirective {
    onInput(event) {
        console.log(event.target.value);
    }
}
```
3. @Directive 어노테이션에서 사용 가능한 메타데이터 구성 요소

- selector
    - 템플릿에서 이 지시문을 식별하고 지시문의 인스턴스화를 트리거하는 CSS 선택기입니다.

- inputs
    - 지시문에 대한 데이터 바인딩 입력 속성 집합을 열거합니다.


- outputs	
    - 이벤트 바인딩 된 출력 특성 세트. 출력 속성이 이벤트를 내 보내면 템플릿의 해당 이벤트에 연결된 이벤트 처리기가 호출됩니다.

- providers	
    - 이 지시문 또는 구성 요소의 인젝터를 종속성 공급자에 매핑되는 토큰으로 구성 합니다.

- exportAs	
    - 이 지시어를 변수에 할당하기 위해 템플릿에서 사용할 수있는 이름. 
    여러 이름의 경우 쉼표로 구분 된 문자열을 사용하세요.

- queries	
    - 지시문에 삽입 될 쿼리를 구성합니다.

- jit	
    - true 인 경우이 지시문 / 구성 요소는 AOT 컴파일러에서 건너 뛰므로 항상 JIT를 사용하여 컴파일됩니다.

- host	
    - 키 : 값 쌍을 사용하여, 호스트 element에 event 이벤트를 연결 합니다.
    호스트 element는 selector에서 선택한 input element입니다.

> 이벤트를 이벤트 핸들러 함수와 연결하려면 이벤트명(input)을 괄호로 감싸면 됩니다.
`'(input)' : 'onInput($event)'` 그리고 이벤트 핸들러 `onInput`을 class에 서 정의하면 됩니다.

#### 참조: https://doitnow-man.tistory.com/177 [즐거운인생 (실패 또하나의 성공]    
