# Kafka Consumer 메시지 Polling

아래 예제코드는 Kafka로부터 동기식으로 메시지를 Polling하는 Consumer 예제코드입니다.

```java
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerSync {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "peter-kafka01.foo.bar:9092, peter-kafka02.foo.bar:9092, peter-kafka03.foo.bar:9092");
        props.put("group.id", "peter-consumer01");
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.reset", "latest");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeSerializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeSerializer");

        Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList("peter-basic01"));

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(1000);
                for (ConsumerRecord<String, String> record: records) {
                    System.out.printf("Topic: %s, Partition: %d, Offset: %d, Key: %s, Received Message: %s \n",
                        record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }
                consumer.commitSync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}
```

`consumer.commitSync()`는 동기식으로 가져옵니다. 만약 비동기식으로 가져올려면 `consumer.commitAsync()`를 호출하면 됩니다.
둘의 차이점은 commitAsync()는 commitSync()와 달리 오프셋 커밋을 실패하더라도 재시도하지 않습니다.

그 이유는 만약 총 10개의 메시지가 있고, 오프셋 1번부터 10번까지 순차적으로 커밋을 한다고 가정하면, 1번 오프셋의 메시지를 읽은 뒤 1번 오프셋을 비동기 커밋
2번, 3번, 4번 오프셋을 각각 읽은 뒤 비동기 커밋이 실패, 5번 오프셋의 메시지를 읽은 뒤 비동기 커밋 성공을 한다고 가정하겠습니다.

만약 비동기 커밋의 재시도로 인해 2번 오프셋의 비동기 커밋이 성공하게 되면, 마지막 오프셋이 2번으로 변경될 것입니다. 만약 현재의 컨슈머가 종료되고 다른 컨슈머가 이어서 작업을 진행한다면
다시 3번 오프셋부터 메시지를 가져오게 될 것이고, 메시지가 중복될 것입니다. 비동기 커밋 재시도로 인해 천 단위, 만 단위의 오프셋이 커밋돼버리면 그 수만큼 중복이 발생하게 됩니다.

> 컨슈머는 반드시 하나의 컨슈머 그룹에 속해 있습니다. 컨슈머 그룹의 컨슈머들은 토픽의 파티션들과 1:1 매핑이 됩니다. 만약 같은 컨슈머 그룹안에서 특정 컨슈머가 장애로 인해 다운된다면 다른 컨슈머가 리밸런싱을 하게됩니다.



> 실전 카프카 개발부터 운영까지
