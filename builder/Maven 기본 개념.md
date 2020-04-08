# Maven의 개념

Maven을 Builder로 사용할 때 꼭 알아둬야 하는 개념들에 대해서 정리하였습니다.

plugin, phase, goal, LifeCycle 총 4가지가 기본 개념인데 이 부분에 대해서 포승팅하겠습니다.

## Plugin

Maven 그 자체로는 기본적인 기능을 제공하는 것만으로는 딱히 의미가 있진 않습니다. 왜냐하면 Maven에서 제공해주는 주요 기능들은 플러그인을 기반으로 동작하기 때문입니다.

Maven에서 활용할 수 있는 대부분의 플러그인은 다음 두개의 웹 사이트에서 제공하고 있습니다.

- 아파치 메이븐 사이트: http://maven.apache.org/plugins/
- 코드하우스 모조 프로젝트: http://mojo.codehaus.org/plugins.html

Maven은 여러개의 플러그인으로 구성되어 있고 플러그인은 하나 이상의 Goal을 가지고 있는 Goal의 집합 단위입니다. 여기서 Goal이란 무엇일까요?

## Goal

`Goal은 Maven 단위 작업 즉 명령어라고 생각하면 됩니다.`

ex) mvn [-option] [<goal(s)>] [<phase(s)>]의 형태

`mvn clean compiler:compile`의 형태로 빌드를 실행했다면 intellij 기준으로 class 파일들이 떨어지는 target/ 디렉토리 밑의 모든 class 파일들을 삭제하고, compiler 플러그인의 compile 골을 실행합니다.

phase에 goal이 연결되어 있기 때문에 phase를 통해 Maven 빌드를 실행한다면 해당 phase에 연결되어 있는 goal이 실행됩니다.

소스코드를 compile하는 compiler Plugin의 compile goal 또는 단위 테스트를 실행할수 있는 Surefire Plugin의 test goal를 들수 있습니다. 아울러 goal은 기능을 변경하는데 사용될 수 있는 설정 속성을 설정됩니다.

예를 들면 Compiler plugin의 compile goal은 대상 JDK버전을 지정할수 있습니다.

## Phase

phase의 사전적 의미는 단계입니다. Maven에서도 phase는 단계를 의미합니다. phase는 빌드 lifecycle에서 빌드 단계와 각 단계의 순서만을 정의하고 있는 개념으로 빌드 과정에서 phase가 빌드 작업을 하지 않습니다. 위에서 말했듯이 실질적인 빌드 작업은 각 phase에 연결되어 있는 플러그인의 goal이 합니다.

보통 phase를 통해 goal을 실행하면 처음부터 해당 단계까지 모두 순차적으로 빌드가 실행됩니다. 예를 들어 mvn test라는 phase를 통해 빌드를 실행하면 log를 통해 process-resources(resources:resources), compile(compiler:compile), process-test-resources(resources:testResources), test-compile(compiler:testCompile), test(surfire:test) 순서로 phase가 실행되는 것을 볼 수 있습니다.

## LifeCycle

Maven이 기본으로 제공하는 Maven 빌드 단계가 있습니다. 일반적으로 프로젝트를 빌드할 때의 과정을 보면 빌드 결과물 삭제, 컴파일에 필요한 자원을 복사, 소스 코드 컴파일, 테스트, 압축(패키지), 배포의 과정을 거치는데 이런 빌드 단계를 Maven은 미리 정의하고 있습니다.

이와 같이 미리 정의하고 있는 빌드 순서를 LifeCycle이라고 합니다. Maven에서는 총 3개의 LifeCycle을 제공합니다.


#### 1. 소스 코드를 컴파일, 테스트, 압축, 배포를 담당하는 기본 LifeCycle


- 기본 라이프 사이클 각 phase
    - compile: 소스 코드를 컴파일 합니다.
    - test: Junit, TestNG와 같은 단위 테스트 프레임워크로 단위 테스트를 수행, 기본 설정은 단위 테스트가 실패하면 빌드 실패로 간주합니다.

    - package: 단위 테스트가 성공하면 pom.xml \<packaging\ 엘리먼트 값(jar, war, ear)등에 따라 압축합니다.

    - install: 로컬 저장소에 압축한 파일을 배포합니다. 로컬 저장소는 개발자의 PC의 저장소를 의미합니다.
    - deploy: 원격 저장소에 압축한 파일을 배포합니다. 원격 저장소는 외부에 위치한 메이븐 저장소를 의미합니다.



#### 2. 빌드한 결과물을 제거하기 위한 clean 라이프사이클

clean phase를 실행하면 Maven 빌드를 통하여 생성된 모든 산출물을 삭제합니다. Maven은 기본으로 모든 산출물을 target 디렉토리에 생성하기 때문에 clean phase를 실행하면 target 디렉토리를 삭제합니다.


#### 3. 프로젝트 문서 사이트를 생성하는 site 라이프 사이클

site 라이프 사이클은 site와 site-deploy 페이즈를 이용하여 실행할 수 있습니다. Maven 설정 파일의 정보를 활용하여 프로젝트에 대한 문서 사이트를 생성할 수 있도록 지원합니다. site phase는 문서 사이트를 생성하고, site-deploy phase는 생성한 문서 사이트를 설정되어 있는 서버에 배포하는 역할을 수행합니다.




