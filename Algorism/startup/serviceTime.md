## 운행 시간 체킹 서비스

```java
import java.util.*;

public class CallBusCheckingService {

    public static void main(String[] args) {
        CallBusCheckingService carNumberCheckingService = new CallBusCheckingService();
        // (0, 1, 2, 3, 4, 5, 6) / 0: 월요일, 6: 일요일
        int day = 0;
        // 0 ~ 23 시간을 의미
        int hourOfDay = 1;
        boolean result = carNumberCheckingService.isServiceTime(day, hourOfDay);
        System.out.println(result);
    }


    private boolean isServiceTime(int day, int hourOfDay) {

        boolean result = false;
        Day[] days = Day.values();

        for (int i = 0; i < days.length; i++) {
            if (day == days[i].getDay()) {
                List<Integer> operationTime = days[i].getOperationTime();
                result = checkServiceTime(hourOfDay, operationTime);
            }
        }

        return result;
    }

    private boolean checkServiceTime(int hourOfDay, List<Integer> operationTime) {

        for (int i = 0; i < operationTime.size(); i++) {
            int serviceHour = operationTime.get(i);
            if (hourOfDay == serviceHour) {
                return true;
            }
        }

        return false;
    }
}

enum Day {
    MONDAY(0, Arrays.asList(1, 2, 3, 4)),
    TUESDAY(1, Arrays.asList(23, 0, 1, 2, 3, 4)),
    WEDNESDAY(2, Arrays.asList(22, 23, 0, 1, 2, 3, 4)),
    THURSDAY(3, Arrays.asList(23, 0, 1, 2, 3, 4)),
    FRIDAY(4, Arrays.asList(23, 0, 1, 2, 3, 4)),
    SATURDAY(5, Arrays.asList(23, 0, 1, 2, 3, 4)),
    SUNDAY(6, Arrays.asList(23, 0, 1, 2, 3, 4));

    private final int day;
    private final List<Integer> operationTime;

    Day(int day, List<Integer> operationTime) {
        this.day = day;
        this.operationTime = operationTime;
    }

    public int getDay() {
        return this.day;
    }

    public List<Integer> getOperationTime() {
        return operationTime;
    }
}
```
