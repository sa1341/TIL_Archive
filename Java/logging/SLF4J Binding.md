## SLF4J Binding이란?
SLF4J 인터페이스를 로깅 구현체와 연결하는 어댑터 역할을 하는 라이브러리 입니다.

- 각각의 SLF4J binding(.jar)은 컴파일 시점에 오직 하나의 Logging 프레임워크를 사용하도록 바인딩 합니다.

- class path에서 바인딩된 구현체가 발견되지 않으면 slf4j는 기본적으로 no-operation으로 설정됩니다. (즉, 출력되는 것이 없습니다.)

- 사용하길 원하는 Logging Framework에 대한 SLF4J 바인딩을 추가해야 합니다.
