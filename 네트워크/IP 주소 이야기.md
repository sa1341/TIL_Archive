## IP 주소의 개념

## 네트워크 

IP는 기본적으로 32자리 2진수로 구성되어 있습니다. 그리고 `네트워크 부분과 호스트 부분으로 구성되어 있습니다.` 여기서 말하는 네트워크는 하나의 브로드캐스트 영역(Broadcast Domain)이라고 생각하면 됩니다.
쉽게 말해서 하나의 PC가 데이터를 뿌렸을 때 그 데이터를 라우터를 거치지 않고도 바로 받을 수 있는 영역이란 뜻입니다.

예를 들어서 A라는 게임방에서 한 PC가 데이터를 그 게임방 안에 있는 다른 PC에게 전송한다면 그 데이터는 라우터를 거치지 않고 바로 전달이 가능합니다. 왜냐하면 두 PC는 같은 네트워크 영역(즉 네트워크 부분이
같다는 것입니다.)안에 있기 때문입니다. 하지만 A라는 게임방에 있는 PC가 인터넷의 어떤 PC(예를 들면 외국에 있는 PC나 서버)에 데이터를 보낸다면 그건 라우터를 거치지 않고는 전달이 불가능합니다.
왜냐하면 두 PC가 같은 네트워크 상에 있지 않기 때문입니다. 라우터는 브로드캐스트 영역을 나누는 역할을 합니다. 따라서 한 브로드캐스트 영역 안에 있는 두 PC는 라우터 없이도 통신이 가능하지만 그렇지 않은 경우에는
라우터를 꼭 거쳐야 한다는 겁니다

그럼 호스트란 무엇일까요?

## 호스트

말뜻 그대로 주인이란 의미가 아니고 그냥 각각의 PC 또는 장비라고 생각하면 됩니다.  네트워크와 호스트는 단순히 IP 주소가 보기에는 그냥 점 3개로 나누어진 4자리 숫자 같지만, 사실 IP 주소 자체는 네트워크
부분과 호스트 부분으로 나누어져 있습니다. 따라서 어떤 네트워크에서든지 `하나의 네트워크`에서는 네트워크 부분은 모두 같아야 되고 호스트 부분은 모두 달라야 정상적인 통신이 일어난다는 겁니다.

에를 들어서 한 사무실에서 50대의 PC가 한 라우터를 통해 연결되어 있다면 이 PC들은 모두 같은 네트워크 상에 있다고 말합니다. 따라서 이 PC들의 IP 주소 중에서 네트워크 부분은 모두 같아야 합니다. 
그래야 라우터를 거치지 않고 서로 통신하는 것이 가능합니다. 하지만 호스트 부분은 서로 모두 달라야 합니다. 만약 호스트가 같다면 두 PC는 서로 IP 주소 충돌이 생겨서 통신이 불가능하게 됩니다. 마치 각 지방별로 
전화번호를 나눌 때 한 지역의 지역번호는 모두 같고(예를 들어 전북 지역의 모든 전화번호는 063으로 모두 같습니다.) 전화번호는 서로 다른 것과 마찬가지 입니다.

좀 더 구체적으로 예를 들어보면 한 PC방에서 쓰는 IP 주소가 203.240.100.1에서 203.240.100.255까지라면 이 중에서 203.240.100 부분은 네트워크 부분입니다. 따라서 이 PC방에서 쓰는 모든 IP 주소 중
이 부분은 모두 동일해야 한다는 겁니다. 그렇지 않으면 통신이 불가능해집니다. 또 하나 호스트 부분은 맨 마지막 자리(마지막 옥텟)가 됩니다. 따라서 1에서 255가 바로 호스트 부분이 되는 겁니다.
호스트 부분 1에서 255까지 모든 PC가 서로 달라야 합니다.

## IP 주소 클래스

IP 주소의 클래스는 A부터 B, C, D, E로 구분됩니다. 이렇게 클래스에 따라서 어디까지 네트워크 부분이고, 어디까지가 호스트 부분인지가 나뉩니다. 이렇게 나눈 이유는 네트워크의 크기에 따른 구분이라고 생각하면 됩니다. 하나의 네트워크가 호스트의 수를 몇 개까지 가질 수 있는가에 따라서 클래스가 나뉩니다.

먼저 클래스 A를 살펴보겠습니다.

클래스 A는 하나의 네트워크가 가질 수 있는 호스트 수가 가장 많은 클래스입니다. IP 주소가 2진수로 표현되기 때문에 클래스 A는 32개의 이진수 중에서 맨 앞쪽 하나가 항상 0으로 시작되는 것들입니다.

즉, 0xxx xxxx, xxxx xxxx, xxxx xxxx, xxxx xxxx와 같이 32개의 이진수 중에 맨 앞 하나는 꼭 0이 나와야 되고 나머지 0과 1중 아무거나 나와도 됩니다.

예를 들어 InterNIC(공인 IP 주소를 분배 관리하는 곳)으로부터 클래스 A 주소로 13.0.0.0 네트워크를 받았다고 가정해보겠습니다.
클래스 A는 맨 앞에 하나의 옥텟만 네트워크 부분이고, 나머지 3개의 옥텟은 호스트 부분이기 때문에 IP 주소를 분배하는 InterNIC에서는 이처럼 앞자리 13만 주게 됩니다. 뒤에 3개의 옥텟, 즉 호스트 부분은
마음대로 정할 수 있다는 겁니다.

따라서 13.0.0.0 ~ 13.255.255.255까지 몇개의 수가 들어가는지 알기만 하면됩니다. 
그런데 호스트가 전부 0인 경우, 즉 13.0.0.0인 경우는 호스트 주소가 아니라 네트워크 전체를 나타내기 때문에 사용하지 않고, 또 호스트가 전부 1인 경우, 즉 13.111.1111.1111 1111.1111 1111(즉
13.255.255.255)인 경우는 13 네트워크 전체에 있는 모든 호스트들에게 전송할 때 사용하는 브로드캐스트 주소이기 때문에 호스트 주소로 사용하지 않습니다. 따라서 13 네트워크에 가능한 호스트 수는 2의 24승에서 2를 뺀 수가 되는데, 이게 바로 16,777,214 입니다.

클래스 B는 맨 앞이 반드시 10(이진수)로 시작됩니다. 뒤에는 어떤 숫자가 와도 상관 없습니다. 즉, 10xx xxxx.xxxx xxxx.xxxx xxxx입니다. 클래스 B의 경우는 앞의 16비트(즉, 옥텟 2개, 맨 앞의 두 자리
십진수 부분)가 네트워크 부분을 나타내고, 나머지 16비트(즉 2개의 옥텟, 나머지 2개의 십진수)가 호스트 부분을 나타낸다는 약속이 있습니다. 그래서 클래스 B는 가장 작은 네트워크 128.0.0.0에서 가장 큰 네트워크
191.255.0.0까지 포함됩니다.

클래스 C의 경우는 맨 앞이 110(이진수)으로 시작됩니다. 뒤에는 어떤 숫자가 와도 상관 없습니다. 즉 110x xxxx, xxxx xxxx, xxxx xxxx 입니다. 따라서 맨 앞에는 110이 반드시 나와야 하고 나머지 29개의 이진수는 0과 1중에서 어떤 수가 와도 됩니다. 


## 서브넷 마스크

서브넷 마스크란, 일단 말뜻 그대로 서브, 즉 메인이 아닌 어떤 가공을 통한 네트워크를 만들기 위해 씌우는 마스크라고 생각하면됩니다. 다시 말해서 우리가 일단 어떤 IP 주소를 배정받게 되면 보통은 이 주소를
그대로 사용하지 않습니다. 왜냐하면 자신의 입맛에 맞추어야 하기 때문입니다.

IP는 무조건 필수적으로 부여받아야 인터넷이 가능하고, 네트워크가 서로 다른 두 장비 간의 통신은 라우터를 통해서만 가능합니다.
TCP/IP 통신할 경우 라우터의 각 인터페이스 역시 IP 주소를 부여해 주는 것이 좋습니다. 라우터의 인터페이스에 IP 주소를 부여할 때는 그 인터페이스가 속한 네트워크의 주소가를 부여해야 합니다. IP 주소를 배정할 때는
그 네트워크에 몇개의 호스트가 접속이 가능한지를 먼저 확인한 후에 배정하는 주소가 이 호스트1를 모두 포함할 수 있는지를 확인해야 합니다.

## 기본 게이트문

기본 게이트웨이는 말 그대로 `기본이 되는 문`입니다. 즉, 내부 네트워크에서는 라우터 없이도 통신이 가능합니다. 같은 브로드캐스트 도메인에서는 라우터 없이 통신이 가능하다는 것을 이제 알고있습니다.
따라서 통신을 할 때 우리가 어떤 곳을 찾아간다면 PC는 그곳을 찾기 위해 내부 네트워크를 먼저 뒤집니다. 전부 찾아봐도 없다면 그 다음은 밖으로 나가서 찾아보는 겁니다. 이때 밖으로 나가는 문이 있는데, 이 문이
바로 기본 게이트 웨이라는 겁니다. 즉, 기본 게이트웨이란, 내부 네트워크에서 없는 녀석을 찾을 때 밖으로 통해 있는 문이 되는 겁니다. 따라서 이 문은 바로 라우터의 이더넷 인터페이스가 되는 겁니다.
