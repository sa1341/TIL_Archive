# systemctl 서비스 관리

systemd는 CentOS 7버전부터 init 시스템 대신 사용되며 Ubuntu에서도 systemd를 사용합니다. 

systemd의 용도는 프로세스를 관리하고 `유닛(Unit)`으로 서비스를 제어하는 시스템자원 통합관리 도구 입니다.

> systemd(sytstem daemon)은 전통적으로 Unix 시스템이 부팅한후에 가장 먼저 생성된 후에 다른 프로세스를 실행하는 init 역할을 대체하는 데몬입니다. 현재는 대부분의 리눅스 시스템에 공식적으로 채택되었습니다.

## systemctl 명령어

1. 서비스 시작: systemctl start 서비스명
    - sudo systemctl start http
    - sudo systemctl start docker
2. 서비스 정지: systemctl stop 서비스명
    - systemctl stop httpd
3. 서비스 구동 상태확인: systemctl status 서비스명
    - systemctl status kafka
4. 서비스 활성화 확인: systemctl is-enabled 서비스명
5. 서비스 활성화: systemctl enable 서비스명
6. 서비스 비활성화: systemctl disable 서비스명
7. 모든 서비스 확인: systemctl list-units --type service -all
8. 서비스(Unit)목록 확인: systemctl list-units


> Unix 기반 시스템에서는 1024번 포트 이하에 있는 포트를 well-known port라고 하는데 이 포트들을 열 때에는 관리자(sudo) 권한이 필요합니다.
