## TCP 네트워킹
TCP(Transmission Control Protocol)는 연결 지향적 프로토콜입니다. 연결 지향 프로토콜이란 클라리언트와 서버가 연결된 상태에서 데이터를 주고받는 프로토콜을 말합니다.
클라이언트가 연결 요청을 하고, 서버가 연결을 수락하면 통신 선로가 고정되고, 모든 데이터는 고정된 통신 선로를 통해서 순차적으로 전달됩니다. 그렇기 때문에 TCP는 데이터를 정확하고 안정적으로 전달합니다. TCP의 단점은 데이터를 보내기 전에 반드시 연결이 형성되어야 하고(가장 시간이 많이 걸리는 작업), 고정된 통신 선로가 최단선(네트워크 길이 측면)이 아닐 경우 UDP(User Datagram Protocol)보다 데이터 전송 속도가 느릴 수 있습니다. 자바는 TCP 네트워킹을 위해 java.net.ServerSocket과 java.net.Socket 클래스를 제공하고 있습니다. 이 두 클래스의 사용 방법을 알아보기로 합시다.

## ServerSocket과 Socket의 용도
TCP 서버의 역할은 두 가지로 볼 수 있습니다. 하나는 클라이언트가 연결 요청을 해오면 연결을 수락하는 것이고, 다른 하나는 연결된 클라이언트와 통신하는 것입니다. 자바에서 이 두 역할별로 별도의 클래스를 제공하고 있습니다. 클라이언트의 연결 요청을 기다리면서 연결 수락을 담당하는 것이 java.net.ServerSocket 클래스이고, 연결된 클라이언트와 통신을 담당하는 것이 java.net.Socket 클래스입니다. 클라이언트가 연결 요청을 해오면 ServerSocket은 연결을 수락하고 통신용 Socket을 만듭니다.

![Untitled Diagram (1)](https://user-images.githubusercontent.com/22395934/85923951-31679d00-b8ca-11ea-8951-64762d6b304b.png)

서버는 클라이언트가 접속할 포트를 가지고 있어야 하는데, 이 포트를 바인딩 포트라고 합니다. 서버는 고정된 포트 번호에 바인딩해서 실행하므로, ServerSocket을 생성할 때 포트 번호 하나를 지정해야 합니다. 위 그림에서는 5001번이 서버 바인딩 포트입니다. 서버가 실행되면 클라이언트는 서버의 IP 주소와 바인딩 포트 번호로 Socket을 생성해서 연결 요청을 할 수 있습니다. ServerSocket은 클라이언트가 연결 요청을 해오면 accept() 메소드로 연결 수락을 하고 통신용 Socket을 생성 합니다. 그리고 나서 클라이언트와 서버는 각각 Socket을 이용해서 데이터를 주고받게 됩니다.

## ServerSocket 생성과 연결 수락
서버를 개발하려면 우선 ServerSocket 객체를 얻어야 합니다. ServerSocket을 얻는 가장 간단한 방법은 생성자에 바인딩 포트를 대입하고 객체를 생성하는 것입니다. 다음은 5001번 포트에 바인딩하는 ServerSocket을 생성합니다.

```java
ServerSocket serverSocket = new ServerSocket(5001);
```

ServerSocket을 얻는 다른 방법은 디폴트 생성자로 객체를 생성하고 포트 바인딩을 위해 bind() 메소드를 호출하는 것입니다. bind() 메소드의 매개값은 포트 정보를 가진 InetSocketAddress입니다.
