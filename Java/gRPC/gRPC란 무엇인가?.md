# gRPC란 무엇인가?

gRPC를 살펴보기전에 RPC(Remote Procedure Call)에 대해서 먼저 개념을 살펴보았습니다.

## 1. RPC

RPC는 Remote Procedure Call의 약자로 분산 네트워크 환경에서 조금 더 편하게 프로그래밍 하기 위해 등장하였습니다. 클라이언트-서버 간의 커뮤니케이션에 필요한 상세한 정보는 최대한 감추고 개발자는 각 로직에만 집중할 수 있도록 클라이언트/서버는 일반 메서드를 호출하는 것처럼 개발을 진행하면 됩니다.

- caller/callee: 개발자가 필요한 로직을 생성하고 정의된 IDL(interface definition languate)로 작성하여 stub를 호출합니다.

- Stub: Stub compiler가 IDL 파일을 읽어 원하는 language로 생성하고 파라미터를 마샬링/언마샬링처리하여 RPC 프로토콜로 전달합니다.

- RPC runtime: 통신하여 각 메시지를 전달하게 됩니다.

## gRPC란

gRPC는 google에서 마이크로서비스에 사용하던 단일 범용 RPC 인프라 Stubby에서 시작하였습니다. Stubby 다음 버전을 계획하면서 외부에 오픈하기로 결정하였다고 합니다. 높은 생산성과 효율적인 유지보수, 다양한 언어와 플랫폼 지원, 높은 메시지 압축률과 성능, 이러한 특징으로 높은 성능의 오픈소스 범용 RPC 프레임워크입니다.


![Untitled Diagram drawio](https://user-images.githubusercontent.com/22395934/134168973-99c45cac-8e44-47d6-8c44-5e8c80ce280c.png)


## gRPC의 장점

### 높은 생산성과 효율적인 유지보수

IDL(Identity Definition Language)로 `protocol buffers(protobuf)`를 사용합니다. IDL만 정의하면 높은 성능을 보장하는 서비스와 메시지에 대한 소스코드가 각 언어에 맞게 자동 생성됩니다.

따라서 개발자들은 생성된 코드를 클라이언트, 서버 간의 사용 언어에 구애받지 않고 사용하기만 되며 정해진 규약을 공통으로 사용하기 때문에 의사소통 비용이 감소하게 됩니다.

### 높은 메시지 압축률과 성능

gRPC는 내부적으로 HTTP/2를 사용하여 헤더 압축률이 높고 protobuf에 의해 통신시점에는 바이너리 데이터로 통신하기 때문에 메시지 크기가 작습니다.
그리고 추가로 HTTP/2이기 때문에 양방향 스트림 통신이 가능합니다.

## gRPC의 단점

protobuf 및 HTTP/2에 대한 아주 약간의 런닝커브가 존재합니다. 또한 일반 REST API와 다르게 메시지가 바이너리로 전달하게 되어 개발자들의 테스트가 쉽지 않습니다. 이를 위해서 POSTMAN과 유사한 bloomRPC를 통해 gRPC 테스트를 해볼 수 있습니다.

## Protocol Buffers

Protocol Buffers에 대해서 간단히 살펴보겠습니다.
gRPC에서 IDL로 사용하고 있는 언어입니다. 구글에서 만들고 사용하는 데이터 직렬화 라이브러리로 아래와 같은 문법을 사용합니다.

```java
message Person {
    required string user_name        = 1;
    optional int64  favourite_number = 2;
    repeated string interests        = 3;
}
```

이렇게 작성된 proto 파일을 통해서 protoc 컴파일러를 통해 각 언어로 소스코드가 생성됩니다. 아래와 같은 예시로 protocol buffer가 선언되었고, 이는 통신하는 시점에 아래 이미지와 같이 인코딩되어 송수신됩니다.
따라서 본문의 사이즈가 훨씬 간결해집니다.


> gRPC 공식문서 링크: https://grpc.io/docs/what-is-grpc/introduction/ 
