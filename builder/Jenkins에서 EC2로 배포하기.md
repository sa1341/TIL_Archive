# Jenkins에서 EC2로 배포하기

GitHub와 젠킨스를 SSH로 연동했다면, 이제 AWS(Amazone Web Service)에서 제공하는 가장 유명한 EC2(Elastic Compute Cloud)에 배포를 해봤습니다.

## 1. Publish Over SSH 플러그인 설치

먼저, Jenkins에서 제공해주는 플러그인 중에 SSH로 EC2에 jar 배포 파일을 전달을 해주는 Publish Over SSH 플러그인을 설치해야 합니다.

#### 플러그인 다운로드 

왼쪽 사이드 바에서 `Jenkins 관리`에서 `플러그인 관리`를 클릭합니다.

![스크린샷 2021-05-20 오후 6 32 45](https://user-images.githubusercontent.com/22395934/118955900-172f2580-b99a-11eb-8452-eca6a2513a71.png)


저는 이미 설치를 한번 했기 때문에 설치된 플러그인 목록에서 `Publish Over SSH`를 보여주지만, 처음 설치하시면 설치 가능에서 `Publish Over SSH` 검색하시면 됩니다.

![image](https://user-images.githubusercontent.com/22395934/118956335-77be6280-b99a-11eb-8296-775333593617.png)


## 2. 시스템 설정

Jenkins 관리에서 시스템 설정을 클릭합니다.

![image](https://user-images.githubusercontent.com/22395934/118956690-ca981a00-b99a-11eb-85c8-7b61d43e702d.png)

맨 하단에 `Publish over SSH`라는 폼 화면이 보이게 됩니다.

![image](https://user-images.githubusercontent.com/22395934/118957186-43977180-b99b-11eb-82f3-e06b55e0ad00.png)

Key에는 AWS EC2 인스턴스 생성 시 발급받은 RSA 키가 필요합니다. `EC2 생성할 때 받은 ssh 접속 키인 pem 파일내용을 붙여넣기를 하면 됩니다.`


![image](https://user-images.githubusercontent.com/22395934/118957368-6de92f00-b99b-11eb-9b20-22faf22fe8c5.png)


>
- Name: 본인이 사용할 임의의 SSH Server의 Name을 입력하면 됩니다. 
- Hostname: 실제로 접속할 원격 서버 ip, 접속 경로를 입력합니다. `ex) 퍼블릭 IPv4 주소: 3.37.87.X`
- Username: 접속할 원격 서버의 user 이름입니다. `ex) ec2-user입니다.`
- Remote Directory: 원격서버에서 접속하여 작업을 하게 되는 디렉토리 입니다.

Test Configuratoin을 눌러서 `Success`라고 문구가 나오면 성공적으로 SSH 설정이 되었습니다.


## 3. Jenkins Item 구성

이제 SSH를 통해 배포 jar 파일을 보낼 준비가 거의 다 되었습니다.
아래 Jenkins Item을 클릭 후 구성 버튼을 누릅니다.

![image](https://user-images.githubusercontent.com/22395934/118958701-9e7d9880-b99c-11eb-92db-57f948e5ff71.png)


### Build

Build 항목은 하단의 셀렉트 박스를 통해서 빌드 시 할 수 있는 작업을 정의해 놓았습니다.

![image](https://user-images.githubusercontent.com/22395934/118959253-1e0b6780-b99d-11eb-9b76-448d38679dd1.png)

젠킨스 통합환경 설정에서 Gradle Toll Configuration에서 Gradle을 다운받아 전역적으로 사용이 가능합니다. 
저는 GitHub에 Gradle wrapper를 push 하였기 때문에 `Invoke Gradle script` 방식으로 Build를 설정하였습니다. 

`Use Gradle Wrapper`를 선택하고, Tasks에 clean 후 Build를 수행하도록 설정하였습니다. 만약 test도 수행하고 싶으면 스프링 부트 프로젝트의 `build.gradle` 파일에 아래 설정을 추가하면 됩니다. 보통은 기본적으로 추가가 되어있습니다.

```gradle
test {
    useJunitPlatform()
}
```

### 빌드 후 조치

이제 스프링 부트 어플리케이션 배포 파일을 보내는 설정을 하고 어떻게 실행할지 정의하는 부분을 입력해야 되는데.. 이게 가장 햇갈렸지만 이미 다른 개발자분들의 좋은 블로그 글을 통해서 금방 이해할 수 있었습니다.

![image](https://user-images.githubusercontent.com/22395934/118962742-a93a2c80-b9a0-11eb-81d1-26a648c5d9c4.png)

>
- `Source files`: 내가 전송할 파일의 위치를 적습니다. Jenkins의 workspace 기준으로 적게 되는데 `git pull`로 당겨온 스프링 부트 앱의 루트 경로라고 생각하면 됩니다. jar 파일의 위치는 `build/libs` 하위에 있습니다. 이 경로를 jar 파일이라고 명시를 해줍니다.
- `Remote Directory`: 파일을 전송할 원격 서버의 디렉토리를 명시하는 부분입니다. 여기서 주의할 점은 위에서 SSH 서버 설정하는 부분에서 이미 경로`(/home/ec2-user)`를 적어줬기 때문에 그 밑의 경로부터 적어야 합니다. 저는 `/deploy`로 적었기 때문에 빌드 후에 ec2 서버의 /home/ec2-user/deploy 하위로 `jar` 파일이 배포가 됩니다.
- `Exec command`: 파일 전송 후 실행할 명령어를 입력합니다. 절대 경로로 ec2 루트경로(`/home/ec2-user/start_server.sh`)부터 적었습니다.

### 배포 후 실행될 스크립트 명령어

```shell
!#/bin/bash

echo "Start Spring Boot Application!"
CURRENT_PID=$(ps -ef | grep java | grep dokotlin | awk '{print $2}')
echo "$CURRENT_PID"

 if [ -z $CURRENT_PID ]; then
echo ">현재 구동중인 어플리케이션이 없으므로 종료하지 않습니다."

else
echo "> kill -9 $CURRENT_PID"
kill -9 $CURRENT_PID
sleep 10
fi
 echo ">어플리케이션 배포 진행!"
nohup java -jar /home/ec2-user/deploy/dokotlin-0.0.1-SNAPSHOT.jar >> /home/ec2-user/deploy/logs/dokotlin.log &
```

bash 쉘을 이용하여 작성한 스크립트로 단순히 grep 명령어로 실행 중인 부트 애플리케이션의 pid (프로세스 식별자)를 조회하여 길이가 0이면 앱을 실행시키고, 만약 떠있는 앱이 존재하면, kill -9 명령어로 강제로 내린 후에 다시 재기동을 하도록 작성하였습니다.


![image](https://user-images.githubusercontent.com/22395934/118968624-194bb100-b9a7-11eb-9fcf-cbbf4161a701.png)


> Name 하위에 고급 버튼을 클릭후 Verbose output in console을 체크하면 빌드할 때 상세 내역이 표시되므로 유용합니다. 


## 4. Jenkins 빌드 결과물

이제 Jenkins로 다시 Build를 수행하면 `/home/ec2-user/deploy/` 디렉토리 하위에 jar 파일이 떨어지고, 파일 전송 후 실행할 어플리케이션 기동 명령어가 저장되어 있는 `start_server.sh`이 실행됩니다. 


아래 젠킨스 콘솔 로그에서 확인 가능합니다.

![image](https://user-images.githubusercontent.com/22395934/118967992-5cf1eb00-b9a6-11eb-9687-e244129cac5d.png)

#### EC2 서버에 배포된 jar 파일

![image](https://user-images.githubusercontent.com/22395934/118969270-d3dbb380-b9a7-11eb-95e0-014b948a18c6.png)


#### EC2 서버 스프링 부트 어플리케이션 실행 화면

![image](https://user-images.githubusercontent.com/22395934/118969088-a131bb00-b9a7-11eb-968e-2f467ef2c216.png)


만약에 EC2 서버 로컬에서 정상적으로 웹 API 호출이 되지만, 내 PC 로컬 브라우저에서 화면이 안보인다면 아래와 같이 EC2 Security Group에서 인바운드 규칙을 살펴봐야 합니다. 저같은 경우에는 스프링 부트 어플리케이션을 8080 포트로 실행시켰기 때문에 인바운드 규칙에 TCP 8080포트를 허용하도록 설정했습니다.


#### 인바운드 규칙 

![image](https://user-images.githubusercontent.com/22395934/118969597-33d25a00-b9a8-11eb-9446-301e2f8a8a39.png)


> 참조 사이트: https://pjh3749.tistory.com/261, https://goddaehee.tistory.com/259?category=399178
