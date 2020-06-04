## IO 패키지

프로그램에서는 데이터를 외부에서 읽고 다시 외부로 출력하는 작업이 빈번합니다. 데이터는 사용자로부터 키보드를 통해 입력될 수도 있고, 파일 또는 네트워크로부터 입력될 수도 있습니다. 데이터는 반대로 모니터로 출력될 수도 있고, 파일로 출력되어 저장될 수도 있으며 네트워크로 출력되어 전송될 수도 있습니다.

자바에서 데이터는 스트림(Stream)을 통해 입출력되므로 스트림의 특징을 잘 이해해야 합니다. 스트림은 단일 방향으로 연속적으로 흘러나가는 것을 말합니다. 물이 높은 곳에서 낮은 곳으로 흐르듯이 데이터는 출발자에서 나와 도착지로 들어간다는 개념입니다.

## 입력 스트림과 출력 스트림

프로그램이 출발지냐 또는 도착지냐에 따라서 스트림의 종류가 결정되는데, 프로그램이 데이터를 입력받을 때에는 입력 스트림(InputStream)이라고 부르고, 프로그램이 데이터를 보낼 때에는 출력 스트림(OutputStream)이라고 부릅니다. 입력 스트림의 출발지는 키보드, 파일, 네트워크상의 프로그램이 될수 있고, 출력 스트림의 도착지는 모니터, 파일, 네트워크상의 프로그램이 될 수 있습니다.

![Untitled Diagram (2)](https://user-images.githubusercontent.com/22395934/83325655-db8edd80-a2a8-11ea-985e-167a20895b27.png)

항상 프로그램을 기준으로 데이터가 들어오면 입력 스트림이고, 데이터가 나가면 출력 스트림이라는 것을 명심해야 합니다. 프로그램이 네트워크상의 다른 프로그램과 데이터 교환을 하기 위해서는 양쪽 모두 입력 스트림과 출력스트림이 따로 필요합니다. 스트림의 특성이 단방향이므로 하나의 스트림으로 입력과 출력을 모두 할 수 없기 때문입니다.

![Untitled Diagram](https://user-images.githubusercontent.com/22395934/83325751-bfd80700-a2a9-11ea-8c23-87523df972da.png)

자바의 기본적인 데이터 입출력 API는 java.io 패키지에서 제공하고 있습니다. java.io 패키지에는 파일 시스템의 정보를 얻기 위한 File 클래스와 데이터를 입출력하기 위한 다양한 입출력 스트림 클래스를 제공하고 있습니다.



## 스트림의 종류

- 바이트 기반의 스트림
    - 그림, 멀티미디어, 문자 등 몬든 종류의 데이터를 받고 보낼 수 있습니다.

- 문자기반의 스트림
    - 오직 문자만 받고 보낼 수 있도록 특화되어 있습니다.

바이트 기반의 입력 스트림 중 대표적인 클래스는 InputStream이라는 최상위 클래스가 있고, 최상위 출력 스트림은 OutputStream이 있습니다. 이 클래스들을 각각 상속받는 하위 클래스는 접미사로 InputStream 또는 OutputStream이 붙습니다. Reader는 문자 기반 입력 스트림의 최상위 클래스이고, Wrtier는 문자 기반 출력 스트림의 최상위 클래스입니다. 이 클래스들을 각각 상속받는 하위 클래스는 접미사로 Reader 또는 Writer가 붙습니다. 예를 들어, 그림, 멀티미디어, 텍스트 등의 파일을 바이트 단위로 읽어들일 때에는 FileInputStream을 사용하고, 바이트 단위로 저장할 때에는 FileOutputStream을 사용합니다. 텍스트 파일의 경우, 문자 단위로 읽어들일 때에는 FileReader를 사용하고, 문자 단위로 저장할 때에는 FileWriter를 사용합니다.

## InputStream 클래스
InputStream은 바이트 기반 입력 스트림의 최상위 클래스로 추상 클래스입니다. 모든 바이트 기반 입력 스트림은 이 클래스를 상속받아서 만들어집니다. FileInputStream,, BufferedInputStream, DataInputStream 클래스는 모두 InputStream 클래스를 상속하고 있습니다.

### read() 메소드
read() 메소드는 입력 스트림으로부터 1바이트를 읽고 4바이트 int 타입으로 리턴합니다. 따라서 리턴된 4바이트 중 끝의 1바이트에만 데이터가 들어 있습니다. 예를 들어 입력 스트림에서 5개의 바이트가 들어온다면 다음과 같이 read() 메소드로 1바이트씩 5번 읽을 수 있습니다.

더 이상 입력 스트림으로부터 바이트를 읽을수 없다면 read() 메소드는 -1을 리턴하는데, 이것을 이용하면 읽을 수 있는 마지막 바이트까지 루프를 돌며 한 바이트씩 읽을 수 있습니다.

```java
Inputstream is = new FileInputStream("C:/test.jpg");
int readByte;
while ((readByte=is.read()) != -1) { ... }
```

### read(byte[] b) 메소드
read(byte[] b) 메소드는 입력 스트림으로부터 매개값으로 주어진 바이트 배열의 길이만큼 바이트를 읽고 저장합니다. 그리고 읽은 바이트 수를 리턴합니다. 실제로 읽은 바이트 수가 배열의 길이보다 적을 경우 읽은 수 만큼 리턴합니다. 에를 들어 입력 스트림에서 5개의 바이트가 들어온다면 다음과 같이 길이 3인 바이트 배열로 두 번 읽을 수 있습니다.

read(byte[] b) 역시 입력 스트림으로부터 바이트를 더 이상 읽을 수 없다면 -1을 리턴하는데, 이것을 이용하면 읽을 수 있는 마지막 바이트까지 루프를 돌며 읽을 수 있습니다.

```java
InputStream is = new FileInputStream("C/test.jpg");
int readByteNo;
byte[] readBytes = new Byte[100];
while (readByteNo = is.read(readBytes)) != -1 {...}
```

입력 스트림으로부터 100개의 바이트가 들어온다면 read() 메소는 100번을 루핑해서 읽어들여야 합니다. 그러나 read(byte[] b) 메소드는 한 번 읽을 때 매개값으로 주어진 바이트 배열 길이만큼 읽기 때문에 루핑 횟수가 현저히 줄어듭니다. 그러므로 많은 양의 바이트를 읽을 때는 read(byte[]) 메소드를 사용하는 것이 좋습니다.

### read(byte[] b, int off, int len) 메서드
read(byte[] b, int off, int len) 메소드는 입력 스트림으로부터 len개의 바이트만큼 읽고, 매개값으로 주어진 바이트 배열 b[off]부터 len개까지 저장합니다. 긜고 읽은 바이트 수인 len개를 리턴합니다. 실제로 읽은 바이트 수가 len개보다 작을 경우 읽은 수 만큼 리턴합니다. 

read(byte[] b, int off, int len) 역시 입력 스트림으로부터 바이트를 더 이상 읽을 수 없다면 -1을 리턴합니다. read(byte[] b) 메서드와의 차이점은 한 번에 읽어들이는 바이트 수를 len 매개값으로 조절할 수 있고, 배열에서 저장이 시작되는 인덱스를 지정할 수 있다는 점입니다. 만약 off를 0으로, len을 배열의 길이로 준다면 read(byte[] b)와 동일합니다.

### close() 메소드
InputStream을 더 이상 사용하지 않을 경우에는 close() 메소드를 호출해서 InputStream에서 사용했던 시스템 자원을 풀어줍니다.

```java
is.close();
```
## OutputStream
OutputStream은 바이트 기반 출력 스트림의 최상위 클래스로 추상 클래스입니다. 모든 바이트 기반 출력 스트림 클래스는 이 클래스를 상속받아서 만들어집니다. 다음과 같이 FileOutputStream, PrintStream, BufferedOutputStream, DataOutputStream 클래스는 모두 OutputStream 클래스를 상속하고 있습니다.

### write(int b) 메소드

write(int b) 메소드는 매개 변수로 주어진 int 값에서 끝에 있는 1바이트만 출력 스트림으로 보냅니다. 매개 변수가 int 타입이므로 4바이트 모두를 보내는 것으로 오해할 수 있습니다.

```java
OutputStream os = new FileOutputStream("C:/test.txt");
byte[] data = "ABC".getBytes();
for(int i = 0; i <data.length; i++) {
    os.write(data[i]); //"A", "B", "C"를 하나씩 출력
}
```

### write(byte[] b) 메소드
write(byte[] b)는 매개값으로 주어진 바이트 배열의 모든 바이트를 출력 스트림으로 보냅니다.

```java
OutputStream os = new FileOutputStream("C:/test.txt");
byte[] data = "ABC".getBytes();
os.write(data); //"ABC" 모두 출력
```


### write(byte[] b, int off, int len) 메소드
write(byte[] b, int off, int len)은 b[off] 부터 len 개의 바이트를 출력 스트림으로 보냅니다.

```java
OutputStream os = new FileOutputStream("C:/test.txt");
byte[] data = "ABC".getBytes();
os.write(data, 1, 2); // "BC"만 출력
```

### flush()와 close() 메소드

출력 스트림은 내부에 작은 버퍼(buffer)가 있어서 데이터가 출력되기 전에 버퍼에 쌓여있다가 순서대로 출력됩니다. flush() 메소드는 버퍼에 잔류하고 있는 데이터를 모두 출력시키고 버퍼를 비우는 역할을 합니다. 프로그램에서 더 이상 출력할 데이터가 없다면 flush() 메소드를 마지막으로 호출하여 버퍼에 잔류하는 모든 데이터가 출력되도록 해야 합니다. OuputStream을 더 이상 사용하지 않을 경우에는 close() 메소드를 호출해서 OutputStream에서 사용했던 시스템 자원을 풀어줍니다.

```java
OutputStream os = new FileOutputStream("C:/test.txt");
byte[] data = "ABC".getBytes();
os.write(data);
os.flush();
os.close();
```

## Reader
Reader는 문자 기반 입력 스트림의 최상위 클래스로 추상 클래스입니다. 모든 문자 기반 입력 스트림은 이 클래스를 상속받아서 만들어집니다. 대표적으로 FileReader, InputStreamReader, BufferedReader 클래스가 있고, 이들 모두 Reader 클래스를 상속하고 있습니다.


### read() 메소드
read() 메소드는 입력 스트림으로부터 한 개의 문자(2바이트)를 읽고 4바이트 int 타입으로 리턴합니다. 따라서 리턴된 4바이트 중 끝에 있는 2바이트에 문자가 들어있습니다. 예를 들어 입력 스트림에서 2개의 문자 (총 4바이트)가 들어온다면 다음과 같이 read() 메소드로 한 문자씩 두 번 읽을 수 있습니다.

read() 메소드가 리턴한 int 값을 char 타입으로 변환하면 읽은 문자를 얻을 수 있습니다.

```java
char charData = (char) read();
```

더 이상 입력 스트림으로부터 문자를 읽을 수 없다면 read() 메소드는 -1을 리턴하는데 이것을 이용하면 읽을 수 있는 마지막 문자까지 루프를 돌며 한 문자씩 읽을 수 있습니다.

```java
Reader reader = new FileReader("C:/test.txt");
int readData;

while ((readData=reader.read() != -1)) {
    char charData = (char) readData;
}
```

### read(char[] cbuf) 메소드

read(char[] cbuf) 메소드는 입력 스트림으로부터 매개값으로 주어진 문자 배열의 길이만큼 문자를 읽고 배열에 저장합니다. 그리고 읽은 문자 수를 리턴합니다. 실제로 읽은 문자 수가 배열의 길이보다 작을 경우 읽은 수만큼만 리턴합니다. 예를 들어 입력 스트림에서 세 개의 문자가 들어온다면 다음과 같이 길이가 2인 문자 배열로 두 번 읽을 수 있습니다.

입력 스트림으로부터 100개의 문자가 들어온다면 read() 메소드는 100번을 루핑해서 읽어들어야 합니다. 그러나 read(char[] cbuf) 메소드는 한번 읽을 때 주어진 배열 길이만큼 읽기 때문에 루핑 횟수가 현저히 줄어듭니다. 그러므로 많은 양의 문자를 읽을 때는 read(char[] cbuf) 메소드를 사용하는 것이 좋습니다.

## Wrtier
Writer는 문자 기반 출력 스트림의 최상위 클래스로 추상 클래스 입니다. 모든 문자 기반 출력 스트림 클래스는 이 클래스를 상속받아서 만들어집니다. 다음과 같이 FileWriter, BufferedWriter, PrintWriter, OutputStreamWriter 클래스는 모두 Writer 클래스를 상속하고 있습니다.

Writer 클래스에는 모든 문자 기반 출력 스트림이 기본적으로 가져야 할 메소드가 정의되어 있습니다.
Writer 클래스의 주요 메소드는 대표적으로 아래와 같은 메소드가 존재합니다.

### write(int c)  메소드
write(int c) 메소드는 매개 변수로 주어진 int 값에서 끝에 있는 2바이트(한개의 문자)만 출력 스트림으로 보냅니다. 매개 변수가 int 타입이므로 4바이트 모두를 보내는 것으로 오해할 수 있습니다.

```java
Writer writer = new FileWriter("C:/test.txt");
char[] data = "홍길동".toCharArray();
for(int i = 0; i < data.length; i++) {
    writer.write(data[i]); // "홍", "길", "동"을 하나씩 출력
}
```

### write(char[] cbuf) 메소드
write(char[] cbuf) 메소드는 매개값으로 주어진 char[] 배열의 모든 문자를 출력 스트림으로 보냅니다.

```java
Writer writer = new FileWriter("C:/test.txt");
char[] data = "홍길동".toCharArray();
writer.write(data); //"홍길동" 모두 출력
```


### write(char[] c, int off, int len) 메소드
write(char[] c, int off, int len)은 c[off] 부터 len개의 문자를 출력스트림으로 보냅니다.
마찬가지로 write(String str) 메소드가 있는데 문자열 전체를 출력 스트림으로 보냅니다.

문자 출력 스트림은 내부에 작은 버퍼(buffer)가 있어서 데이터가 출력되기 전에 버퍼에 쌓여있다가 순서대로 출력됩니다. flush() 메소드는 버퍼에 잔류하고 있는 데이터를 모두 출력시키고 버퍼를 비우는 역할을 합니다. 프로그램에서 더 이상 출력할 문자가 없다면 flush() 메소드를 마지막으로 호출하여 모든 문자가 출력되도록 해야 합니다. 마지막으로 Writer를 더 이상 사용하지 않을 경우에는 close() 메소드를 호출해서 Writer에서 사용했던 시스템 자원을 풀어줍니다.
