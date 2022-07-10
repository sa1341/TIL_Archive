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


## 4. Redis의 특징

Redis는 Collection을 제공합니다. 주로 게임에서 사용자가 많은 랭킹을 산출할 경우에도 많이 사용하고, `Sorted Set`을 사용하면 랭킹 서버를 쉽게 구현가능합니다.  

`Redis Transaction`은 한번의 딱 하나의 명령만 수행할 수 있습니다. 이에 더하여 `single-thread` 특성을 유지하고 있기 때문에 다른 스토리지 플랫폼보다는 이슈가 덜합니다. 하지만 이러한 특징때문에 더블클릭 같은 동작으로 같은 데이터가 2번씩 들어가게 되는 불상사는 막을 수 없기 때문에 별도 처리가 필요합니다.

주로 인증 토큰을 저장하거나 유저 API limit을 두는 상황 등에서 Redis를 많이 사용하고 있습니다.

## 5. Spring Boot와 Redis 연계하기

간단하게 어떻게 Redis를 설치하고 Spring Boot와 연계하는지 실습을 해봤습니다.

### 5.1 Docker 설치 

먼저, Redis를 Docker를 사용하여 설치하였습니다. 도커는 URL을 통해 설치하시면 됩니다. 

[Docker 설치하기](https://www.docker.com/get-started)

### 5.2 Redis 설치 
[Redis 설치 및 실행 방법](https://emflant.tistory.com/235)



### 5.3 의존성(dependency) 추가

```kotlin
api("org.springframework.boot:spring-boot-starter-data-redis")
```

application.yml 설정 파일에 연동할 Redis의 접속정보를 기술합니다.

```kotlin
spring:  
  redis:
    host: localhost
    port: 6379
```

> 스프링 부트는 기본적으로 Redis 포트를 `6379`로 설정하고 있습니다. 

## 6. Redis 실습

### 6.1 StringRedisTemplate

저는 `Kotlin`을 사용하여 Redis의 명령어들을 사용하였습니다. 그 중 `strings` 타입의 데이터를 저장하는 `StringRedisTemplate`을 Spring Boot에서 사용하는 예제입니다.

먼저, StringRedisTemplate을 사용하기 위해 Bean을 정의해줍시다.

```kotlin
StringRedisTemplate

```

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

### 6.2 RedisEntity 실습

이번에는 Entity를 Redis에 저장하는 예제를 살펴보겠습니다.

```kotlin
@RedisHash(value = "fund")
class Fund(
    @Id
    val id: Long? = null

) {
    val name = "name-$id"
}

interface FundRedisRepository: CrudRepository<Fund, Long>
```

Redis 엔티티 Fund를 정의하였습니다. 
`@RedisHash("fund")`으로 해당 엔티티가 Redis 엔티티임을 명시하였습니다.

@Id 어노테이션이 적용된 맴버변수에 값이 실제 hash_id가 됩니다. 만약 null 값을 주게 된다면 Redis 내부적으로 random한 값을 넣어주게 됩니다.

앞으로 Fund라는 엔티티 데이터들을 Redis에 무수히 저장이 될텐데, 이 엔티티들만 보관하는 하나의 Hash 키 값이 `@RedisHash("fund")` 이고, 이 Hash 공간에서 각 엔티티들이 fund:hash_id라는 키 값을 가지게 됩니다. 

![Untitled Diagram drawio (5)](https://user-images.githubusercontent.com/22395934/178099529-c95901de-ad3f-4d8c-9feb-941ab3910921.png)

> fund:hash_id Key와 하나 이상의 `Field/Element` 값으로 저장할 수 있으며, Value에는 기본적으로 strings 데이터를 저장할 수 있습니다. 

즉, HashMap<String,HashMap<String,Fund>> 구조로 구성되어 있습니다.


이제 테스트 코드를 작성하여 실제 Fund 엔티티들을 Redis에 저장해봤습니다.

```kotlin
@SpringBootTest
class FundRedisRepositoryTests {

    @Autowired
    lateinit var fundRedisRepo: FundRedisRepository

    @Test
    @DisplayName("펀드 엔티티들을 Redis에 저장한다")
    fun saveFundWithRedis() {

        val funds = (1..10).map {
            Fund(
                id = it.toLong()
            )
        }
        fundRedisRepo.saveAll(funds)
    }
}
```

 Redis를 GUI로 쉽게 접근할 수 있는 [`Medis`](https://github.com/luin/medis)라는 Tool을 사용해서 결과를 확인해봤습니다.


#### 실행 결과

![image](https://user-images.githubusercontent.com/22395934/178098818-0e977eef-7280-4a12-b29e-7e3425350518.png)

짜잔... Fund 엔티티들이 Redis에 저장되었습니다. 아래 이미지를 보면 Redis에 총 11개의 key 값이 존재합니다. 

![image](https://user-images.githubusercontent.com/22395934/178098946-d25d3d64-190c-4b7d-af76-517506b8550d.png)

`fund`는 위에서 Redis 엔티티를 정의할 때 작성한 `@RedisHash("fund")`를 의미합니다. 그 외에 뒤에 붙은 1 ~ 10까지의 숫자들은 hash_id를 의미합니다.


![image](https://user-images.githubusercontent.com/22395934/178098851-0020c8e3-ae04-43ab-98bd-7dfd7b4f0632.png)

타입을 살펴보면 fund key는 `Set` 타입이고, 
fund:hash_id key는 `Hash` 타입입니다.

### 6.3 Redis Hash 타입 명령어

Hash 타입의 데이터를 처리할 때는 `hmset, hget, hgetall, hkey, hlen` 명령어를 사용합니다.

대표적으로 아래 3개의 명령어를 찾아봤습니다.

- hget hash_id field: 해당 key에 대한 필드의 value를 검색

- hgetall hash_id: 해당 key에 대한 모든 field와 value들을 검색

- hexists hash_id field: 해당 key에 대한 field가 존재하는지 여부 확인

![image](https://user-images.githubusercontent.com/22395934/178099804-7f507165-34f3-4a4b-950e-4b7ff6f7abb2.png)


> 참조 사이트: https://velog.io/@max9106/Spring-Boot-Redis,https://coding-start.tistory.com/130, https://cheese10yun.github.io/redis-getting-started/
