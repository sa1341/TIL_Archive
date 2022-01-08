# Elasticsearch 클러스터 구성

먼저, Elasticsearch에 개념부터 정리하면 Apache Lucene(아파치 루씬) 기반의 Java 오픈소스 분산 검색 엔진입니다. 

Elasticsearch는 역 색인(Inverted Index)를 사용합니다. ES는 `문서 -> 내용`으로 저장하는 것 외에 `단어 -> 문서`로 찾을 수 있는 테이블을 추가로 만듭니다. 이렇게하면 특정 단어가 포함된 문서를 빠르게 찾을 수 있습니다.

주로 실무에서도 많이 쓰이는 `ELK(Elasticsearch/ Logstash / Kibana)` 스택으로 사용되기도 합니다.

- Logstash: 다양한 소스(DB, csv 파일 등)의 로그 또는 트랜잭션 데이터를 수집, 집계, 파싱하여 Elasticsearch로 전달 합니다.

- Elasticsearch: Logstash로부터 받은 데이터를 검색 및 집계를 하여 필요한 관심있는 정보를 획득합니다.

- Kibana: Elasticsearch의 빠른 검색을 통해 데이터를 시각화 및 모니터링을 담당합니다.

![image](https://user-images.githubusercontent.com/22395934/148631213-b6d443ff-c83c-4352-addd-7d3b9153008e.png)

## Elasticsearch와 관계형 DB 비교

아래 표는 흔히 사용하고 있는 관계형 데이터베이스는 Elasticsearch에서 아래와 같이 대응시킬 수 있습니다.

| 관계형 데이터베이스 | Elasticsearch |
|:-------:|:-------:|
| Database | Index |
| Table | Type |
| Row | Document |
| Index | Analyze |
| Primary key | _id |
| Schema | Mapping |
| Physical partition | Shard |
| Logical partition | Route |
| Relational | Parent/Child, Nested|
| SQL | Query DSL |
 

## Elasticsearch 클러스터 용어정리

### Cluster(클러스터)

클러스터란 Elasticsearch에서 가장 큰 시스템 단위를 의미합니다. 최소 하나 이상의 노드로 이루어진 노드들의 집합입니다.

서로 다른 클러스터는 데이터 접근, 교환을 할 수 없는 독립적인 시스템으로 유지되며, 여러 대의 서버가 하나의 클러스터를 구성할 수 있고, 한 서버에 여러개의 클러스터가 존재할 수도 있습니다.

### node(노드)

노드는 Elasticsearch를 구성하는 하나의 단위 프로세스를 의미합니다. 그 역할에 따라 `Master-eligible`, `Data`, `Ingest`, `Tribe` 노드로 구분할 수 있습니다.

### Master-eligible

클러스터를 제어하는 마스터로 선택할 수 있는 노드를 말합니다. master 노드는 아래와 같은 역할을 합니다.

- 인덱스 생성, 삭제
- 클러스터 노드들의 추적, 관리
- 데이터 입력 시 어느 샤드에 할당할 것인지

### Data node

데이터와 관련된 `CRUD` 작업과 관련있는 노드입니다. 이 노드는 CPU, 메모리 등 자원을 많이 소모하므로 모니터링이 필요하며, master 노드와 분리되는 것이 좋습니다.

### Ingest node

데이터를 변환하는 등 사전 처리 파이프라인을 실행하는 역할을 합니다.

### Coordination only node

data node와 master-eligible node의 일을 대신하는 이 노드는 대규모 클러스터에서 큰 이점이 있습니다. 즉 로드벨런서와 비슷한 역할을 한다고 봅니다. 

### Shard(샤드)

샤딩은 데이터를 분산해서 저장하는 방법을 의미합니다. 즉, Elasticsearch에서 스케일 아웃을 위해 index를 여러 shard로 쪼갠 것입니다. 기본적으로 1개가 존재하며, 검색 성능 향상을 위해 클러스터의 샤드 갯수를 조정하는 튜닝을 하기도 합니다.

![Untitled Diagram drawio (2)](https://user-images.githubusercontent.com/22395934/148633756-113e09f2-d5ea-4b5f-9dda-4265da95927b.png)

샤드를 사용하는 이유는 예를 들어 문서가 1~100번까지 있다고 하면, 이 문서를 여러 곳에 쪼개어 저장할 경우 여러 곳에서 한번에 검색되도록하면 컴퓨팅 리소스를 분산해서 사용하기 때문에 검색속도 향상시킬 수 있는 장점이 있습니다.

또한 이렇게 구성하면 노드를 늘리고 샤드도 늘려서 손쉽게 `scale out`을 할 수 있습니다.

### Replica(레플리카)

샤드는 노드끼리 서로 다른 데이터를 저장하는 거지만, 레플리카는 동일한 데이터를 저장합니다. 

Replica는 또 다른 형태의 shard라고 할 수 있습니다. 노드를 손실했을 경우 데이터의 신뢰성을 위해 샤드를 복제하는 것입니다. 따라서 replica는 서로 다른 노드에 존재할 것을 권장하고 있습니다. 

![Untitled Diagram drawio (3)](https://user-images.githubusercontent.com/22395934/148633816-b16235dc-7858-47cc-88fb-56943678a4db.png)

위의 이미지처럼 특정 노드에 문제가 발생하여 내려갔을 때 해당 노드가 포함하고 있던 데이터를 다른 곳에도 저장을 하고 있습니다. 따라서 서비스가 계속 가능합니다.

## Elasticsearch 클러스터 구성하기

EC2 인스턴스 4개를 구성하여 Elasticsearch 클러스터를 구성해보겠습니다.

먼저, 각 ec2 인스턴스에 ssh로 접속하여 docker를 설치합니다.

[도커설치방법](https://github.com/sa1341/TIL/blob/master/docker/%EC%8A%A4%ED%94%84%EB%A7%81%20%EB%B6%80%ED%8A%B8%20%EA%B8%B0%EB%B0%98%20%EB%8F%84%EC%BB%A4%ED%8C%8C%EC%9D%BC%20%EC%83%9D%EC%84%B1%ED%95%98%EA%B8%B0.md)

ES에서는 가상 메모리를 많이 필요하기 때문에 각 ec2 인스턴스마다 가상 메모리 사이즈를 늘려줘야 합니다.

```java
// 가상메모리 사이즈 늘리기
sudo sysctl -w vm.max_map_count=262144
```


### Elasticsearch 실행

1번 노드에서만 ES 클러스터 구성을 위해 도커 네트워크를 생성합니다.
그리고 es01, es02, es03, es04 노드들의 클러스터를 생성하기 위한 아래 명령어들을 입력합니다.

```java
// 도커 네트워크 생성
docker network create somenetwork

// 1번 노드에서만 실행
docker run -d --name elasticsearch --net somenetwork -p 9200:9200 -p 9300:9300 -e "discovery.seed_hosts=172.31.36.213,172.31.43.4,172.31.40.131" -e "node.name=es01" -e "cluster.initial_master_nodes=es01,es02,es03,es04" -e "network.publish_host=172.31.47.200" elasticsearch:7.10.1

// 2번 노드 
docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.seed_hosts=172.31.47.200,172.31.43.4,172.31.40.131" -e "node.name=es02" -e "cluster.initial_master_nodes=es01,es02,es03,es04" -e "network.publish_host=172.31.36.213" elasticsearch:7.10.1

// 3번 노드
docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.seed_hosts=172.31.47.200,172.31.36.213,172.31.40.131" -e "node.name=es03" -e "cluster.initial_master_nodes=es01,es02,es03,es04" -e "network.publish_host=172.31.43.4" elasticsearch:7.10.1

//4번 노드
docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.seed_hosts=172.31.47.200,172.31.36.213,172.31.43.4" -e "node.name=es04" -e "cluster.initial_master_nodes=es01,es02,es03,es04" -e "network.publish_host=172.31.40.131" elasticsearch:7.10.1
```

도커 환경변수 설정하는 부분에서 `discovery.seed_hosts`는 현재 노드의 ip를 제외한 나머지 노드들의 ip를 명시해줘야하고, 반대로 `network.publish_host`는 현재 노드의 ip로 설정해야 합니다. 

이렇게 각 ES 노드 컨테이너를 실행시키고 http://{ES 노드 외부ip}:9200로 접속해보면 아래와 같이 노드 정보가 나옵니다.

![스크린샷 2022-01-08 오후 4 51 01](https://user-images.githubusercontent.com/22395934/148637921-f8c9f5b0-2201-44a2-86b2-beb6b2166a76.png)

하지만 보기 불편하기 때문에 크롬에서 제공해주는 ElasticSearch Head 플러그인을 설치 후 이곳에서 ES 클러스터 노드들의 상태를 확인 합니다.

![image](https://user-images.githubusercontent.com/22395934/148637964-abe220a9-4cf3-4a24-8fdb-e4d6a3d5b8ea.png)

## ES와 DB의 비교

ES는 DB의 상위호환이진 않습니다. 둘다 데이터를 저장하는 공통점이 있지만, 목적과 방식이 조금 다릅니다. 

ES는 실시간 처리가 불가합니다. 어플리케이션에서 DB 테이블에 INSERT를 하고 바로 조회가 가능하지만 ES는 내부적으로 처리하는 로직이 있어서 조회하는데 딜레이가 어느정도 있습니다. 한마디로 실시간 처리는 불가합니다.

두번째로는 트랜잭션과 롤백을 제공하지 않습니다. ES는 노드에 데이터를 분산 저장하기 때문입니다. 

마지막으로 ES는 데이터를 실제로 업데이트 하지 않습니다. 물론 업데이트 API를 제공하지만 내부적으로는 데이터를 삭제 후 INSERT를 하도록 되어있습니다.


#### 참조 사이트: https://victorydntmd.tistory.com/308
