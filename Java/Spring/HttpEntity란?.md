## HttpEntity란?

Spring에서 HttpEntity란 클래스를 제공하는데 이 클래스의 역할은 Http 프로토콜을 이용하는 통신의 header와 body 관련 정보를 저장할 수 있게 합니다. 그리고 이를 상속받은 클래스로 RequestEntity와 ResponseEntity가 있고, 통신 메시지 관련 header와 body의 값들을 하나의 객체로 저장하는 것이 HttpEntity 클래스 객체입니다. Request 부분일 경우 HttpEntity를 상속받은 RequestEntity가 Response 부분일 경우 HttpEntity를 상속받은 ReponseEntity가 하게 됩니다.

`@ResponseBody나 ReponseEntity를 return 하는거나 결과적으로 같은 기능이지만, 그 구현 방법이 다릅니다.` header 값을 변경시켜야 할 경우엔 @ReponseBody의 경우 파라미터로 Reponse 객체를 받아서 이 객체에 header를 변경시켜야 하고, ReponseEntity에서는 이 클래스 객체를 생성한 뒤에 객체에서 header 값은 변경시키면 됩니다.

## REST API 설계

1. 리턴 값이 JSON, Text, XML인 것
2. 오류시 리턴 값
3. REST API에 대한 설명 문서 작성
 - URI, HTTP 메서드, 필요 파라미터, HTTP 상태코드, 리턴 값
 - 클라이언트 개발자가 문서를 보고 만들 수 있을 정도로 만들고 배포해야 합니다. 수정시마다 이야기하는 것 보다는 프로그램 혹은 웹으로 배포하는게 좋습니다.

 4. 클라이언트 프로그램에서는 Ajax 방식으로 호출

 REST API를 설계할 때 URI를 어떻게 구성할 것인지에 대한 항상 고민이 있었습니다. 그래서 URI에 대한 개념을 살펴보았습니다.

 원래 웹 자원은 HTML 문서 또는 파일이었습니다. 오늘날 웹 자원의 정의는 웹에서 식별되고 카탈로그화 할 수 있는 엔티티를 포함하도록 확장되었습니다. 예를들어 데이터베이스의 레코드 또는 레코드 그룹을 리소스로 표시 할 수 있습니다. 자원은 다른 자원과 관계를 가질 수 있습니다.

 URI는 자원을 식별하는 데 사용됩니다. 예를 들어, 학생 개체의 집합은 URI /students에 의해 식별 될 수 있습니다. 특정 학생은 URI: `/students/123`에 의해 엑세스 될 수 있습니다.
 
 URI는 또한 리소스 간의 1:N 관계를 설명하는 직관적인 방법을 제공합니다. 한 학생의 수업 로드는 URI:`/students/123/courses`로 표현할 수 있습니다.


 | <center>Action</center> |  <center>URI</center> | <center>액션 타입</center> | <center>HTTP Methods<center> | <center></center>
|:--------|:--------:|:--------|:--------|:--------|
|  Read all students | <center>/students</center> | Read | GET | 리스트 
| Read a single student | <center>/students/{id}</center> | Read | GET | 상세정보 |
| Add a Student | <center>/students</center> | Create | POST | 생성 | 
| Update a Student | <center>/students</center> | Update | PUT | 수정 |
| Delete a Student | <center>/students/{id}</center> | Delete | DELETE | 삭제 |

## Media Types

Media Types는 HTTP 요청 중 특정한 미디어 타입의 요청만 사용할 때 씁니다.

요즘 트렌디한 데이터는 주로 JSON을 대부분 사용하는 추세입니다.
클라이언트는 요청 헤더의 수락 행에서 수락 한 미디어 유형을 지정할 수 있습니다.

ex) Content-Type: application/json

Content-Type는 서버가 반환하는 미디어 유형을 지정하기 위해 응답 헤더에도 사용됩니다.


#### 참조: https://dbheart.tistory.com/29
