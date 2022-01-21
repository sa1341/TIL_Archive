# Java NIO 패키지 

이전에 올렸던 java.io 패키지에 대해서 공부하고 포스팅했지만, Java 4부터 등장한 java.nio에 대해서도 궁금하여 포스팅하였습니다.

## 1. IO와 NIO의 차이

NIO는 의미만 봤을 때 Non-blocking IO의 줄임말이라고 생각했지만, 사실 `New IO`의 줄임말이였습니다. 

java.io 패키지랑 무슨 차이가 있는지 javadoc에서 찾아보니 파일에 데이터를 읽고 쓰는 통로 역할을 하는 채널은 `버퍼`라는 곳에 항상 데이터를 read하거나 write 하도록 되어있다고 나와있습니다.

IO 같은 경우에는 Stream을 통해서 파일로부터 데이터를  읽거나 쓰도록 되어 있고, NIO는 Channel을 통해서 `무조건` 버퍼에 데이터를 읽거나 씁니다.

### Stream

- 파일을 읽기 위한 InputStream, 파일을 쓰기 위한 OutputStream 객체가 별도로 존재하고, 단방향으로만 데이터가 흐릅니다.

### Channel
- 양방향으로 데이터가 흐를 수 있고, ByteChannel, FileChannel을 만들어서 읽고 쓰는게 가능합니다.

- io와 다르게 Non-Blocking 방식도 가능합니다. 하지만 언제나 Non-blocking 방식으로 동작하는 것이 아니라는것을 명심해야 합니다.


## 2. NIO Channel 

Channel을 살펴보기전에 기본적으로 Buffer에 대한 개념을 알아야 합니다. 채널을 통한 파일 입출력은 무조건 버퍼를 사용해야 합니다. 기본적으로 nio 패키지에서 정적(static) 메서드를 이용하여 생성할 수 있습니다.

### Channel을 통한 파일 읽기 예제

```java
public class ChannelReadExam {

    public static void main(String[] args) {

        Path path = Paths.get("/Users/limjun-young/workspace/privacy/dev/test/video/video/temp.txt");
        // 채널 객체를 파일 읽기 모드로 생성합니다.
        try (FileChannel ch = FileChannel.open(path, StandardOpenOption.READ)) {
            // 1024 바이트 크기를 가진 Buffer 객체 생성
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            ch.read(buffer);

            buffer.flip();
            Charset charset = Charset.defaultCharset();
            String inputData = charset.decode(buffer).toString();
            System.out.println("inputData: " + inputData);

            buffer.clear();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("파일 작업 실패");
        }
    }
}
```

### Channel을 통한 파일 wirte 예제 코드

```java
public class ChannelWriteExam {

   public static void main(String[] args) {

        Path path = Paths.get("/Users/limjun-young/workspace/privacy/dev/test/video/video/output.txt");

        try (FileChannel ch = FileChannel.open(path, 
        StandardOpenOption.WRITE, 
        StandardOpenOption.CREATE)) {
            
            String data = "NIO Channel을 이용해서 파일에 데이터를 써보겠습니다.";
            Charset charset = Charset.defaultCharset();
            ByteBuffer buffer = charset.encode(data);
            ch.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

#### 실행 결과

![image](https://user-images.githubusercontent.com/22395934/116965493-eb991380-ace8-11eb-9ac4-f8d776f60918.png)


### Channel 객체 생성

- 채널(Channel) 생성 옵션을 가진 기본 라이브러리 Enum 클래스
- open() 메서드를 이용한 채널 인스턴스 생성 시 옵션은 여러 개 중복으로 넣어줄 수 있습니다.

| 옵션 | 설명 
|:-------:|:-------:|
| READ | 읽기용으로 파일을 엽니다.|
| WRITE | 쓰기용으로 파일을 엽니다.|
| CREATE | 파일이 없으면 새 파일을 생성합니다.|
| CREATE_NEW | 새 파일 생성합니다. (기존에 존재하면 예외 발생)|
| APPEND | 추가 모드로 파일을 엽니다.(EOF 위치부터 시작, WRITE / CREATE와 같이 사용)|
| DELETE_ON_CLOSE | 채널이 닫힐 때 파일을 삭제합니다.|
| TRUNCATE_EXISTING | 파일을 열 때 파일 내용을 모두 삭제 합니다.(0 바이트로 만들고, WRITE와 같이 사용합니다.)|

> java.nio.Path.Files 클래스를 이용해 미리 파일 상태를 확인해서 Path 객체를 생성한 뒤 적절한 옵션을 사용하면 됩니다. 

### ByteBuffer 객체 생성

파일 I/O를 자주하면 allocate()를 크게 하나 만들어두고 계속 사용합니다.

```java
ByteBuffer buffer = ByteBuffer.allocate(10);
```

기본적으로 아래와 같이 메모리에 버퍼가 생성되고, 파일의 데이터를 가르키는 파일 포인터처럼 버퍼도 `버퍼 포인터가 존재합니다.`

- Capacity: 버퍼의 전체 크기
- Position: 현재 버퍼를 쓰거나 읽을 위치, 파일 포인터의 개념과 같은 버퍼 포인터라고 보면 됩니다.
- Limit: 전체 크기 중에 실제 읽고 쓸 수 있는 위치를 따로 지정한 것으로 기존에 Capacity와 동일하게 생성됩니다.


![image](https://user-images.githubusercontent.com/22395934/116962077-0024de00-ace0-11eb-93bf-d2fc66b6c3ed.png)

### Charset 객체 생성

위의 과정을 통해 파일과 채널을 생성하고 읽고 쓸 수 있는 버퍼 생성까지 완료했습니다. 이제 파일을 I/O 준비가 되었습니다. 


외부의 문자 데이터를 주고 받을 때는 서로 같은 인코딩 타입을 사용하지 않을 수 있습니다. Window 환경에서는 메모장은 ANSI 코드를 사용하고, Java는 charset으로 유니코드를 사용합니다. 따라서 한글처럼 2byte 이상으로 이루어진 문자를 유니코드를 출력해도 메모장에서는 해당 문자가 깨지게 됩니다. 

이러한 문제를 해결하기 위해 인코딩 타입 간 변환을 위해 일단 `Charset` 클래스의 인스턴스를 하나 생성해야 합니다.

아래와 같이 2가지 방법으로 Charset 인스턴스 생성이 가능합니다.

```java
Charset charset = Charset.defaultCharset();
Charset charset = Charset.forName("UTF-8");
```

- defaultCharset(): OS의 인코딩 타입 간 변환을 해주는 객체 생성합니다.
- forName("타입"): 직접 입력한 타입 간 변환을 해주는 객체를 생성합니다.

위 예제 코드에서는 파일 읽기 용으로 Channel을 생성하였기 때문에, Buffer에 파일 데이터를 읽어와서 Charset을 통해서 해당 인코딩 타입으로 다시 디코드하여 문자열을 출력하고 있습니다.

### Charset 인코딩 

`encode()` 데이터를 `UTF-8`로 인코딩 후 버퍼에 저장하고 있습니다.

```java
String data = "NIO Channel을 이용해서 파일에 데이터를 써보겠습니다.";
Charset charset = Charset.defaultCharset();
ByteBuffer buffer = charset.encode(data);
```

### Charset 디코딩

`decode()` 메서드는 버퍼에 저장된 바이너리 값을 `UTF-8`로 디코딩 후 문자열로 리턴하고 있습니다.

```java
Charset charset = Charset.defaultCharset();
String inputData = charset.decode(buffer).toString();
System.out.println("inputData: " + inputData);
```


## 3. NIO에 대한 오해

> 
NIO가 의미만 봤을 때 Non-Blocking 방식으로 동작할 것 같지만 생각만큼 Non-Blocking 하지 않다고 합니다.

예를 들어서 아래 java.nio.Files는 NIO 중에서 File I/O를 담당합니다. 파일을 읽는데 사용되는 `Files.newBufferedReader(), Files.newInputStream()`등은 모두 blocking 입니다. 마찬가지로 `Files.newBufferedWriter(), Files.newOutputStream` 등도 모두 blocking 입니다.

그렇다면 왜 사용할까 찾아보니 성능적인면에서 FILE I/O에 사용되는 Channel이 blocking 모드로 동작하지만 데이터를 `Buffer`를 통해 이동시키므로써 기존의 java.io 패키지에서 사용하는 Stream I/O에서 `병목을 유발하는 몇가지 레이어를 건너뛸 수 있어서 성능상의 이점을 누릴 수 있다고 합니다.`

## 4. Non-Blocking 방식으로 I/O 처리

Java 7부터 도입되어 NIO2라고 불리는 NIO에는 `AsynchronousFileChannel`이 Non-Blocking 모드로 동작합니다. 

### AsynchronousFileChannel 예제 코드

아래는 AsynchronousFileChannel 객체를 이용하여 Non-Blocking 처리하는 예제 코드를 작성해봤습니다. 여기서 try-with-resources를 사용할 경우 파일을 버퍼에 저장 후 CompletionHandler의 콜백 함수가 실행이 되는데 try-with-resources에서 자동으로 닫히기 때문에 비동기 방식으로 파일을 읽으려고 하는 순간 예외가 발생합니다. 그래서 try-with-resources 구문을 사용하지 않고 콜백 함수에서 close 하도록 처리하였습니다.

```java
public class AsynchronousFileChannelExam {

    public static void main(String[] args) {

        Path path = Paths.get("/Users/limjun-young/workspace/privacy/dev/test/video/video/output.txt");

        try {

            AsynchronousFileChannel ch = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            long position = 0;

            ch.read(buffer, position, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    // 읽은 바이트 수를 리턴합니다.
                    System.out.println("result = " + result);

                    attachment.flip();
                    byte[] data = new byte[attachment.limit()];
                    attachment.get(data);
                    System.out.println(new String(data));
                    attachment.clear();

                    // AsynchronousFileChannel close 처리
                    if (ch != null || ch.isOpen()) {
                        try {
                            ch.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    System.out.println("파일 읽기 실패");
                    exc.printStackTrace();
                }
            });
            System.out.println("Non-Blocking 중이니?");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

> 참조 사이트: https://codevang.tistory.com/160
