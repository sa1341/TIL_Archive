# HashMap, TreeMap 그리고 LinkedHashMap의 차이

Map은 key-value 형식의 비정형 데이터를 저장할 수 있는 구조입니다. 

아래 클래스들은 Map 인터페이스의 구현체로입니다.

- HashMap
- HashTable
- LinkedHashMap

## 1. HashMap

HashMap은 실무에서 가장 많이 쓰는 Collection 객체중 하나입니다. 내부적으로 Entry<K, V> {} Entry Array로 구성되어 있습니다. Array의 index를 Hash 함수를 통해 계산합니다.

Hash 값에 의해 특정 Bucket에 담기는데, 만약 Hash 값이 같다면 Bucket에 List로 연결됩니다. Entry 안에는 next Entry를 지정하는 변수가 존재합니다.
`Hash 값을 이용하여 저장하기 때문에 순서를 보장하지는 않습니다.` get() 메서드는 Hash 값으로 해당 Array에 바로 접근이 가능하기 때문에 시간복잡도는 O(1)으로 빠릅니다.

## 2. TreeMap

TreeMap은 HashMap과 다르게 Entry가 Tree 구조로 저장되어 있는 것이 특징입니다.

Tree 구조 특성상 특정 Entry를 접근하려면 O(logn) 성능을 보입니다.

TreeMap은 SortedMap 인터페이스를 구현하고 있어 Key 값을 기준으로 정렬이 되어 있는데, 이는 Comparator를 구현하여 정렬 순서를 변경할 수 있습니다.

## 3. LinkedHashMap

HashMap과 대부분 동일합니다. 하지만 Entry 내부에 Before, After Entry가 저장되어 있는 것이 특징입니다.

`이를 통해서 put() 메서드로` 순서를 보장할 수 있게 됩니다.

## 4. HashTable

Map 인터페이스를 구현한 클래스로 중복을 허용하지 않습니다. 또한 동기화(Synchronized)처리가 되어 있어 Thread-safe 하다는 특징이 있습니다.

HashMap과 다르게 Key 값으로 null을 허용하지 않는 것이 특징 중 하나입니다.

하지만 대부분의 Java 개발자는 HashMap을 HashTable보다 선호합니다. get() 메서드 성능이 O(1)로 빠르기 때문입니다. 특별한 이유가 없다면 get() 성능이 빠른 HashMap을 사용해야합니다. 순서 보장이 필요하다면 TreeMap이나 LinkedHashMap을 고려해 볼 수도 있습니다. 만약 동기화 이슈가 있다면 HashTable도 고려할 수 있습니다. 다만 동기화 수준에 따라 ConcurrentHashMap을 사용해도 성능 상 효과를 볼수 있을 것 입니다.


#### 참조 블로그: https://tomining.tistory.com/168




