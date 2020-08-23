# 삼성 디스플레이 Custom 소스 코드 분석 및 리뷰


## HttpURLConnection 객체
이 객체를 알기 전에 URLConnection 클래스에 대해서 알아보겠습니다. 그 이유는 HttpURLConnection 클래스는 URLConnection을 구현한 클래스이기 때문입니다.

### URLConnection의 사용처
- URLConnection은 웹을 통해 데이터를 주고 받는데 사용됩니다. (RFC 2616을 따릅니다.)
- 데이터의 타입이나 길이는 거의 제한이 없습니다.
- HTTP URL을 처리할 때 도움이 되는 몇 가지 추가적인 메소드를 가집니다.
- 요청 방식을 확인 or 설정, redirect 여부 결정, 응답 코드와 메시지를 read, 프록시 서버가 사용되었는지 여부 확인 메서드등을 가지고 있습니다.
- 다양한 HTTP 응답 코드에 해당하는 상수 값들이 정의되어 있습니다.
- URLConnection 클래스의 getPermission() 메서드를 오버라이드 해놓았습니다.

URLConnection 클래스와 마찬가지로 생성자가 protected로 선언되어있기 때문에 기본적으로 개발자가 직접 HttpURLConnection 객체를 직접 생성할 수 없습니다.

하지만 http URL을 사용하는 URL 객체의 `openConnection()` 메서드가 리턴하는 URLConnection 객체는 HttpURLConnection의 인스턴스가 될수 있기 때문에 리턴된 URLConnection을 다음처럼 HttpURLConnection으로 캐스팅해서 사용합니다.

```java
URL u = new URL("http://www.naver.com");
HttpURLConnection http = (HttpURLConnection) u.openConnection();
```

## 요청 방식 설정

- HttpURLConnection은 기본적으로 GET 방식으로 설정되어 있습니다.
- setRequestMethod() 메서드를 사용해서 POST, PUT, DELETE와 같은 메서드 변경 가능합니다.
- 요청방식은 대문자로 전달해야 합니다.
- 지정된 요청 방식 이외의 파라미터 전달시 `java.net.ProtocolException` 예외 발생


##  Method
요청 메서드 종류
1. GET

 - 웹 서버로부터 리소스를 가져온다.

2. POST

 - 폼에 입력된 내용을 서버로 전송한다.


3. DELETE

 - 웹 서버의 리소스를 지운다.

 - 대부분의 서버는 기본적으로 DELETE를 허용하지 않거나 인증을 요구한다.

 - 서버는 이 요청을 거절하거나 인증을 요청할 수 있으며, 허용하는 경우에도 응답은 구현에 따라 차이가 있다.

 - 서버설정에 따라 파일을 지우기, 휴지통으로 이동, 파일을 읽을 수 없도록 표시 하는 등의 행위를 하게 된다.

4. put

 - 웹 서버로 리소스를 전송한다.

 - PUT 요청도 파일을 지울 때와 마찬가지로 보통 사용자 인증을 요구하며, PUT 메서드를 지원하도록 설정해줘야 한다.



5. OPTIONS

 - 특정 URL에 대해 지원되는 요청 메서드의 목록을 리턴한다.

 - 요청 URL이 *인 경우 헤당 요청의 대상은 서버에 있는 하나의 특정 URL이 아니라 서버 전체에 적용된다는 것을 의미한다.


6. TRACE

 - 요청을 추적한다.

 - 클라이언트가 보낸 요청이 클라이언트와 서버 사이에 있는 프록시 서버에서 변경되었는지를 확인할 필요가 있는 경우 등에 쓰임


## HttpURLConnection 객체의 메소드 정보

setDoInput(boolean) : `Server 통신에서 입력 가능한 상태로 만듬 `
- OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.

setDoOutput(boolean) : `Server 통신에서 출력 가능한 상태로 만듬`
 - `Server와 통신을 하고자 할때는 반드시 위 두 method를 true로 해야합니다.`
 - InputStream으로 서버로 부터 응답을 받겠다는 옵션.


setUseCaches(boolean) : Server 통신에서 캐시 설정 여부를 나타내는데 캐시에 저장한 값을 읽어올 것인지 아니면 동적으로 생성된 데이터를 읽을것인지 결장하는 설정 값으로... `서버에 요청 파라미터를 보내면 그 파라미터 값에 따라 웹 페이지 결과과 그 순간 순간 파라미터의 값에 따라 달라지기 때문에 false로 설정하는 것이 좋습니다.`


setConnectTimeout(int): `TimeOut 시간 (서버 접속시 연결 시간)`

setReadTimeout(int): `TimeOut 시간 (Read시 연결 시간)`


setRequestProperty("Content-Type", "application/json"): `타입설정(application/json) 형식으로 전송 (Request Body 전달시 application/json로 서버에 전달.)`



```java
// 실제 서버로 Request 요청 하는 부분. (응답 코드를 받는다. 200 성공, 나머지 에러)
int responseCode = conn.getResponseCode();
 
// 접속 종료
conn.disconnect();
```

출처: https://goddaehee.tistory.com/161 [갓대희의 작은공간], https://ggoreb.tistory.com/114 [나는 초보다]
