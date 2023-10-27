
## 메모리 영역

- Resident Area: 사용자가 Redis 서버에 접속해서 처리하는 모든 데이터가 가장 먼저 저장되는 영역이며 실제 작업이 수행되는 공간이고 WorkingSet 영역이라고 표현합니다.
  
- Data Structure: Redis 서버를 운영하다 보면 발생하는 다양한 정보와 서버 상태를 모니터링하기 위해 수집한 상태정보를 저장하고 관리하기 위한 메모리 공간이 필요합니다. 이를 저장하는 메모리 영역을 Data Structure 영역이라고 합니다.

## 파일영역
- AOF 파일: 중요한 데이터의 경우 사용자의 필요에 따라 지속적으로 저장해야 할 필요가 있는데 이를 위해 제공되는 디스크 영역이 AOF 파일입니다.

- DUMP 파일: AOF 파일과 같이 사용자 데이터를 디스크 상에 저장할 수 있지만 소량의 데이터를 일시적으로 저장할 때 사용되는 파일입니다.

## 프로세스 영역

- Server Process: redis-server.exe 또는 redis-sentinel.exe 실행코드에 의해 활성화 되는 프로세스를 서버 프로세스라고 하며 Redis 인스턴스를 관리해 주며 사용자가 요구한 작업을 수행하는 프로세스입니다. Redis Server 프로세스는 4개의 멀티 스레드로 구성되는데 main thread, sub thread 1 (BIO-Close-File), sub thread 2(BIO-AOF-Resync), sub thread 3(BIO-Lazy-Free) 입니다.

- Client Process: redis-cli.exe 또는 사용자 애플리케이션에 의해 실행되는 명령어를 실행하기 위해 제공되는 프로세스 입니다.

