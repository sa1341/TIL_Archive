## Javascript 코드 실행하는 방법

V8에서 Javascript가 어떻게 해석되어, 실행 되는지 이해하는지 구글링을 통해 살펴보았습니다. 브라우저는 html과 css를 이용해서 화면을 만들고, Javascript를 이용해 화면을 조작하는 것은 웹에 입문한 개발자라면 대부분 아는 사실입니다.

크롬 브라우저는 크게 Blink라는 Renderer 엔진 (html, css)과 V8이라는 Javascript 엔진을 가지고 있습니다.

## V8의 특징

V8 엔진은 C++로 작성되어 있으며, ECMA-262에 기재된 ECMAScript 및 WebAssembly를 처리할 수 있습니다.

V8은 크롬이 아니더라도, 독립적으로 실행이 가능한데, 대표적인 예가 V8으로 빌드 된 Node.js가 있습니다.

V8은 아래 특징을 지닙니다.

- Javascripit 소스 코드를 컴파일 하고, 실행합니다.
- 생성하는 Object를 메모리에 할당합니다.
- 가비지 콜렉션을 이용해 더 이상 사용되지 않는 Object의 메모리를 해제합니다.
- Hidden Class를 이용해 빠르게 프로퍼티에 접근합니다.
- TurboFan을 이용해 최적화된 코드로 만들어 속도 및 메모리를 최적화 합니다.


## JIT 컴파일러

Javascript는 보통 js 파일 (text)로 배포되고, 이를 브라우저에서 사용합니다.

브라우저에서는 Javascript를 처리하기 위해서, Javascript 엔진으로 Javascript 소스를 내부에서 이해할 수 있는 언어로 변환하고 실행하는데, 이를 컴파일이라고 부릅니다.

브라우저에서 Javascript의 컴파일은 보통 Interpreter로 처리된다고 알려져 있지만, V8 엔진에서는 꼭 그렇지도 않습니다.

브라우저는 Javascript를 매번 브라우저가 이해할 수 있는 언어로 변환해야 하는데, Interpreter의 경우 항상 같은 코드를 반복해서, Compile하고 실행합니다. 웹의 특성상 새로고침이나 페이지 이동이 잦은데, 항상 같은 코드를 반복해서 compile하는 경우가 많습니다.

V8에서는 먼저 Javascript 코드를 Interpreter 방식으로 컴파일하고, 이를 바이트 코드로 만들어 냅니다.

그리고 컴파일 속도를 높이기 위해, 이 바이트 코드를 캐싱해 두고, 자주 쓰이는 코드를 인라인 캐싱과 같은 최적화 기법으로 최적화한 후, 이후 컴파일 할 시에 참조하여 속도를 높입니다. 이러한 방식으로 JIT(Just-In-Time) 컴파일러라고 하며, Interpreter의 느린 실행 속도를 개선할 수 있습니다.


## V8 컴파일 과정

Javascript도 사람이 읽을 수 있는 코드이기 때문에, 기계가 읽을 수 있도록 기게어로 Compile 해야 합니다. V8에서 Javascript 컴파일 과정은 다음과 같습니다. 

1. Blink에서 <script> 태그를 만나면, Javascript 스트리밍을 시작합니다.
2. 스트리밍으로 전달 받은 UTF-16 문자열은 Scanner를 이용해 Token (let, for)을 생성합니다.

3. 생성된 Token을 가지고, parser가 추상 구문 트리를 만듭니다.

4. 만들어진 AST는 Ignition에서 바이트 코드로 컴파일합니다.

5. 컴파일 된 바이트 코드를 실행함으로써 원하는 Javascript 동작이 실행됩니다.

이 때 컴파일한 내용을 V8에서는 최적화를 진행합니다.

- 바이트 코드를 실행하면서, Profiling을 통해 최적화 해야하는 데이터를 수집합니다.

- Profiling을 통해 찾은 데이터를 TurboFan을 통해 자주 사용되는 함수나 데이터를 기반으로 최적화를 진행하며, Optimized Machine Code를 생성합니다.

- 이후 Optimized Machine Code를 실행하며, 메모리 사용량을 줄이고, 기계어에 최적화되어, 속도와 성능을 향상시킵니다.


#### 참조: https://medium.com/@pks2974/v8-%EC%97%90%EC%84%9C-javascript-%EC%BD%94%EB%93%9C%EB%A5%BC-%EC%8B%A4%ED%96%89%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95-%EC%A0%95%EB%A6%AC%ED%95%B4%EB%B3%B4%EA%B8%B0-25837f61f551
