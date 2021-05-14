# @JsonNaming과 @JsonProperty

Spring Boot에서 클라이어트로부터 JSON String 포맷으로 데이터를 주고 받을 경우 간혹 Javascript에서 Snake Case 방식으로 key 값을 전송하는 경우가 있습니다.

> 스네이크 케이스는 phone_number 처럼 단어마다 언더스코어가 들어가는 변수 네이밍 규칙입니다. Java는 카멜 케이스를 사용하지만 보통 Javascript에서는 스네이크 케이스 방식도 많이 사용된다고 합니다.

물론 회사 정책에 따라서 왠만하면 key 값을 서로 맞추겠지만, 간혹 key 값을 맞출 수 없는 경우가 존재할 수도 있다고 생각하여 이러한 문제를 간단히 해결할 수 있는 @JsonProperty, @JsonNaming 어노테이션에 대해서 알아보았습니다. 

## 1. Kotlin Rest API 예제코드

언어는 Kotlin입니다. Java 기반이기 때문에 이해하는데 딱히 어려움은 없었습니다. 
아래 코드들은 Controller와 DTO 객체입니다.


#### RestTController

```kotlin
@RequestMapping("/api/post")
@RestController
class PostApiController {

    @PostMapping
    fun test(@RequestBody userRequest: UserRequest): UserRequest {
        return userRequest
    }
}
```

#### UserRequest

아래 DTO는 실제로 Javascript 클라이언트에서 전달할 JSON String 포맷과 매핑할 클래스를 정의하였습니다. 변수 포맷은 Camel Case 방식을 따르고 있습니다.

```kotlin
data class UserRequest(
    var name: String? = null,
    var age: Int? = null,
    var email: String? = null,
    var address: String? = null,
    var phoneNumber: String? = null,
}
```

### API 요청 결과

Google 크롬 플러그인에서 제공하는 `Talend API Tester`를 이용하여 테스트 하였습니다. 


```json
{
  "name":"임준영",
  "age": 30,
  "email": "A79007714@gmail.com",
  "address": "광주광역시",
  "phone_number": "010-7900-7714"
}
```

`phone_number` key 값이 `스네이크 케이스` 포맷입니다. 실제로 요청을 POST 방식으로 요청을 하면 디버깅을 하였을 경우 UserRequest 객체의 phoneNumber 변수만 `null`인걸 확인 할 수 있습니다.


![스크린샷 2021-05-14 오후 5 26 13](https://user-images.githubusercontent.com/22395934/118243544-84921080-b4d9-11eb-92f9-306b92033b96.png)


![image](https://user-images.githubusercontent.com/22395934/118241603-501d5500-b4d7-11eb-985d-f11381c00e95.png)


## 2. @JsonProperty

@JsonProperty 어노테이션은 객체의 JSON 변환 시 key 값을 개발자가 원하는 대로 설정할 수 있게 해줍니다. 

```kotlin
data class UserRequest(
    var name: String? = null,
    var age: Int? = null,
    var email: String? = null,
    var address: String? = null,

    @field:JsonProperty(value = "phone_number")
    var phoneNumber: String? = null
}
```

phoneNumber 변수명은 그대로 두고,  `@JsonProperty` 어노테이션의 value 값에 JSON 변환 시 적용할 변수명을 명시할 수 있습니다. 

만약 회사 API  정책 중 특정 변수 포맷이 카멜 케이스에서 스네이크 케이스로 변경할 경우 유용합니다.

### 테스트 수행 결과

아래 이미지 화면을 보면 @JsonProperty 어노테이션을 적용 후 클라이언트로부터 정상적으로 phoneNumber 변수에 매핑된 것을 확인할 수 있습니다.

![image](https://user-images.githubusercontent.com/22395934/118244828-f1f27100-b4da-11eb-9ba0-d7e19113c57c.png)


#### 응답부문

![image](https://user-images.githubusercontent.com/22395934/118245020-367e0c80-b4db-11eb-809b-aee363a238e0.png)

## 2. @@JsonNaming

만약 변수 일부분이 아니고 전체 API 변수 명 정책이 카멜 케이스에서 스네이크 케이스 방식으로 변경되면 각각 @JsonPropery로 수정을 하면 개발자들이 힘들 것입니다. 이런 경우에 @JsonNaming 어노테이션 입니다



```kotlin
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class UserRequest(
    var name: String? = null,
    var age: Int? = null,
    var email: String? = null,
    var address: String? = null,
    var phoneNumber: String? = null
```

@JsonNaming의 PropertyNamingStrategy 엘리먼트의 값을 `PropertyNamingStrategy.SnakeCaseStrategy::class` 주게 되면 해당 객체의 JSON 변수 key 이름은 전부 스네이크 케이스 방식으로 변경 됩니다. 


> 코틀린에서는 Java와 다르게 .class로 넘기지 않고 참조 방식으로 ::class 형태로 전달합니다. 

### PropertyNamingStrategy.class

![image](https://user-images.githubusercontent.com/22395934/118245470-cfad2300-b4db-11eb-9141-3129bc33ed54.png)

PropertyNamingStrategy를 상속받는 계층 구조입니다. 

테스트 수행 결과는 @JsonProperty와 동일하여 생략하였습니다.

