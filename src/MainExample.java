public class MainExample {

    public static void main(String[] args) {


/*
        //static 변수는 static 메소드를 통해 접근되도록 권장됨.
        Member.Builder builder = new Member.Builder("sa1341", "junyoung");
        builder.age(28)
                .hobby("soccer");
*/
        //각 줄마다 builder를 타이핑 하지 않아도 되어 편리합니다.
        Member member = new Member.Builder("sa1341", "junyoung") // 필수값 입력
                .age(28)
                .hobby("농구")
                .build(); // build() 메소드가 객체를 생성해 돌려줍니다.


        System.out.println(member.getId());
        System.out.println(member.getName());
        System.out.println(member.getAge());
        System.out.println(member.getHobbyy());

    }
}
