# QueryDSL 서브쿼리 사용하기
  
QueryDSL에서 스칼라 서브쿼리를 사용하는 방법에 대해서 간단하게 예제 코드를 작성해보았습니다.
기본적으로 같은 테이블에서 조회할 경우 Q 클래스의 생성자 파라미터명을 다르게 줘야합니다. 저는 `memberSub`라는 alias를 주었습니다.
서브쿼리를 사용하기 위해서는 `JPAExpressions` 클래스를 사용하여 서브쿼리를 작성하면 됩니다. 
  
```java
 @Test
 public void 회원의_평균연령을_구한다() throws Exception {
     QMember memberSub = new QMember("memberSub");

     List<Tuple> result = queryFactory
                .select(member.name,
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub))
                 .from(member)
                 .fetch();

        
      for (Tuple tuple: result) {
            System.out.println("name = " + tuple.get(member.name));
            System.out.println("age = " + tuple.get(JPAExpressions.select(memberSub.age.avg())
                  .from(memberSub)));
       }
}
```

프로젝션으로 조회할때 기본적으로 2개이상 컬럼을 조회하는 경우 튜블로 결과반환을 하여 위의 코드처럼 사용할 수 있습니다.
