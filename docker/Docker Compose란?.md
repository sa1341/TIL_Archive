# Docker Compose란?

Docker를 공부하다 보니 Docker Compose에 대해서 자연스럽게 접할 수 있었습니다. `Compose`는 `구성하다`라는 뜻인데 도커를 구성한다? 즉, 복수 개의 컨테이너를 실행시키는 도커 애플리케이션이 정의를 하기 위한 `툴`입니다. 

Compose를 사용하면 YAML 파일을 사용하여 애플리케이션 서비스를 구성할 수 있습니다. Compose는 모든 환경(production, staging, development, testing 및 CI 워크플로우)을 포함합니다.

## 1. Docker Compose 실행 방법

- docker-compose.yml 파일을 생성 후 앱을 구성할 수 있는 서비스를 정의합니다. 그래서 단 하나의 환경에서 실행할 수 있도록 합니다.

- docker-compose up 명령어를 실행합니다. 

```bash
limjun-young  ~/wp  docker-compose up  
```

## 2. Docker Compose 예제 코드

아래 Compose 파일은 mysql과 wordpress라는 서비스를 정의하고 연동하여  하나의 어플리케이션 서비스로 구성하였습니다.

```docker
version: '2'
services:
  db:
    image: mysql:5.7
    volumes:
      - ./mysql:/var/lib/mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: wordpress
      MYSQL_DATABASE: wordpress
      MYSQL_USER: wordpress
      MYSQL_PASSWORD: wordpress
  wordpress:
    image: wordpress:latest
    volumes:
      - ./wp:/var/www/html
    ports:
      - "8000:80"
    restart: always
    environment:
      WORDPRESS_DB_HOST: db:3306
      WORDPRESS_DB_USER: wordpress
      WORDPRESS_DB_PASSWORD: wordpress
```

### Docker Compose 주요 속성 값들

- restart: 컨테이너 종료 시 Docker 어플리케이션이 항상 재실행 하도록 수행하는 명령어 옵션 입니다.

- volumes: Docker Container 내부 데이터를 로컬 패스로 연결하기 위한 옵션 입니다.

- environment: Docker Container 환경변수를 설정하는 옵션 입니다.

