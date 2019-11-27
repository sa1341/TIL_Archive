
# JPA 사실에 대한 오해

NHN  FORWARD 워크숍에 참가하여 신동민 개발자님이 발표한 JPA 사실에 대한 오해에 대해서 듣고 실습을 통해서 의미있는 유익한 시간을 보냈습니다.


JPA에 대해서 흔히 잘못알고 있는 사실 중 하나가 엔티티와의 연관관계는 단방향이면 매핑이 끝나는 것은 맞지만 성능상 일대다 단방향 관계를 가질때 자식엔티티를 영속석 전이를 통해 저장하게 되면 트랜잭션 커밋시점에 플러시가 호출되어 insert 쿼리가 발생 한 후에 자식 엔티티에 대해서 update 문이 자식엔티티 수만큼 수행되기 때문에 성능상의 문제가 발생합니다. 

다음은 `OrderDetail -> Order` 단방향 연관관계를 가질때 발생하는 쿼리를 확인해 보겠습니다.

## @ManyToOne 단방향일 경우

```java
// Order(주문정보) 엔티티
@Getter
@Setter
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_dt")
    private LocalDateTime orderDate;
}
```

```java
// OrderDetails(주문내역) 엔티티
@Getter
@Setter
@Entity
@Table(name = "OrderDetails")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "order_detail_id")
    private Long orderDetailId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;

    private String type;

    private String description;

}
```

간단한 테스트를 위해서 lombok을 이용하여 setter를 넣었지만 실환경에서 영속성을 이용한 수정 과정에서 공유 참조를 하여 실수로 값 변경이 잘못되면 큰 재앙을 초래하기 때문에 `setXxx()` 메소드를 써야할지 고민해야 된다고 생각합니다.



```java
// 실행 클래스
@Bean
CommandLineRunner onStartUp(OrderService orderService) {
    return args -> {
        orderService.createOrderWithDetails();
    };
}

@Service
public class OrderService {

    private final OrderDetailRepository orderDetailRepository;


    public OrderService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }


    @Transactional
    public void createOrderWithDetails() {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setOrder(order);
        orderDetail1.setType("type1");
        orderDetail1.setDescription("order1-type1");

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setOrder(order);
        orderDetail2.setType("type2");
        orderDetail2.setDescription("order1-type2");

        orderDetailRepository.saveAll(Arrays.asList(orderDetail1, orderDetail2));
    }
```

`CommandLineRunner`를 스프링 빈으로 등록하여 웹 애플리케이션 실행 시에 Order 엔티티와 OrderDetail 엔티티의 영속성에 저장할때 자식 엔티티인 OrderDetail에 영속성 전이를 설정했기 때문에 자식 엔티티를 영속성 컨텍스트에 저장하면 자동으로 부모 엔티티도 영속화 됩니다. 실제 트랜잭션이 커밋되는 시점에 플러시가 호출되어 insert 쿼리가 부모 1, 자식 2개에 대해서만 데이터베이스에 보내는 것을 알 수가 있습니다.

```java
// 영속성 전이 설정
@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;
```


## 실행결과
![스크린샷 2019-11-27 오후 9 15 28](https://user-images.githubusercontent.com/22395934/69722684-1594f080-115b-11ea-9c3f-41ae9792f832.png)


이제 `일대다(@OneToMany)` 단 방향 연관관계일 때 영속성 컨테스트에 저장 시 발생하는 쿼리 수를 확인 해보겠습니다.

```java
@Getter
@Setter
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_dt")
    private LocalDateTime orderDate;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
}


@Getter
@Setter
@Entity
@Table(name = "OrderDetails")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "order_detail_id")
    private Long orderDetailId;

    private String type;
    private String description;

}

@Transactional
public void createOrderWithDetails() {
    Order order = new Order();
    order.setOrderDate(LocalDateTime.now());
    orderRepository.save(order);

    OrderDetail orderDetail1 = new OrderDetail();
    orderDetail1.setType("type1");
    orderDetail1.setDescription("order1-type1");

    OrderDetail orderDetail2 = new OrderDetail();
    orderDetail2.setType("type2");
    orderDetail2.setDescription("order1-type2");

    order.getOrderDetails().add(orderDetail1);
    order.getOrderDetails().add(orderDetail2);
}
```

위의 코드를 보면 일대다 단 방향 연관관계를 가질 경우에는 Order 엔티티에서 연관관계의 주인이지만 실제로 JPA에서 영속성 저장 후 insert 쿼리를 보면 외래키는 다에 해당하는 OrderDetail 테이블에 들어가는 것을 확인할 수 있습니다.
그렇기 때문에 플러시를 호출하면 OrderDetail 엔티티에 대한 UPDATE 쿼리가 발생하기 때문에 성능적으로 좋지 않습니다.

## 일대다(@OneToMany) 단 방향 연관관계 실행 결과
![스크린샷 2019-11-27 오후 9 41 18](https://user-images.githubusercontent.com/22395934/69724173-ac16e100-115e-11ea-9b84-4b094328f50a.png)

실행 결과를 보면 insert 쿼리 3개, UPDATE 쿼리 2개가 발생한 것을 확인 할 수가 있습니다... 만약 자식 엔티티의 수가 더 많았다면 자식 엔티티 수 만큼 쿼리가 데이터베이스에 전송될 것 입니다.

그럼 이제 오해를 풀기 위해서  일대다(@ManyToOne) 양 방향 연관관계로 설절 후에 영속성 컨텍스트에 저장 시 발생하는 쿼리를 확인해 보겠습니다.

```java
@Getter
@Setter
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_dt")
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
}


@Getter
@Setter
@Entity
@Table(name = "OrderDetails")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "order_detail_id")
    private Long orderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private String type;
    private String description;

}


@Transactional
public void createOrderWithDetails() {
    Order order = new Order();
    order.setOrderDate(LocalDateTime.now());

    OrderDetail orderDetail1 = new OrderDetail();
    orderDetail1.setOrder(order);
    orderDetail1.setType("type1");
    orderDetail1.setDescription("order1-type1");

    OrderDetail orderDetail2 = new OrderDetail();
    orderDetail2.setOrder(order);
    orderDetail2.setType("type2");
    orderDetail2.setDescription("order1-type2");

    order.getOrderDetails().add(orderDetail1);
    order.getOrderDetails().add(orderDetail2);

    orderRepository.save(order);
}
```
 
 일대다 양방향 연관관계 매핑 시 영속성 컨텍스트에 저장을 하면 아래 실행 결과처럼 다대일 단방향 연관관계처럼 insert 쿼리가 3개만 나가는 것을 확인 할 수 있었습니다.

## 일대다(@ManyToOne) 양 방향 연관관계 실행 결과
![스크린샷 2019-11-27 오후 9 48 35](https://user-images.githubusercontent.com/22395934/69724650-b08fc980-115f-11ea-8695-105297875eeb.png)

위의 예제코드를 통해서 `일대다 양방향 매핑`이 `단방향 매핑` 보다는 조금 더 복잡하지만 실환경에서 성능상의 이점을 누릴 수 있다는 사실을 깨닫게 되었습니다. 


# N+1 문제

그 다음 내용은 JPA를 프로젝트를 하면서 가장 많이 겪게되는 `N+1` 문제에 대해서 리뷰하겠습니다.
N + 1 문제는 엔티티에 대해 하나의 쿼리로 N개의 레코드를 가져왔을 때, 연관관계 엔티티를 가져오기 위해 쿼리를 N번 추가적으로 수행하는 문제를 말합니다.

대부분 N+1 문제에 대한 오해는 흔히 글로벌 페치 전략인 `즉시 로딩(EAGER)`에서 발생한다고 많이 알고 있지만 사실 즉시 로딩뿐만 아니라 `지연 로딩(LAZY)`에서도 `N+1`은 존재합니다. 지연 로딩은 조회하려는 엔티티를 가지고 올때 연관된 엔티티는 조회하지 않고 실제로 객체 그래프 탐색으로 연관된 엔티티를 사용하는 시점에 프록시를 통해서 조회를 요청하는 페치 전략 입니다.
따라서 결국에는 영속성 컨텍스트에 해당 엔티티가 존재하지 않는다면 데이터베이스를 통해서 쿼리를 발송해야 하기 때문에 N + 1은 피할 수 없는 문제입니다.

다음과 같이 2가지의 대표적인 해결 방법이 있습니다.

- Fetch Join
- Entity Graph



JPA는 `단건 조회(findOne())`를 할 경우네는 외래키에 null 허용여부에 따라 선택적 관계 또는 필수적 관계를 가지게 됩니다. 이때 JPA는 선택적 관계이면 null이 존재할 수 있다고 가정하여 left outer join으로 연관된 엔티티를 한번에 가져오게 됩니다. 필수적 관계일 경우에는 null을 허용하지 않기 때문에 최적의 성능으로 inner join을 사용하여 연관관계를 가져옵니다. 하지만 JPQL로 작성한 `findAll()`과 같은 여러 건의 엔티티를 조회하는 메소드는 실제로 조회를 했을 때 데이터베이스에서 연관관계를 가진 엔티티를 join을 해서 가져오지만 영속상태로 반환하지 않습니다. 그리고 스프링 Data JPA는 다시 글로벌 패치전략을 보고 즉시 로딩일 경우 데이터베이스에 연관관계를 가진 엔티티를 다시 조회하게 됩니다. 그렇기 때문에 N + 1 문제가 발생하는 겁니다.

위의 설명한 이러한 문제를 해결하기 위해서 join fetch를 사용하여 N + 1 문제가 발생이 안하는지 확인하겠습니다.

```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o" +
    " join o.orderDetails od")
    public List<Order> getAllOrderWithDetails();

}

// 실행결과
@Bean
CommandLineRunner onStartUp(OrderService orderService) {
    return args -> {
        orderService.createOrderWithDetails();
        orderService.getAllOrderWithDetails();
    };
}



Hibernate: select order0_.order_id as order_id1_1_, order0_.order_dt as order_dt2_1_ from orders order0_ inner join order_details orderdetai1_ on order0_.order_id=orderdetai1_.order_id

Hibernate: select orderdetai0_.order_id as order_id4_0_0_, orderdetai0_.order_detail_id as order_de1_0_0_, orderdetai0_.order_detail_id as order_de1_0_1_, orderdetai0_.description as descript2_0_1_, orderdetai0_.order_id as order_id4_0_1_, orderdetai0_.type as type3_0_1_ from order_details orderdetai0_ where orderdetai0_.order_id=?

Hibernate: select orderdetai0_.order_id as order_id4_0_0_, orderdetai0_.order_detail_id as order_de1_0_0_, orderdetai0_.order_detail_id as order_de1_0_1_, orderdetai0_.description as descript2_0_1_, orderdetai0_.order_id as order_id4_0_1_, orderdetai0_.type as type3_0_1_ from order_details orderdetai0_ where orderdetai0_.order_id=?
```

위의 JpaRepository 인터페이스를 확장한 `OrderRepository` 클래스를 만들어서 JPQL로 `@Query` 어노테이션을 적용한 쿼리 메소드를 작성하였습니다. Order 엔티티와 OrderDetail 엔티티를 `join`을 수행하는 메소드이지만 실제로 실행 결과를 보면 select 쿼리문이 데이터베이스에 3번 전송되는 것이 확인됩니다.

만약 OrderDetail 엔티티들이 같은 Order 엔티티를 참조하게 된다면 추가로 OrderDetail 엔티티는 한번만 조회하겠지만 대부분은 아마 각각 다른 Order 엔티티를 참조하기 때문에 이미 처음에 조회된 Order 엔티티들의 수 만큼 참조하는 OrderDetail 엔티티에 대한 조회가 N + 1만큼 이루어 집니다.
이런한 문제를 `join fetch`를 사용하면 아래와 같은 실행 결과가 나옵니다.


# 페치 조인(Fetch Join)
```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o" +
    " join fetch o.orderDetails od")
    public List<Order> getAllOrderWithDetails();
}

// 실행 결과
Hibernate: select order0_.order_id as order_id1_1_0_, orderdetai1_.order_detail_id as order_de1_0_1_, order0_.order_dt as order_dt2_1_0_, orderdetai1_.description as descript2_0_1_, orderdetai1_.order_id as order_id4_0_1_, orderdetai1_.type as type3_0_1_, orderdetai1_.order_id as order_id4_0_0__, orderdetai1_.order_detail_id as order_de1_0_0__ from orders order0_ inner join order_details orderdetai1_ on order0_.order_id=orderdetai1_.order_id
```

짠!.... 각각의 Order 엔티티를 OrderDetail 엔티티가 참조하고 있지만 join fetch로 인해서 한번만 쿼리가 발생하는 것을 확인할 수 가 있습니다.


# Entity Graph 방법

사실 객체 그래프 방법은 오늘 처음 듣게 되었고 처음 실습시간에 사용해봤습니다. 이 객체 그래프 방식은 실제 엔티와의 연관관계가 복잡해질때 어디까지 연관된 엔티티를 조회할지 개발자가 직접 정의해서 사용할 수 있는 방법입니다. 객체 그래프 방법은 도메인애 `@NamedEntityGraphs` 어노테이션을 적용하여 repository에서 도메인에 정의된 @NamedEntityGraphs의 name을 이용하여  @Query 메소드와 함께 사용할 수 있습니다.


Member 엔티티와 MemberDetails 엔티티가 `일대다(@OneToMany)` 양 방향 연관관계를 가지고 있을때 객체 그래프 방법으로 `join`을 하는 예제코드를 살펴보겠습니다.

```java
@NamedEntityGraph(name = "memberWithDetails", attributeNodes = {
        @NamedAttributeNode("details")
})
@Getter
@Setter
@Entity
@Table(name = "Members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "member_id")
    private Long memberId;

    private String name;

    @Column(name = "create_dt")
    private LocalDateTime createDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "member")
    private List<MemberDetail> details = new ArrayList<>();

}


@Getter
@Setter
@Entity
@Table(name = "MemberDetails")
public class MemberDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "member_detail_id")
    private Long memberDetailId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String type;

    private String description;

}

public interface MemberRepository extends JpaRepository<Member, Long> {
    // TODO : @EntityGraph로 설정한 Entity Graph를 이용
    @EntityGraph("memberWithDetails")
    @Query("select m from Member m")
    List<Member> getAllBy();

}
```

위의 코드를 살펴보면 `@NamedAttributeNode("details")`는 Member 엔티티가 참조하고 있는 MemberDetail 엔티티의 컬렉션 객체의 참조 변수를 넣었습니다.
즉, 연관관계를 조회할 때 MemberDetail 엔티티까지 조회하겠다는 뜻입니다.

그리고 MemberRepository에 @Query 어노테이션 안에 있는 쿼리는 Member 엔티티만 조회를 하지만 `@EntityGraph` 어노테이션에서 Member 도메인에 정의 되어있는 `memberWithDetails`를 명시해주었기 때문에 실제로 getAllBy() 메소드를 호출하게 되면 Member와 MemberDetail 엔티티를 조인한 쿼리문을 데이터베이스에 전송하여 조회하게 됩니다.


## 실행결과
```java
Hibernate: select member0_.member_id as member_i1_3_0_, details1_.member_detail_id as member_d1_2_1_, member0_.create_dt as create_d2_3_0_, member0_.name as name3_3_0_, details1_.description as descript2_2_1_, details1_.member_id as member_i4_2_1_, details1_.type as type3_2_1_, details1_.member_id as member_i4_2_0__, details1_.member_detail_id as member_d1_2_0__ from members member0_ left outer join member_details details1_ on member0_.member_id=details1_.member_id
```

여기까지 제가 오늘 NHN 워크숍에서 배운 JPA에 대한 사실과 오해에 대한 발표내용이였습니다. N + 1 문제를 해결하는 방법은 join fetch 방법밖에 몰랐었는데 객체 그래프 방법을 배움으로써 상황에 따라서 객체 그래프 방법을 이용하면 페치 조인 방법처럼 성능을 최적화 할 수 있다고 생각합니다.
