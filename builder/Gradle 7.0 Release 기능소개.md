# Gradle 7.0 Release

이번에 기존에 구축되었던 프로젝트를 분석하면서 settings.gradle.kts 파일에서  `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
`라는 코드를 보게 되었는데, 이게 궁금해서 Gradle 공홈에서 직접 찾아봤습니다.

설명을 보면 `Gradle 7.0` 버전부터 지원해주는 실험적인 기능이라고 나와있었습니다. 일단 해당 기능을 활성화 시키는 방법은 아래와 같이 한줄만 추가하면 됩니다.  

```kotlin
rootProject.name = "spring-batch-study"

include("chapter02")
include("chapter03")
include("batch-domain")

// 프로젝트 TYPE-SAFE 하게 접근할 수 있도록 기능 활성화
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    ...
}
```

이 기능을 활성화 시키면 기존의 멀티모듈 기반의 프로젝트에서 의존성이 필요한 모듈의 dependency를 받아올 때 문자열 표현식이 아니라 코드로 안전하게 접근하여 빌드할 수 있도록 해주는 장점이 있습니다.

```kotlin
// enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS") 적용 전
dependencies {
    api(project(":batch-domain"))
    implementation("org.springframework.boot:spring-boot-starter-batch")
    runtimeOnly("mysql:mysql-connector-java")
    implementation("org.springframework.batch:spring-batch-test")
} 

// enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS") 적용 후
dependencies {
    implementation(projects.batchDomain)
    implementation("org.springframework.boot:spring-boot-starter-batch")
    runtimeOnly("mysql:mysql-connector-java")
    implementation("org.springframework.batch:spring-batch-test")
}
```

심지어 코드로 작성하기 때문에 `IntlliJ의 자동완성 + 스마트 컴파일`로 오류를 찾아낼 수 있는 장점도 있습니다.

기존에는 스크립트 방식으로 빌드를 하기 때문에 만약 특정 모듈의 이름이 변경되었거나, 삭제되면 프로젝트를 빌드 시에 확인할 수 있어서 확실히 휴먼에러를 줄일 수 있는 좋은 기능이라고 생각됩니다. 


Gradle 7.0이 Release 된지 꽤 되었는데.. 이제야 알게 되었습니다. 지금쯤이면 안정화가 이미 다 되어있어서.. 앞으로 멀티모듈 프로젝트 기반에서 꼭 활용하도록 해야겠습니다.

참조: https://proandroiddev.com/using-type-safe-project-dependencies-on-gradle-493ab7337aa
