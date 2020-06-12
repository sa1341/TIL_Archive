## SLF4J Binding이란?
SLF4J 인터페이스를 로깅 구현체와 연결하는 어댑터 역할을 하는 라이브러리 입니다.

- 각각의 SLF4J binding(.jar)은 컴파일 시점에 오직 하나의 Logging 프레임워크를 사용하도록 바인딩 합니다.

- class path에서 바인딩된 구현체가 발견되지 않으면 slf4j는 기본적으로 no-operation으로 설정됩니다. (즉, 출력되는 것이 없습니다.)

- 사용하길 원하는 Logging Framework에 대한 SLF4J 바인딩을 추가해야 합니다.


## SLF4J Bridging Modules

- SLF4J이외의 다른 로깅 API로 Logger 호출을 SLF4J 인터페이스로 연결하여 SLF4J API가 대신 처리할 수 있도록 하는 일종의 어댑터 역할을 하는 라이브러리 입니다.

## Bridging legacy logging APIs

- 여러 다른 로깅 API를 사용하는 components에 대해 Single channel을 통해 Logging을 통합하는 것이 바람직합니다.

- 이를 위해 SLF4J에서 log4j API, JCL API 및 JUL API에 대한 호출을 대신 SLF4J API에 대한 것처럼 리디렉션하는 여려 Bridging Modules을 제공합니다.

