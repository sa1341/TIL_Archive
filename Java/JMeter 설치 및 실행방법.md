# JMeter 실행 방법 및 용어 정리

Apach JMter를 사용하기 위해 알아야하는 용어들에 대햇거 정리하였습니다. 기본적으로 REST API 기능 개발을 하고 부하 테스트를 위해 많이 사용하는 툴 중 하나이기 때문에 배워두면 유용할 것 같습니다.

JMeter는 원하는 만큼 쓰레드를 생성하여 서버에 부하를 줄 수 있는 도구입니다. 

## 1. 설치 방법

Mac을 사용하기 때문에 HomeBrew라는 편리한 패키지 매니저로 설치하였습니다.

```brew
brew install jmeter
```

## 2. 실행 방법

CLI에서 아래 명령어를 입력하면 웅장한 화면의 JMeter가 바로 실행됩니다.

```brew
open /usr/local/bin/jmeter
```

#### 실행화면

![image](https://user-images.githubusercontent.com/22395934/122951139-0407df00-d3b8-11eb-9fac-91b98ffccfe4.png)


## 3. 플러그인 설치

![image](https://user-images.githubusercontent.com/22395934/122951691-6eb91a80-d3b8-11eb-8b2b-149fe46da7fa.png)

Option -> Plugin Manager를 들어가면 위 이미지와 같은 화면이 나옵니다. Available Plugins에 들어가서 해당 플러그인들을 설치해야 합니다.

- 3 Basic Graphs
- Custom Thread Groups

## 4. 간단한 사용방법

기본적으로 Test Plan(테스트 진행 계획) 하위에 Thread Group를 생성해야 합니다.

Test Plan을 우클릭하고 `Add` -> `Threads(Users)` -> `Thread Group`를 눌러 추가시켜줍니다.

![image](https://user-images.githubusercontent.com/22395934/122952568-0b7bb800-d3b9-11eb-96f1-80e5477cd16a.png)

### Http Request 추가하기

간단하게 웹 서버에 http 요청을 보내서 부하를 줘보겠습니다.

![image](https://user-images.githubusercontent.com/22395934/122952711-277f5980-d3b9-11eb-995d-4037e4588e60.png)

Http Request 하위에 아래 Listener를 추가해줍니다.

![image](https://user-images.githubusercontent.com/22395934/122953764-dae84e00-d3b9-11eb-83b7-b8f4b92aecee.png)


### Thread Group 설정하기

Test Plan 하위에 Thread Group를 클릭하면 아래와 같은 항목이 나오는데 각각의 항목들은 아래 정의하였습니다.

![image](https://user-images.githubusercontent.com/22395934/122954105-1edb5300-d3ba-11eb-9b96-f09e97ee0e19.png)


- Number of Thread(users): 가상의 생성자를 몇 명으로 설정할지에 대한 값입니다. 즉, 스레드의 개수를 의미한다고 생각하시면 됩니다. 이 값이 커질수록 서버는 많은 부하를 받습니다.

- Ramp-up Period(in seconds): 한번의 실행을 몇초 동안 완료 시킬것인지에 대한 설정 값입니다.

- Loop Count: 한 쓰레드당 반복하고자 하는 횟수입니다. infinite를 체크하면 무제한 실행됩니다.


### Http Request 설정하기

![image](https://user-images.githubusercontent.com/22395934/122954350-50ecb500-d3ba-11eb-9c48-2a07a7972491.png)

포스트맨을 써보면 대충 짐작이 갑니다만 Method는 웹 서버에 HTTP로 요청을 할 경우 지정할 메서드이고, Path는 요청할 서버의 End Point 입니다. 위 예시에서는 ip는 localhost, port는 8080으로 설정하였습니다.

### Summary Report 분석하기

![image](https://user-images.githubusercontent.com/22395934/122955390-091a5d80-d3bb-11eb-99ba-a0036cf15aa1.png)

위의 이미지는 Http 요청을 통한 서버의 성능 분석을 위해 기본적으로 알아야되는  `Summary Report` 화면을 보여줍니다.

- Sample: 생성한 스레드들의 접속 수를 의미합니다. 
- Max: 스레드 중에 처리가 가장 오래걸린 시간을 의미합니다.(단위는 m/s)
- Min: 스레드 중에 처리가 가장 적게걸린 시간을 의미합니다. (단위는 m/s)
- Average: 평균 처리 시간을 의미합니다. (단위는 m/s)
- Throughput: 단위 시간당 대상 서버(웹 서버, WAS, DB 등)에서 처리되는 요청의 수를 말합니다. JMeter는 시간 단위를 보통 `TPS(Transaction Per Second)로 표현합니다.`
- Error %: 서버에서 얼마나 에러가 발생하는지 확인하는 지표입니다.
- Std. Dev.: 표준편차로써 평균으로부터 각각의 요청하는 시간이 얼마나 떨어져있는지를 의미합니다. 만약 0이면 모두 평균과 똑같은 시간을 사용했다는 것을 의미합니다. 만약 0보다 크다면 들쑥날쑥한 것을 의미합니다.

> TPS의 개념은 무조건 클라이언트에서 서버로의 요청(request) 갯수로 이해하면 안됩니다. 한번의 요청이라고 하더라도 단순한 웹사이트 호출일 수도 있고, 특정 데이터를 전송하여 백엔드에서 처리되도록 하는 경우도 있습니다. 핀트는 한번의 요청으로 몇 번의 비즈니스 처리(트랜잭션)가 일어나는지 확인하는 것이 중요합니다. 만약, POST 방식으로 10개의 데이터를 서버로 전송하고 건당 비즈니스 로직으로 처리가 된다면 1번(요청수) * 10개(데이터수)=10 TPS라고 이해하는 것이 좋습니다.


위에서 JMeter의 설치 및 실행방법을 살펴보면서 느낀점은 서버에 대한 성능을 가시적으로 볼 수 있고 부하 요청에 따라 시스템이 어떻게 반응하는지 알 수 있었습니다.
