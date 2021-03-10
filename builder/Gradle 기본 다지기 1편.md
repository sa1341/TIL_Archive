## Gradle 기본 다지기

회사에서는 Gradle 대신 다른 빌드 툴인 Maven으로 프로젝트에서 사용하는 외부 라이브러리들을 땡겨오고 빌드를 하고 있습니다. 개인적으로 집에서 스프링 부트를 공부하면서 다른 빌드 툴을 사용하고 싶어서 Gradle을 사용한적이 있습니다. 물론 시중에 나온 대부분의 책이 Gradle을 사용하여 프로젝트를 진행하기도 해서 무작정 사용만 하고 빌드 스크립트에 대해서 자세히 알아보지는 않아서 오늘은 Gradle에 대해서 최소한 기본적인 것을 배우고 싶어서 정리를 해보았습니다.

## Gradle의 등장

기본적으로 Java에서는 빌드 도구를 이용해서 프로젝트 관리가 이루어졌고, Apach Ant라는 빌드 도구가 등장한 것은 2000년입니다. 그 후에 아직까지도 실무에서 많이 사용하고 있는 Apach Maven이라는 빌드 툴이 등장하였고, `이것이 현 시점 Java 빌드 도구의 사실상 표준이라고 할 수 있습니다.`

하지만 이 Mavne을 위협할 만한 강력한 라이벌이 `Gradle`이라는 소프트 웨어입니다. Gradle은 Groovy라는 언어를 기반으로 만들어진 빌드 도구입니다. Groovy를 배운적이 없어서 구글링한 결과 Java와 마찬가지로 Java 가상 머신에서 실행되는 스크립트 언어입니다. 차이점이 있다면 Java와 달리 소스 코드를 컴파일 할 필요는 없습니다. Groovy는 스크립트 언어이며, 소스 코드를 그대로 실행합니다. 또한 Java와 호환이 되고, Java 클래스 파일을 그대로 Groovy 클래스로 사용할 수 있습니다. 문법도 Java에 아주 가까워, Java를 보다 사용하기 쉽게 한 것으로 느낄 수 있습니다. 

이 간편하게 사용할 수 있는 Java라고 할 수 있는 Groovy를 사용하여 빌드 처리를 작성하고, 실행하는 것이 Gradle 입니다.

기존에 Maven을 이용하면 XML 기반으로 빌드 처리를 작성합니다. 내용이 복잡해지면 XML 기반에 의한 묘사는 상당히 어렵습니다. 반면에 Gradle은 Java와 거의 비슷한 코드를 써서 빌드 처리를 관리 할 수 있습니다. 그래서 많은 Java 개발자들이 Maven을 Gradle을 더 선호하는지도 모르겠습니다.


## Gradle 프로젝트 초기화

Gradle 개발을 하기 위해서 먼저 프로젝트를 준비합니다.

1. 프로젝트를 생성할 위치로 이동

- cd /Users/junyoung/privacy/dev

2. 프로젝트 디렉토리를 만듭니다.

- 여기에서는 `GradleApp`이라는 이름으로 디렉토리를 만들기 위해 다음과 같이 명령을 실행합니다.

- mkdir GradleApp

3. 프로젝트 디렉터리로 이동합니다.

- cd GradleApp

4. 마지막으로 프로젝트를 초기화 합니다.

- gradle init --type java-application

`gradle init`이라는 것이 Gradle 초기화를 위한 명령어입니다. 이후에 --type java-application는 Java 응용 프로그램 프로젝트 유형을 지정합니다.

## Gradle 프로젝트 구조

Gradle 프로젝트 구조는 아래와 같습니다.

![image](https://user-images.githubusercontent.com/22395934/110622153-6dcff480-81de-11eb-9dbc-4764751fcf13.png)

#### .gradle 디렉토리

Gradle이 사용하는 폴더입니다. 작업(task)로 생성된 파일이 저장됩니다. 이 내용을 편집하는 일은 거의 없습니다.

#### gradle 디렉토리

이것도 Gradle이 필요한 경우 사용할 디렉토리입니다. 기본적으로 Gradle 환경을 정리한 `Wrapper 파일`이라는 파일들이 저장되어 있습니다.

#### src 디렉토리

프로젝트에서 만든 프로그램 관련 디렉토리입니다. 프로젝트에서 사용하는 파일(소스코드, 각종 리소스 파일 등)은 모두 포함됩니다.

#### build.gradle

Gradle 기본 빌드 설정 파일입니다. 이 안에 프로젝트 빌드 처리에 대해서 내용이 작성되어 있습니다.

#### gradlew, gradlew.bat

이 2개는 Gradle 명령입니다. bat가 붙어있는 것이 Windows 용이고, macOS 및 Linux 용입니다.

#### settings.gradle

프로젝트에 대한 설정 정보를 작성한 파일입니다.
가장 중요한 것은 src 디렉토리입니다. 이 안에 개발하는 프로그램에서 사용하는 파일이 모두 저장됩니다. 다음으로 중요한 것은 `build.gradle` 파일입니다. 이것은 빌드 파일이고 해서 빌드 처리의 내용을 작성하는 파일입니다. 이 파일은 Groovy 언어로 작성되어 있습니다.

>> MSA(Micro Service Architecture) 구조로 프로젝트를 구성할 경우 root 프로젝트 하위로 모듈을 추가할 경우 settings.gradle 파일에 모듈을 추가한다고 명시를 해야 합니다.

## gradle init 명령과 type 종류

- java-application: Java 애플리케이션 프로젝트 작성에 대한 타입입니다. 기본적으로 App.java 파일이 제공됩니다.

- java-library: Java 라이브러리 프로젝트 생성을 위한 타입입니다. 단순히 샘플로 제공되는 소스 코드 파일이 응용 프로그램의 메인 클래스가 되어 있지 않다는 정도의 차이입니다. 

## 컴파일 및 실행

#### 프로그램 컴파일

- gradle compileJava 

컴파일은 compileJava라는 테스크로 제공합니다. 이것은 Java 소스 코드를 컴파일 하기 위한 것입니다.

#### 프로그램 실행

- gradle run

java-application 타입의 프로젝트에는 run 테스크라는 것이 제공되고, 이를 실행하여 메인 클래스를 실행할 수 있습니다. 디폴트로 App.java가 실행됩니다.

#### 프로그램 패키지

- gradle jar

jar 테스크는 그 이름대로 프로그램을 jar 파일에 모와서 저장합니다. 이는 프로젝트에 생성되는 build 디렉토리 하위에 libs 디렉토리에 저장됩니다.

#### 프로젝트 클린

- gradle clean

프로젝트를 빌드할 때에 build 디렉토리에 여러 파일이 저장됩니다. clean 작업은 이 파일들을 제거하고 빌드 이전 상태로 되돌립니다.
