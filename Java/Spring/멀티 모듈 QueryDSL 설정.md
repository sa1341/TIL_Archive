# 멀티 모듈에서 QueryDSL 설정 방법

최근에 Spring Boot를 사용하여 멀티 모듈을 구성하였고, 하위 모듈별로 꼭 필요한 의존성만 설정하도록 했습니다. 

#### 기술 스펙은 아래와 같습니다.

- Spring Boot 2.3
- Gradle 6.3

## 1. 멀티모듈 구성 화면

![image](https://user-images.githubusercontent.com/22395934/116354455-68347980-a833-11eb-8711-d25bfac1d6a7.png)

core 모듈은 여러 하위 모듈에서 공통으로 사용하는 도메인 계층을 관리합니다.

### settings.gradle 

gradle이 빌드 과정에서 settings.gradle 파일을 참조하기 때문에 반드시 root 모듈과 sub 모듈을 include 해야합니다.

#### settings.gradle 파일

```java
rootProject.name = 'yolo'
include 'yolo-core'
include 'yolo-api'
```

## 2. QueryDSL 설정 방법

저같은 경우에는 core 모듈에서 도메인 영역인 Entity, Repository를 가지고 있기 때문에 core 모듈에서 QueryDSL 플러그인과 의존성을 가지고 있도록 설정했습니다.

#### core - build.script

```java
plugins {
    // (1) QueryDSL 플러그인 추가
    id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"
}

bootJar {
    enabled = false
}

jar {
    enabled = true
}

dependencies {
    runtimeOnly 'com.h2database:h2'
    runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
    implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.8'
    // (2) QueryDSL 의존성 추가
    compile('com.querydsl:querydsl-jpa') 
}

// (3) QClass 생성 위치
def querydslDir = "$buildDir/generated/querydsl"

querydsl {
    jpa = true
    querydslSourcesDir = querydslDir
}

// (4) QClass 소스 위치를 지정합니다.
sourceSets {
    main.java.srcDir querydslDir
}
// (5) gradle 5.0 설정
configurations {
    querydsl.extendsFrom compileClasspath
}

compileQuerydsl {
    options.annotationProcessorPath = configurations.querydsl
}
```

>
(1) 플러그인을 사용할 수 있도록 설정합니다.
(2) QueryDSL 의존성 라이브러리를 추가합니다.
(3) QueryDSL를 사용하기 위해서는 `QClass`라는 도메인의 `meta model`이 필요한데 `QClass`를 생성하고 빌드 후 `generaged/querydsl` 디렉토리 하위로 파일이 생성됩니다.


아래 코드는 QueryDSL 플러그인 저장소 url를 repositories에 선언하여 의존성을 받아오도록 설정한 코드입니다.

```java
repositories {
    mavenCentral()
    maven {
        url "https://plugins.gradle.org/m2/"
    }
}
```


### dependency 추가

만약 멀티 모듈을 사용할 경우, 저처럼 core 상위 모듈에서 QueryDSL을 사용할 경우 테스트 코드는 api 하위 모듈에서 작성하게 됩니다. 그러면 테스트 코드에서 JPAQueryFactory를 주입받아서 repository 계층에 대해서 테스트를 진행하게 되는데, 이 경우에 테스트는 문제없이 성공하지만 build를 실행할 경우 `JPAQueryFactory does not exist`라는 에러 문구가 나오면서 build가 실패합니다.

여기서 많은 삽질을 하였였는데, 실패 사유는 아래와 같이 build.script에서 `implementation`으로 선언해서 발생한 문제였습니다. `implementation`으로 의존성이 등록되면, 하위 프로젝트들은 해당 의존성을 가질 수 없습니다. 

즉, 아래와 같이 api 하위 모듈이 core 상위 모듈을 의존하고 있다면 api는 `implementation`으로 선언된 `com.querydsl:querydsl-jpa`을 사용할 수가 없습니다. 이렇게 함으로써 멀티 모듈에서의 의존관계에 개방/폐쇄를 적용할 수가 있습니다.

#### core 모듈 build.script 수정 전

```java
implementation('com.querydsl:querydsl-jpa') 
```

그래서 저는 아래와 같이 api 모듈에서도 사용할 수 있도록 compile로 변경했습니다.

#### core 모듈 build.script 수정 후

```java
compile('com.querydsl:querydsl-jpa') 
```

## 3. QueryDSL 상세 설정

### jpa

아래 설정에서 jpa는 QClass를 자동으로 생성할지 결정합니다.
기본값은 `false` 입니다.

만약, true일 경우 com.querydsl.apt.jpa.JPAAnnotationProcessor가 추가되면서 프로젝트에 사용됩니다.

> JPAAnnotationProcessor는 meta model(QClass)를 생성합니다.

`false`일 경우 build 타임에 QCalss들이 생성되지 않게 됩니다.

```java
querydsl {
    jpa = true
    querydslSourcesDir = querydslDir
}
```

### querydslSourceDir

>
- 어디에 QClass를 생성할지 설정합니다.
- 기본값은 `src/querydsl/java` 입니다.

```java
compileQuerydsl {
    options.annotationProcessorPath = configurations.querydsl
}
```

### sourceSet

```java
sourceSets {
    main.java.srcDir querydslDir
}
```

>
- QClass가 생성된 위치를 나타냅니다.
- 위에서 선언한 querydslSourceDir가 위치가 같아야 합니다.
- 기본적으로 자바 소스와 리소스 파일의 그룹을 나타냅니다.
- 기본적으로 java 플러그인은 `src/main/java`와`src/test/java`를 기본 소스 디렉토리로 인식합니다.
- `main.java.srcDir`는 `build/generated/querydsl`이 되게 됩니다.
- 나중에 User라는 도메인을 생성하면 해당 위치에 QUser라는 QueryDSL용 도메인이 생성되고 사용하게 됩니다.

## 4. gradle 5.0 설정

```java
configurations {
    querydsl.extendsFrom compileClasspath
}

compileQuerydsl {
    options.annotationProcessorPath = configurations.querydsl
}
```

#### gradle 5+ 버전을 사용할 경우 위와 같은 설정 값을 추가해야 합니다. 

### configuration

>
- QueryDSL의 컴파일된 클래스 패스 경로를 지정합니다.

### compileQuerydsl

>
- querydsl-apt의 annotation processor 경로를 지정합니다.
- gradle 5 버전에서는 자체 annotation processor를 사용합니다
- querydsl-apt의 annotation processor와 충돌
