## Gson을 이용하여 객체로 바인딩 하기
오늘은 JSP 페이지에서 Json 문자열로 ajax를 통해 비동기 통신을 하여 Controller에서 command 패턴으로 객체를 바인딩하는 업무를 수행하였는데..json 배열 객체를 json 문자열 포맷으로 변경 후에 객체의 String 타입으로 바인딩을 하고나서 이 문자열을 다시 List<String> 타입의 객체로 deserialize하는 방법이 없을까 찾아보다가 Gson 객체와 TypeToken<T> 객체에 대해서 알게 되었습니다.


아래 간단한 예제를 통해서 Json 문자열 배열을 List<String> 타입의 객체로 변환하는 방법을 살펴보겠습니다.

대충 아래와 같이 취미를 여러개 입력해야 한다는 요청이 있다고 가정하겠습니다. 물론 말이 안되는 상황이긴 하지만... 딱히 생각나는
예제가 없기 때문에 저의 목적인 json array를 Gson을 이용하여 객체로 변환하는 과정을 보여주기 위해서 갑작스럽게 만든 예제이기 때문입니다.

```javascript
<form>
<table class="table table-bordered">
        <thead>
            <caption> 회원가입 </caption>
        </thead>
        <tbody>
            <tr>
                <th>ID:</th>
                <td><input type="text" placeholder="ID를 입력하세요." id="myId" class="form-control"/></td>
            </tr>
            <tr>
                <th>이름을 입력하세요: </th>
                <td><input type="text" id="myName" name="myName" class="form-control"/></td>
            </tr>
            <tr>
                <th>취미를 입력하세요: </th>
                <td><input type="text"name="myHobby"  value="soccer"  class="form-control"/></td>
            </tr>
            <tr>
                <th>취미를 입력하세요: </th>
                <td><input type="text" name="myHobby" value="baseball"  class="form-control"/></td>
            </tr>
            <tr>
                <th>취미를 입력하세요: </th>
                <td><input type="text" name="myHobby" value="basketball"  class="form-control"/></td>
            </tr>
            <tr>
                <th>취미를 입력하세요: </th>
                <td><input type="text" name="myHobby" value="seraching"  class="form-control"/></td>
            </tr>
                <tr>
                    <td colspan="2">
                        <input type="reset" value="리셋" class="btn btn-light"/>
                        <button type="button" id="insert" class="btn btn-primary">전송하기</button>
                    </td>
                </tr>
            </tbody>
    </table>
</form>

        
    <script type="text/javascript" language="javascript">
        $('#insert').click(function(){
           
            let hobby = new Array();

            $('input[name=myHobby]').each(function(index){
               hobby.push($(this).val());
            });

            let inputArrays = JSON.stringify(hobby);

            let jsonData = JSON.stringify({
                id: $('#myId').val(),
                name: $('#myName').val(),
                hobby: hobby
            });

            $.ajax({
                url: "http://localhost:8080/convToObject",
                type: "POST",
                data: jsonData,
                contentType: "application/json",
                success: function (data, status,xhr) {
                    alert('요청 성공!' + data);
                },
                error: function (xhr, status) {
                    alert('내용을 입력하셔야 합니다.');
                    console.log(xhr);
                    console.log(status);
                }
            });
        });
    </script>
```

html 코드에서 input Tag의 name property가 myHobby인 것만 배열에 담아서, 이것을 JSON.stringfy() 메소드를 이용하여 문자열로
변환하였습니다.

```javascript
 let hobby = new Array();

$('input[name=myHobby]').each(function(index){
    hobby.push($(this).val());
});

let inputArrays = JSON.stringify(hobby);
console.log(inputArrays);

// 출력결과: "["soccer","baseball","basketball","searching"]"
```


이제 위의 전송하기 버튼을 클릭하게 된다면 javascript에 있는 ajax가 호출되어 비동기로 Controller에 POST 방식으로 RequestMapping 되어 있는 convToObject() 메소드를 통해 아래와 같이 Member 객체로 바인딩이 됩니다.

```java
@Getter
@Setter
public class Member{
    
    private String id;
    private String name;
    private String hobby;

}



@Controller
public class GsonExamController {
    
    @RequestMapping(value = "/convToObject", method = RequestMethod.POST, produces = "application/text; charset=utf8")
    @ResponseBody
    public String convToObjectFromGson(@RequestBody Member member) {
        
        // Gson 객체를 생성
        Gson converter = new Gson();

        //Type이 List인 경우입니다.
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> hobbyList = converter.fromJson(member.getHobby(), type);
    
      
        return hobbyList.toString();
    }
}
```
GsonExamController의 convToObjectFromGson()의 메소드를 호출하면 변환할 타입과 객체의 파라미터를 이용하여 객체로 바인딩을 해주는 Gson 객체를 생성하게 됩니다. 그리고 위의 예제에서는 List<String> 객체를 TypeToken 객체의 타입 파라미터로 명시해주었습니다. 이를 통해 내부적으로 리플렉션을 사용해서 Type을 구하는지는.. 정확히 모르겠지만 어쨌든 해당 타입을 구하게 됩니다. 물론 역시 List<String> 타입을 참조하게 되겠죠. 그리고 jsp 페이지에서 Json 문자열로 변환한 배열을 Argument로 넣어주게 되면 List<String> 타입 객체로 변환되는 것을 확인할 수 있습니다.

앞으로 프로젝트에 기능 구현에서도 유용하게 사용할 수 있는 좋은 객체라고 생각하여 정리하였습니다.
