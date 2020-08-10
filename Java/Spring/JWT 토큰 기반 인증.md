
JWT 토큰을 사용자에게 발급하여 이 토큰을 통해서 사용자의 권한 및 API 호출을 제어할 수 있고 서버에서 세션을 관리할 필요가 없기 때문에 확장성 측면에서 많은 이점을 누릴 수 있기 때문에 JWT를 사용하기전에 토큰 기반의 인증 방식에 대한 내용과 JWT 기본 개념에 대해서 포스팅을 하였습니다.

# 토큰(Tocken)기반 인증에 대한 소개

토큰(Tocken)기반 인증은 모던 웹서비스에서 정말 많이 사용되고 있습니다. 웹 서비스를 개발한다면 토큰을 사용하여 유저들의 인증작업을 처리하는것이 가장 좋다고 합니다.

토큰은 `Stateless` 서버입니다. `Stateful` 서버와 다르게 세션 값으로 클라이언트 상태 유지를 하지 않습니다. 상태정보를 저장하지 않으면, 서버는 클라이언트 측에서 들어오는 요청으로만 작업을 처리합니다. 이렇게 상태가 없는 경우 클라이언트와 서버의 연결고리가 없기 때문에 서버의 확장성(Scalaability)이 높아집니다.

> 모바일 어플리케이션에 적합합니다.

만약에 Android/iOS 모바일 어플리케이션을 개발 한다면, 안전한 API를 만들기 위해선 쿠키같은 인증시스템은 이상적이지 않습니다. 토큰 기반 인증을 도입한다면, 더욱 간단하게 이 번거로움을 해결 할 수 있습니다.

> 인증 정보를 다른 어플리케이션으로 전달

대표적인 예로 OAuth가 있습니다. 페이스북/구글 같은 소셜 계정들을 이용하여 다른 웹 서비스에서도 로그인 할 수 있게 할 수 있습니다.

> 보안

토큰 기반 인증 시스템을 사용하여 어플리케이션의 보안을 높일 수 있습니다. 단, 이 토큰 기반 인증을 사용한다고 해서 무조건 해킹의 위험에서 벗어나는건 아닙니다.


# 토큰 기반 인증 시스템을 사용하는 이유

토큰 기반 인증 시스템이 어떻게 동작하고, 또 이로 인하여 얻을 수 있는 이득에 대하여 알아보기전에, 이 토큰 기반 인증 시스템이 나온 이유에 대해서 살펴보는게 가장 이해가 빠를거 같습니다.

![스크린샷 2019-12-05 오후 7 38 57](https://user-images.githubusercontent.com/22395934/70227987-e9025980-1796-11ea-887b-1baf571cd88b.png)

### 서버 기반 인증
과거 인증 시스템은 서버측에서 유저들의 정보를 기억하고 있어야 합니다. 이 세션을 유지하기 위해서는 여러 가지 방법이 사용됩니다. 메모리 / 디스크 / 데이터베이스 시스템에 이를 담곤 했습니다.

#### 서버 기반 인증 시스템 흐름
![Untitled Diagram](https://user-images.githubusercontent.com/22395934/70230640-1998c200-179c-11ea-99e1-dfe3f106a3d7.png)

하지만 이 방식은 서버를 확장하기가 어려워졌습니다.

# 서버 기반 인증의 문제점

## 세션 
유저가 인증을 할 때, 서버는 이 기록을 서버에 저장을 해야합니다. 이를 세션이라고 부릅니다. 대부분의 경우엔 메모리에 이를 저장하는데, 로그인 중인 유저의 수가 늘어난다면 서버의 램에 많은 과부화가 걸리게됩니다. 이를 피하기 위해서는 세션을 데이터베이스 시스템에 저장하는 방식도 있지만, 이 또한 유저의 수가 많으면 데이터베이스의 성능에 무리를 줄 수 있습니다.

## 확장성

세션을 사용하면 서버를 확장하는 것이 어려워집니다. 여기서 서버의 확장이란, 단순히 서버의 사양을 업그레이드 하는것이 아니라, 더 많은 트래픽을 감당하기 위하여 여러개의 프로세스를 돌리거나, 여러대의 서버 컴퓨터를 추가 하는것을 의미합니다. 세션을 사용하려면 분산된 시스템을 설계하는건 불가능한것은 아니지만 과정이 매우 복잡해집니다.


# 토큰 기반 시스템의 작동 원리

토큰 기반 시스템은 Stateless 합니다. 즉 상태유지를 하지 않습니다. 이 시스템에서는 더 이상 유저의 인증 정보를 서버나 세션에 담아두지 않습니다. 이 개념 하나만으로도 위에서 서술한 서버에서 유저의 인증 정보를 서버측에 담아둠으로서 발생하는 많은 문제점들이 해소됩니다.

세션이 존재하지 않으니, 유저들이 로그인 되어있는지 안되어있는지 신경을 1도 쓰지 않기 때문에 서버를 손쉽게 확장 할 수 있습니다.

토큰 기반 시스템의 구현 방식은 시스템마다 크고 작은 차이가 있겠지만, 대략적으로 아래 절차와 같습니다.

1. 유저가 아이디와 비밀번호를 입력하여 로그인을 합니다.
2. 서버측에서 해당 계정정보를 검증합니다.
3. 계정정보가 정확하다면, 서버측에서 유저에게 signed 토큰을 발급해줍니다.
> signed의 의미는 해당 토큰이 서버에서 정상적으로 발급된 토큰임을 증명하는 signature를 지니고 있다는 것을 의미합니다.

4. 클라이언트 측에서 전달받은 토큰을 저장해두고, 서버에 요청을 할 때 마다, 해당 토큰을 함께 서버에 전달합니다.
5. 서버는 토큰을 검증하고, 요청에 응답합니다.

#### 토큰 기반 시스템 처리 과정
![Untitled Diagram (1)](https://user-images.githubusercontent.com/22395934/70232647-3c2cda00-17a0-11ea-8448-c6b2b143356f.png)

웹서버에서 토큰을 서버에 전달 할 때에는, HTTP Request 헤더에 토큰값을 포함시켜서 전달합니다.

# 토큰의 장점

### `무상태(stateless)이며 확장성(scalability)`입니다.

이 개념은 토큰 기반 인증 시스템의 중요한 속성입니다. 토큰은 클라이언트 사이드에 저장하기 때문에 완전히 `stateless`하며, 서버를 확장하기에 매우 적합한 환경을 제공합니다. 만약에 세션을 서버측에 저장하고 있고, 서버를 여러대를 사용하여 요청을 분산하였다면, 어떤 유저가 로그인 했을땐, 그 유저는 처음 로그인했었던 해당 서버에만 요청을 보내도록 설정을 해야합니다. 하지만 토큰을 사용한다면 어떤 서버로 요청이 들어가던 상관이 없습니다.

### 보안성

클라이언트가 서버에 요청을 보낼 때, 더 이상 쿠키를 전달하지 않음으로 쿠키를 사용함으로 인해 발생하는 취약점이 사라집니다. 토큰을 사용하는 환경에서도 취약점이 존재할 수있으니 언제나 취약점에 대비해야합니다.


### 확장성
토큰을 사용하여 다른 서비스에서도 권한을 공유 할 수 있습니다. 예를 들어서, 스타트업 구인구직 웹서비스인 `로켓펀치`에서는 Facebook, LinkedIn, GitHub, Google 계정으로 로그인을 할 수 있습니다. 토큰 기반 시스템에서는, 토큰에 선택적인 권한만 부여하여 발급을 할 수 있습니다. (예를들어서 로켓펀치에서 페이스북 계정으로 로그인을 했다면, 프로필 정보를 가져오는 권한은 있어도, 포스트를 작성 할 수 있는 권한은 없습니다.)

# 여러 플랫폼 및 도메인

서버 기반 인증 시스템의 문제점을 다룰때 어플리케이션과 서비스 규모가 커지면, 우리는 여러 디바이스를 호환 시키고, 더 많은 종류의 서비스를 제공하게 됩니다. 토큰을 사용한다면, 그 어떤 디바이스나 도메인에서도 토큰만 유효하다면 요청이 정상적으로 처리됩니다. 서버측에서 어플리케이션의 응답 부분에 다음 헤더만 포함시켜주면 됩니다.

> Access-Control-Allow-Origin: *

이런 구조라면, assets 파일들(이미지, css, js, html 파일 등)은 모두 CDN에서 제공을 하도록 하고, 서버측에서는 오직 API만 다루도록 하도록 설계 할 수도 있습니다.


 JWT는 웹 표준 RFC 7519에 등록이 되어있습니다. 따라서 여러 환경에서 지원이 되며 수많은 회사의 인프라스트럭쳐에서 사용 되고 있습니다.


# JWT(JSON Web Token)을 이용한 API 인증 

JWT는 Claim 기반의 토큰입니다. Claim이라는 사용자에 대한 프로퍼티나 속성을 이야기 합니다. 토큰자체가 정보를 가지고 있는 방식인데, JWT는 이 Claim을 JSON을 이용해서 정의합니다. 다음은 Claim을 JSON으로 서술한 예입니다. JSON 자체를 토큰으로 사용하는 것이 Claim 기반의 토큰 방식입니다.

ex) Claim 기반의 토큰 정보
```json
{
    "id":"junyoung",
    "role":["admin", "user"],
    "company":"pepsi"
}
```

이러한 Claim 방식의 토큰의 장점은 토큰을 이용해서 요청을 받는 서버나 서비스 입장에서는 이 서비스를 호출한 사용자에 대한 추가 정보는 이미 토큰안에 다 들어가 있기 때문에 다른 곳에서 가져올 필요가 없다는 것입니다.

`"사용자 관리"` 라는 API 서비스가 있다고 가정합니다.
아 API는 `관리자(admin)` 권한을 가지고 있는 사용자만이 접근이 가능하며, "관리자" 권한을 가지고 있는 사용자는 그 관리자가 속해 있는 `회사(company)`의 사용자 정보만 관리할 수 있다고 정의해봅시다. 이 시나리오에 대해서 일반적인 스트링 기반의 토큰과 JWT와 같은 Claim 기반의 토큰이 어떤 차이를 가질 수 있는지 알아보겠습니다.

### OAuth 토큰의 경우
![Untitled Diagram (2)](https://user-images.githubusercontent.com/22395934/70235440-494cc780-17a6-11ea-87f2-acc51cfc10d4.png)


1. API 클라이언트가 Authorization Server(토큰 발급서버)로 토큰을 요청합니다.
이때, 토큰 발급을 요청하는 사용자의 계정과 비밀번호를 넘기고, 이와 함께 토큰의 권한(용도)를 요청합니다. 여기서는 일반 사용자 권한(basic)과 관리자 권한(admin)을 같이 요청하였습니다.

2. 토큰 생성 요청을 받은 Authorization Server는 사용자 계정을 확인한 후, 이 사용자에게 요청된 권한을 부여해도 되는지 계정 시스템등에 물어본 후, 사용자에게 해당 토큰을 발급이 가능하면 토큰을 발급하고, 토큰에 대한 정보를 내부(토큰 저장소)에 저장해놓습니다.

3. 이렇게 생성된 토큰은 API 클라이언트로 저장됩니다.

4. API 클라이언트는 API를 호출할때 이 토큰을 이용해서 Resource Server(API 서버)에 있는 API를 호출합니다.

5. 이때 호출되는 API는 관리자 권한을 가지고 있어야 사용할 수 있기 때문에, Resource Server가 토큰 저장소에서 토큰에 관련된 사용자 계정, 권한 등의 정보를 가지고 옵니다. 이 토큰에 (관리자)admin 권한이 부여되어 있기 때문에, API 호출을 허용합니다. 위에 정의한 시나리오에서는 그 사용자가 속한 “회사”의 사용자 정보만 조회할 수 있습니다. 라는 전제 조건을 가지고 있기 때문에, API 서버는 추가로 사용자 데이타 베이스에서 이 사용자가 속한 “회사” 정보를 찾아와야합니다.

6. API서버는 응답을 보낸다.


### JWT와 같은 Claim 기반의 토큰 흐름
![Untitled Diagram](https://user-images.githubusercontent.com/22395934/70235939-469ea200-17a7-11ea-8bae-126898156c12.png)


1. 토큰을 생성 요청하는 방식은 동일합니다.  마찬가지로 사용자를 인증한다음에, 토큰을 생성합니다.

2. 다른 점은 생성된 토큰에 관련된 정보를 별도로 저장하지 않는다는 것입니다. 토큰에 연관되는 사용자 정보나 권한등을 토큰 자체에 넣어서 저장합니다.

3. API를 호출하는 방식도 동일합니다.

4. Resource Server (API 서버)는 토큰 내에 들어 있는 사용자 정보를 가지고 권한 인가 처리를 하고 결과를 리턴합니다.

결과적으로 차이점은 토큰을 생성하는 단계에서는 생성된 토큰을 별도로 서버에서 유지할 필요가 없으며 토큰을 사용하는 API 서버 입장에서는 API 요청을 검증하기 위해서 토큰을 가지고 사용자 정보를 별도로 계정 시스템 등에서 조회할 필요가 없다는 것입니다.


# JWT에 대한 소개

Claim 기반의 토큰에 대한 개념을 대략적으로 이해했다면, 그러면 실제로 JWT가 어떻게 구성되는지에 대해서 살펴보겠습니다.

Claim (메시지) 정의
JWT는 Claim을 JSON 형태로 표현하는 것인데, JSON은 "\n" 등 개행문자가 있기 때문에, REST API 호출 시 HTTP Header등에 넣기가 매우 불편합니다. 그래서 이 Claim JSON 문자열을 BASE64 인코딩을 통해서 하나의 문자열로 변환합니다.


```json
{
    "id":"junyoung",
    "role":["admin", "user"],
    "company":"pepsi"
}
```

#### 문자열을 BASE64로 인코딩 한 결과

> ew0KICAiaWQiOiJ0ZXJyeSINCiAgLCJyb2xlIjpbImFkbWluIiwidXNlciJdDQogICwiY29tcGFueSI6InBlcHNpIg0KfQ0K

#### BASE64 인코딩이란?

- 2진 데이터를 ASCII 형태의 텍스트로 표현 가능
- 웹 인증 중 기본인증에 사용
- 끝 부분의 Padding(==)으로 식별 가능
- 64개의 문자 사용 (영문 대, 소문자, 숫자 , + , / )
- 데이터를 6bit 단위로 표현





# 변조 방지

위의 Claim 기반의 토큰을 봤다면, 첫번째 들 수 있는 의문이 토큰을 받은 다음에 누군가 토큰을 변조해서 사용한다면 어떻게 막느냐 입니다. 이렇게 메시지가 변조 되지 않았음을 증명하는 것을 무결성(Integrity)라고 하는데, 무결성을 보장하는 방법 중 많이 사용되는 방법이 서명(Signature)이나 HMAC 사용하는 방식입니다. 즉 원본 메세지에서 해쉬값을 추출한 후, 이를 비밀 키를 이용해서 복호화 시켜서 토큰의 뒤에 붙입니다. 이게 HMAC 방식인데, 누군가 이 메세지를 변조했다면, 변조된 메시지에서 생성한 해쉬값과 토큰뒤에 붙어있는 해쉬값이 다르기 때문에 메세지가 변조되었음을 알 수 있습니다. 다른 누군가가 메세지를 변조한 후에, 새롭게 HMAC 값을 만들어내려고 하더라도, HMAC은 앞의 비밀키를 이용해서 복호화 되었기 때문에, 이 비밀키를 알 수 없는 이상 HMAC를 만들어 낼 수 없습니다.

앞의 JSON 메세지에 대해서 SHA-256이라는 알고리즘을 이용해서 비밀키를 “secret” 이라고 하고, HMAC을 생성하면 결과는 다음과 같습니다.

> i22mRxfSB5gt0rLbtrogxbKj5aZmpYh7lA82HO1Di0E

# JWT의 기본 구조
```java
Header . Payload . Signature
```

## Header

> JWT 웹 토큰의 헤더 정보

- typ: 토큰의 타입, JWT만 존재
- alg: 해싱 알고리즘.(HMAC SHA256 or RSA) 헤더를 암호화 하는게 아닙니다. 토큰 검증시 사용합니다.

```json
{
    "alg" : "HS256",
    "typ" : "JWT"
}
```

위의 내용을 BASE64로 인코딩합니다. => eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
`BASE64`는 암호화된 문자열이 아닙니다. 같은 자열에 대해서는 항상 같은 인코딩 문자열을 반환합니다.


## Payload
> 실제 토큰으로 사용하려는 데이터가 담기는 부분. 각 데이터를 Claim이라고 하며 다음과 같이 3가지 종류가 있습니다.

- Reserved claims: 이미 예약된 Claim. 필수는 아니지만 사용하길 권장합니다. key는 모두 3자리 String입니다.

    - iss(String): issuer, 토큰 발행자 정보
    
    - exp(Number): expiration time, 먼료일
    
    - sub(String): subject, 제목
    
    - and(String): audience



- Public claims: 사용자 정의 Claim
  - Public이라는 이름처럼 공개용 정보
  - 충돌 방지를 위해 URI 포맷을 이용해 저장합니다.



- Private Claims: 사용자 정의 Claim
    - Public claims과 다르게 사용자가 임의로 정한 정보
    - 아래와 같이 일반 정보를 저장합니다.

    ```java
        {
            "name" : "hak",
            "age"  : 26
        }
    ```

## Signature

> Header와 Payload의 데이터 무결성과 변조 방지를 위한 서명
Header + Payload를 합친 후, Secret 키와 함께 Header의 해싱 알고리즘으로 인코딩

```java
HMACSHA256( 
    base64UrlEncode(header) + "." + 
    base64UrlEncode(payload), 
    secret)

```

## JWT 구조
> JWT는 [Header Payload Signature] 각각 JSON 형태의 데이터를 base 64 인코딩 후 합칩니다.
아래와 같은 순서로 . 을 이용해 합칩니다.
최종적으로 만들어진 토큰은 HTTP 통신 간 이용되며, Authorization 이라는 key의 value로서 사용됩니다.


![250AC0505861FCE02E](https://user-images.githubusercontent.com/22395934/70238751-43a6b000-17ad-11ea-85f1-5c1191a17522.png)

## JWT 인증 과정

![2268544E5861FD0F13](https://user-images.githubusercontent.com/22395934/70238798-5e792480-17ad-11ea-88d7-272d90a4d4f7.png)



# JWT의 단점 & 도입시 고려사항

- Self-contained: 토큰 자체에 정보가 있다는 사실은 양날의 검이 될수 있습니다.

    - 토큰 길이: 토큰 자체 payload에 Claim set을 저장하기 때문에 정보가 많아질수록 토큰의 길이가 늘어나 네트워크에 부하를 줄 수있습니다.

    - payload 암호화: payload 자체는 암호화 되지 않고 base64로 인코딩한 데이터입니다. 중간에 payload를 탈취하면 디코딩을 통해 데이터를 볼 수 있습니다.
    JWE를 통해 암호화하거나, payload에 중요 데이터를 넣지 않아야 합니다.

    - Stateless: 무상태성이 때론 불편할 수 있습니다. 토큰은 한번 만들면 서버에서 제어가 불가능합니다. 토큰을 임의로 삭제할 수 있는 방법이 없기 때문에 토큰 만료시간을 꼭 넣어주는게 좋습니다.

    - tore Token: 토큰은 클라이언트 side에서 관리해야하기 때문에 토큰을 저장해야 합니다.



# JWT 토큰 생성 구현 예제

이제 위에서 배운 Claim 토큰 기반 시스템인 JWT 토큰을 생성하고 토큰을 검증하는 간단한 예제를 Intellij 스프링 부트를 통해서 작성해보았습니다.


# 1.dependecy 추가

JWT 토큰을 생성해주는 라이브러리가 필요한데 저는 스프링 부트에서 jjwt를 이용하셔 생성하도록 하였습니다.

```java
dependencies{
    implementation 'io.jsonwebtoken:jjwt:0.6.0'
}
```

builder는 `gradle`을 사용하기 때문에 위와 같이 설정을 추가하였습니다. 
사실 `0.9.0 버전` 이상도 존재하지만 이번 예제에서는 JWT를 맛보기로 알아보기 위해서 구글링을 통해서 `0.6.0 버전`으로 적용해보았습니다. 


# 2. JWT 흐름

- JWT 토큰 생성
- JWT 토큰 파싱 및 검증

> 토큰 생성
```java
// Interceptor 구현
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private static final String HEADER_AUTH = "Authorization";

    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        final String token = request.getHeader(HEADER_AUTH);

        // preHandle의 return은 컨트롤러 요청 uri로 가도 되냐 안되냐를 허가하는 의미합니다.
        // 따라서 리턴 값이 true 일때, 요청한 컨트롤러 메소드를 수행합니다.
        if(token !=null && jwtService.isValid(token)){
            return true;
        }else{
            throw new UnauthorizedException();
        }
    }
}


public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = -2238030302650813813L;

    public UnauthorizedException(){
        super("계정 권한이 유효하지 않습니다.\n다시 로그인을 해주세요.");
    }
}
```
가장 먼저 HandlerInterceptor를 구현한 JwtInterceptor를 작성하였습니다. JwtInterceptor는 preHandle 메소드만 오버라이드 하여 `Authorization` Key 값인 String 타입의 `JWT(JSON Web Token)`이 존재하는지 checking 하는 역할을 합니다. 만약 토큰이 존재하지 않는다면 클라이언트에서 요청한 Url에 매핑되는 컨트롤러의 메소드를 수행하지 않고 예외를 발생시키도록 코드를 작성하였습니다.


# Config(설정)
```java
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    private static final String[] EXCLUDE_PATHS = {
            "/member/**",
            "/error/**"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATHS);
    }
}
```

Spring Boot에서 WebMvcConfigurer는 자동 구성된 Spring MVC 구성을 큰 변경없이 추가적인 조작을 하기 위해서 구현합니다. `addInterceptors()` 메소드만 선택적으로 구현하였습니다. 위에서 제가 작성한 JwtInterceptor를 빈으로 주입하여 Spring MVC에서 Interceptor로 추가하였습니다. 또한 클라이언트에서 요청한 Url을 가로채도록 `addPathPatterns()` 메소드로 경로패턴을 정의하였고, `excludePathPatterns()` 메소드로 `"/member/**", "/error/**"`로 들어오는 요청만은 가로채지 않도록 제외시켰습니다.


# 서비스

이제 가장 중요한 JWT 생성을 하는 서비스 코드를 작성하였습니다.

```java
public interface JwtService {

    public <T> String create(String key, T data, String subject);

    public boolean isValid(String jwt);

    public Map<String, Object> get(String key);
}

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    //서버쪽에서 토큰을 생성하기 위한 비밀키 입니다.
    private static final String SALT = "secret";


    // Jwt를 이용하여 파싱을 하는데 여기서 파싱이 된다면 정상적인 토큰으로 간주하고 여기서 파싱이 되지 않는다면 catch 문에 잡힙니다.
    @Override
    public boolean isValid(String jwt) {
        try{
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(this.generateKey())
                    .parseClaimsJws(jwt);
            return true;
        }catch (Exception e){
            throw new UnauthorizedException();
        }
    }

    //byte로 변환된 비밀키를 이용하여 JWT에서 제공하는 SHA256 알고리즘을 이용해 해시값을 생성한 후에 인코딩 후 JWT 서명을 생성합니다.
    @Override
    public <T> String create(String key, T data, String subject) {

        String jwt = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject(subject)
                .claim("exp", System.currentTimeMillis() + (1000 *60))
                .claim(key, data)
                .signWith(SignatureAlgorithm.HS256, this.generateKey())
                .compact();
        return jwt;
    }

    // 비밀키를 UTF-8로 인코딩하여 byte 형태로 변환합니다.
    private byte[] generateKey() {
        byte[] key = null;
        try {
            // SALT 값을 UTF-8로 인코딩한 각각의 문자열들의 byte 값을 담고 있는 배열을 리턴합니다.
            key = SALT.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            if (log.isInfoEnabled()) {
                e.printStackTrace();
            } else {
                log.error("Making JWT Key Error ::: {}", e.getMessage());
            }
        }
        return key;
    }
}
```

- 토큰 생성

    클라이언트에서 로그인을 통해서 인증을 성공하게 된다면 서버측에서 JWT 토큰을 생성하여 Response Header에 넣어서 클라이언트에 전송해줘야 하는데 `create()` 메소드로 Jwts의 builder() 메소드로 JWT 토큰을 생성하고 있습니다. 보시면 chain 방식으로 각각의 `JWT를 구성하는 Header(헤더), Payload(데이터), Sign(서명)`을 설정해주고 있습니다. 헤더의 경우 위에서 언급했기 때문에 자세한 내용은 생략하겠습니다.

    Payload는 회원정보, 만료시간 1분을 넣어주었습니다. 토큰의 변조를 방지하기 위해서 가장 중요한 Sing은 위에서 정의한 비밀키 `secret`를 바이트 타입으로 변환 후에  JWT가 제공해주는 대표적인 해시 알고리즘인 `HSA256`방식으로 암호화 하도록 설정하였 습니다. 그리고 마지막으로 토큰을 새성하기 위해서는 Jwts.builder().compact()를 호출하면 됩니다.




- 토큰 검증

클라이언트에서 정상적으로 생성된 토큰을 받은 후에 서버의 API를 호출하거나 접근 권한이 필요한 Resource들에 접근하게 된다면 JWT 토큰을 통해서 서버에서 자체적으로 검증을 해야하는데 저는 이부분을 간단한게 `isValid()` 메소드를 호출하여 검증하도록 하였습니다.

```java
@Override
public boolean isValid(String jwt) {
    try{
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(this.generateKey())
                .parseClaimsJws(jwt);
        return true;
    }catch (Exception e){
        throw new UnauthorizedException();
    }
}
```

우선 JWT 토큰은 String 타입으로 생성이 됩니다. 우리는 이것을 우리가 사용하기 위한 형태로 `parsing`하기 위해서 Jwts.parse()를 이용해야 합니다. 그 후 token을 생성할 때 사용했던 비밀키를 set 해줘야 합니다. `setSigningKey(this.generateKey())` 다음으로는 parseClaimsJws() 메소드를 이용해 토큰을 jws로 파싱합니다.

만약 정상적인 토큰이라면 true를 리턴할 것이고, 파싱이 안되는 토큰이면 비정상으로 판단하여 `UnauthorizedException` 예외를 발생시킬 것입니다.



# Controller

```java
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberRestController {

    private final MemberService memberService;

    private final JwtService jwtService;

    @PostMapping("/signin")
    public Result signin(HttpServletResponse response){

        // 회원가입 수행
        Member newMember = new Member();
        newMember.setUid("dlawnsdud2");
        newMember.setUpw("wnsdud2");
        newMember.setEmail("a7900@gmai.com");
        newMember.setUname("임준영");

        log.info("" + newMember.getUid() + " " + newMember.getUpw() + " " + newMember.getEmail() + " " + newMember.getUname());

        memberService.save(newMember);

        Member findMember = memberService.findById(newMember.getUid());

        // JWT 토큰을 생성할때 Payload 부분에서 회원 정보를 넣어주도록 설정하였습니다. 
        String token = jwtService.create("username", findMember,findMember.getUid());
        // Response Header에 JWT 토큰을 넣어줍니다.
        response.setHeader("Authorization", token);
        Result result = new Result();
        result.setData(findMember);

        return result;
    }
}
```

위의 컨트롤러에서는 단순하게 회원가입 샘플을 만들기 귀찮아서 임의로 값을 아무거나 넣어서 Member 객체를 생성하였습니다. 사실.. 위의 코드들을 작성한 목적은 JWT 토큰이 제대로 생성이 되고 정상적인 토큰인지 확인하는것이 목적이기 때문에 양해 부탁드립니다.

결과적으로 Response 객체의 Header에 `Authorization`을 Key 값으로, String 타입인 JWT 토큰은 value로 값을 넣어주었습니다.



#### 실행 결과


#### PostMan(포스트맨)
![스크린샷 2019-12-08 오전 1 33 57](https://user-images.githubusercontent.com/22395934/70377702-d173ce00-195a-11ea-8a5e-b9218c238b3c.png)


#### [jwt.io](https://jwt.io/) 검증 결과
![스크린샷 2019-12-08 오전 1 25 07](https://user-images.githubusercontent.com/22395934/70377599-9b821a00-1959-11ea-800b-bc7ecc77029c.png)

이미지를 보시면 왼쪽이 Encoded된 문자열이고, 오른쪽이 Encoded 된 문자열을 Decoded하면 오른쪽과 같은 Data로 됩니다.


 #### 참고 사이트 : https://velopert.com/2350, https://bcho.tistory.com/999, https://sanghaklee.tistory.com/47,https://coding-start.tistory.com/157
