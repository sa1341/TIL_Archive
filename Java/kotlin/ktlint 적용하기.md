# ktlint 적용하기

이번에 Kotlin 기반의 프로젝트를 진행하면서 `ktlint`라는 모듈을 적용해보기로 했습니다.

기존에 `eslint`처럼 Javascript에서 사용하는 플러그인은 써봤지만 ktlint는 처음 사용해봤기 때문에 좀 더 구글링을 통해서 자세히 알아보기로 했습니다.

## ktlint란?

ktlint는 eslint 플러그인과 마찬가지로 Kotlin 코드에 대해서 lint 규칙을 설정할 수 있는 모듈입니다.

기본적으로 Kotlin 공홈에서는 코딩 컨벤션 가이드를 제공하고 있습니다. 

ktlint는 [Kotlin 공식 코딩 컨벤션 가이드](https://kotlinlang.org/docs/coding-conventions.html#names-for-backing-properties)와 일치하는 프로그래밍을 할 수 있도록 지원합니다.

기본적으로 `kitlint`는 jcenter에 배포되어 있고, jar 파일형태로 import하여 별도의 Gradle plugin 없이 사용이 가능하지만 ktlint 공홈에서는 Gradle pugin을 더 권장하고 있습니다.


## .editorconfig 사용

`ktlint`는 .editorconfig 파일에 선언된 규칙을 포함하여 코드 스타일을 검사합니다.

.editorconfig는 다양한 에디터와 IDE에서 공통적으로 지원하는 코드 스타일에 대한 설정 파일입니다.

실제 파일 경로는 프로젝트 root 위치에 직접 만들어서 생성하였습니다.

### .editorconfig 적용 예제

```kotlin
root = true

[*]
charset = utf-8
end_of_line = lf
indent_style = space
trim_trailing_whitespace = true
indent_size = 4
max_line_length = 120
tab_width = 4

[{*.yaml,*.yml}]
indent_size = 2

disabled_rules = no-wildcard-imports,import-ordering,comment-spacing
```

실제 저의 예제 프로젝트에서 ktlint가 참고할 설정 파일입니다. 

### .editorconfig 파일 이점

ktlint는 .editorconfig 파일이 없어도 default 규칙으로 실행이 가능하지만, 의도치 않게 상위 디렉토리의 .editorconfig가 참조되는 경우를 막기 위해서 권고되어 집니다.

위에서 `root = true`는 .editorconfig 파일의 규약으로, 이 파일을 지원하는 도구들은 `root = true` 선언이 된 .editorconfig 파일을 찾기 전까지는 모든 상위 디렉토리를 탐색합니다.

이렇게 root 경로에 .editorconfig 파일을 설정하면서 얻을 수 있는 이점으로는 ktlint 버전이 올라가면서 기존의 규칙의 디폴트 값이 변경되는 경우 대비가 가능하다는 이점이 있습니다.

## IntelliJ에 ktlint 적용하기

위에서 대략적인 ktlint 개념에 대해서 알아보았다면 실제 백문이 불여일견이라고..  IntelliJ로 직접 샘플 프로젝트를 만들어서
`ktlint`를 적용했습니다.

build.script는 kts 기반의 DSL로 작성하였습니다.


### build.gradle.kts

```kotlin
plugins {
    ...
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("org.jlleitschuh.gradle.ktlint-idea") version "10.2.1"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    ...
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

group = "com.junyoung"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.1.3")
    implementation("org.apache.httpcomponents.client5:httpclient5-fluent:5.1.3")
    implementation("io.github.microutils:kotlin-logging:2.1.23")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("mysql:mysql-connector-java")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

...
```

위 설정 후에 실제 ktlint가 정상 동작하는지 스타일을 검사하도록 실행해봤습니다.

### 스타일 검사

```gradle
./gradlew 사tlintCheck
```

```kotlin
fun main() {
    println("Duration: ${Duration.ofSeconds(1).toSeconds()}")println("Duration: ${Duration.ofSeconds(1).toSeconds()}")println("Duration: ${Duration.ofSeconds(1).toSeconds()}")println("Duration: ${Duration.ofSeconds(1).toSeconds()}")
}
```

.editorconfig 파일에서 정의한 `max_line_length = 120`이 넘어가는 경우에 어떻게 보여주는지 궁금해서 위의 예제코드를 작성한 다음 `ktlintCheck` task를 수행해본 결과는 아래와 같았습니다.

```
BUILD FAILED in 3s
7 actionable tasks: 2 executed, 5 up-to-date

 ✘ limjun-young  ~/workspace/privacy/dev/cicd-gradle-project   main ●✚  cat /Users/limjun-young/workspace/privacy/dev/cicd-gradle-project/build/reports/ktlint/ktlintTestSourceSetCheck/ktlintTestSourceSetCheck.txt
/Users/limjun-young/workspace/privacy/dev/cicd-gradle-project/src/test/kotlin/com/junyoung/cicdgradleproject/practice/Practice01.kt:6:1: Exceeded max line length (120)
```

빌드가 실패나면서 reports 디렉토리 하위에 텍스트 파일이 생성된 것을 확인 할 수 있었습니다.
해당 파일을 열어보니 라인 최대 길이가 120자를 넘었다고 나옵니다.

그리고 다시 120자 이내로 수정 후 `ktlintCheck`를 수행해보니 정상으로 빌드 된 것을 확인 할 수 있었습니다.

참고로 Gradle에서는 `ktlintFormat` task도 지원해주는데 이건 자동으로 코딩 컨벤션에 맞게 스타일을 일괄로 변환해서 수정해주는 테스크입니다. 

```gradle
./gradlew ktlintFormat
```

하지만 `ktlintFormat`은 가끔 파일이 삭제될 수 있는 리스크가 있을 수 있어서 왠만하면 권하지 않는다고 합니다.

