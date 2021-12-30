# Jenkins를 이용한 배포 방법

회사에서 개발을 하고 Github로 Push 후 빌드를 할 때 Jenkins라는 툴을 사용했는데 사실 단순히 배포만 하는 툴로만 알고 있다가 이번에 조금이라도 더 Jenkins에 대해서 알고 싶어서 로컬환경에서 실습을 해봤습니다.

젠킨스에 대해서 알기 전에 CI/CD라는 개념을 알아야 합니다.

## CI/CD란?

CI(Continuous Intergration): 뜻 그대로 지속적인 통합을 의미합니다. 여러 개발자들이 협업을 통해서 코드를 계속해서 통합하는 것 입니다.

CD(Continuous Delivery): 개발자들이 코드를 계속 작성하면서, 사용자 및 내부 사용자들(즉, QA 등등)이 계속 쓸 수 있게 만드는 것, 즉 지속적으로 배포가능한 상태를 유지하는 것을 의미합니다.

## Jenkins란?

- Java Runtime 위에서 동작하는 자동화 서버
- 다양한 플러그인을 종합해서 CI/CD Pipeline을 만들어서 자동화 작업

위에 설명한 다양한 플러그인 중 SonaQube라는 플러그인이 있었는데 빌드 도중에 소스를 검사하여 일정 품질을 보장해주는 플러그인도 있습니다.

AWS ec2 인스턴스를 총 2개 생성하였습니다. 하나는 Jenkins 서버가 설치될 `Jenkins 인스턴스`이고, 나머지 하나는 도커 컨테이너가 실행될 `워커 인스턴스` 입니다.

## EC2 인스턴스 생성 

![스크린샷 2021-12-25 오후 5 06 07](https://user-images.githubusercontent.com/22395934/147380523-b5c83da8-dd51-48c9-a656-b0036ac908e6.png)

먼저, jenkins 인스턴스에 ssh로 접속하여 필요한 패키지들을 설치해야 합니다.

```java
// Jenkins instance에 ssh 접속
ssh -i keypair.pem ec2-user 54.180.100.239
```

## Java 8 설치

젠킨스의 java 버전은 8버전이므로 java 8버전을 설치해야 합니다.

```java
// yum 패키지를 최신버전으로 업데이트
sudo yum -y update

// Java 8 설치
sudo yum install java-1.8.0

// Java 8 및 컴파일러까지 포함
sudo yum install java-1.8.0-openjdk-devel.x86_64
```

위의 일반 버전으로만 설치하면 ec2에 javac 컴파일러가 설치가 되지 않기 때문에 `openjdk-devel`가 붙은 패키지로 설치하는 걸 권고합니다. Jenkins webhook 연동 시에 컴파일러가 없으면 build가 되지 않기 때문에 저는 해당버전으로 설치하였습니다.

> Java 8버전이 아닌 버전이 EC2에 이미 설정되어 있다면 아래와 같은 명령어로 버전 변경이 가능합니다.

```java
// java 버전변경
sudo /usr/sbin/alternatives --config java

// 기존 7버전 삭제 
sudo yum remove java-1.7.0-openjdk -y
```


## Jenkins 설치

```java
// jenkins repository 추가
sudo wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo 

sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io.key

yum install jenkins
```

yum으로 jenkins 설치 시 패키지가 없기 때문에 별도로 [젠킨스 공식 홈페이지](https://pkg.jenkins.io/redhat-stable/)를 보고 다음과 같이 추가해줘야 합니다.

설치가 완료됬다면 Jenkins를 실행시켜 봅니다.

만약 위의 과정을 거쳐도 jenkins 패키지 설치가 실패한다면 에러에 대해서 스택오버플로우 형님들이 해결해주니 걱정할 필요가 없습니다.
저같은 경우에는 아래와 같이 `amazon-linux-extras` 패키지관리자로 epel을 추가 설치하고 다시 젠킨스 설치를 실행하니 정상적으로 동작했습니다.

```java
sudo amazon-linux-extras install epel
```

```java
// 젠킨스 기동
sudo systemctl start jenkins

// 젠킨스 상태값 확인
sudo systemctl status jenkins
```

systemctl의 status를 통해서 아래와 같이 active로 나온다면 정상 기동된 것을 확인할 수 있습니다.

![스크린샷 2021-12-25 오후 5 47 07](https://user-images.githubusercontent.com/22395934/147381322-506de691-5c01-4899-97fa-e6360059f304.png)

이제, ec2 퍼브릭 IPv4 주소로 젠킨스 서비스가 정상적으로 동작하는지 확인하고, 초기 설정을 하면 됩니다.

```java
// 기본포트는 8080 
http://54.180.100.239:8080/
```

## Jenkins 플러그인 설치

SSH 프로토콜로 cpu-worker-instance 서버로 배포를 해야 하기 때문에 `Publish Over SSH` 플러그인을 설치해야합니다.

- Jenkins 대시보드 -> Jenkins 관리 -> 플러그인 관리 -> 설치 가능 클릭

설치 가능 탭에서 filter 검색 창에 ssh를 검색하여 `Publish Over SSH` 플러그인을 젠킨스 재기동 없이 설치하면 끝입니다!


## worker 인스턴스 ssh 접속 설정

이제 jenkins 인스턴스에서 worker 인스턴스로 배포를 하기 위해서 가장 중요한 단계는 jenkins 인스턴스에서 공개기/비밀키를 생성하여 worker 인스턴스에 ssh 접속 설정을 하여 배포할 수 있도록 세팅하는 부분이 필요합니다.

### Jenkins 서버 공개키/개인키 생성

```java
ssh-keygen -t rsa -f ~/.ssh/id_rsa
```

위의 명령어를 실행하면 /home/.ssh 디렉토리 하위에 id_rsa(개인키), id_rsa.pub(공개키)가 생성된 것을 확인 할 수 있습니다.

ssh를 사용하는 이유는 해커들의 worker 인스턴스의 접속을 막기 위해서 비대칭키 방식을 사용하여 오로지 젠킨스 인스턴스만 접속할 수 있도록 하기 위해서 입니다.

id_rsa.pub 파일을 열어서 해당 공개키를 worker 인스턴스의 `/home/.ssh/authorized_keys` 파일에 복붙 합니다.

그리고 반드시 아래와 같이 .ssh 디렉토리와 authorized_keys 파일의 권한을 아래와 같이 변경해야 합니다. 

```java
// drwx------
chmod 700 ~/.ssh
// -rw-------
chmod 600 ~/.ssh/authorized_keys
```

## Jenkins SSH 빌드 구성

다시 Jenkin 대시보드에서 Jenkins 관리 -> 시스템 설정으로 들어가서 아래와 같이 Jenkins 인스턴스에서 생성한 id_rsa(개인키) 파일을 열어서 아래와 같이 복붙을 합니다. SSH Server에 worker 인스턴스의 정보를 셋팅해주면 됩니다.

![스크린샷 2021-12-25 오후 6 18 00](https://user-images.githubusercontent.com/22395934/147381970-d65f936c-803c-4d9f-b641-203cde010f5c.png)

젠킨스 인스턴스와 워커 인스턴스는 같은 네트워크 그룹에 속하기 때문에 내부 ip로 Hostname을 설정해줍니다. 그리고 저장을 합니다.


### Item 구성

다시 젠킨스 대시보드에서 배포 스크립트를 작성할 프로젝트를 생성해야하는데 대시보드에서 새로운 Item을 클릭합니다.

저는 cpu-worker-instance-1 deploy라는 Item 명으로 생성을 하였고, 구성에 들어가서 빌드 후 조치에서 `Send build artifacts over SSH`로 설정 하면 SSH Server에서 worker 인스턴스만 나오는것을 확인 할 수 있습니다. 여기서 고급 버튼을 클릭하여 `verbose output in console`을 체크하면 젠킨스 build 시 상세한 로그를 볼 수 있습니다.

마지막으로 배포 스크립트를 Exec command에 작성하면 됩니다.

![스크린샷 2021-12-25 오후 6 33 10](https://user-images.githubusercontent.com/22395934/147382206-34692995-890e-4f58-b43b-8a60d1c78ae6.png)

> nohub &은 백그라운드 모드에서 실행시키는 명령어로 만약 생략하게 되면 젠킨스에서 docker 컨테이너를 실행시키고 스프링 부트 어플리케이션이 실행됩니다. 이때 백그라운드 모드가 아니기 때문에 젠킨스는 배포가 종료되지 않았다고 생각하여 작업이 완료되지 않게 됩니다.

설정을 위와 같이 했다면 마찬가지로 저장을 하면 됩니다.


## Jenkins 빌드 및 배포

빌드 및 배포 과정은 젠킨스 인스턴스가 ssh로 워커 인스턴스에 접속하여 미리 도커 허브 리포지터리에 저장된 도커 이미지를 pull받고 run까지 수행하도록 설정합니다.

따라서 worker 인스턴스에 도커를 설치해야 합니다.

도커 설치 후 도커가 실행되는지 확인하고 만약 안되면 아래와 같이 `666` 권한을 주고 다시 실행하면 됩니다.

```java
sudo chmod 666 /var/run/docker.sock
```

다시 cpu-worker-instance-1 deploy Item으로 돌아와서 `Build Now`를 하면 worker 인스턴스에서 docker 이미지 파일을 pull 받은 후 컨테이너를 실행시키는것을 확인할 수 있습니다.

## 실행 결과

이제 스프링 부트 어플리케이션이 정상적으로 동작하는지 웹브라우저에서 api를 호출해봤습니다.

![스크린샷 2021-12-25 오후 6 41 05](https://user-images.githubusercontent.com/22395934/147382341-8707a9b6-df00-4931-9a97-98c400678df9.png)

!!뚜둥... 잘나옵니다. 

여기까지 클라우드 환경에서 젠킨스 셋팅 및 배포서버로 빌드 및 배포하는 과정에 대해서 전반적으로 살펴봤습니다.

> 참조: https://velog.io/@jellyb3ar/CICD-Jenkins-%EC%A0%95%EB%A6%AC
