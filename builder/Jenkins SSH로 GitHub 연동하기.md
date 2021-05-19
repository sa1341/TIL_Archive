# Jenkins SSH를 이용한 GitHub 연동방법

젠킨스와 GitHub를 연동하여 프로젝트 소스를 가져와서 빌드하는 방법이 여러가지가 있지만, `사용자명과 비밀번호` 인증방식은 보안상 추천하지 않기 때문에 SSH기반으로 젠킨스와 GitHub를 연동하는 방법에 대해서 직접 구축해봤습니다.

> Jenkins 설치가 안되어있다면 [Jenkins 설치 방법](http://localhost:8080)를 참조하시면 됩니다.

## 1. 키파일 생성

먼저, Jenkins가 실행중인지 확인합니다.

```bash
ps aux | grep jenkins
```

![image](https://user-images.githubusercontent.com/22395934/118796206-585e0180-b8d6-11eb-8429-7748735c64cf.png)


구글링을 통해서 키 파일 생성방법을 찾을 때 `jenkins` 권한을 가진 `jenkins` 계정으로 접속하라는 글을 보았지만, 저 같은 경우에는 제 별도의 계정으로 jenkins를 실행했기 때문에 `jenkins`라는 계정이 존재하지 않았습니다. 실제로 실행중인 프로세스를 보면 `limjun-young` 계정으로 jenkins가 실행되는 것을 볼 수 있습니다.

그럼 본격적으로 키 생성을 위해 bash 쉘로 접속합니다.

```bash
sudo -u limjun-young /bin/bash
```

![image](https://user-images.githubusercontent.com/22395934/118797016-1aada880-b8d7-11eb-9944-2ffdd34596cf.png)


`.ssh` 디렉토리를 하나 생성합니다.

```bash
mkdir /var/lib/jenkins/.ssh
cd /var/lib/jenkins/.ssh
```

혹시 디렉토리 생성 권한이 없다고 나오면 sudo를 앞에 붙여서 다시 실행합니다.

이제 여기서 ssh 키를 생성합니다.

```bash
ssh-keygen -t rsa -f /var/lib/jenkins/.ssh/github_ansible-in-action
```

![image](https://user-images.githubusercontent.com/22395934/118797759-dcfd4f80-b8d7-11eb-9ea6-878d6f8734d0.png)

> 비밀번호는 모두 입력없이 바로 Enter로 넘어갑니다.

위와 같이 터미널 콘솔에 출력되었다면 키들이 RSA 공개키/비밀키가 잘 생성되었는지 확인합니다.

![image](https://user-images.githubusercontent.com/22395934/118797995-1df56400-b8d8-11eb-9fe7-b1ddb5071f6f.png)


## 2. GitHub

이제 젠킨스로 연동하고 싶은 GitHub의 Setting 페이지로 이동합니다.

`Deploy keys -> Add deploy key` 클릭 하면 아래와 같이 공개키를 입력하는 폼 화면이 나옵니다. 

![스크린샷 2021-05-19 오후 7 28 07](https://user-images.githubusercontent.com/22395934/118798239-644ac300-b8d8-11eb-87a0-e71b76e40597.png)

젠킨스 서버에서 생성한 공개키 코드를 복사해서 붙이면 됩니다.

### 공개키 

```bash
cat /var/lib/jenkins/.ssh/github_ansible-in-action.pub
```

```bash
bash-3.2$ cat github_ansible-in-action.pub
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQCnLB6lRaWdLRCDjhoIj1n9BYWd7oTCKbKQXuaxH82tkhFmmgL4VNPlUZV+TKLaQpyQN3ZxG86fGXNQFmuDRdmuJvLtQyeVY2abKkv9KFvmpeUV1++G7JBduxV9WU7qtlGt/LoR+s9Wvlv9Tc6hdDMiTg/jKSK5Ppk5d3HUotTQNfncPYzU6lqyZhR9x+uUhEUKObh3oGi0Xz+x2VHEYrDYpnWQPOsmYJbH8uqA12coQyL+9YPt4wfbuqsGl9qUy3eS7YkRCQQCQXYUiTibPvz6DLieWKF+EO/lVZO/3rCAvX9EI4zhre2ATDLVOjqMmWnL+Db3OoAjGCOxz5FuIm6CojKNdj2BJdadipd8Qop+uPbBnkePnYSx4b4Arlh5OFSONdeyxTV5CoF+NTQCzGRcc+Yj3sAbOKZvU3qX5NwBZRSUskvla1w3Bg+ybU59f5sHc5PuHAF46BdS28J2tJ6Sep0c8nMeZTNJjAqAhlJQ9NjmHViQ2kRzxt1nnre7mD8= root@imjun-yeong-ui-MacBookPro.local
```

출력된 코드를 key 항복에 붙여넣습니다.

![image](https://user-images.githubusercontent.com/22395934/118798689-e1763800-b8d8-11eb-947f-7b6bddf7e4ea.png)


## 3. Jenkins

젠킨스 웹 화면에서 `Jenkins 관리 -> Security의 Manage Credentials -> Stores scoped to Jenkins의 Domains의 global 클릭 ->  왼쪽 사이드바의 Add Credentials 클릭`합니다.

![image](https://user-images.githubusercontent.com/22395934/118799255-78db8b00-b8d9-11eb-9bb5-cd8ee94c7206.png)

비밀키를 등록하기 위해 젠킨스 서버에 있는 비밀키를 복사합니다.

```bash
cat /var/lib/jenkins/.ssh/github_ansible-in-action
```

- kind
    - 인증 방식을 선택합니다.
    - 여기선 비밀키 방식을 선택해야 GitHub의 공개키/비밀키로 인증이 가능합니다.

- Username
    - 각 젠킨스 Job에서 보여줄 인증키 이름입니다.
    - 키 이름 그대로 사용해도 됩니다.
- Private Key
    - 복사한 비밀키를 그대로 붙여넣습니다.

이제 고대했던 젠킨스와 GitHub 연동이 끝났습니다. 실제로 ssh 연동이 잘 되는지 확인이 필요합니다.

Jenkins 웹 화면에서 `새로운 Item`을 클릭합니다.

![image](https://user-images.githubusercontent.com/22395934/118799696-fdc6a480-b8d9-11eb-9c81-c88418979446.png)

FreeStyle 프로젝트를 생성합니다.

![image](https://user-images.githubusercontent.com/22395934/118799859-34042400-b8da-11eb-8851-eaab07b3095d.png)


GitHub의 코드를 가져올 수 있는지 소스코드 관리 항목에 입력합니다.

![image](https://user-images.githubusercontent.com/22395934/118800059-7299de80-b8da-11eb-9555-90251224c24b.png)

>
- Repository URL에는 공개키를 등록한 URL을 등록합니다.
- Credentials에는 이전에 등록한 젠킨스 인증 Username을 선택합니다.
- 브랜치는 기본 브랜치인 master를 등록합니다.

이제 저장 후 GitHub에 있는 소스코드를 `Build` 해보겠습니다.

아래와 같이 GitHub에서 코드를 가져오는 로그가 출력된다면 연동이 성공한 것입니다.

![image](https://user-images.githubusercontent.com/22395934/118800566-04a1e700-b8db-11eb-9134-5dc496c4408d.png)

## 4, Build 결과물

Build가 성공했으니 실제 결과물을 확인해야 합니다. 저는 FreeStyle 프로젝트 생성 시 입력한 타이틀을 github로 하였습니다. 작업공간을 클릭합니다.

![image](https://user-images.githubusercontent.com/22395934/118800654-20a58880-b8db-11eb-843b-916158ed25b4.png)


짜잔!! 실제 GitHub에서 가져온 소스코드를 빌드한 결과물을 zip 파일로 압축이 가능해졌습니다.

![image](https://user-images.githubusercontent.com/22395934/118800735-361ab280-b8db-11eb-9908-6cd8ad45e122.png)

이제 개발할 때마다 깃허브에 PUSH를 하면 Jenkins에서 변경된 사항에 대해서 Build가 가능해집니다.


#### 참조 사이트: https://jojoldu.tistory.com/442
