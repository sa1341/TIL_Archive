# Dockerfile 만들기

Spring Boot Application을 도커 이미지로 빌드하고 도커 허브에 push, 클라우드 환경에서 다시 도커 허브로부터 이미지를 pull 받아서 컨테이너를 실행시키는 가장 기본적인 실행 절차들을 살펴봤습니다.

먼저, https://spring.io/guides/gs/spring-boot-docker/ 해당 가이드를 참조하였습니다.

## 도커파일 생성하기

가장 먼저 스프링 부트 애플리케이션 프로젝트 하위에 `Dockerfile`을 만들고 위 가이드를 따라서 아래와 같이 코드를 작성합니다.

```java
FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

alpine linux를 기본으로 해서, 애플리케이션 구동을 위한 이미지들이 이미 많이 있는데, java의 경우 openjdk기반의 alpine 이미지가 있습니다. java 8을 쓰기 때문에 `openjdk:8-jdk`로 정의했습니다. 

## Containerize

저는 빌드 툴로 Maven을 사용했기 때문에 아래와 같은 명령어를 사용하여 도커 이미지를 빌드했습니다.

```java
docker build -t sa1341/spring-boot-cpu-bound .
```

이제 로컬에서 테스트로 이미지가 잘 build 됬는지 실행시켜 봅니다.

```java
docker run -p 80:80 sa1341/spring-boot-cpu-bound
```

> 물론 사전에 도커서버가 이미 설치되고, 실행중인 상태여야 합니다.


## 도커허브에서 저장소 생성하기

도커허브 로그인을 합니다. 

> https://hub.docker.com/

상단의 Repositories에 탭을 누르고 Create Repository를 클릭하여 원하는 리포지터리명을 작성하고 public 모드로 리포지터리를 생성합니다.

## Docker Image push

빌드된 도커 이미지를 위에 도커허브에 로그인하여 생성한 리포지터리로 푸쉬합니다.

```java
docker push sa1341/spring-boot-cpu-bound
```

push 뒤에는 `사용자명/리포지터리 저장소`를 의미합니다.

만약 denied라는 로그가 나온다면 아래 명령어로 도커허브 로그인을 합니다.

```java
docker login
```

그리고 다시 push를 하면 정상적으로 되는 것을 확인할 수 있습니다.

## Docker Image pull

이제 클라우드 환경에서 위에서 빌드한 도커 이미지를 pull 받은 후 실행해보았습니다.

기본적으로 Google Cloud Platform의 기본 인스턴스를 사용하였습니다.

### 도커 설치 및 실행

```java
// 도커 설치
sudo yum install docker

// 도커 실행
sudo systemctl start docker
```

위의 명령어들을 실행하고 도커허브로부터 도커 이미지를 pull 받은 후 실행시키는 명령어는 아래와 같습니다.

### 도커 이미지 pull 받기

```java
docker pull sa1341/spring-boot-cpu-bound
```

### 도커 실행

```java
sudo docker run -p 80:80 sa1341/spring-boot-cpu-bound
```

이제 어플리케이션을 컨테이너화하여 클라우드 환경에서도 쉽게 실행시킬수 있는 가장 기본적인 방법에 대해서 살펴봤습니다.



