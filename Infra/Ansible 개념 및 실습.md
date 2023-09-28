# Ansible이란?

## Ansible 개념 및 등장배경

IT 회사에서 신규 어플리케이션을 런칭하게 되면 개발하는 것 만큼 힘든 업무가 인프라 환경을 셋팅하는 것이라고 생각됩니다.

요즘 같은 분산서버 환경에서는 여러 서버에 어플리케이션 인스턴스를 여러개 띄우기 때문에 같은 환경을 보장하도록 하는것도 중요합니다.

어떻게 보면 이러한 작업들을 개발자가 직접하게 된다면 반복적이고, 휴먼에러를 발생시킬 리스크가 있습니다.

이러한 복잡성을 해결하기 위해 `Ansible`이라는 오픈소스 기반의 자동화 툴이 등장했습니다.

공홈에서 정의를 살펴보면 `Ansible`은 설정관리, 어플리케이션 배포, 오케스트레이션 등 많은 다른 수작업들을 자동화 해준다고 합니다.

`Ansible`은 IaaS(Infrastructure as a Service)로 인프라환경 구성을 코드로 관리하기 때문에 물리적으로 다른 서버들의 환경을 보장해주기도 합니다.

### 멱등성

`Asnible`의 모듈은 정말 많은 기능들을 제공하지만 동일한 연산을 여러 번 실행해도 같은 결과를 보장합니다.

## Ansible 실습

실제로 `Ansible`이 어떻게 동작하는지 궁금해서 AWS EC2 환경에서 실습하였습니다.

먼저, Docker를 사용하여 `Ansible`을 실행할 컨테이너와 Ansible이 관리할 노드를 위한 별도의 `docker-server`라는 컨테이너를 총 2개 띄웠습니다.

```docker
# Ansible Server

1. docker run --privileged --itd -p 20022:22 -p 8081:8080 --name ansible-server ubuntu

2. docker exec -it ansible_server bash

3. yum install -y ansible

4. ansible --version

# docker-server

1. docker run --privileged --itd -p 10022:22 -p 8082:8080 --name docker-server ubuntu
```

Ansible-server에서 `Ansible`을 정상적으로 설치가 됬다면 버전정보를 확인해보면 아래와 같이 나옵니다.

```
ansible [core 2.13.4]
  config file = None
  configured module search path = ['/root/.ansible/plugins/modules', '/usr/share/ansible/plugins/modules']
  ansible python module location = /usr/local/lib/python3.8/site-packages/ansible
  ansible collection location = /root/.ansible/collections:/usr/share/ansible/collections
  executable location = /usr/local/bin/ansible
  python version = 3.8.8 (default, Aug 25 2021, 16:13:02) [GCC 8.5.0 20210514 (Red Hat 8.5.0-3)]
  jinja version = 3.1.2
  libyaml = True
```

Ansible은 복잡성을 제거하기 위해 agent가 없으므로 관리 대상인 노드에 별도의 프로그램 설치가 필요가 없습니다.

대신 관리대상 노드들에게 명령을 전파하기 위해서는 `SSH` 접속이 가능해야합니다.

Docker 명령어를 사용하여 현재 bridge를 확인하면 아래와 같습니다.

```
1. docker network inspect bridge
```

![image](https://user-images.githubusercontent.com/22395934/209459123-e61b4be6-014e-4c89-a504-00d31dd474b2.png)

Ansible-server 컨테이너로 접속하여 해당 디텍토리를 생성하고 `Ansible`이 관리할 대상들을 설정하였습니다.

```
1. mkdir /etc/ansible

2. vi hosts

[devops]
172.17.0.3
172.17.0.4
```
 
`172.17.0.3` IP는 Ansible-server이고, 0.4는 docker-server 입니다. 일단은 간단한 사용방법을 위해서 Ansible이 설치된 로컬서버도 관리노드로 지정했습니다.

이제 Ansible-server에서 관리노드로 로그인 없이 접근을 하기 위해서 SSH 설정을 해보겠습니다.

```
# Ansible-server

1. ssh-keygen // RSA 알고리즘을 사용한 공개비/비밀키 생성

2. ssh-key-id root@172.17.0.4 // docker-server로 공개키 전달

3. ssh-key-id root@172.17.0.3 // Ansible-server(로컬) 공개키 전달
```

이제 SSH 셋팅이 완료되었으니 `Ansible` 모듈이 제공하는 일부 명령어를 사용해봤습니다.

### ping 명령어

Ansible 명령어가 실제 관리대상 노드랑 정상적으로 접속이 되는지 확인하기 위해서 `ping` 명령어를 사용해봤습니다.

```
ansible all -m ping
```

all은 아까 위에서 설정한 `/etc/ansible/hosts` 파일에 정의한 전체 노드 그룹들을 의미합니다. 지금은 devops라는 별도의 그룹만 넣었기 때문에 만약 특정 그룹들에게만 명령어를 전달하고 싶다면 all 대신 devops를 주면 됩니다.

![image](https://user-images.githubusercontent.com/22395934/209459285-d4f51fc7-2afa-4941-a678-37d303302a5d.png)


정상적으로 접근이 된다면 응답으로 `SUCCESS`가 오는것을 확인할 수 있었습니다. 만약 관리노드 중 한 곳을 내린다면 에러가 떨어지는 것도 확인 할 수 있었습니다.

### copy 명령어

이제 가장 많이 사용할것 같은 파일 복사 명령어는 아래와 같습니다.

```
ansible devops -m copy -a "src=./test.txt dest=/tmp"
```

위 명령어를 사용하면 `ansible-server`의 현재 디렉토리 기준으로 존재하는 test.txt 파일을 관리대상 노드들의 /tmp 하위에 copy하는 명령어 입니다.

`-m`은 모듈을 의미하고, `-a`는 아규먼트를 의미한다고 보면 됩니다.


### yum 패키지 설치 명령어

위에 copy보다 더 많이 사용하는 yum 패키지관리자를 이용해 관리대상 노드들 환경에 일괄적으로 패키지 설치를 하는 명령어는 아래와 같습니다.

```
ansible devops -m yum -a "name=httpd state=present"
```

저는 ansible로 `httpd` 패키지 설치를 실행하였습니다.

```
yum list installed | grep httpd
```

`Ansible-server, docker-server`에 접속 후 아래 명령어로 실제로 `httpd` 패키지가 설치된 것도 확인할 수 있었습니다.

![image](https://user-images.githubusercontent.com/22395934/209459492-c0608e69-b16d-48dd-aae8-b00c385fac8d.png)


`Ansible`이 제공하는 명령어는 이것말고도 더 많았고, 아래 URL을 통해서 앞으로 자동화하고 싶은 프로세스가 있다면 해당 명령어를 찾아서 사용해보면 될 것 같습니다.

> https://docs.ansible.com/ansible/2.9/modules/list_of_all_modules.html

## Ansible Playbook 사용하기

위에 Ansible이 제공해준 명령어로 관리대상 노드들에게 일괄적으로 명령어를 실행하는 예제코드를 실습해봤습니다. 하지만 이러한 명령어들을 매번 자주 사용하게 된다면 그때마다 명령어를 보관하고 있다가 매번 직접 실행해야 합니다. 이렇게 되면 실수로 잘못된 명령어를 전달할 가능성도 있기 때문에 별도로 `yml` 설정파일에 해당 task들을 가지고 있다가 스크립트 방식으로 실행이 가능합니다.

`ansible-playbook`이 이러한 기능들을 제공합니다.

```
# Ansible-server

1. vi first-playbook.yml // yml 파일 생성

2. 관리노드 그룹 추가를 위한 playbook 설정파일 생성
---
- name: Add an ansible hosts
  hosts: localhost
  tasks:
   - name: Add an ansible hosts
   - blockinfile:
       path: /etc/ansible/hosts
       block: |
         [mygroup]
         172.17.0.5
```

일단 Ansible-server에 관리대상 노드그룹을 추가하기 위한 명령어를 스크립트로 생성했습니다.


playbook 명령어 실행

```
ansible-playbook first-playbook.yml
```

#### 실행결과

```
cat /etc/ansible/hosts

[devops]
172.17.0.3
172.17.0.4
# BEGIN ANSIBLE MANAGED BLOCK
[mygroup]
172.17.0.5
# END ANSIBLE MANAGED BLOCK
```

정상적으로 관리대상 그룹이 추가된 것을 확인할 수 있었습니다.


### 파일 copy playbook

```
- name: Ansible Copy Example Local to Remtoe
  hosts: devops
  tasks:
    - name: copying file with playbook
      copy:
        src: ~/sample.txt
        dest: /tmp
        owner: root
        mode: 0644
```

### Tomcat 설치 playbook

```
---
- name: Download Tomcat9 from tomcat.apache.org
  hosts: devops
  tasks:
   - name: Create a Directory /opt/tomcat9
     file:
       path: /opt/tomcat9
       state: directory
       mode: 0755
   - name: Download Tomcat using get_url
     get_url:
       url: https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.65/bin/apache-tomcat-9.0.65.tar.gz
       dest: /opt/tomcat9
       mode: 0755
       checksum: sha512:https://downloads.apache.org/tomcat/tomcat-9/v9.0.65/bin/apache-tomcat-9.0.65.tar.gz.sha512
```

> 참고: 인프런 CI/CD 강의
