## HttpEntity란?

Spring에서 HttpEntity란 클래스를 제공하는데 이 클래스의 역할은 Http 프로토콜을 이용하는 통신의 header와 body 관련 정보를 저장할 수 있게 합니다. 그리고 이를 상속받은 클래스로 RequestEntity와 ResponseEntity가 있고, 통신 메시지 관련 header와 body의 값들을 하나의 객체로 저장하는 것이 HttpEntity 클래스 객체입니다. Request 부분일 경우 HttpEntity를 상속받은 RequestEntity가 Response 부분일 경우 HttpEntity를 상속받은 ReponseEntity가 하게 됩니다.

`@ResponseBody나 ReponseEntity를 return 하는거나 결과적으로 같은 기능이지만, 그 구현 방법이 다릅니다.` header 값을 변경시켜야 할 경우엔 @ReponseBody의 경우 파라미터로 Reponse 객체를 받아서 이 객체에 header를 변경시켜야 하고, ReponseEntity에서는 이 클래스 객체를 생성한 뒤에 객체에서 header 값은 변경시키면 됩니다.
