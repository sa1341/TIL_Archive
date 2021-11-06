# Forward Proxy와 Reverse Proxy의 차이

먼저 둘의 차이점을 알기 전에 Prxoy에 대해서 간단히 정의하자면 프록시 서버는 대행자로서 클라이언트가 자신을 통해서 다른 네트워크 서비스에 간접적으로 접속할 수 있게 해주는 컴퓨터 시스템이나 응용 프로그램을 의미합니다.


## Forward Proxy란?

클라이언트(사용자)가 인터넷에 직접 접근하는게 아니라 Foward Proxy 서버가 요청을 받고 인터넷에 연결하여 결과를 클라이언트에게 전달해줍니다. 

![Untitled Diagram drawio (1)](https://user-images.githubusercontent.com/22395934/140603730-873b960f-1e09-452b-aa2e-930fd75f7808.png)

마치 사용자가 자신의 IP 주소의 노출을 숨기기 위해서 Forward Proxy를 통해서 서비스를 제공해주는 서버에 간접적으로 접근하고 있다고 생각하면 됩니다.

> 프록시 서버는 Cache를 자주 사용하는 데이터라면 요청을 보내지 않고 캐시에서 가져올 수 있기 때문에 성능 향상이 가능합니다.

## Reverse Proxy란?

클라이언트(사용자)가 인터넷에 데이터를 요청하면 Reverse Proxy가 이 요청을 받아 내부 서버에서 데이터를 받은 후 클라이언트에게 전달합니다.

클라이언트는 내부 서버에 대한 정보를 알 필요 없이 리버스 프록시에만 요청하면 됩니다. 
내부 서버(WAS)에 직접적으로 접근하려면 DB에 접근이 가능하기 때문에 중간에 Reverse Proxy를 두고 클라이언트와 내부 서버 사이의 통신을 담당합니다.

또한 내부 서버에 대한 설정으로 로드밸런싱(Load Balancing)이나 서버 확장등에 유리합니다.

![Untitled Diagram drawio (2)](https://user-images.githubusercontent.com/22395934/140603910-f005acbc-1f63-4b14-8c83-268a83396ba1.png)

Reverse Proxy는 Forward Proxy와 반대로 서비스를 제공해주는 내부 서버의 정보를 은닉화하기 위해서 사용합니다. 즉, 네트워크 보안을 위해서 사용하기도 합니다.


## Forward Proxy와 Reverse Proxy의 차이점

Forward Proxy는 클라이언트가 요청하는 End Point가 실제 서버 도메인이고 Proxy는 둘 사이의 통신을 담당해주는 브로커입니다. 반대로 Reverse Proxy는 클라이언트가 요청하는 End Point가 프록시 서버의 도메인이고 실제 서버의 정보는 알 수 없습니다.



