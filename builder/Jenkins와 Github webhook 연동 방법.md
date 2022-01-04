# Jenkins와 Github webhook 연동 방법

ec2에서 내가 개발한 어플리케이션을 dockerized(도커 이미지 빌드)하여 Jenkins 인스턴스에서 어플리케이션 서버 인스턴스에 배포하는 실습을 하면서 자주 사용하는 github에서 어떻게 Jenkins와 연동해서 배포하는지 살펴보았습니다.

먼저, Jenkins 대시보드 플러그인 관리자에서 `GitHub Integration Plugin`을 설치해야 합니다.


1.Jenkins에서 배포 Item에서 소스코드 관리에 build 하기 위한 git 저장소 URL을 작성합니다.

![스크린샷 2021-12-31 오전 2 19 59](https://user-images.githubusercontent.com/22395934/147774113-d075a14f-31d6-4771-8327-06e9532559b0.png)

아래 옵션을 체크합니다.

- Generic Webhook Trigger
- GitHub hook trigger for GITScm polling


2.빌드 후 조치 탭 클릭 후 아래와 같이 Jenkins에서 build 후 생성된 jar 파일명과 실행 명령어를 작성합니다. 배포할 인스턴스가 여러개이면 동일하게 적어줍니다.

![스크린샷 2021-12-31 오전 2 23 37](https://user-images.githubusercontent.com/22395934/147774313-c8c6a6f9-f7a1-4447-bbc8-e37153da5925.png)

## Build

Jenkins에서 Github에 있는 소스 코드를 빌드 하기 위해 빌드 환경에 아래 이미지 처럼 빌드 명령어를 작성했습니다. 

![스크린샷 2021-12-31 오전 2 32 01](https://user-images.githubusercontent.com/22395934/147774880-bdba71b2-2b56-44fb-bde1-6019bb0b0baf.png)

빌드 툴을 어떤걸 쓰냐에 따라서 아래 명령어로 작성해주며 됩니다. 저는 maven으로 빌드 툴을 사용했습니다.

### Maven

```java
chmod 544 ./mvnw
./mvnw clean package
```

### Gradle

```java
chmod 544 ./gradlew
./gradlew clean build
```

## 배포 실행 명령어

```java
// 무중단 배포를 위한 sleep 명령어
sleep 30 
// 기존의 실행중인 어플리케이션 종료
sudo kill -15 $(sudo lsof -t -i:8080)
// 실행가능한 어플리케이션 jar 파일 실행
nohup sudo java -jar cpu-0.0.1-SNAPSHOT.jar > nohup.out 2>&1 &
```

사실 위의 sleep 명령어는 굳이 명시하지 않아도 되지만 만약 무중단 배포를 하고 싶다면 업무 요건에 맞게 배포 스크립트를 수정하면 됩니다. 

저 같은 경우에는 nginx의 revese proxy를 이용해 로드 밸런싱을 인스턴스 3개로 설정했기 때문에 무중단 배포를 위해 30초간의 sleep 명령어를 주어 순차적으로 배포되도록 스크립트를 작성했습니다.



## GitHub webhook 설정하기

이제 Jenkins 설정은 끝났고 가장 중요한 GitHub webhook 설정을 하겠습니다. webhook을 사용하는 이유는 만약 원격 저장소로 수정된 소스코드를 push하면 GitHub webhook이 Jenkins 인스턴스에 특정 이벤트 발생 내용을 알려주어 자동 소스코드 build를 수행하고 배포되도록 할 수 있습니다. 

즉, CI/CD가 자동화되기 때문에 개발자 입장에서는 편해집니다.

## webhook 설정

![스크린샷 2021-12-31 오전 2 44 42](https://user-images.githubusercontent.com/22395934/147775808-d580d616-10d7-4604-b464-14af00d3e3a7.png)

Payload URL에 반드시 `http://{jenkins-ip}:{port}/github-webhook/` 마지막 슬래시(/)까지 입력을 해줘야 합니다.

```java
// 예시
http://52.78.52.581:8080/github-webhook/
```

그리고 `Just the push event` 옵션을 체크하고  아래 Update webhook을 클릭합니다.

만약 아래와 같이 연결 설정이 안되어 빨간 불이 켜질 경우에는 github-webhook이 사용하는 ip가 ec2 인스턴스 보안그룹 규칙에 설정이 안되어 연결이 안될 수 있기 때문에 https://snepbnt.tistory.com/510 해당 블로그를 참조하여 ip들을 jenkins 인스턴스의 네트워크 보안 규칙에 추가해주면 됩니다.

![스크린샷 2021-12-31 오전 2 49 31](https://user-images.githubusercontent.com/22395934/147776174-b4ac41e1-3898-455b-b59e-690909d93c59.png)


