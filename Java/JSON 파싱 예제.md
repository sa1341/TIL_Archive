```java
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;

public class JsonParsingExam {
    public static void main(String[] args) throws IOException {

        String jsonFormat = "{\"name\": \"junyoung\", \"age\": 29, \"wishList\": [{\"name\": \"book1\", \"price\": 10000 }, {\"name\": \"book2\", \"price\": 20000} ] }";

        JsonParser jsonParser= new JsonParser();

        JsonElement jsonElement = jsonParser.parse(new FileReader("/Users/limjun-young/workspace/privacy/dev/memberInfo.json"));

        JsonElement jsonElement = jsonParser.parse(jsonFormat);

        String name = jsonElement.getAsJsonObject().get("name").getAsString();

        System.out.println(name);

        JsonArray jsonArray = jsonParser.parse(jsonFormat).getAsJsonObject().get("wishList").getAsJsonArray();
        System.out.println(jsonArray.size());


        for (int i = 0; i < jsonArray.size(); i++) {

            JsonObject obj = jsonArray.get(i).getAsJsonObject();
            System.out.println(obj.get("name"));
            System.out.println(obj.get("price"));
        }


    }
}

```

구글에서 제공해주는 Google + JSON이라고 불리는 GSON 라이브러리를 이용하여 JSON 포맷의 데이터를 파싱하는 예제코드를 작성하였습니다.
평상시에 자주 쓰지않다가 요즘에 고객사의 custom 개발 작업을 하면서 사용하게 될 일이 있어서 쉽게 파싱할 수 있는 방법이 있나 싶어서 찾아본 결과 GSON을 사용하니까
편리하게 JSONObject나 JSONArray 객체로 변환하여 접근할 수 있었습니다.

JSONElement 객체는 JSONObject나 JSONArray 객체가 될 수 있는 잠정적인 녀석입니다.
`/Users/limjun-young/workspace/privacy/dev/memberInfo.json` 해당 경로에 존재하는 JSON 포맷 데이터를 파싱하여 JSONElement 객체로 변환합니다.
그리고 KEY 값에 해당하는 value 타입이 JSONObject냐 JSONAraay냐에 따라서 getAsJsonArray() 혹은 getAsJsonObject() 메소드로 해당 객체에 접근하여 사용하면 됩니다. gson 라이브러리는 또한 json 파싱뿐만 아니라 객체로 역직렬화를 해주는 편리한 기능들도 제공하고 있습니다. 나중에 기회가 되면 포스팅하겠습니다.
