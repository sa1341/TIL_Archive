오늘 주문 목록을 조회하는 부분에서 아래와 같은 에러가 발생하였습니다.

## 에러 로그 내용

- 2021-06-18 13:53:53.669 ERROR 25610 --- [nio-8080-exec-1] c.d.s.c.ExceptionHandlerController       : Type definition error: [simple type, class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor]; nested exception is com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) (through reference chain: com.dalchaebi.shop.common.dto.ResponseDto["data"]->java.util.ArrayList[0]->com.dalchaebi.shop.product.Product["productInfo"]->com.dalchaebi.shop.product.ProductInfo["productSpecificInfo"]->com.dalchaebi.shop.product.ProductSpecific$HibernateProxy$dMDOpVsT["hibernateLazyInitializer"])
2021-06-18 13:53:53.675  WARN 25610 --- [nio-8080-exec-1] .m.m.a.ExceptionHandlerExceptionResolver : Resolved [org.springframework.http.converter.HttpMessageConversionException: Type definition error: [simple type, class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor]; nested exception is com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) (through reference chain: com.dalchaebi.shop.common.dto.ResponseDto["data"]->java.util.ArrayList[0]->com.dalchaebi.shop.product.Product["productInfo"]->com.dalchaebi.shop.product.ProductInfo["productSpecificInfo"]->com.dalchaebi.shop.product.ProductSpecific$HibernateProxy$dMDOpVsT["hibernateLazyInitializer"])]

## 원인 분석하기

디버깅을 해보니까 상품과 재고 엔티티의 연관관계가 @OneToOne이였고, 연관관계의 주인은 재고 엔티티였었습니다. 기본적으로 @OneToOne 연관관계를 가질 경우 연관관계의 주인이 아닌 곳에서
조회를 할 경우에는 Lazy여도 EAGER로 조회를 한다고 합니다.

@JsonIgnoreProperties 어노테이션은 지연로딩으로 설정되어 있는 연관관계의 엔티티를 json 포맷에 제외시키는 어노테이션입니다. 사실 Product와 ProductSpecificInfo 엔티티는 @OneToOne 관계이고, 
Product가 일방적으로 참조하고 있는 단방향인데. 왜 이런 이슈가 발생했는지 이해가 안갑니다. 그 전에는 ProductStock이 생기기 전에 잘되었다면... ProductStock 엔티티와 연관이 있을거라 추정했지만, 
실제로 디버깅을 할 경우 ProductStock은 정상적으로 DB로부터 로드되어 잘 참조하고 있습니다. 
Product와 ProductStock은 @OneToOne 연관관계를 가졌고, 연관관계의 주인은 ProductStock입니다. 기본적으로 일대일 연관관계 주인이 아닌 곳에서 조회를 할경우에는 Lazy로 설정되어있어도 EAGER방식으로 조회한다고 합니다.  즉, Product에서  ProductStock을 조회하면 Lazy여도 EAGER로 가져온다고 합니다.
아래 블로그를 참조하였을 때 단방향으로 주인이 지연처리로 설정되어 있는 경우 Jackson에서 직렬화 하지 못한다고 하는데.. 사실 이것도 그렇게 이해되지는 않습니다. 


## 해결방법 

1. Product 엔티티가 참조하고 있는 ProductSpecificInfo 엔티티에 아래 어노테이션을 적용함. 

```java
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
```

ProductStock 엔티티에 @JsonIgnore 옵션 적용 

2. ProductStock과 ProductSpecificInfo를 둘다 LAZY Loading일 경우에 위의 에러가 발생함. ProductStock을 json 포맷에 제외시키고 ProductSpecifidInfo 엔티티를 EAGER로 조회할 경우에는 정상적으로 처리가 되는 것을 확인하였습니다. ProductStock에 @JsonIgnore를 적용하지 않을 경우에는 ProductSpecifidInfo 엔티티를 EAGER로 조회해도 익셉션이 발생... 원인 파악이 정확하게 되진 않지만 Product가 참조하고 있는 두 엔티티가 문제가 되는것은 확실해보입니다. 둘다 공통적으로 OneToOne 관계임.

3. 서비스단에서 Product 도메인을 바로 리턴하지말고 DTO로 변환하여 필요한 데이터만 매핑하여 앱으로 리턴해주는 방법이 있습니다. 사실 DTO로 넘겨주는게 API 스펙변경에 더 유연하게 대처할 수 있어서 더 좋은 방법이라고 생각합니다.


#### 참조 블로그: [https://gist.github.com/syakuis/3ff066c040d21b18daa62857bbf937e9](https://www.notion.so/3ff066c040d21b18daa62857bbf937e9)
[https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=rorean&logNo=221553310279](https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=rorean&logNo=221553310279)
