## 싱글톤

싱글톤 패턴은 GoF 디자인 패턴 중 하나로 Java의 힙 메모리 영역에서 오직 유일무이하게 존재하는 객체 인스턴스를 의미합니다.
싱글톤을 사용하면 오로지 힙 메모리 영역에서 하나만 존재하기 때문에 메모리를 줄일 수 있는 장점이 있지만, 잘못 사용하면 심각한 오류와 성능 이슈를 야기할 수도 있습니다.

```java
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.locks.ReentrantLock;

public class SingleTonExample {

    private static final Logger logger = LoggerFactory.getLogger(SingleTonExample.class);
    private static SingleTonExample singleTonExample;
    private int maxPrice = 0;

    {
        logger.info("default constructor!!");
    }


    static {
        if (singleTonExample == null) {
            synchronized (SingleTonExample.class) {
                if (singleTonExample == null) {
                    singleTonExample = new SingleTonExample();
                }
            }
        }
    }

    private SingleTonExample() {
        logger.info("basic Constructor");
    }

    public static SingleTonExample getInstance() {
        return singleTonExample;
    }

    @Test
    public void bid() throws Exception {

        //given
        SingleTonExample singleTonExample = new SingleTonExample();

        ReentrantLock reentrantLock = new ReentrantLock();
        try {
            reentrantLock.lock();
            // do something
            
        } finally {
            reentrantLock.unlock();
        }
        //when

        //then
    }
}
```

위의 싱글톤 생성하는 예제코드는 일반적으로 많이 쓰이는 방식으로 클래스가 로딩되는 시점에 static 블록에서 인스턴스를 생성하게 됩니다. 이것을 double checked라고 부르는데 이렇게 하면 쓰레드들이 경합을 벌일 때
해당 객체에 lock을 걸어서 여러번 인스턴스를 메모리에 생성하는 것을 막을 수 있습니다. @Test 코드를 보면 ReentrantLock 객체를 사용하고 있는데 이건 synchronized 처럼 특정 영역에 lock을 걸어서 사용하지만
다른점은  명시적으로 lock을 걸기 때문에 더 직관적이다는 장점이 있습니다. 쓰레드 프로그래밍은 항상 어렵지만, 잘 사용하면 효율적이고 성능을 더 높일 수 있는 양날의 검이 될 수 있다고 생각합니다. 
 


