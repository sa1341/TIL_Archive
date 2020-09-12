## 리눅스 환경에서 Java 프로그램 컴파일 및 실행 방법


### 컴파일

- -cp옵션은 클래스패스를 지정하는 옵션으로, 아래 명령어는 현재 디렉토리를 클래스패스로 지정하고 lib/custom-1.0.1-RELEASE-with-libs.jar 파일을 참조하여 test.java 파일을 컴파일하는 명령어입니다.

javac -cp ".:./lib/custom-1.0.1-RELEASE-with-libs.jar" test.java

### 실행

실행 명령어도 비슷하지만 마지막 argument는 java 파일명만 입력하여 실행시켜줍니다.

java -cp ".:./lib/custom-1.0.1-RELEASE-with-libs.jar" test


>> 클래스패스는 말 그대로 클래스 파일들이 있는 곳으로, jvm 기동 시 클래스로더가 클래스패스에 있는 클래스 파일을 jvm이 관리하는 runtimedataArea 영역에 적재하기 때문에 반드시 지정을 해주어야 
JVM 위에서 Java 파일이 컴파일 및 실행이 가능합니다.
