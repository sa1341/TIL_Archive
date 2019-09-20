public class Member {

    private final String id;
    private final String name;
    private final int age;
    private final String hobbyy;

    public static class Builder {

        private final String id;
        private final String name;
        private int age;
        private String hobby;


        public Builder(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder age(int age){
            this.age = age;
            return this;
        }

        public Builder hobby(String hobby){
            this.hobby = hobby;
            return this;
        }

        public Member build() {
            return new Member(this);
        }

    }


    public Member(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.age = builder.age;
        this.hobbyy = builder.hobby;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getHobbyy() {
        return hobbyy;
    }



}



