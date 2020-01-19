## Vert.x란?

Vertx는 다양한 모델이 있는데 그 중에서 vert.x core에 대해서 살펴보겠습니다. vert.x core는 이벤트 주도 방식에서 많이 사용하는 라이브러리의 일종으로 저수준의 기능들을 제공합니다.
그 전에 사전 지식으로 http 통신과 tcp 통신의 차이점을 아는것도 중요합니다.

### Http 통신 방식
Client 요청이 있을 때만 서버가 응답하여 해당 정보를 전송하고 곧바로 연결을 종료하는 방식입니다.
단방향적인 통신 방법이죠. Server가 Client로 요청을 보낼수는 없습니다. 주로 데이터가 필요한 경우에만 Server로 접근하는 콘텐츠 위주의 데이터를 사용할 때 용이합니다.만약 tcp처럼 게시물에 대한 내용을 요청하기 위해 실시간으로 연결을 유지하는 Socket 통신을 사용하게 되면, 게시물을 받은 후에도 계속 통신을 위한 연결이 성립되어 있어 부하가 걸리게 됩니다. 일반적으로 모바일 어플리케이션은 필요한 경우에만 Server로 정보를 요청하는 경우가 많은데, 이러한 Web Server로 http 통신을 주로 사용하여 비용 및 유지보수등 대부분의 방면에서 좋습니다.

#### [ Http 통신의특징 ]
- Client가 요청을 보내는 경우에만 Server가 응답하는 단방향 통신이다.
-Server로부터 응답을 받은 후에는 연결이 바로 종료된다. 
- 실시간 연결이 아니고, 필요한 경우에만 Server로 요청을 보내는 상황에 유용하다.
- 요청을 보내 Server의 응답을 기다리는 어플리케이션(Android or Ios)의 개발에 주로 사용된다


### Socket 통신 방식
- Socket 통신은 Server와 Client가 특정 Port를 통해 실시간으로 양방향 통신을 하는 방식입니다.
- Socket 통신은 Http 통신과 달리 Server와 Client가 특정 Port를 통해 연결을 성립하고 있어 실시간으로 양방향 통신을 하는 방식입니다. 


Client만 필요한 경우에 요청을 보내는 Http 통신과 달리 Socket 통신은 Server 역시 Client로 요청을 보낼 수 있으며, 계속 연결을 유지하는 연결지향형 통신이기 때문에 실시간 통신이 필요한 경우에 자주 사용됩니다. 예를 들면, 실시간 Streaming 중계나 실시간 채팅과 같이 즉각적으로 정보를 주고받는 경우에 사용합니다. 예를 들어 실시간 동영상 Streaming 서비스를 Http 통신으로 구현하였다고 가정하겠습니다. 이러한 경우에 사용자가 서버로 동영상을 요청하기 위해서는 동영상이 종료되는 순간까지 계속해서 Http 통신을 보내야 하고 이러한 구조는 계속 연결을 요청하기 때문에 부하가 걸리게 됩니다. 그러므로 이러한 경우에는 Socket을 통해 구현하는 것이 적합합니다.

#### [ Socket 통신의 특징 ]

- Server와 Client가 계속 연결을 유지하는 양방향 통신이다.
- Server와 Client가 실시간으로 데이터를 주고받는 상황이 필요한 경우에 사용된다.
- 실시간 동영상 Streaming이나 온라인 게임 등과 같은 경우에 자주 사용된다.

## Router란?
Router은 Vert.x-Web에서 가장 핵심적인 개념중 하나입니다. Router는 하나 이상의 route를 가지고 있습니다. Router는 Http request 요청을 request 큐에서 가져와서 해당 요청에 적합한 첫번째로 매칭되는 route를 찾습니다.
route는 요청과 관련된 handler를 가질수 있습니다. 요청을 받은 후 처리할 수 도 있습니다. 그리고 그것을 다음 매칭되는 handler에게 넘기거나 종료할 수도 있습니다.

RoutingContext 객체에는 Vert.x의 HttpServerRequest, HttpServerResponse 객체가 포함되어 있습니다. 
route는 하나 이상의 handler를 가질 수 있습니다. 만약 같은 요청에 대해서 handler로 처리할 때 동일한 RoutingContext 인스턴스를 사용하기 때문에 handler를 통해서 처리한 후에 다른 handler에게 동일한 RoutingContext를 넘겨서 처리하도록 위임할 수 있는 장점이 있습니다. 


Vert.x를 이용한 간단한 예제 코드

#### 클라이언트 코드
```java
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import stduy.vertx.Member;

import java.io.IOException;

public class ClientVertx extends AbstractVerticle {

    @Override
    public void start() throws Exception {

        HttpClient httpClient = Vertx.vertx().createHttpClient();
        ObjectMapper mapper = new ObjectMapper();


        httpClient.getNow(8081, "localhost", "/hello2",

                res -> {
                    res.bodyHandler(bh -> {
                        String body = bh.toString();
                        System.out.println("body: " + body);
                        try {
                            Member member = mapper.readValue(body, Member.class);
                            System.out.println(member.getAge() + " " + member.getId() + "호출완료!!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                    System.out.println("Response received");
                });
    }

    @Override
    public void stop() throws Exception {

    }

    public static void main(String[] args) throws Exception {
        ClientVertx clientVertx = new ClientVertx();
        clientVertx.start();

    }
}
```

#### 서버 코드
```java
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import stduy.vertx.Member;

public class VerticleProducer extends AbstractVerticle {


    @Override
    public void start() throws Exception {

        Vertx vertx = Vertx.vertx();

        HttpServer httpServer = vertx.createHttpServer();

        Router router = Router.router(vertx);

        Member member = new Member("sa1341", 28);

        JsonObject json = new JsonObject().put("id", member.getId()).put("age",member.getAge());

        router.route("/hello2").handler(routingContext -> {

            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/plain");

            response.end(json.toString());

        });
        httpServer.requestHandler(router).listen(8081);
    }

    @Override
    public void stop() throws Exception {
    }

    public static void main(String[] args) throws Exception {
        VerticleProducer verticle = new VerticleProducer();
        verticle.start();
    }
}
```
위에서 명시한 코드들은 클라이언트와 서버 http 통신을 vert.x를 이용하여 구현한 예제 코드입니다. 기본적으로 통신을 하기 위해서는 vert.x core와 vert.x-web 라이브러리가 필요합니다. 
vert.x 서버에서 Vertx 객체를 생성하여 내부적으로 HttpServer 객체를 생성합니다. 참고로 vert.x는 라이브러리이기 때문에 톰켓에 붙어서 실행시킬 수도 있다고 합니다.
저도 거기까진 안써봤지만 나중에 쓸 기회가 많지 않을까 싶습니다. 간단하게 client에서 request를 요청하면 vert.x server쪽에서 8081로 들어오는 요청이 존재하면
requestHandler() 메소드를 통해서 Router 객체에게 요청처리를 위임하게 됩니다. 그리고 Router는 해당 요청 URI에 매칭되는 route을 찾으면서 매칭되는 route가 존재하면 해당 route가 
요청을 처리하는 방법으로 응답을 하게 됩니다.
