# Channel을 이용한 파일 IO 예제 코드


```java
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BufferedFileIO {

    public static void main(String[] args) {

        Path sourcePath = Paths.get("/Users/limjun-young/workspace/privacy/dev/test/test.txt");
        Path targetPath = Paths.get("/Users/limjun-young/workspace/privacy/dev/test/test_new.txt");

        Charset charset = Charset.forName("UTF-8");

        try(BufferedReader bufferedReader = Files.newBufferedReader(sourcePath, charset);
            BufferedWriter bufferedWriter = Files.newBufferedWriter(targetPath, charset)
        ) {

            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(line, 0, line.length());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
```
