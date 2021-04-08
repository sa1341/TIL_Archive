# Java 날짜 및 시간 알고리즘

연도, 월, 일을 입렫받으면 해당 날짜에 매칭되는 요일을 출력하는 예제 코드를 작성하였습니다.

```java
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class JavaDateTimeExample {
    public static void main(String[] args) throws Exception {

        int year = 2015;
        int day = 5;
        int month = 8;

        try {
            String inputDate = String.format("%d%02d%02d", year, month, day);
            System.out.println(inputDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = dateFormat.parse(inputDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayNum = calendar.get(Calendar.DAY_OF_WEEK);
            String result = "";

            switch (dayNum) {
                case 1:
                    result = "SUNDAY";
                    break;
                case 2:
                    result = "MONDAY";
                    break;
                case 3:
                    result = "TUESDAY";
                    break;
                case 4:
                    result = "WEDNESDAY";
                    break;
                case 5:
                    result = "THURSDAY";
                    break;
                case 6:
                    result = "FRIDAY";
                    break;
                case 7:
                    result = "SATURDAY";
                    break;

            }
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
