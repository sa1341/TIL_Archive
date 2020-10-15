## lib 충돌 시 같은 jar 파일 찾는 예제코드

Jeus8에서 das에서 ms server1에 컨테이너를 2개 배포하였는데 classNotFound Exception이 발생하였다. 분명 톰캣에서 이상없이 잘 올라갔는데.. 찾아보니 라이브러리가 충돌나면 발생할 소지가 있는 예외라서
WEB-INF/lib 디렉토리 하위에서 각각 같은 jar 파일이 있는지 비교하는 코드를 한 번 작성해보았다...  사실 이걸로 해결될 일은 아니겠지만, 비교할 jar 파일이 많다면 유용할 수도 있다 생각해서 심심해서 만들어 보았습니다.

```java
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileFinder {

    private static final Logger logger = LoggerFactory.getLogger(FileFinder.class);

    @Test
    public void findOtherLib() throws Exception {

        //given
        // pj1, pj2 디렉토리 하위에 다른 jar 파일명을 넣기 위한 Collection 타입의 객체를 생성 
        List<String> differentLib = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        
        // pj1 디렉토리 하위에 있는 파일 리스트 추출
        File[] pj1 = new File("/Users/limjun-young/workspace/privacy/dev/test/pj1").listFiles();
        // pj2 디렉토리 하위에 있는 파일 리스트 추출
        File[] pj2 = new File("/Users/limjun-young/workspace/privacy/dev/test/pj2").listFiles();

        // pj1을 기준으로 map 객체에 put (사실 getOrDefault 메소드를 사용하지 않아도 무조건 jar 파일명은 1개만 존재할 것 입니다.) 
        for (File file: pj1) {
            if (map.containsKey(file.getName())) {
                map.put(file.getName(), map.getOrDefault(file.getName(), 0) + 1);
            }
            map.put(file.getName(), 1);
        }
        // pj2 jar 파일 명이 pj1 파일명이 들어있는 map 객체에 있는 key에 포함되어 있지 않으면 differentLib 리스트에 넣습니다.
        for (File file: pj2) {
            if (map.containsKey(file.getName())) {
                map.put(file.getName(), map.get(file.getName()) - 1);
            } else {
                differentLib.add(file.getName());
            }
        }
        differentLib.forEach(f -> logger.debug("file: {}", f));
        //when
    }
}
```

위 코드에서 pj1, pj2 디렉토리 하위에 있는 충돌 가능성이 있는 같은 jar 파일명을 비교하는 코드를 작성해보았습니다. 물론 버전이 다르면 jvm 스펙상 어떤 클래스를 로드할지 알 수 없기 때문에 충돌난 라이브러리를 찾기
쉽지는 않겠지만 최소한 비교를 통해서 공통적으로 사용하는 라이브러리는 Jeus에서 도메인에서 공통으로 사용하는 lib 디렉토리에 넣어서 관리할 수 있습니다.


