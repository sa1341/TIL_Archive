# Spring Cloud Config란?

MSA 환경에서 각 서비스에서 필요한 설정 `property`들을 어떻게 관리하고 읽어올수 있는지 구글링을 통해서 여러 문서와 블로그를 찾아보았고 그중에서 `Spring Cloud Config`에 대해서 아주 기본적인 부부만 `얕게` 살펴봤습니다.


## 1. Spring Cloud Config 살펴보기

Spring Cloud Config의 정의는 [공홈](https://cloud.spring.io/spring-cloud-config/reference/html/#_spring_cloud_config_server)에서 들어가면 아래 문구로 설명하고 있습니다.

```
Spring Cloud Config provides server-side and client-side support for externalized configuration in a distributed system.

With the Config Server, you have a central place to manage external properties for applications across all environments. 
```

직역하자면 Spring Cloud Config는 분산 시스템에서 외부화된 설정에 대해서 `client-server`측에 대한 지원을 제공해준다?

`Config Server`를 통해서 모든 환경 전반에 걸쳐서 외부 프로퍼티들을 관리하기 위한 중심지를 가지게 된다는 의미였습니다.

### 1.1 Spring Cloud Config Flow 다이어그램

사실 위 설명만으로는 잘 이해가 되지 않아서 Spring Cloud Config에 대해서 잘 정리한 [블로그](https://madplay.github.io/post/introduction-to-spring-cloud-config)를 참조하였고, 도식화된 이미지를 통해서 저도 증권에서 사용하게 될 경우 어떻게 설정 파일을 읽을 수 있을지 대략적인 다이어그램으로 표현해 봤습니다.

![Untitled Diagram drawio](https://user-images.githubusercontent.com/22395934/179750754-e47b3715-40fb-42f0-a237-6a40ce413543.png)


## 2. Spring Cloud Config 실습예제

실제로 문서와 블로그로는 크게 와닿지 않아서 직접 간단한 Spring Cloud Config 샘플 예제를 작성해 봤습니다.

### 2.1 Spring Cloud Config Server 프로젝트 생성

먼저, `Git Repositry`를 통해서 외부 설정 파일을 가져오는 Spring Cloud Config Server 모듈부터 생성해보았습니다.

`Spring Cloud Config Server`라는 단어가 너무 길어서 그냥 Config 서버라고 표현하겠습니다. 

![스크린샷 2022-07-19 오후 9 53 01](https://user-images.githubusercontent.com/22395934/179754951-00a50aa9-fb97-4cfd-a93c-04c14c763666.png)

아래는 `Spring Cloud Config Server를 위한 dependency 입니다.`

#### build.gradle.kts

```kotlin
...

extra["springCloudVersion"] = "2021.0.3"

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.cloud:spring-cloud-config-server")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

...
```

아래 Spring Boot를 실행하는 클래스에 `@EnableConfigServer` 어노테이션을 추가하면 외부 설정값들을 제공하기 위한 API를 제공해주는 Config Server의 역할을 하게 됩니다. 

>> 제공해주는 API 목록은 [공홈](https://cloud.spring.io/spring-cloud-config/reference/html/#_setting_http_connection_timeout)에서 확인 할 수 있습니다.

```kotlin
@SpringBootApplication
@EnableConfigServer
class SpringCloudConfigServerApplication

fun main(args: Array<String>) {
	runApplication<SpringCloudConfigServerApplication>(*args)
}
```

#### application.yml

```kotlin
server:
  port: 8088

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/sa1341/spring-cloud-config-repository
          search-paths: fund,account // 설정파일이 위치한 sub-directory
```

이제 외부 설정파일을 어떻게 가져올지는 `yml` 파일에 정의하면 기본적인 셋팅은 끝이납니다. 
`spring.cloud.config.server.git.uri`는 실제 사용자가 입력한 설정 값을 push 하는 git 저장소의 uri를 기입합니다.

> Spring Cloud Config Server Git 주소: https://github.com/sa1341/spring-cloud-config-server

### 2.2 Spring Cloud Config Repository 프로젝트 생성

이제 실제로 외부 설정파일들을 관리하는 `Git 저장소`를 생성합니다.

![image](https://user-images.githubusercontent.com/22395934/179759402-c0ba5bb9-c404-45ea-aa55-25f9a4ad9233.png)

설정 파일을 생성할때는 `{application}-{profile}.yml` 형태로 생성합니다. 저 같은 경우에는 `fund-config`로 application 이름을 만들었습니다. 

이제 GitHub에 `PUSH`만 하면 Config 저장소 설정도 끝이납니다.

![image](https://user-images.githubusercontent.com/22395934/179760221-8dee3327-677f-4224-81d8-763eefbb0182.png)

>> Git 저장소의 주소는 반드시 Config Server에서 작성한 uri랑 동일해야 합니다.

#### Config 파일 테스트

위에서 생성한 `Config Server`를 구동하고 PostMan으로 실제 Git Repository에서 외부 설정 값들을 가져오는지 테스트 해봤습니다.

![image](https://user-images.githubusercontent.com/22395934/179760760-d99b9cc8-0e0b-4cca-801e-39491b42f656.png)

`Git Repository`에 있는 fund 디렉토리 하위에 있는 `fund-config-sandbox.yml`에 정의된 `fund.service.url": "https://sandbox.fund-channel-server.com` 를 정상 조회되는 것을 확인할 수 있었습니다. 

> Spring Cloud Config Repositry Git 주소: https://github.com/sa1341/spring-cloud-config-repository

### 2.2 Spring Cloud Config Client 프로젝트 생성

이제 실제로 설정값을 주입받아서 사용해야 되는 각 서비스에 해당하는 `Spring Cloud Config Client` 모듈을 생성해봤습니다. 
마찬가지로 용어는 `Config 클라이언트`로 부르겠습니다. 

위의 도식화로 봤을 때는 `펀드, 계좌, 매매 서비스`가 Config 클라이언트가 됩니다.

![image](https://user-images.githubusercontent.com/22395934/179763293-8dd5df7a-1822-43c0-a728-d0371cbe564e.png)


`dependency` 추가는 아래와 같습니다.

```kotlin
...

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.cloud:spring-cloud-starter-config")
	...
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

...
```

어떻게 `Config Server`로부터 외부 설정 값을 받아올지에 대한 기본적인 설정정보를 `yml` 파일에 정의해봤습니다.

사실 이부분을 작성하는 부분에서 `공홈`에서는 스프링 부트가 기동될 때 `application.yml` 파일보다 먼저 읽어들이는 `bootstrap.yml` 파일에 config 설정 정보를 기입하라고 나왔지만, 블로그를 찾아보니 2.4 버전부터는 조금 변경이 되어서  `bootstrap.yml`이 아니라 `applicatio.yml`에 정의해야 된다고 합니다.

#### Spring Boot 2.4버전 이전 버전

```kotlin
server:
  port: 8089

spring:
  profiles: # 여기에 지정해도 되고, 실행할 때 지정해도 된다.
    active: dev
  application:
    name: config
  cloud:
    config:
      uri: http://localhost:8088

management:
  endpoints:
    web:
      exposure:
        include: refresh
```


#### Spring Boot 2.4버전 이후 버전

```kotlin
server:
  port: 8089

spring:
  profiles:
    active: dev
  application:
    name: spring-cloud-config-client
  config:
    import: optional:configserver:http://localhost:8088 // optional 접두사 제거 시 Config 서버와 연결할 수 없는 경우 
  cloud:
    config:
      name: fund-config

// Actuator의 config refresh 기능 활성화
management:
  endpoints:
    web:
      exposure:
        include: refresh
```

이제 Config 클라이언트가 기동하게 되면 Config 서버로 연결을 시도합니다. 

만약 `Config 서버`가 기동되지 않을 경우 `Config 클라이언트`에서는 설정 값을 주입받지 못해서 기동조차 되지 않는 것을 확인했습니다.

![image](https://user-images.githubusercontent.com/22395934/179767725-8f3a1eb0-c423-423d-8ba6-32f3257c0eca.png)

- management.endpoints. web.exposure.include: refresh: 이 설정 값은 Spring Boot Application을 모니터링 및 관리하는 Actuator가 제공해주는 `config refresh` EndPoint를 제공해주는 설정 값입니다.

> 이 값을 refresh로 설정한 이유는 설정값이 동적으로 변경되면 그 값을 다시 주입받기 위해서는 서비스를 다시 빌드와 배포를 해야되는데 `refresh`로 하게 되면 API(/actuator/refresh)를 통해서 graceful 하게 설정 값 refresh가 가능합니다.



### 2.2 Config 주입 테스트

이제 Config 클라이언트도 생성 및 설정이 다되었으니 실제로 API를 호출하여 `Config Server`부터 설정 값을 정상적으로 잘 받아왔는지 확인해봤습니다.

아래 이미지는 제가 만든 프로젝트 패키지 구조입니다. 예제는 참조한 블로그에서 작성한 대로 따라서 호출해봤습니다.

![image](https://user-images.githubusercontent.com/22395934/179770655-c6e0cff4-ac16-4601-96e7-cb3df0f4f3ec.png)

#### Controller

```kotlin
@RestController
class ConfigApi(
    private val staticConfigService: StaticConfigService,
    private val dynamicConfigService: DynamicConfigService
) {

    @GetMapping("/static")
    fun getConfigFromStatic(): Map<String, String> {
        return staticConfigService.getConfig()
    }

    @GetMapping("/dynamic")
    fun getConfigFromDynamic(): Map<String, String> {
        return dynamicConfigService.getConfig()
    }
}
```

#### Service

```kotlin
@Service
class StaticConfigService {

    @Value("\${fund.service.url}")
    lateinit var url: String

    fun getConfig(): Map<String, String> {
        var configMap = hashMapOf<String, String>()
        configMap["url"] = url

        return configMap
    }
}

@Service
@RefreshScope
class DynamicConfigService {

    @Value("\${fund.service.url}")
    lateinit var url: String

    fun getConfig(): Map<String, String> {
        var configMap = hashMapOf<String, String>()
        configMap["url"] = url

        return configMap
    }
}
```

서비스는 `StaticConfigService`와 `DynamicConfigService`를 작성했는데 둘의 차이점은 클래스 상위에 `@RefreshScope` 어노테이션 적용여부 뿐입니다. 이 어노테이션은 Config 저장소에서 값이 변경될 경우 Actuator를 통해서 다시 Config Refresh를 호출하면 설정 값이 갱신이 되는지 안되는지를 확인하기 위해서 작성한 예제 코드입니다.

- StaticConfigService 호출 결과

![image](https://user-images.githubusercontent.com/22395934/179772721-3243ec6a-5603-4c22-adf0-b7d3c550bf2e.png)

- DynamicConfigService 호출 결과

![image](https://user-images.githubusercontent.com/22395934/179772862-4b471f30-925a-448d-a7b8-c781575a82c0.png)

둘다 결과는 동일 합니다. 이제 Git Config 저장소에서 값을 아래처럼 변경해보고 Actuator를 통해서 `/actuator/refresh` EndPoint를 호출해보고 실제 재기동 없이 값이 변경됬는지 확인해보았습니다.

 - 변경전 fund.service.url: https://sandbox.fund-channel.server.com 
 - 변경 후 fund.service.url: https://sandbox.fund-channel.server.kakaopaysec.com 

![image](https://user-images.githubusercontent.com/22395934/179773874-b4ebc01b-7aea-4e59-a200-7cfb458e3cf8.png)


#### Config Refresh API 호출

![image](https://user-images.githubusercontent.com/22395934/179775588-73fb6e34-1e8e-41ed-ac91-b3a3aea8c11e.png)

- StaticConfigService 재호출 결과

![image](https://user-images.githubusercontent.com/22395934/179776030-a39cf426-216e-45a9-bd30-f46b2c387107.png)


- DynamicConfigService 재호출 결과

![image](https://user-images.githubusercontent.com/22395934/179775803-d8124dac-3d3f-4474-8a12-1825323497d0.png)

실제로 DynamicConfigService는 `@RefreshScope` 적용 때문에 재기동 없이도 Config Refresh API 호출 만으로 변경된 설정 값을 다시 주입받을 수 있는 것을 확인할 수 있는 예제였습니다.

## Spring Cloud Config 실습 후 고찰 

Spring Cloud 생태계가 워낙 잘 갖춰져 있었고, 아직 Spring Cloud Config에 대해서 공부해야 되는 부분이 끝도 없지만 공홈에서 이해되지 않는 부분은 그나마 좋은 블로그를 찾아보고 대략적으로나 정말 얕게...Spring Cloud Config가 이렇게 동작하는것을 조금이라도 알게 되었습니다. 

이제 `Spring Cloud Config`를 어떻게 증권 서비스에 접목시킬 수 있을지 고민을 해봐야 되는 포인트들은 실시간으로 운영환경에서 Config 값이 변경될때 마다 서비스 재기동 없이 어떻게 `Config Refresh`를 할 수 있는지 입니다.  

위에서 `Actuator`에서 제공해주는 `Refresh EndPoint`로 할 수는 있지만, 수동으로 해야되는 문제점이 있습니다. 만약 각 서비스(Config 클라이언트) 인스턴스가 많이 늘어날수록 수동으로 호출해야되는 Refresh API도 많아지게 되고 몇몇 인스턴스는 Refresh 호출이 누락될 가능성도 생기게 됩니다.

이 부분에 대해서도 자동화 할 수 있는 방안 역시 찾아 볼수 있었습니다.  `Spring Cloud Bus`라는 dependecy가 있는데 간단히 요약하면 카프카나 RabbitMQ 같은 메시지 플랫폼을 연계해서 Config 저장소에 변경 이벤트가 발생하면 `GitHub webhook`을 설정하여 Config Server로 Config 변경 이벤트를 보내게 되면 Config 서버에서 이벤트를 수신하고 Spring Cloud Bus에 갱신 이벤트를 전달하게 됩니다. 

갱신 이벤트를 전달받은 `Spring Cloud Bus`는 연결된 모든 클라이언트 인스턴스에게 Actuator의 Config Refhres API를 호출하여 서비스 재기동 없이 동적으로 변경된 Config를 주입받도록 하는 방법이 나와 있습니다. 이 부분은 별도로 다시 공부해서 정리해보겠습니다.

> Spring Cloud Config Client Git 주소: https://github.com/sa1341/spring-cloud-config-client

참조: https://madplay.github.io/post/introduction-to-spring-cloud-config,
https://cloud.spring.io/spring-cloud-config/reference/html/#_spring_cloud_config_server 
