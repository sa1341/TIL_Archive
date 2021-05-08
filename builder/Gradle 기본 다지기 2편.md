# Gradle 기본 다지기 2편

Spring Boot에서 빌드 툴로 gradle을 사용할 경우에 이제까지 모르고 썼던 코드들에 대해서 정리를 해봤습니다.

먼저, 의존성 라이브러리를 땡겨오기 위해 작성하는 build.script에 대해서 살펴보겠습니다.

## 1.build.gradle 

```java
buildscript { 
    ext { 
        springBootVersion = '1.5.9.RELEASE' 
    } 
    repositories { 
        mavenCentral() 
    } 
    dependencies { 
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}") 
        classpath("io.spring.gradle:dependency-management-plugin:1.0.4.RELEASE") 
    } 
}
```

buildscript는 gradle로 task를 수행할 때에 사용되는 설정입니다. 즉, 소스 컴파일과는 상관이 없습니다. 

> buildscript 밖에 정의된 dependencies는 소스를 컴파일 하는 경우 참조하게 되고, 안에서 정의된 dependencies는 task를 사용할 때 사용되는 라이브러리입니다. 라이브러리를 등록할 때에 버전 지정을 안하는 것은 `spring-boot` 플러그인을 적용하여 관련된 라이브러리 버전이 이미 설정되어 있기 때문입니다.


## 2. 의존성 옵션 - implementation과 compile의 차이


- compileOnly: compile 시에만 빌드하고 빌드 결과물에는 포함되지 않습니다.

- runtimeOnly: runtime 시에만 필요한 라이브러리인 경우에 사용합니다.

- compile: A < B < C 의 경우 C에서 A를 접근할 수 있습니다. 이럴 경우에는 A가 수정되면 B, C 모두 재빌드가 필요합니다. 현재 compile 옵션은 Gradle 3.0부터는 deprecated 되었습니다. 

- api: A < B < C 의 경우 C에서 A를 접근할 수 있습니다. 이럴 경우에는 A가 수정되면 B, C 모두 재빌드가 필요합니다.

>
api는 compile과 똑같이 동작합니다. 이 옵션은 조심히 사용해야 하는데 그 이유는 api 종속성이 외부 api를 변경하면 Gradle이 컴파일 시 해당 종속성에 엑세스할 권한이 있는 모듈을 모두 다시 컴파일해야 하기 때문입니다. 그러므로 빌드 시간도 상당히 증가하는 단점이 있습니다. 종속성의 API를 별도의 모듈에 노출시키고 싶은것이 아니라면 라이브러리 모듈은 implementation 종속성을 대신 사용해야 합니다.


- implementation: A < B < C의 경우 C에서 A를 접근할 수 없습니다. A가 수정되면 B만 재빌드 필요합니다.

>
implementation의 장점은 당연히 속도입니다. 연결된 dependency가 확 줄어들고, 변경이 발생하더라도 recompile을 적게하기 때문입니다. 또한 개방할 때에는 개방하고, 닫힐 때에는 닫혀있어서 객체지향의 디자인 패턴을 잘 따르고 있는 옵션이기도 합니다.




> 참조 사이트: https://bkjeon1614.tistory.com/395

