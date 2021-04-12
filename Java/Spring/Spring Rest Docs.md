
# Spring Rest Docs란?

Spring Rest Docs는 테스트 코드를 기반으로 자동으로 API 문서를 작성할 수 있게 도와주는 프레임워크입니다.

그렇기 때문에 반드시 Test가 통과되어야 문서가 작성 된다는 장점이 있습니다. 

>
Test 통과과 전제조건이기 때문에 API Spec이 변경되거나 추가/삭제 되는 부분에 대해 항상 테스트 코드를 수정해야되고, API 문서가 최신화 될 수 있도록 해줍니다.

처음에는 마크다운이 저에게 익숙하기 때문에 마크다운으로 API 문서를 작성하려고 했지만, 설정을 하는 부분도 번잡하고, 대부분의 큰 서비스 회사에서도 asciidoc을 채택하는 것 같아서 asciidoc을 사용하여 문서를 작성하기로 했습니다. asciidoc은 마크다운과 비슷하게 html를 작성할 수 있는 언어입니다.

## 1. Spring Rest Docs 설정 방법

일단, 의존성 라이브러리로 JUnit, MockMvc, asciidoc을 선택하여 최소한의 라이브러리로 구현하였습니다.

#### build.gradle

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '2.2.2.RELEASE'
    id 'io.spring.dependency-management' version '1.0.8.RELEASE'
    id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"
    id "org.asciidoctor.convert" version "1.5.9.2" // (1)
}

group 'com.kakaopay'
version '1.0-SNAPSHOT'
sourceCompatibility = '1.8'

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor 'org.projectlombok:lombok'
    implementation 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.projectlombok:lombok'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'com.querydsl:querydsl-jpa'
    implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.8'
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    testImplementation 'org.springframework.restdocs:spring-restdocs-mockmvc' // (5)
}

test {
    useJUnitPlatform()
}

// (2)
asciidoctor {
    dependsOn test  
}

// (3)
bootJar {
    dependsOn asciidoctor
    from("${asciidoctor.outputDir}/html5") {
        into "BOOT-INF/classes/static/docs"
    }
}

// (4)
task copyDocument(type: Copy) {
    dependsOn asciidoctor
    from file("build/asciidoc/html5")
    into file("src/main/resources/static/docs")
}

// (6)
build {
    dependsOn copyDocument
}

def querydslDir = "$buildDir/generated/querydsl"

querydsl {
    jpa = true
    querydslSourcesDir = querydslDir
}
sourceSets {
    main.java.srcDir querydslDir
}
configurations {
    querydsl.extendsFrom compileClasspath
}
compileQuerydsl {
    options.annotationProcessorPath = configurations.querydsl
}
```

> dependsOn은 종속 테스크를 지정할 때 사용하는 기능입니다.


#### 테스크 순서

>
1. (1), (2)는 asciidoc 파일을 컨버팅하고 Build 디렉토리로 복사하기 위한 플러그인 입니다. asciidoctor Task를 통해 html 문서로 processing 되어 build/asciidoc/html5 하위에 html 문서로 생성이 됩니다.
2. (3)은 gradle build 시에 asciidoc -> bootJar 순으로 수행이 되고, (4)은 실제 배포 시, BOOT-INF/classes가 classpath가 되기 때문에 아래와 같이 파일을 복사해야 합니다.
3. (5)는 mockMvc를 restdocs에 사용할 수 있게 하는 라이브러리 입니다.
4. (6)은 build 테스크를 수행하기 전에 소스 코드에 html파일을 복사하는 테스크 작업입니다.


## 2. RestDocs 예제 코드

#### ApiDocumentUtils 클래스

```java
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

public interface ApiDocumentUtils {
    static OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(
                modifyUris() // (1)
                        .scheme("http")
                        .host("investment.api.com")
                        .port(8080),
                prettyPrint());
    }

    static OperationResponsePreprocessor getDocumentResponse() {
        return preprocessResponse(prettyPrint());
    }
}
```

위의 ApiDocumentUtils 클래스의 getDocumentRequest() 메서드는 문서상 uri를 기본 값인 `http://localhost:8080`에서 `http://investment.api.com`으로 변경하기 위해 사용합니다.

prettyPrint()는 문서의 request를 보기좋게 출력하기 위해 사용합니다.
getDocumentResponse() 메서드 역시 문서의 response를 보기 좋게 출력하는 용도로 사용합니다.


#### InvestmentRestControllerTests 클래스

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class InvestmentRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    WebApplicationContext context;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @DisplayName("회원이 특정 투자상품에 투자 요청을 정상적으로 처리한다.")
    @Test
    public void 투자결과_리턴_테스트() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("itemId", 1);
        jsonObject.put("investingAmount", 100000);
        String jsonBody = jsonObject.toString();

        ResultActions result = mockMvc.perform(post("/api/investment")
                .content(jsonBody)
                .header("X-USER-ID", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("success")))
                .andDo(document("investment",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("itemId").type(JsonFieldType.NUMBER).description("투자상품"),
                                fieldWithPath("investingAmount").type(JsonFieldType.NUMBER).description("투자 금액")
                        ),
                        responseFields(
                                fieldWithPath("result").type(JsonFieldType.STRING).description("투자 결과")
                        )
                ));
    }
}
```

기본적으로 MockMvc 기반으로 테스트를 하기 전에 setUp()에서 두 가지 방식으로 진행해야 하는데, 저는 webAppContextSetup을 이용하여 WebApplicationContext의 인스턴스로 작동하도록 셋업을 하였습니다. 이렇게 하면 스프링 컨트롤러는 물론 의존성까지 로드되기 때문에 완전한 통합 테스트를 할 수 있습니다.

나머지 하나는 아래 코드처럼 standaloneSetup()을 사용하여 한 컨트롤러에 집중하여 테스트 하는 용도로만 사용한다는 점에서 유닛 테스트와 유사합니다.

```java
this.mockMvc = MockMvcBuilders
            .standaloneSetup
            .apply(documentationConfiguration(restDocumentation))
            .build()
```

JUnitRestDocumentation는 JUnit 프레임워크로 RestDocs를 실행하기 때문에 필요한 객체 입니다. 

requestFields, responseFields 메서드는 사용자 정의 API 스펙에 따라서 요청 필드, 응답 필드를 정의할 수 있습니다.

> requestFields(fieldWithPath), responseFields(fieldWithPath())는 request-fields asciidoc, response-fields asciidoc 파일들을 생성 합니다.

위 테스트 코드가 성공하면 `build/generated-snippets/investment 디렉토리 하위에 adoc 파일이 생성되는 것을 확인할 수 있습니다.

![image](https://user-images.githubusercontent.com/22395934/114401793-36d66f80-9bde-11eb-9acf-bde81ddb21bf.png)


## 3. Snippet 문서

RestDocs에 대한 명세내역을 코드에 작성한 후 테스트 케이스가 정상적으로 실행이 되었다면 RestDocs의 내용을 기준으로 Snippet 문서가 생성되는데, Gradle 기준으로 build/generated-snippets 디렉토리 하위에 생성이 됩니다.

> 
이 Snippet이란 문서의 조각을 의미합니다. Gradle 빌드 스크립트에서 asciidoctor로 직접 작성한 문서와 Spring MVC 테스트를 통해 자동 생성된 스니펫을 결합해서 웹 문서를 만듭니다.`

## 4. Snippet 종류

- curl-request.adoc : 호출에 대한 curl 명령을 포함 하는 문서
- httpie-request.adoc : 호출에 대한 http 명령을 포함 하는 문서
- http-request.adoc : http 요청 정보 문서
- http-response.adoc : http 응답 정보 문서
- request-body.adoc : 전송된 http 요청 본문 문서
- response-body.adoc : 반환된 http 응답 본문 문서
- request-parameters.adoc : 호출에 parameter 에 대한 문서
- path-parameters.adoc : http 요청시 url 에 포함되는 path - parameter 에 대한 문서
- request-fields.adoc : http 요청 object 에 대한 문서
- response-fields.adoc : http 응답 object 에 대한 문서

이중에서 RestDocs 문서를 만들기 위해 사용될때 가장 우선순위가 높은 문서는 `(curl-request.adoc, request-parameters.adoc, path-parameters.adoc, request-fields.adoc, response-fields.adoc)` 가 되는데 이 외의 스니펫 문서는 필요에 따라 선택적으로 포함 시켜 줍니다.




## 5. asciidoc html 파일 작성

이제 Snippet 문서를 html 문서로 생성하기 위해 `src/docs/asciidoc/api-guide.adoc` 문서를 작성하면 됩니다.

![image](https://user-images.githubusercontent.com/22395934/114402079-756c2a00-9bde-11eb-957e-213a881e6917.png)

> 참고로 Gradle에서는 Maven이랑 다르게 src/docs/asciidoc 디렉토리 하위에 adoc 문서를 생성해야 합니다. 그렇지 않으면 에러가 발생합니다.


```asciidoc
ifndef::snippets[]
:snippets: ../../../build/generated-snippets
endif::[]
:doctype: investment
:icons: font
:source-highlighter: highlightjs
:toc: left
:toclevels: 2
:sectlinks:
:operation-http-request-title: Example Request
:operation-http-response-title: Example Response

[[resources]]
= Resources

[[resources-investment]]
== Investment

[[resources-investment]]
=== 정보 가져 오기
include::{snippets}/investment/http-request.adoc[]
include::{snippets}/investment/http-response.adoc[]
include::{snippets}/investment/response-fields.adoc[]
include::{snippets}/investment/request-fields.adoc[]
```

저 같은 경우에는 외부 API를 사용하기 위해서 정말 필요하다고 생각되는 snippet만 adoc 파일에 포함시켰습니다. `include::~` 이 부분이 실제 html 태그안에 삽입하는 부분입니다.

이제 Gradle로 build 테스크를 수행하면 위 스크립트에서 작성한 `static/docs/api-guide.html` 파일이 생성되는 것을 확인할 수 있고, `http://localhost:8080/docs/api-guide.html`로 접근이 가능하면 아래와 같은 웹 문서가 렌더링 됩니다.

#### 실행 결과

![스크린샷 2021-04-12 오후 3 21 27](https://user-images.githubusercontent.com/22395934/114400576-122dc800-9bdd-11eb-90f2-9ffe7487e622.png)


> 참조 사이트: https://jogeum.net/16, https://jaehun2841.github.io/2019/08/04/2019-08-04-spring-rest-docs/#userkt, [우아한 형제 기술 블로그](https://woowabros.github.io/experience/2018/12/28/spring-rest-docs.html)
