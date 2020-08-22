## 서블릿에서 post body를 읽는 방법

주로 http 통신으로 서버에 request를 읽어들이는 방법은 아래 크게 3가지 방법이 쓰입니다.

- HttpServletRequest.getInputStream()

- HttpServletRequest.getReader()

- HttpServletRequest.getParameter() - `family`

위에서 Reader와 InputStream은 자바에서 제공하는 인터페이스를 구현했다고 보면 됩니다.

서블릿 스펙을 살펴보면 getReader와 getInputStream을 동시에 사용하는 것을 금지하고 있는데, 이는 둘을 동시에 사용했을 경우에 디자인적으로 올바르지 못하고, byte 단위로 읽어들이는 inputStream에서 일부만 바이트 데이터를 읽어들이는 경우, Reader에서 인코딩이 정상적으로 동작하는 것을 보장할 수 없기 때문으로 보입니다.

이제 가장 핵심은 getParameter는 post랑 어떤 연관이 있는거냐 입니다.
이에 대해서는 아래와 같이 나와 있습니다.

- getParameter
- getParameterNames
- getParameterValues
- getParameterMap

위에서 언급된 4개의 메서드를 getParameter Family라고 부르는데, 무엇을 호출하든 무조건 map을 생성해 map에 값을 넣게 되어 있습니다.

이 때 Map<String, String[]>의 구조를 가지게 되는데, key-value 쌍에서 같은 key 값에 여러 value가 들어가는 경우에 배열 뒤에 계속 추가하는 형태로 되어 있습니다.

스펙을 보면 알겠지만, 이 맵에 넣을 수 있는 것은 http 요청 URI에 포함되어 있는 query String과 body에 들어가 있는 post data가 있습니다. post data에는 page24의 조건이 붙는데 이는 뒤에서 다시 살펴보겠습니다.

 
일단 전자는 request URI을 보면 ? 뒤에 들어가 있는 부분으로,
?type=post&returnURL=%2Fmanage%2Fposts%2F와 같은 형태로 구성되어 있습니다. 

후자는 post data의 body에 type=post&returnURL=%2Fmanage%2Fposts%2F와 같이 들어가게 된다. 
둘다 형태는 동일한데, URI에 들어가느냐, body에 들어가느냐의 차이가 있습니다.
html 페이지에서는 아래 코드와 같은 방법으로 HTTP Post 요청을 해당 형태로 보낼수 있게 되어 있습니다.

```java
<form action="/asdf" method="post" >
    <input name="say" value="Hi">
    <input name="to" value="Mom">
    <button>Send my greetings</button>
</form>
```
http request body에 say=Hi&to=Mom 위와 같이 들어가서 요청이 전송되는 것을 볼 수 있습니다.

여기서 중요한 건 post body의 데이터는 그냥 parameter 맵에 넣어주지 않습니다. 이부분은 위에서 언급한 page24의 조건을 살펴봐야 합니다.


3.1.1 When Parameters Are Available The following are the conditions that must be met before post form data will be populated to the parameter set:

The request is an HTTP or HTTPS request.
The HTTP method is POST.
The content type is application/x-www-form-urlencoded.
The servlet has made an initial call of any of the getParameter family of methods on the request object.

If the conditions are not met and the post form data is not included in the parameter set, the post data must still be available to the servlet via the request object’s input stream. If the conditions are met, post form data will no longer be available for reading directly from the request object’s input stream.

즉, HTTP 요청이여야 하고, POST method를 써야 하며, content-type 또한 지정된 content-type을 써야 합니다. 그리고 맵은 처음에 getParameter family 중에 하나가 처음 호출되었을 때에만 만듭니다.

조건이 충족되지 않으면 이 메서드는 아무 동작을 하지 않는데, 조건을 충족하면 맵을 만들게 됩니다. 이때 `InputStream`을 재사용할 수 없게 됩니다.
맵을 만드는 과정에서 InputStream을 통해 데이터를 가져와야하기 때문입니다.

만약에 filter에서 getParameter를 호출하고, servlet에서 getInputStream을 호출하는 경우, 위 조건을 만족시킨다는 가정하에, getInputStream에서 더이상 읽을 수 있는 값은 없게 되는 것입니다.


#### 참조: https://jongqui.tistory.com/9
