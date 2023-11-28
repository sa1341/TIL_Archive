## 도커란?

도커(Docker)는 리눅스 컨테이너에 여러 기능을 추가해서 어플리케이션을 컨테이너로 쉽게 사용할 수 있게 해주는 프로젝트로 Go 언어로 만들어졌습니다.

## 가상 머신(Virtual Machine) VS 도커 컨테이너(Docker Container)
기존의 가상화 기술은 가상 머신(VM)이라하여 하이퍼바이저(Hypervisor)를 이용해 여러 개의 운영체제를 하나의 호스트에서 생성해 사용하는 방식이었습니다. 
이 때 생성되고 관리되는 운영체제는 게스트 운영체제(Guest OS)라고 하며, 각 게스트 운영체제는 독립된 공간과 시스템 자원을 할당받아 사용합니다. 
대표적인 툴로는 VirtualBox, VM Ware가 있습니다.
하지만 가상 머신은 게스트 운영체제(Guest OS)를 사용하기 위해 커널과 라이브러리 등이 포함이 되기 때문에 무겁고 용량이 큽니다. 
대신에 도커 컨테이는 가상화 공간을 만들기 위해 리눅스의 자체 기능인 chroot, namespace, cgroup을 사용하기 때문에 성능 손실이 거의 없으며,
컨테이너에 필요한 커널은 호스트 운영체제의 커널을 공유해 사용 하기 때문에, 가볍습니다.


## 도커 명령어 옵션

![image](https://user-images.githubusercontent.com/22395934/89712235-85d26200-d9ca-11ea-80d5-5e7d64ac9d74.png)


ex)
- docker run -d -p 1234:6379 redis
- -p 옵션을 이용: 호스트의 1234포트를 컨테이너의 6379포트로 연결, localhost의 1234포트로 접속하면 하면 redis를 사용 가능

## 도커 이미지 빌드 

Jenkins에서 Github 소스코드를 빌드 후에 Docker 컨테이너에 톰캣내부로 배포를 하는 경우 아래와 배포서버에 도커와 `Dockerfile`이 있으면 가능합니다.
먼저, Dockerfile을 vi 에디터로 연후에, 아래와 tomcat 9.0 버전 이미지를 받은 후 해당 컨테이너의 webapps 경로로 war 파일을 복사하도록 도커파일을 작성하였습니다.

```java
FROM tomcat:9.0

LABEL org.opencontainers.image.authors="a79007741@gmail.com"

COPY ./hello-world.war /usr/local/tomcat/webapps
```

### 도커파일 작성

가장 단순한 Node.js 프로그램용 애플리케이션 이미지를 빌드하는 예제입니다.
하기에 package.json, server.js라는 두 가지 파일로 구성했습니다.
실습을 하기전에 `npm install express --save` 명령으로 Express에 대한 의존성을 설정하고 설치해야 합니다.

#### package.json 파일

```js
{
    "name": "simple-node",
    "version": "1.0.0",
    "description": "A sample simple application for Kubernetes Up & Running",
    "main": "server.js",
    "scripts": {
        "start": "node server.js"
    },
    "author": "jean",
    "dependencies": {
        "express": "^4.18.2"
    }
}

```

#### server.js 파일 

```js
var express = require('express');

var app = express();
app.get('/', function(req, res) {
    res.send('Hello World!');
});

app.listen(3000, function() {
    console.log('Listening on port 3000!');
    console.log('http://localhost:3000');
});
```

이를 도커 이미지로 패키징하려면 .dockerignore와 Dockerfile이라는 두 개의 파일을 추가로 생성해야 합니다.

#### .dockerignore

```sh
node_modules
```

.dockerignore 파일은 이미지에 복사할 때 무시해야 되는 파일 세트를 정의합니다.

#### Docerfile 

```Dockerfile
# Node.js 16 이미지에서 시작
FROM node:16

# 모든 명령이 실행될 이미지 내부의 디렉토리를 지정
WORKDIR /usr/src/app 

# 패키지 파일 복사와 의존성 설치
COPY package*.json ./
RUN npm install
RUN npm install express

# 모든 앱 파일을 이미지에 복사
COPY . .

# 컨테이너를 시작할 때 실행할 기본 명령 지정
CMD [ "npm", "start" ]
```

모든 도커파일은 컨테이너 이미지를 기반으로 빌드됩니다. 위 도커파일은 빌드 시 도커허브의 `node:16` 이미지에서 시작되도록 지정했습니다.

## 도커 gracefully 종료

도커 컨테이너를 종료할 때 실행중인 어플리케이션이 요청을 다 처리하고 종료할 수 있도록 사용하는 명령어는 아래와 같습니다.

### 도커 이미지 생성

```sh
docker build -t simple-node .

docker run --rm -p 3000:3000 simple-node
```


```java
// 도커 컨테이너 id 확인
docker ps | grep application-name

// gracefully 종료 명령어
docker container kill -s 15 container_id
```


#### 참조: http://pyrasis.com/Docker/Docker-HOWTO#search, Kubernetes Up & Running
