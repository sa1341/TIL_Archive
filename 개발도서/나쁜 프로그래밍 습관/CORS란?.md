## 크로스 도메인이란?

자바스크립트(Javascript)의 보안 정책의 하나인 Same-Origin Policy는 스크립트 실행되는 페이지와 비동기 호출시 주소의 프로토콜,호스트,포트가 같아야 합니다.

	 
- 웹페이지 서버 : http://localhost.com:3010	
- API 서버 : http://localhost.com:3000



웹 페이지의 포트 번호와 API 서버의 포트가 다르므로 XMLHttpRequest cannot load라는 에러가 console에 출력됩니다.
도메인이 서로 다를 때 발생하는 문제로 프로토콜,  host, port가 하나라도 달라도 생길 수 있는 문제
preflight = ajax로 서버에 호출 시 사전에 OPTIONS 메소드로 서버에 테스트 방식으로 미리 요청을 보내는데 만약에 상태 코드가 200이 아닌 경우 CORS 에러를 던지게 됩니다.
이 문제를 해결하기 위한 방법은 여러가지가 존재합니다.

1.jsonpCallback 방식(jsp, jquery를 이용한 회피 방식) 

서버와 클라이언트의 약속 방식 서버가 응답값을 줄 때 콜백명을 명시해서 넣어줌

```java
return HttpResponseEntity<>("callback(result)",HttpStatus.OK);
```

jsp 페이지에 해당 콜백 함수가 무조건 있어야 합니다. 

- 단점: 서버 api 호출 시 get 방식으로만 요청해야 되는 단점이 존재


2.서버에 CORSFilter 필터 클래스 작성
	 
이 방식의 Filter 구현 클래스를 하나 만들어서 HttpServletResponse header 값에 모든 도메인 요청을 허용 하도록 설정해야 합니다.


#### filter class

```java
@Override
public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT");
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader("Access-Control-Allow-Headers", "x-requested-with, origin, content-type, accept");
    filterChain.doFilter(servletRequest, response);
}
```

#### web.xml

```xml
<filter>
    <filter-name>CORSFilter</filter-name>
    <filter-class>com.epapyrus.common.http.filter.CORSFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>CORSFilter</filter-name>
    <url-pattern>/v4/custom/*</url-pattern>
</filter-mapping>
```

이 방식의 단점은 만약 사이트에서 웹 서버를 별도로 사용하고 있는데, 요청 메소드를 GET, POST 방식만 받도록 설정되어 있고, OPTIONS가 뚫리지 않으면 별도로 설정이 필요함. WAS에서 설정해도 웹 서버에서 막히기 때문에 둘다 설정해줘야 됩니다. 

가장 좋은 방법은 서버 to 서버 단으로 API를 호출하면 CORS 문제가 발생하지 않습니다.
도메인이 다른 자바스크립트 페이지에서 다른 서버 API를 호출하지 않도록 하는게 `Best` 방식입니다.
