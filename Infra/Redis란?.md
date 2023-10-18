# Redis란?

백엔드 공부를 하면서 성능을 더 높이기 위해 빠르게 조회용으로 쓰는 NoSQL에 대해서 많이 들어봤습니다. 그 중에서 대표적인 몽고 DB와 Redis가 유명한데 Redis가 무엇인지 궁금하여 찾아봤습니다.

## 1. Redis의 특징

Redis는 In-Memory 기반의 `키-값` 형식의 비정형 데이터 구조를 가졌습니다. 따라서 별도의 쿼리 없이도 데이터를 간단히 조회할 수 있습니다.

Redis는 크게 String, Set, Sorted Set, Hash, List 자료구조를 지원하고, 서비스의 특성에 따라서 캐시로도 사용가능하고, Persistence Data Storage로 사용할 수도 있습니다.

## 2. 캐시 사용의 이점

서비스 요청이 증가할수록, 특히 DB에서 데이터를 조회하는 경우에는 많은 부하를 줄 수 있습니다. 이런 상황에서 `나중에 요청된 결과를 미리 저장해두었다가 빨리 제공하기 위해 캐시를 사용합니다.`

Redis는 메모리 기반이기때문에 디스크에 비해서 용량은 적지만 접근 속도는 빠릅니다. 

## 3. 캐시 사용 방법

![Untitled Diagram (2)](https://user-images.githubusercontent.com/22395934/119265876-6045da80-bc23-11eb-8f19-93c47433cd08.png)


- Look aside cache 방식
    - (1) 웹 서버는 클라이언트 요청을 받아서, 데이터가 존재하는지 캐시를 먼저 확인합니다.
    - (2) Cache에 데이터가 있으면 조회를 하고, 만약 없으면 DB에서 읽어서 캐시에 저장 후 클라이언트한테 데이터를 전달합니다.

- Write Back
    - 데이터를 캐시에 전부 먼저 저장해놓았다가 특정 시점마다 한번씩 캐시 내 데이터를 DB에 INSERT 하는 방법입니다.
    - Insert를 1개씩 500번 수행하는 것보다 500개를 한번에 삽입하는 동작이 훨씬 빠르고, write back 방식도 성능면에서는 뒤쳐지지 않다고 합니다.
    - 단점은 데이터를 일정 기간 동안은 유지하고 있어야 하고, 이때 이걸 유지하고 있는 Storage는 메모리 공간이므로 서버 장애 상황에서 데이터가 손실될 우려가 있습니다. 그래서 재생 가능한 데이터나, 극단적으로 무거운 데이터에서 write back 방식을 많이 사용합니다.


## 3. Redis의 특징

Redis는 Collection을 제공합니다. 주로 게임에서 사용자가 많은 랭킹을 산출할 경우에도 많이 사용하고, `Sorted Set`을 사용하면 랭킹 서버를 쉽게 구현가능합니다.  

`Redis Transaction`은 한번의 딱 하나의 명령만 수행할 수 있습니다. 이에 더하여 `single-thread` 특성을 유지하고 있기 때문에 다른 스토리지 플랫폼보다는 이슈가 덜합니다. 하지만 이러한 특징때문에 더블클릭 같은 동작으로 같은 데이터가 2번씩 들어가게 되는 불상사는 막을 수 없기 때문에 별도 처리가 필요합니다.

주로 인증 토큰을 저장하거나 유저 API limit을 두는 상황 등에서 레디스를 많이 사용하고 있습니다.

## 4. Spring Boot와 Redis 연계하기

간단하게 어떻게 Redis를 설치하고 Spring Boot와 연계하는지 실습을 해봤습니다.

### Docker 설치 

먼저, Redis를 Docker를 사용하여 설치하였습니다. 도커는 URL을 통해 설치하시면 됩니다. 

[Docker 설치하기](https://www.docker.com/get-started)

### Redis 설치 
[Redis 설치 및 실행 방법](https://emflant.tistory.com/235)

## Redis 기본명령어 정리

```sh
keys *2 <- 현재 저장되어 있는 key 중에 2로 끝나는 key만 검색 

rename 1113 1116 <- key:1113을 key:1116으로 변경

randomkey <- 현재 key 중에 랜덤으로 key 검색

exists 1116 <- 1116의 존재여부 검색 (1이면 존재, 0이면 존재하지 않음)

strlen 1111 <- key 1111의 value 길이

flushall <- 현재 저장되어 있는 모든 key 삭제

setex 1111 30 "JEAN CALM" <- EX는 expire(일정시간 동안만 저장)

ttl 1111 <- 현재 남은 시간 확인

mset 1113 "NoSQL User Group" 1115 "PIT" <- 여러 개 필드를 한번에 저장

mget 1113 1115 <- mset에 저장된 값을 한번에 다중 검색

mget 1113 1115 nonexisting <- nonexisting은 존재하지 않는 경워 nil 출력

mset seq_no 20231017 <- 연속번호 발행을 위한 key/value 저장

incr seq_no <- Incremental 증가값 +1

decr seq_no <- Decremental 증가값 -1

append 1115 " co." <- 현재 value에 value 추가

save <- 현재 입력한 key/value 값을 파일로 저장

info <- Redis 서버 설정 상태 조회 명령어

time <- 데이터 저장시간
```


### 의존성(dependency) 추가

```kotlin
api("org.springframework.boot:spring-boot-starter-data-redis")
```

스프링 부트는 기본적으로 Redis 포트를 `6379`로 설정하고 있습니다. 
따라서 yml 파일에 별도로 설정이 필요하지 않습니다.

#### StringRedisTemplate

저는 Java 기반인 코틀린을 사용하여 Redis의 명령어들을 사용하였습니다.

```kotlin
@Component
class RedisRunner(val redisTemplate: StringRedisTemplate): ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        val values = redisTemplate.opsForValue();
        values.set("junyoung", "30")
        values.set("hobby", "coding study")
        values.set("dream", "take a walking with my puppy")
    }
}
```

StringRedisTemplate을 의존성으로 주입받아서 set으로 `key, value`를 저장하였습니다.

> opsForValue() 메서드는 value관련된 operation들을 제공하는 객체를 받아옵니다.

#### DokotlinApplication

```java
@EnableJpaAuditing
@SpringBootApplication
class DokotlinApplication(var boardRepository: BoardRepository) {

fun main(args: Array<String>) {
	runApplication<DokotlinApplication>(*args)
}
```

스프링 부트 어플리케이션을 실행하면 아래와 같이 `key-value`가 들어간것을 Redis Client에서 확인이 가능합니다.

#### 실행 결과

![image](https://user-images.githubusercontent.com/22395934/119310255-72b72700-bcaa-11eb-9591-ce9d024bb21e.png)

`keys *` 명령어는 Redis에 저장된 key 값들을 전부 조회하는 명령어로 테스트를 위해서 사용하였지만, 실무에서는 사용하지 않을 것을 권고하고 있습니다. 

> get hobby 명령어를 실행하면 hobby에 매칭되는 value인 "coding study"가 나오는 것을 확인할 수 있었습니다.

> 참조 사이트: https://velog.io/@max9106/Spring-Boot-Redis
