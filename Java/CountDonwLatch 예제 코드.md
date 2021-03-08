
## CountDownLatch

오늘 업무 시간에 코드를 보다가 CountDownLatch라는 객체를 사용하는 코드 부분이 있어서 한번 찾아보았는데 Concurrency(병행성)에 관련된 API라는 것을 알 수 있었습니다.
CountDownLatch라는 용어는 단어사전에 쳐보니 `~에 걸쇠를 걸다`라는 뜻을 가지고 있습니다. 정확히는 이 단어 뜻으로는 이해가 안되어 구글링을 한 결과 역시... 훌륭한 자료들이 나와있었습니다.

그럼 CountDownLatch는 언제 쓰는지 주로 예제코드를 통해서 확인해 봤습니다. 만약 특정 리스트에 있는 자료구조를 병렬로 처리하고 후처리로 DB에 업데이트를 하는 작업 및 다른 시스템으로 push 하는 경우에도 
CountDownLatch 객체를 사용한다고 합니다. 

아래 예제 코드를 통해서 CountDownLatch 객체를 병렬 처리를 할 경우 어떤식으로 사용하는지 직접 작성하여 확인하였습니다.

먼저, CountDownLatch를 사용하지 않을 경우 메인 쓰레드에서 별도로 다른 쓰레드들이 작업이 종료될 떄 까지 기다리지 않고 각각 비동기적으로 수행이 됩니다.

```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchExample {

    public static void main(String[] args) throws InterruptedException {

        int maxCount = 10;
        // 기본적으로 CountDownLatch 객체 생성 시 정수 값을 넣어 줍니다. 10부터 시작...
        CountDownLatch countDownLatch = new CountDownLatch(maxCount);
        // 쓰레드 10개를 관리하는 쓰레드 풀 객체 생성
        ExecutorService executorService = Executors.newFixedThreadPool(maxCount);

        // 10번의 루프를 돌면서 아래 Work 객체를 쓰레드 풀이 관리하는 queue에 등록하여 쓰레드가 작업을 처리하도록 제출.
        for (int i = 0; i < maxCount; i++) {
            executorService.submit(new Worker(i, countDownLatch));
        }
        // 작업이 모두 완료되면 쓰레드 풀을 종료.
        executorService.shutdown();
        System.out.println("Done awaiting");
    }

}

class Worker implements Runnable {

    private final CountDownLatch countDownLatch;
    private final int index;

    public Worker(final int index, final CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        this.index = index;
    }

    @Override
    public void run() {
        try {
            System.out.println("Starting thread... " + index);
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Finishing Trhead... " + index);
        }
    }
}
```

## 실행결과 

![image](https://user-images.githubusercontent.com/22395934/110334418-19096e00-8066-11eb-8c2e-729e52041645.png)

여기서 main 쓰레드에서 아래와 같이 CountDownLatch.await() 함수를 호출하면 쓰레드들이 작업이 완료될 때까지 대기하게 됩니다. 정확하게 말하면 쓰레드가 실행 중인 작업이 완료될때 마다
CountDownLatch.countDown() 함수를 호출하면 초기에 정수 값이 -1만큼 감소하게 되고, 결국 모든 쓰레드가 작업이 완료되면 0이 됩니다. 그리고 나서 main 쓰레드에서 await가 해지가 되어 후 처리 작업을 수행할 수 있게
됩니다.

```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchExample {

    public static void main(String[] args) throws InterruptedException {

        int maxCount = 10;
        CountDownLatch countDownLatch = new CountDownLatch(maxCount);
        ExecutorService executorService = Executors.newFixedThreadPool(maxCount);

        for (int i = 0; i < maxCount; i++) {
            executorService.submit(new Worker(i, countDownLatch));
        }

        executorService.shutdown();
        // awiat() 메서드 호출 시 모든 쓰레드가 작업이 완료가 되어서, CountDownLatch가 0일 경우 해제 됩니다.
        countDownLatch.await();
        System.out.println("Done awaiting");
    }

}

class Worker implements Runnable {

    private final CountDownLatch countDownLatch;
    private final int index;

    public Worker(final int index, final CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        this.index = index;
    }

    @Override
    public void run() {
        try {
            System.out.println("Starting thread... " + index);
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Finishing Trhead... " + index);
            // 작업 완료 후, countDown() 메서드 호출 시 CountDownLatch의 값이 -1 씩 감소... 
            countDownLatch.countDown();
        }

    }
}
```

## 실행결과

![image](https://user-images.githubusercontent.com/22395934/110334962-af3d9400-8066-11eb-9b70-68ed45296fdb.png)



#### 참조: https://imasoftwareengineer.tistory.com/100
