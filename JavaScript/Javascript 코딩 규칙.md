# 자연스럽고 일관성 있게 javascript를 코딩하는 규칙 


## 1. 할당, 선언, 함수(일반, 표현식, 생성자)
```javascript
// 1.1
// 변수
var foo = "bar",
  num = 1,
  undef;

// 리터럴 표기법:
var array = [],
  object = {};


// 1.2
// 함수 같은 유효 범위에 `var` 를 하나만 사용하면 가독성이 높아집니다.
// 이렇게 하면 선언 목록도 깔끔해집니다(아울러 키보드로 입력해야 할 양도 줄어들지요)

// 나쁜 스타일
var foo = "";
var bar = "";
var qux;

// 좋은 스타일
var foo = "",
  bar = "",
  quux;

// 또는..
var // 아래 변수에 대한 설명
foo = "",
bar = "",
quux;

// 1.3
// var 문은 관련있는 스코프(함수) 안의 시작하는 곳에 있어야합니다.
// ECMAScript 6의  const, let도 같은 맥락으로 이 규칙이 적용됩니다.

// 나쁜 스타일
function foo() {

  // 여기에 어떤 구문이 있음
  var bar = "",
    qux;
}

// 좋은 스타일
function foo() {
  var bar = "",
    qux;

  // 변수 선언이후에 모든 구문들이 옴.
}
```
```javascript
// 2.1
// 일반 함수 선언
function foo( arg1, argN ) {

}

// 사용법
foo( arg1, argN );


// 2.2
// 일반 함수 선언
function square( number ) {
  return number * number;
}

// 사용법
square( 10 );

// 아주 부자연스러운 연속 전달 스타일(continuation passing style) (CPS에 대해서는 http://goo.gl/TA32o를 참고)
function square( number, callback ) {
  callback( number * number );
}

square( 10, function( square ) {
  // 콜백 문장
});


// 2.3
// 함수 표현식
var square = function( number ) {
  // 가치 있고 의미 있는 뭔가를 반환합니다
  return number * number;
};

// 식별자를 지닌 함수 표현식
// 아래와 같은 형태는 자기 자신을 호출할 수 있으면서
// 스택 트레이스상에서 식별할 수 있다는 부가적인 장점이 있습니다:
var factorial = function factorial( number ) {
  if ( number < 2 ) {
    return 1;
  }

  return number * factorial( number-1 );
};


// 2.4
// 생성자 선언
function FooBar( options ) {

  this.options = options;
}

// 사용법
var fooBar = new FooBar({ a: "alpha" });

fooBar.options;
// { a: "alpha" }
```


## 2. 중괄호{}, 괄호(), 줄 바꾸기
```javascript
// if나 else, for while, try를 쓸 때에는 항상 빈 칸을 띠우고, 괄호를 사용하고, 여러 줄로 나누어 쓰세요.
// 이렇게 하면 가독성이 더 좋아집니다.

// 1.1
// 빼곡해서 알아보기 어려운 구문의 예

if(condition) doSomething();

while(condition) iterating++;

for(var i=0;i<100;i++) someIterativeFn();


// 1.1
// 가독성이 높아지도록 빈 칸을 띠워주세요.

if ( condition ) {
  // 코드
}

while ( condition ) {
  // 코드
}

for ( var i = 0; i < 100; i++ ) {
  // 코드
}

// 아래처럼 하면 더 좋습니다:

var i,
  length = 100;

for ( i = 0; i < length; i++ ) {
  // 코드
}

// 아니면 이렇게 할 수도 있죠...

var i = 0,
  length = 100;

for ( ; i < length; i++ ) {
  // 코드
}

var prop;

for ( prop in object ) {
  // 코드
}


if ( true ) {
  // 코드
} else {
  // 코드
}
```

## 예외 사항, 약간의 탈선
```javascript
// 1.1
// 콜백을 포함한 함수
foo(function() {
  // 참고로 함수 호출을 실행하는 첫 괄호와 "function"이라는
  // 단어 사이에는 별도의 공백이 없습니다.
});

// 배열을 받는 함수일 때에는 공백 없음
foo([ "alpha", "beta" ]);

// 2.C.1.2
// 객체를 받는 함수일 때에는 공백 없음
foo({
  a: "alpha",
  b: "beta"
});

// 괄호안에 괄호가 있을 때에는 공백 없음
if ( !("foo" in obj) ) {

}
```




#### 참조: https://github.com/rwaldron/idiomatic.js/tree/master/translations/ko_KR
