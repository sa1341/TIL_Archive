# 코드 커버리지(Code Coverage)란?


## 코드 커버리지

의식적으로 TDD를 실천하기 위한 방법 중 하나로 `Jacoco`라는 Java 진영의 코드의 커버리지를 체크하는 오픈 소스 라이브러리를 사용해봤습니다. 

`Jacoco`를 적용하면서 코드 커버리지라는 용어를 막연하게만 알고 있었지 정확한 의미를 알지 못한다고 생각해서 다시 한번 정리해봤습니다.

위키피디아에서 코드 커버리지 정의는 아래와 같이 정의하고 있습니다.

> 컴퓨터 과학에서 코드 커버리지는 소프트웨어의 테스트 케이스가 얼마나 충족되었는지를 나타내는 `지표` 중 하나라고 설명하고 있습니다. 즉, 테스트를 진행했을 때  `코드 자체가 얼마나 실행되었느냐`는 것이고, 이는 수치를 통해 확인할 수 있습니다.


코드 커버리지는 소스 코드를 기반으로 수행하는 `화이트 박스 테스트`를 통해 측정합니다.

사실 화이트 박스 테스트랑 블랙 박스 테스트라는 용어는 기억이 나지 않아서 이 용어에 대해서 잘 정리되어 있는 [기술 블로그](https://tecoble.techcourse.co.kr/post/2020-10-24-code-coverage/)에서 참조했습니다.

- 블랙 박스 테스트(Black-box-text)
    - 소프트 웨어의 내부 구조나 작동 원리를 모르는 상태에서 동작을 검사하는 방식
    - 올바른 입력과 올바르지 않은 입력을 입력하여 올바른 출력이 나오는지 테스트하는 기법
    - 사용자 관점의 테스트 방법

- 화이트 박스 테스트(White-box-test)
    - 응용 프로그램의 내부 구조와 동작을 검사하는 테스트 방식
    - 소프트웨어 내부 소스 코드를 테스트하는 기법
    - 개발자 관점의 단위테스트 방법

## 코드 커버리지 측정기준

코드 커버리지 측정기준은 아래 크게 3가지로 나뉩니다.

- 구문(Statement)
- 조건(Condition)
- 결정(Decision)

위의 3가지 요소가 코드의 구조이고, 코드 커버리지는 이러한 코드의 구조를 `얼마나 커버했느냐에 따라서 측정기준이 나뉘게 됩니다.`

### 구문(Statement)

라인(Line) 커버리지라고 부릅니다. `코드 한 줄이 한번 이상 실행된다면 충족됩니다.`

```kotlin
@Transactional(readOnly = true)
fun findBoardById(id: Long): BoardDto.Res {

    val board = boardRepository.findByIdOrNull(id) // (1)번

    return board?.let { // (2)번
        BoardDto.Res(it.id, it.author, it.title, it.content, // (3)번
        DateUtils.parseCreatedDt(it.createdAt), DateUtils.parseUpdatedDt(it.updatedAt)) //(4)번
    }?: kotlin.run {  //(5)번
        throw EntityNotFoundException("해당 게시판이 존재하지 않습니다.: $id") //(6)번
    }
}
```

위의 코드는 게시글 번호로 게시글을 조회하는 메서드 입니다. `board?` 세이프 콜을 사용하여 게시글이 존재하지 않을 경우 예외를 던지도록 작성한 코드입니다.

위의 코드를 테스트할 경우 게시글이 존재하는 경우에는 6번 코드는 실행되지 못합니다. 총 6개의 라인에서 1,2,3,4,5번의 라인만 실행되므로 `구문 커버리지`는 `5 / 6 * 100 = 83%`가 됩니다.

## 조건(Condition)

- 모든 조건식의 내부 조건이 `true/false`을 가지게 되면 충족 됩니다.

사실 조건 커버리지가 이해하기가 제일 힘들었습니다.내부 조건이라는 용어가 무엇을 의미하는지 파악하기 힘들었기 때문입니다.

다행히 조건 커버리지에 대한 설명이 잘되어 있는 블로그가 있어서 참고했습니다.

```kotlin
void foo (int x, int y) {
    system.out("start line"); // 1번
    if (x > 0 && y < 0) { // 2번
        system.out("middle line"); // 3번
    }
    system.out("last line"); // 4번
}
```

내부조건이라는 것은 조건식 내부의 각각의 조건이라고 이해하면 편합니다. 

위 코드를 예시로 보면 모든 조건식으로는 2번 if 문이 있고, 그중 내부 조건은 조건식 내부의 `x > 0, y < 0`을 말합니다.

위의 코드를 테스트한다고 가정하면 조건 커버리지를 만족하는 테스트 케이스로는 `x = 1, y = -1, x = -1, y = -1` 아 있습니다. 이는 `x > 0` 내부 조건에 대해 `true/false`를 만족하고, `y < 0` 내부 조건에 대해 `false/true`를 만족합니다.

하지만 결국 테스트 케이스는 if문은 조건에 대해 `false`만 반환합니다.

즉, 내부 조건 `x > 0, y < 0`에 대해서는 각각 `true/false` 모두 나왔지만 if 조건문의 관점에서 보면 `false`에 해당하는 결과만 발생했습니다.

조건 커버리지는 만족했을지 몰라도 if문 내부 코드 (3번)은 실행되지 않았기 때문에 라인 커버러지를 만족하지 못하고, if문의 `false`에 해당하는 시나리오만 체크되었기 때문에 뒤에 나오는 브랜치 커버리지도 만족하지 못하게 됩니다.

## 결정(Decision)

- 브랜치(Branch) 커버리지라고 부르기도 합니다.

- 모든 조건식이 `true/false`을 가지게되면 충족됩니다.


```kotlin
void foo (int x, int y) {
    system.out("start line"); // 1번
    if (x > 0 && y < 0) { // 2번
        system.out("middle line"); // 3번
    }
    system.out("last line"); // 4번
}
```

위의 코드를 테스트하면 if문의 조건에 대해 `true/false` 모두 가질 수 있는 테스트 케이스로는 `x = 1, y = -1, x = -1, y = 1`이 있습니다. 

위의 두 데이터 모두 if문 관점에서 `true/false`를 모두 반환하므로 결정 커버리지를 충족하고 있습니다.

위의 세 가지 코드 커버리지 중에서는 구문 커버리지가 가장 대표적으로 많이 사용되고 있습니다.

조건 커버리지나 브랜치 커버리지의 경우 코드 실행에 대한 테스트보다는 `로직의 시나리오에 대한 테스트`에 더 가깝기 때문입니다.

참고로 조건문이 존재하지 않는 코드는 위 두 커버리지(조건, 브랜치) 대상에서 아예 제외됩니다. 즉, 해당 코드들은 아예 테스트를 하지 않습니다.

`핀트는 라인 커버리지를 사용하면 모든 시나리오를 테스트한다는 보장은 할 수 없지만, 어떤 코드가 실행되더라도 해당 코드는 문제가 없다는 보장은 할 수 있습니다.`

이러한 이유로 `라인 커버리지`를 더 많이 사용한다고 합니다.


## Gradle Jacoco 라이브러리 적용

위에서 코드 커버리지 개념에 대해서 정리하였고, 이제 실제로 Gradle 기반의 Spring Boot에 적용해봤습니다.

저는 코틀린을 공부하고 있기 때문에 `build.gradle.kts` Kotlin DSL로 작성했습니다.

### Jacoco 플러그인 추가 

참고로 프로젝트는 Multi Module을 기반으로 하고 있습니다.

```kotlin
plugins {
    jacoco
}

subprojects {
    apply(plugin = "jacoco")

    jacoco {
        toolVersion = "0.8.6"
    }
}
```

위의 스크립트에 Jacoco 플러그인 설정이 가능합니다.

- reportDir: 테스트 결과 리포트를 저장할 경로를 바꿀 수 있습니다. default 경로는 `${project.reporting.baseDir}/jacoco` 입니다.

### Jacoco task 설정

Jacoco 플러그인은 `jacocoTestReport, jacocoTestConverageVerification` Task들을 제공합니다.

- jacocoTestReport: 바이너리 커버리지 결과를 사람이 읽기 좋은 형태의 리포트로 저장합니다. html 파일로 생성해 가독성 좋게 눈으로 확인할 수도 있고, SonarQube 등으로 연동하기 위해 xml, csv 같은 형태로도 리포트를 생성할 수 있습니다.

- jacocoTestConverageVerification: 내가 원하는 커버리지 기준을 만족하는지 지원해주는 task 입니다. 만약 위에서 설명한 브랜치 커버리지를 최소한 `80%` 이상으로 유지하고 싶다면, 이 task에 설정하면 됩니다. 

```kotlin
tasks.jacocoTestReport {
   
    dependsOn(tasks.test)
    reports {
        html.isEnabled = true
        html.destination = file("$buildDir/reports/myReport.html")
        csv.isEnabled = true
        xml.isEnabled = false
    }

    var excludes = mutableListOf<String>()
    excludes.add("com/yolo/jean/config")
    excludes.add("com/yolo/jean/global")
    excludes.add("com/yolo/jean/kafka")
    excludes.add("com/yolo/jean/board/dto")
    excludes.add("com/yolo/jean/reply/dto")

    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude(excludes)
        }
    )

    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {

    var Qdomains = mutableListOf<String>()

    for (qPattern in 'A' .. 'Z') {
        Qdomains.add("*.Q${qPattern}*")
    }

    violationRules {
        rule {
            enabled = true
            element = "CLASS"

            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.10".toBigDecimal()
            }

            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "200".toBigDecimal()
            }

            excludes = Qdomains
        }
    }

    var excludes = mutableListOf<String>()
    excludes.add("com/yolo/jean/config")
    excludes.add("com/yolo/jean/global")
    excludes.add("com/yolo/jean/global")
    excludes.add("com/yolo/jean/reply/service/ReplyService.class")
    excludes.add("com/yolo/jean/board/service/BoardSearchService.class")

    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTreematching {
            exclude(excludes)
        }
    )
}
```

뭔가.. 스크립트가 장황하게 작성되었지만 사실 별거 없는 설정들입니다. 

`jacocoTestReport` 테스크 설정부터 살펴보면 원하는 리포트를 활성화 할 수 있도록 `true/false`로 설정이 가능합니다. 저 같은 경우에는 html, csv 파일만 리포트로 생성하도록 활성화 시켰습니다.

- classDirectories.setForm으로 JacocoTestReport 테스크에서 리포트 생성으로 제외시킬 클래스들이 속한 패키지들을 명시할 수 있습니다.

저 같은 경우 config 파일들은 딱히 볼 필요가 없기 때문에 제외시켰습니다.

그 다음 코드 커버리지 기준에 대한 룰을 정의하는 jacocoTestCoverageVerification Task에 대해서 살펴보겠습니다.

```kotlin
violationRules {
    rule {
        enabled = true
        element = "CLASS" // 커버리지를 체크할 기준

        limit {
            counter = "BRANCH"
            value = "COVEREDRATIO"
            minimum = "0.80".toBigDecimal()
        }

        limit {
            counter = "LINE"
            value = "TOTALCOUNT"
            maximum = "200"toBigDecimal()
        }

        excludes = Qdomains
    }
}
```

- counter: limit 메서드를 통해 지정할 수 있으며 `커버리지 측정의 최소 단위`를 말합니다. 이때 측정은 java byte code가 실행된 것을 기준으로 측정되고, 총 6개의 단위가 존재합니다.

    - BRACNH: 조건문 등의 분기 수
    - CLASS: 클래스 수, 내부 메서드가 한번이라도 실행된다면 실행된 것으로 간주함
    - COMPLEXITY: 복잡도
    - INSTRUCTION: Java 바이트코드 명령 수
    - METHOD: 메서드 수, 메서드가 한 번이라도 실행된다면 실행된 것으로 간주함
    - LINE: 빈 줄을 제외한 실제 코드의 라인 수, 라인이 한번이라도 실행되면 실행된것으로 간주함

> 값을 지정하지 않으면 Default 값은 `INSTRUCTION` 입니다.

- value: limit 메서드를 통해서 지정할 수 있으며 측정한 커버리지를 어떠한 방식으로 보여줄 것인지를 말합니다. 총 5개의 방식이 존재합니다.

    - COVEREDCOUNT: 커버된 개수
    - COVEREDRATIO: 커버된 비율, 0부터 1사이의 숫자로 1이 100% 입니다.
    - MISSEDCOUNT: 커버되지 않는 개수
    - MISSEDRATIO: 커버되지 않는 비율, 0부터 1사으이 숫자로 1이 100% 입니다.
    - TOTALCOUNT: 전체 개수

저는 브랜치 커버리지를 `80% 이상` 달성하도록 룰을 정의했습니다.


- minium: 마찬가지로 limit 메서드를 통해 지정할 수 있으며 counter 값을 value에 맞게 표현했을 때 `최솟값`을 말합니다. 이 값을 통해 `jacocoTestCoverageVerification`의 성공 여부가 결정됩니다.

- excludes: 커버리지 측정할 때 제외할 클래스를 지정할 수 있습니다. 패키지 레벨의 경로로 지정하여야 하고 경로에는 와일드 카드로 `*, ?`를 사용할 수 있습니다.

그리고 저는 QueryDSL을 사용하기 때문에 QClass 메타 모델들은 코드 커버리지에 제외하도록 excludes로 설정했습니다. 

### 최종 build.gradle.kts

최종적으로 작성한 build.gradle.kts는 아래와 같습니다.


```kotlin
plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa") apply false
    kotlin("kapt")
    id("io.spring.dependency-management")
    id("org.springframework.boot")
    jacoco
}

allprojects {
    repositories {
        mavenCentral()
    }

    group = "com.yolo.jean"   
    version = "0.0.1-SNAPSHOT" 
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-spring")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "maven")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")

    dependencies {
        kapt("org.springframework.boot:spring-boot-configuration-processor")
        implementation(kotlin("stdlib-jdk8"))
        implementation(kotlin("reflect"))
    }

    java.sourceCompatibility = JavaVersion.VERSION_11

    jacoco {
        toolVersion = "0.8.6"
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            html.isEnabled = true
            html.destination = file("$buildDir/reports/myReport.html")
            csv.isEnabled = true
            xml.isEnabled = false
        }

        var excludes = mutableListOf<String>()
        excludes.add("com/yolo/jean/config")
        excludes.add("com/yolo/jean/global")
        excludes.add("com/yolo/jean/kafka")
        excludes.add("com/yolo/jean/board/dto")
        excludes.add("com/yolo/jean/reply/dto")

        classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                exclude(excludes)
            }
        )
        finalizedBy(tasks.jacocoTestCoverageVerification)
    }

    tasks.jacocoTestCoverageVerification {

        var Qdomains = mutableListOf<String>()

        for (qPattern in 'A' .. 'Z') {
            Qdomains.add("*.Q${qPattern}*")
        }

        violationRules {
            rule {
                enabled = true
                element = "CLASS"

                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.10".toBigDecimal()
                }

                limit {
                    counter = "LINE"
                    value = "TOTALCOUNT"
                    maximum = "200".toBigDecimal()
                }

                excludes = Qdomains
            }
        }

        var excludes = mutableListOf<String>()
        excludes.add("com/yolo/jean/config")
        excludes.add("com/yolo/jean/global")
        excludes.add("com/yolo/jean/global")
        excludes.add("com/yolo/jean/reply/service/ReplyService.class")
        excludes.add("com/yolo/jean/board/service/BoardSearchService.class")

        classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                exclude(excludes)
            }
        )
    }

    tasks.test {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }

    tasks.compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.compileTestKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.bootJar {
        enabled = false
    }


    tasks.jar {
        enabled = true
    }
}

tasks.bootJar {
    enabled = false
}
```

test task 수행 시 junit5로 동작하도록 설정 하였고, 반드시 jacocoTestReport 테스크 수행 전에 test를 수행하도록 `finalizedBy`로 테스크 순서를 지정해줘야 합니다. 


jacocoTestCoverageVerification를 jacocoTestReport 이후에 수행하도록 선언한 이유는 만약 커버리지를 달성하지 못하면 gradle 빌드가 멈추게 되고 리포트 생성이 안되기 때문에 이전에 생성된 리포트를 볼 수 있는 가능성이 생기게 됩니다. 따라서 리포트를 생성 후 커버리지 측정을 하도록 설정했습니다. 

즉, `test -> jacocoTestReport -> jacocoTestCoverageVerification` 순서로 task를 진행하도록 설정해줘야 합니다.


## Jacoco HTML Report 

아래 이미지는 JacocoTestReport로 생성된 html 입니다. 


![image](https://user-images.githubusercontent.com/22395934/175803634-3768210d-349c-4bb6-b04c-08b5d79418c8.png)


![image](https://user-images.githubusercontent.com/22395934/175803599-7b7a1e4b-6198-4a86-87da-180a680392d6.png)

각 커버리지 항목마다 총 개수와 놓친 개수를 친절하게 표시해주고 있습니다. 저는 프로젝트에서 라인 커버리지와 브랜치 커버리지를 모두 확인하기 때문에 각각의 커버리지 정보도 함께 보여주고 있습니다.

![image](https://user-images.githubusercontent.com/22395934/175803739-740d47be-bc25-4111-802d-4cf301ad1752.png)

리포트에서 클래스의 메서드를 클릭하면 해당 메서드에서 커버된 라인들과 놓친 라인들도 확인이 가능합니다.


![image](https://user-images.githubusercontent.com/22395934/175804501-60a6cedf-5e47-4992-8fbe-ade99c604d79.png)

위 이미지는 코드 커버리지를 충족시키지 못할 경우 Gradle build가 실패나면서 커버리지 충족 기준들을 보여주고 있습니다.

> 참조: https://tecoble.techcourse.co.kr/post/2020-10-24-code-coverage/, [우아한형제 기술 블로그](https://techblog.woowahan.com/2661/), https://seller-lee.github.io/java-code-coverage-tool-part2
