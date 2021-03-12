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

## Gradle 플러그인

- Java 플러그인 추가

```java
apply plugin: 'java'
```

차음에 `apply plugi:`라는 것은 Gradle 플러그인을 사용하기 위한 것입니다.

`java`는 Java 프로그램을 위한 기능을 제공하는 플러그인입니다. compileJava라는 테스크는 Java 플러그인에서 제공되는 것입니다.

- application 플러그인 추가

```java
apply plugin: 'application'
```

이 `application`은 응용 프로그램에 대한 기능을 제공하는 플러그인입니다. run 응용 프로그램을 실행하는 테스크도 `application` 플러그인에 의해 제공되는 테스크 입니다.

- 메인 클래스 이름
```java
mainClassName = 'App'
```
이것은 application 플러그인으로 사용되는 것으로, 메인 클래스를 지정합니다. run으로 응용 프로그램을 실행할 수 있었던 것도 이 mainClassName 메인 클래스가 지정되어 있었기 때문입니다.


## 저장소(repositories)

build.gradle에 기술된 내용에는 "의존 라이브러리"에 대한 기술이 있었습니다. Gradle에는 프로그램으로 필요한 라이브러리를 자동으로 다운로드하고 통합하는 기능이 있습니다. 따라서 중요해지는 것은 `저장소(repository)`입니다.

저장소라는 것은 각종 프로그램들이 저장되는 위치입니다. 이 저장소는 "어떤 저장소를 사용하는지"를 빌드 파일에 작성하여 설정할 수 있습니다.

```java
repositories {
    .... 저장소 설정 .....
}
```

위 내용이 저장소를 지정하기 위한 것입니다. 이 {} 안에 저장소를 설정하는 문장을 작성합니다. 온라인으로 접속하여 사용할 수 있는 저장소로 Gradle에서는 대체로 다음 2개의 저장소 서비스를 이용합니다.


### Maven 중앙 저장소

```java
mavenCentral()
```

이것은 Apach Maven 중앙 저장소를 이용하기 위한 것입니다. Gradle은 중앙 저장소를 그대로 사용할 수 있습니다.


```java
jcenter()
```

이 밖에 JCenter라는 저장소를 사용할 수 있습니다. 이것은 Maven과 Gradle 등 각종 빌드 도구에서 사용할 수 있는 공개 저장소입니다. mavenCentral(), jcenter()는 Gradle 메서드입니다. 이러한 repositories 안에서 호출하여 지정된 저장소를 사용할 수 있습니다.

## 의존 라이브러리(dependencies)

저장소에서 필요한 라이브러리를 사용할 수 있는 것이 dependencies라는 문장입니다. 아래와 같이 기술합니다.

```
dependencies {
    ... 라이브러리 지정 ....
}
```

이 {} 안에 의존 라이브러리들을 작성합니다. 여기에는 2개의 문장이 작성되어 있습니다.

### 컴파일시 의존 라이브러리 

```java
compile 'org.projectlombok:lombok';
```

이것은 컴파일시에 사용하는 라이브러리를 지정하고 있습니다. `compile~` 이라고 기술하는 것으로 그 라이브러리가 컴파일 시에 참조되는 것을 의미합니다.

### 테스트 컴파일시 의존 라이브러리

```java
testCompile 'junit:junit:4.12'
```

이것은 테스트 컴파일에 사용하는 라이브러리를 지정합니다. `testCompile`라고 기술하는 것으로 그 라이브러리가 테스트 컴파일 시에 참조되는 것을 나타냅니다.

이 외에도 다양한 처리를 수행할 때 참조하는 종속 라이브러리를 지정할 수 있습니다. 하나 기억해야 할 것은 클래스 패스의 지정입니다.

```java
classpath '...라이브러리...'
```

이렇게 하면 지정된 라이브러리를 클래스 경로에 추가할 수 있씁니다. 컴파일 실행시까지 의존하는 라이브러리 지정에 사용합니다. 

## 테스크 정의

Gradle은 명령에 의해 `테스크(task)`을 수행하는 프로그램입니다. 위에서  gradle compileJava라든지 gradle run와 같은 명령어를 사용하였는데, 이들도 모두 `compileJava 테스크 수행`, `run 테스크 수행`이라는 것입니다.

### 테스크 정의 기본

이 테스크는 사용자가 정의할 수 있습니다. 빌드 파일(build.gradle)에서 테스크의 처리를 기술해두면, 그것을 gradle 명령으로 호출 실행 시킬 수 있습니다.

```java
task 테스크명 {
    ... 수행할 처리 ...
}
```

테스크는 `task`라는 키워드를 사용하여 정의합니다. 이 후에 테스크명을 작성하고, 그 다음에 {} 내에 테스크의 내용을 작성합니다. 테스크 선언은 다른 작성법도 있는데, 다음과 같은 작성도 가능합니다.

```java
task (테스크명) { ... }
task ('테스크명') { ... }
```

이것으로 {}안에 기술된 처리를 실행하는 작업을 정의할 수 있습니다. 

build.gradle 아래 부분에, 아래와 같이 코드를 작성해봅니다.

```java
task hello {
    println("이것은 hello 테스크를 실행한 것입니다.")
}
```

그리고 파일을 저장하고, 명령 프롬프트 또는 터미널에서 다음과 같이 실행하면 됩니다.

```java
gradle hello
```

이렇게 하면 hello 테스크가 실행됩니다. 실행해 보면, println으로 출력하는 문장 이외에도 다양한 문장이 출력됩니다.

>> quite모드로 테스크를 수행하면 많은 부분이 사라집니다. -q  옵션을 지정하고 아래와 같이 실행합니다.

```java
gradle -q hello
```

이로 표시되는 출력은 상당히 심플하게 될 것입니다.

## doFirst와 doLast

테스크는 이렇게 task 후에 {} 부분에 처리를 쓰는 것만으로 만들 수 있습니다. 사실 보통은 이런 작성법은 많이 쓰지 않습니다.

일반적인 테스크의 형태를 정리하면, 대체로 다음과 같은 형태가 됩니다.

```java
task 테스크명 {

    doFirst {
        ...... 수행할 처리 ......
    }

    doLast {
        ...... 수행할 처리 ......
    }
}
```

테트크 {}에는 doFirst, doLast라는 것이 준비됩니다. 이것은 일종의 클로저입니다. 이들은 각각 다음과 같은 기능을 한다.

 - doFirst: 최초에 수행하는 액션입니다.
 - doLast: 최후에 수행하는 액션입니다.

 테스크는 준비된 `액션`을 순서대로 실행해 나가는 역할을 합니다. 액션이라는 것은 구체적으로 처리의 `실행 단위`같은 것입니다. 테스크 중에는 여러 가지 액션이 준비되어 있고, 그것이 순차적으로 실행됩니다.

 doFirst과 doLast 그 액션의 최초, 최후에 실행됩니다. 즉, `테스크의 기본적인 처리`등이 있을 때는 그전에 실행하는 것과 후에 실행하는 것을 이렇게 준비합니다.

 아래 예제코드를 살펴보겠습니다.

```java
task hello {
    doLast {
        println("이것은 hello 테스크의 doLast입니다.");
    }
    doFirst {
        println("이것은 hello 테스크의 doFirst입니다.");
    }
}
```
위의 테스크를 실해앟면 doFirst의 처리내용이 먼저 실행이 되고, doLast의 println 함수가 호출이 됩니다.


## 매개 변수 전달

테스크는 수행할 때 필요한 값을 매개 변수로 전달할 수 있습니다. 단순히 작업 처리 중 변수를 사용하면 됩니다. 예를 들어, 다음과 같습니다.

```java
gradle msg -Px=값
```

이렇게 -P 다음에 변수명을 지정하고 그 뒤에 등호로 값을 지정합니다. 변수 name에 `junyoung` 값을 전달하고 싶다면 -Pname=junyoung 식으로 기술하면 됩니다.

또 다른 사용예제를 살펴보겠습니다. 아래는 숫자를 전달하여 그 숫자까지를 더하는 예제입니다.

```java
task hello {
    doLast {
        def n = max.toInteger()
        for (def i in 1..n) {
          println("No," + i + " count.")
        }
        println("end.")
     }
}
```

테스크는 "max"라는 변수를 사용하여 최대값을 지정합니다. 

```java
gradle hello -Pmax=5
```

#### 실행결과

```java
> Tas: hello
No,1 count.
No,2 count.
No,3 count.
No,4 count.
No,5 count.
end.
```

이 예제에서는 def n = max.toInteger()와 같이 하여 변수 max를 정수 값으로 변환한 것을 변수 n에 대입하고 있습니다. 그리고 이 n 값을 이용하여 for문으로 반복 계산을 실시하고 있습니다. 이런 상태로 매개 변수를 사용하여 쉽게 값을 변수로 전달할 수 있습니다.

## 다른 테스크 호출

테스크에서 다른 테스크를 호출해야 하는 경우도 있습니다. 예를 들어 아래와 같은 테스크가 있다고 합시다.

```java
task a {...}
task b {...}
```

a와 b라는 테스크가 있을 때, 테스크 a에서 테스크 b를 호출하려면 어떻게 해야 하는가? Java 적으로 생각하면 아래와 같이 호출하면 될거라 생각합니다.

```java
b()
```

하지만 이렇게 작동을 하지 않습니다. `tasks`에 있는 작업 객체 안의 메소드를 호출하여 수행해야 합니다

작업하는 것은 모든 tasks라고 객체에 정리하고 있습니다. 예를 들어 a, b라는 테스크가 있다면 tasks.a과 tasks.b로 지정할 수 있습니다. 이 테스크 객체 안에 있는 `execute`라는 메서드를 호출하여 테스크를 수행할 수 있습니다.

```java
tasks.a.execute()
tasks.b.execute()
```

이런 식으로 실행하여 테스크 a, b를 호출합니다.

### 종속 테스크 지정

어떤 테스크를 수행할 때, 다른 작업 수행이 필수적인 경우도 있습니다. 이러한 경우에는 `dependsOn`라는 기능을 사용할 수 있습니다. 이는 다음과 같이 작성합니다.

```java
task 테스크명 (dependsOn: '테스크') {
    ..... 중략 .....
}
```

또는 다음과 같은 작성도 가능합니다.

```java
task 테스크명 {
    dependsOn: '테스크'
    ...... 중략 ......
}
```

이와 같이 기술해 두면 작업이 호출될 때, 먼저 dependsOn에 지정된 작업을 수행하고 그것이 끝난 후에 테스크의 본 처리를 수행합니다.

여러 테스크를 지정해야 하는 경우는 테스크명을 배열로 지정합니다. ['a', 'b', 'c']와 같은 식입니다. 이 경우 최초에 작성한 테스크부터 실행됩니다.

##### 예제코드 

```java
task hello(dependsOn:['aaa', 'bbb'])  {
    doFirst {
        println("*** start:hello task ***")
    }
    doLast {
        println("*** end:hello task ***")
    }
}
 
task aaa {
    doLast {
        println("<< This is A task! >>")
    }
}
 
task bbb {
    doLast {
        println("<< I'm task B!! >>")
    }
}
```

#### 실행결과

```java
> Task :aaa
<< This is A task! >>

> Task :bbb
<< I'm task B!! >>

> Task :hello
*** start:hello task ***
*** end:hello task ***
```
최초에 aaa 테스크, bbb 테스크가 실행되면, 이후에 hello 테스크가 호출되었는지 알 수 있습니다. dependsOn에 의해, aaa, bbb가 종속 테스크가 되는 테스크가 실행된 후가 아니면 hello가 실행되지 않게 됩니다.

 #### 참조: http://www.devkuma.com/books/pages/1076

