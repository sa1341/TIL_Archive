## Jenkins 설치 및 구동하기

대표적인 CI 도구인 Travis-CI와 젠킨스가 있지만, 그 중에서 오래된 역사와 다양한 플러그인을 제공해주는 Jenkins를 설치하는 방법에 대해서 살펴보았고, 직접 저의 로컬환경에서 구축해봤습니다. 

#### 설치환경
>
- Mac OS
- 패키지 관리자 homebrew

## 1. Jenkins 설치

먼저, GitHub와 연동해야 할 대표적인 CI 툴인 `젠킨스`를 설치해야 합니다.
제 PC 환경은 Mac OS이기 때문에 패키지 관리자인 `homebrew`를 통해서 설치하였습니다.

### Jenkins 설치 명령어

```vim
brew install jenkins
```

```vim
xcrun: error: invalid active developer path (/Library/Developer/CommandLineTools), missing xcrun at: /Library/Developer/CommandLineTools/usr/bin/xcrun
```

혹시 설치가 정상적으로 안되고  위와 같은 에러로그가 찍히면 `xcode-select --install`를 설치하시면 됩니다.


### Jenkins 포털 설정 화면

![unlock jenkins](https://user-images.githubusercontent.com/22395934/118790638-ee8f2900-b8d0-11eb-9336-5d557c532048.png)

`Administrator password`를 입력하면 되는데 패스워드는 아래 경로에 있는 파일을 열어서 복사 후 입력하시면 됩니다.

```vim
$ vi /var/lib/jenkins/secrets/initialAdminPassword
```

### 플러그인 설치 화면

패스워드를 입력 후 아래와 같이 플러그인 설치화면이 나오는데 특별히 설치할 플러그인이 필요없다면 `install suggested plugins`를 선택합니다.

![jenkins install](https://user-images.githubusercontent.com/22395934/118791200-72491580-b8d1-11eb-9567-d898b94e78d0.png)


### Admin 계정 생성

이제 Admin 사용자를 생성하고 다음 화면을 누르면 Jenkins 화면이 보이는 것을 확인할 수 있습니다. 

![스크린샷 2021-05-19 오후 4 18 20](https://user-images.githubusercontent.com/22395934/118791512-bc31fb80-b8d1-11eb-9eb4-70d4fee8b123.png)


## 2.외부 IP로 접근 가능하도록 설정

해당 Mac 호스트를 CI 전용 서버로 쓰는 경우 localhost:8080 이외의 도메인이나 공인 IP, 외부 IP로 접속하기를 원할 수 있습니다. 이 경우에는 아래와 같은 경로에서 `--httpListenAddress` 값을 127.0.0.1에서 0.0.0.0으로 변경해야합니다. 만약 Jenkins 포트번호도 변경하고 싶다면 `--httpPort={port번호}`를 수정하면 됩니다. 그럼 이제 공인이나 사설 IP로도 Jenkins로 접근이 가능해집니다.


```bash
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
  <dict>
    <key>Label</key>
    <string>homebrew.mxcl.jenkins</string>
    <key>ProgramArguments</key>
    <array>
      <string>/usr/libexec/java_home</string>
      <string>-v</string>
      <string>1.8.0_201</string>
      <string>--exec</string>
      <string>java</string>
      <string>-Dmail.smtp.starttls.enable=true</string>
      <string>-jar</string>
      <string>/usr/local/opt/jenkins/libexec/jenkins.war</string>
      <string>--httpListenAddress=0.0.0.0</string>
      <string>--httpPort=8888</string>
    </array>
    <key>RunAtLoad</key>
    <true/>
  </dict>
</plist>
```

> -Dmail.smtp ~ 위에 설정한 JDK 버전 값은 만약 /usr/libexec/java_home -V에서 여러개의 JDK를 설치된 경우 특정 JDK 버전을 지정하기 위해서 명시한 설정 값입니다. 저 같은 경우에는 JDK가 2개가 설치되어 있어서 1.8.0_201버전으로 Jenkins의 Job을 구동하도록 설정하였습니다.


## 3. Jenkins 웹 UI 화면

만약 저처럼 위 설정파일에서 젠킨스 실행포트를 `8888`로 변경하지 않았다면 기본인`http://localhost:8080`로 접속하면 아래와 같이 젠킨스 웹 화면이보이게 됩니다.

![스크린샷 2021-05-19 오후 6 58 04](https://user-images.githubusercontent.com/22395934/118794093-32cff880-b8d4-11eb-8e8e-585a756410a4.png)


#### 참조 사이트: https://wan-blog.tistory.com/74?category=776763
