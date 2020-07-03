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

만약 서버 PC에 멀티 IP가 할당되어 있을 경우, 특정 IP로 접속할 때만 연결 수락을 하고 싶다면 다음과 같이 작성하되, "localhost" 대신 정확한 IP를 주면 됩니다.

ServerSocket을 생성할 때 해당 포트가 이미 다른 프로그램에서 사용 중이라면 BindException이 발생합니다. 이 경우에는 다른 포트로 바인딩 하거나, 다른 프로그램을 종료하고 다시 실행하면 됩니다.

포트 바인딩까지 끝났다면 ServerSocket은 클라이언트 연결 수락을 위해 accept() 메소드를 실행해야 합니다. accept() 메소드는 클라이언트가 연결 요청하기 전까지 블로킹 되는데, 블로킹이란 스레드가 대기 상태가 된다는 뜻입니다. 그렇기 때문에 UI를 생성하는 스레드나, 이벤트를 처리하는 스레드에서 accept() 메소드를 호출하지 않도록 합니다. 블로킹이 되면 UI 갱신이나 이벤트 처리를 할 수 없기 때문입니다. 클라이언트가 연결 요청을 하면 accept()는 클라이언트와 통신할 Socket을 만들고 리턴합니다. 이것이 연결 수락입니다. 만약 accept()에서 블로킹되어 있을 때 ServerSocket을 닫기 위해 close() 메소드를 호출하면 SocketException이 발생합니다. 그렇기 때문에 예외 처리가 필요합니다.

```java
try {
    Socket socket = ServerSocket.accept();

} catch(Excpetion e) { }
```

연결된 클라이언트의 IP와 포트 정보를 알고 싶다면 Socket의 getRemoteSocketAddress() 메소드를 호출해서 SocketAddress를 얻으면 됩니다. 실제 리턴되는 것은 InetSocketAddress 객체이므로 다음과 같이 타입 변환할 수 있습니다.

```java
InetSocketAddress socketAddress = (InetSokcetAddress) socket.getRemoteSocketAddress();
```

InetSocketAddress에는 IP와 포트 정보를 리턴하는 다음과 같은 메소드들이 있습니다.

- getHostName(): 클라이언트 IP를 리턴
- getPort(): 클라이언트 포트 번호 리턴
- toString(): "IP 포트번호" 형태의 문자열 리턴


더 이상 클라리언트 연결 수락이 필요 없으면 ServerSocket의 close() 메소드를 호출해서 포트를 언바인딩시켜야 합니다. 그래야 다른 프로그램에서 해당 포트를 재사용 할 수 있습니다.

```java
serverSocket.close();
```

아래 코드는 반복적으로 accept() 메소드를 호출해서 다중 클라리언트 연결을 수락하는 가장 기본적인 코드를 보여줍니다.
```java
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerExample {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("localhost", 5001));

            while (true) {
                System.out.println("[연결 기다림]");
                Socket socket = serverSocket.accept();
                InetSocketAddress isa =  (InetSocketAddress) socket.getRemoteSocketAddress();
                System.out.println("[연결 수락함]" + isa.getHostName());
            }

        } catch (Exception e) {}


        if (!serverSocket.isClosed()) {

            try {
                serverSocket.close();
            }catch (IOException e1) {}
        }
    }
}
```

클라이언트가 서버에 연결 요청을 하려면 java.net.Socket을 이용해야 합니다. Socket 객체를 생성함과 동시에 연결 요청을 하려면 생성자의 매개값으로 서버의 IP 주소와 바인딩 포트 번호를 제공하면 됩니다. 다음은 로컬 PC의 5001 포트에 연결 요청하는 코드입니다.

```java
try {
    Socket socket = new Socket("localhost", 5001);
    Socket socket = new Socket(new InetSocketAddress("localhost", 5001));
} catch (UnknownHostException e) {
    // IP 표기 방법이 잘못되었을 경우
} catch (IOException e) {
    //해당 포트의 서버에 연결할 수 없는 경우
}
```

외부 서버에 접속하려면 localhost 대신 정확한 IP를 입력하면 됩니다. 만약 IP 대신 도메인 이름만 알고 있다면, 도메인 이름을 IP 주소로 번역해야 하므로 InetSocketAddress 객체를 이용하는 방법을 사용해야 합니다. Socket 생성과 동시에 연결 요청을 하지 않고, 다음과 같이 기본 생성자로 Socket을 생성한 후, connect() 메소드로 연결 요청을 할 수도 있습니다.


다음 에제는 localhost 5001 포트로 연결을 요청하는 코드입니다. connect() 메소드가 정상적으로 리턴하면 연결이 성공한 것입니다.

```java
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientExample {
    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket();
            System.out.println("연결 요청");
            socket.connect(new InetSocketAddress("localhost", 5001));
            System.out.println("[연결 성공]");
        } catch (Exception e) {}
        
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e1) {}
        }
    }
}
```

## Soket 데이터 통신
클라이언트가 연결 요청(connect())하고 서버가 연결 수락(accept()) 했다면, 양쪽의 Socket 객체로부터 각각 입력 스트림(InputStream)과 출력 스트림(OutputStream)을 얻을 수 있습니다.

다음은 Socket으로부터 InputStream과 OutputStream을 얻는 코드입니다.

```java
// 입력 스트림 얻기
InputStream is = socket.getInputStream();

// 출력 스트림 얻기
OutputStream os = socket.getOutputStream();
```

상대방에게 데이터를 보내기 위해서는 보낼 데이터를 byte[] 배열로 생성하고, 이것을 매개값으로 해서 OutputStream의 write() 메소드를 호출하면 됩니다. 다음은 문자열을 UTF-8로 인코딩한 바이트 배열을 얻어내고, write() 메소드로 전송합니다.

```java
String data = "보낼 데이터";
byte[] byteArr = data.getByte("UTF-8");
OutputStream outputStream = socket.getOutputSteam();
outputStream.write(byteArr);
outputStream.flush();
```

상대방이 보낸 데이터를 받기 위해서는 받은 데이터를 저장하할 byte[] 배열을 하나 생성하고, 이것을 매개값으로 해서 InputStream의 read() 메소드를 호출하면 됩니다. read() 메소드는 읽은 데이터를 byte[] 배열에 저장하고 읽은 바이트 수를 리턴합니다. 다음은 데이터를 읽고 UTF-8로 디코딩한 문자열을 얻는 코드입니다.

```java
byte[] byteArr = new byte[100];
InputStream inputStream = socket.getInputStream();
int readByteCount = inputStream.read(byteArr);
String data = new String(byteArr, 0, readByteCount, "UTF-8");
```

