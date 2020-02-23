# Vert.x 용어 정리

## Verticle: vert.x에서 수행되는 하나의 프로그램
- vert.x에서 배치(deploy)의 기본 단위다. Java의 경우라면 main 메서드가 있는 클래스가 된다. 

- Verticle은 또한 main 메서드에서 참조되는 다른 스크립트를 포함할 수 있다. .jar 파일이나 리소스를 포함할 수 있다. 

- 애플리케이션은 하나의 Verticle로 이루어질 수도 있고, 
event bus를 통해 서로 통신하는 여러 개의 Verticle로 이루어질 수도 있다. 
Java로 생각하면 독립적으로 실행 가능한 Class 또는 .jar 파일로 이해할 수 있겠다.

## vert.x instance: 하나의 vert.x 서버 프로세스

- Verticle은 vert.x 인스턴스 안에서 실행되고, vert.x 인스턴스는 자신의 JVM 인스턴스 안에서 실행됩니다. 단일 vert.x 인스턴스 안에서는 동시에 실행되는 많은 Verticle이 존재할 수 있습니다.

각각의 Verticle은 고유의 클래스 로더를 가질 수 있습니다.
이로 인해 Verticle 간에 스태틱 맴버, 글로벌 변수등을 통한 직접적인 상호작용을 막을 수 있습니다. 네트워크상의 여러 호스트에서 동시에 많은 vert.x 인스턴스가 실행될 수 있고 event bus를 형성해서 vert.x 인스턴스 간에 클러스터링 되도록 설정할 수 있습니다.


> 하나의 Vert.x 인스턴스는 JVM 위에서 동작합니다.


![vertx](https://user-images.githubusercontent.com/22395934/75112497-b37b5700-5687-11ea-8d52-aa0cb2e4439d.png)

verticle code는 클래스 로더를 통해 인스턴스로 객체화 됩니다.
이 인스턴스들은 Waiting Queue에서 기다리다가 순서대로 event loop 스레드를 점유하면서 실행됩니다. 수행되는 순간에는 싱글 스레드 모델이기 때문에 그 순간에는 하나의 verticle만 수행됩니다. (즉 verticle이 100개이고, ELP가 1개라도 모든 100개의 verticle은 ELP안에서 순차적으로 수행됩니다.)

## Worker Verticle

일반 Verticle은 개별 socket에 대해서 ELP를 돌면서 이벤트를 처리한다. 그래서 해당 이벤트 처리에 오랜 시간이 걸리면 전체적으로 성능이 많이 떨어지기 때문에 문제가 된다.

예를 들어,

개별 이벤트 처리속도 = 100ms
연결된 커넥션 = 100개 

Q. 전체 socket에 대해서 모두 이벤트 처리를 하는데 걸리는 시간은? 
=> 100ms * 100개 = 10000ms (10초)

즉, 클라이언트 입장에서 연속적으로 첫번째, 두번째 요청을 했을때 두번째 요청을 응답받기까지의 시간이 10초 후 라는 것입니다. 
이는 vert.x가 single thread 모델이기 때문에 생기는 문제인데, vertx에서는 이 문제를 해결하기 위해 Worker Verticle 이라는 형태의 verticle을 제공합니다. 

이 worker verticle은 쉽게 생각하면, Message Queue를 Listen하는 message subscriber라고 생각하면 됩니다.

vert.x 내부의 Event bus를 이용해서 메세지를 보내면, 뒷단의 worker verticle이 message queue에서 메세지를 받아서 처리한 후에 그 결과값을 다시 event bus를 통해 caller에게 보내는 형태입니다. (Asynchronous call back 패턴)

event bus로 request를 보낸 verticle은 resonse를 기다리지 않고 바로 다음 로직을 진행하다가 worker verticle에서 작업이 끝난 이벤트 메세지가 오면 다음 ELP가 돌때 그 이벤트를 받아서 응답 메세지 처리를 합니다.
DB 작업이나 시간이 오래 걸리는 작업은 이렇게 worker verticle을 이용해서 구현할 수 있습니다.

![jvm_messageque](https://user-images.githubusercontent.com/22395934/75113125-0c4dee00-568e-11ea-815a-3074117edfb9.png)


![vertx_architecture](https://user-images.githubusercontent.com/22395934/75113126-0d7f1b00-568e-11ea-8e5f-78a4c7b0e4a5.jpeg)
