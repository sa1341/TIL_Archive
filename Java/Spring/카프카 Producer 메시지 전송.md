# 카프카 Proudcer와 Consumer 예제코드

아래는 Apache Kafka Producer를 이용한 동기식 메시지 전송 예제 입니다.

## Producer 동기식 전송 예제코드

```java
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

public class ProducerSync {
    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "peter-kafka01.foo.bar:9092, peter-kafka02.foo.bar:9092, peter-kafka03.foo.bar:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        try {
            for (int i = 0; i < 3; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>("peter-basic01",
                    "Apache Kafka is a distributed streaming platform - " + i);
                RecordMetadata metadata = producer.send(record).get();
                System.out.printf("Topic: %s, Partition: %d, Offset: %d, Key: %s, Received Message: %s \n",
                    metadata.topic(), metadata.partition(), metadata.offset(), record.key(), record.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
```

- bootstrap.servers: 클라이언트가 카프카 클러스터에 처음 연결하기 위한 호스트와 포트 정보를 나타냅니다. 카프카 클러스터는 클러스터 마스터라는 개념이 없으므로, 클러스터 내 모든 서버가 클라이언트의 요청을 받을 수 있습니다.
- key.serializer, value.serializer: 메시지 키와 벨류는 문자열 타입이므로 카프카의 기본 StringSerializer를 지정합니다.

위의 kafka Producer 예제 코드는 동기 전송의 방법으로 프로듀서는 메시지를 보내고 send() 메소드의 Future 객체를 리턴하며, get() 메소드를 이용해 Future를 기다린 후 send()가 성공했는지 실패했는지 확인합니다.
RecordMetadata를 읽어 들여 파티션과 오프셋의 정보를 확인할 수 있으며, 이 방법으로 메시지 전달의 성공여부를 파악할 수 있습니다.


## Producer 비동기식 전송 예제코드

```java
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class ProducerCallback implements Callback {

    private ProducerRecord<String, String> record;

    public ProducerCallback(ProducerRecord<String, String> record) {
        this.record = record;
    }

    @Override
    public void onCompletion(RecordMetadata metadata, Exception e) {
        if (e != null) {
            e.printStackTrace();
        } else {
            System.out.printf("Topic: %s, Partition: %d, Offset: %d, Key: %s, Received Message: %s \n",
                metadata.topic(), metadata.partition(), metadata.offset(), record.key(), record.value());
        }
    }
}

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ProducerAsync {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "peter-kafka01.foo.bar:9092, peter-kafka02.foo.bar:9092, peter-kafka03.foo.bar:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        try {
            for (int i = 0; i < 3; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>("peter-basic01",
                    "Apache Kafka is a distributed streaming platform - " + i);
                producer.send(record, new ProducerCallback(record));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
```

콜백을 사용하여 비동기 메시지 전송방식을 구현한 예제코드입니다. 콜백을 사용하기 위해서 `org.apache.kafka.clients.producer.Callback`을 구현하는 클래스가 필요합니다.
카프카가 오류를 리턴하면 onCompletion()은 예외를 갖게 되며, 실제 운영환경에서는 추가적인 예외처리가 필요합니다.

동기 전송방식과 다르게 비동기 전송방식은 빠른 전송이 가능하며, 메시지 전송이 실패한 경우라도 예외를 처리할 수 있어서 에러로그 등에 기록할 수도 있습니다.


> 참조: 실전 카프카 개발부터 운영까지 
