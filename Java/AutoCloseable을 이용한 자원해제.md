# AutoCloseable 클래스

`try-with-resources`는 try(...) 문에서 선언된 객체들에 대해서 try가 종료될 때 자동으로 자원을 해제해주는 기능입니다. 주로 외부 자원인 파일 관련 객체와 socket Handler 객체와 같은 자원들은 try-catch-finally 문을 사용하여 마지막에 다 사용한 자원을 해제하는 코드를 많이 보았을 겁니다. AutoCloseable은 try에 선언된 객체가 AutoCloseable을 구현했더라면 java는 try 구문이 종료될 때 객체의 close() 메소드를 호출해 줍니다.

자바6에서 리소스 사용및 해제하는 방법을 한번 살펴보고, try-with-resources로 동일한 코드를 리팩토링해보면서 장점이 무엇인지 살펴보겠습니다.

예를 들어, 다음 코드는 try-catch-finally을 사용하여 파일을 열고 문자열을 모두 출력하는 코드입니다.


```java
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class TraditionalResourceCloseable {

    public static void main(String[] args) throws IOException {

        FileInputStream is = null;
        BufferedInputStream bis = null;

        try{
            is = new FileInputStream("/Users/limjun-young/test.txt");
            bis = new BufferedInputStream(is);

            int n = -1;
            while ((n = bis.read()) != -1) {
                System.out.println((char) n);
            }

        }finally {
            is.close();
            bis.close();
        }
    }
}
```

코드를 보시면 try에서 inputStream 객체를 생성하고 finally에서 close를 해주었습니다. try 안의 코드를 실행하다 Exception이 발생하는 경우 모든 코드가 실행되지 않을 수 있기 때문에 finally에 close 코드를 넣어주어야 합니다. 심지어 InputStream 객체가 null인지 체크해줘야 하며 close에 대한 Exception 처리도 해야합니다. 저는 main에서 IOException을 throws한다고 명시적으로 선언했기 때문에 close에 대한 try-catch 구문을 작성하지 않았습니다.


## Try-with-resources로 자원 쉽게 해제

Java 7부터 Try-with-resources를 사용하여 InputStream으로 파일의 문자열을 모두 출력하는 코드입니다. 실행 결과는 위의 예제와 동일합니다.

```java
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class TraditionalResourceCloseable {

    public static void main(String[] args) {

        // try-with-resources로 자원 해제
        try (
                FileInputStream is = new FileInputStream("/Users/limjun-young/test.txt");
                BufferedInputStream bis = new BufferedInputStream(is);
        ) {
            int n = -1;
            while ((n = bis.read()) != -1) {
                System.out.println((char) n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

코드를 보시면, try(...)안에 InputStream 객체 선언 및 할당하였습니다. 여기에서 선언한 변수들은 try안에서 사용할 수 있습니다. 코드의 실행 위치가 try 문을 벗어나면 try-with-resources는 try(...)안에 선언된 객체의 close() 메소드를 호출해야합니다. 그래서 finally에 close()를 명시적으로 호출해줄 필요가 없습니다.

try-with-resources에서 자동으로 close가 호출되는 것은 AutoCloseable을 구현한 객체에만 해당이 됩니다. 이 부분은 아래에서 좀 더 자세히 설명하겠습니다.

try-with-resources의 장점은 코드를 짧고 간결하게 만들어 읽고 쉽고 유지보수가 쉬워집니다. 또한 명시적으로 close를 호출하려면 많은 if와 try-catch를 사용해야 하기 때문에 실수로 close를 빼먹는 경우가 있습니다. 이것을 이용하면 이런 자잘한 버그들이 발생할 가능성이 적습니다.

## Try-with-resources로 close() 메소드가 호출되는 객체

Try-with-resources가 모든 객체의 close()를 호출해주지는 않습니다. AutoCloseable을 구현한 객체만 close()가 호출됩니다.

```java
package java.lang;

public interface AutoCloseable {
    void close() throws Exception;
}
```

> 주의할 점은 BufferedInputStream 객체는 InputStream 객체를 상속받습니다. 만약에 아래 코드와 같이 InputStream 객체가 AutoCloseable를 상속받은 Closeable을 구현하였을 때 BufferedInputStream 객체가 Try-with-resources에 의해서 해제될 수 있습니다.

```java
public abstract class InputStream extends Object implements Closeable {
    ...
}

public interface Closeable extends AutoCloseable {
    void close() throws IOException;
}
```

##  AutoCloseable을 구현하는 클래스 만들기
내가 만든 클래스가 try-with-resources으로 자원이 해제되길 원한다면 AutoCloseable을 implements 해야합니다.

아래 코드에서 CustomResource 클래스는 AutoCloseable을 구현하였습니다. main에서는 이 객체를 try-with-resources로 사용하고 있습니다.

```java
public class AutoCloseableExample {
    public static void main(String[] args) {
        try (CustomResource customResource = new CustomResource()){
            customResource.doSomething();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

public class CustomResource implements AutoCloseable {

    public void doSomething(){
        System.out.println("Do something ...");
    }

    @Override
    public void close() throws Exception {
        System.out.println("CustomResource Closed!");
    }
}
```

실행해보면 다음과 같이 출력됩니다. 이 예제에서는 close()가 호출될 때 로그를 출력하기 때문에 close()가 호출되는지 눈으로 확인할 수 있습니다.

#### 실행 결과
![image](https://user-images.githubusercontent.com/22395934/75355616-1e2fcb00-58f2-11ea-8643-70376e7face7.png)

> 참고로 close 메서드 구현시 구체적인 exception을 throw 하고, close 동작이 전혀 실패할 리가 없을 때는 exception을 던지지 않도록 구현하는 것을 강력하게 권고하고 있습니다. 특히 close 메서드에서 InterruptedException을 던지지 않는 것을 강하게 권고합니다. InterruptedException은 쓰레드의 인터럽트 상태와 상호작용 하므로 interruptedException이 억제되었을 때 런타임에서 잘못된 동작이 발생할 수 있기 때문입니다.



#### 참조: https://codechacha.com/ko/java-try-with-resources/
