```java
public class CarNumberCheckingService {
    public static void main(String[] args) {
        CarNumberCheckingService carNumberCheckingService = new CarNumberCheckingService();
        String carNum = "서울27가8421";
        boolean result = carNumberCheckingService.isRightCarNumFormat(carNum);
        System.out.println(result);
    }

    private boolean isRightCarNumFormat(String carNum) {
        String[] format = {"ch", "ch", "num", "num", "ch", "num", "num", "num", "num"};
        if (format.length != carNum.length()) return false;

        if (checkType(carNum, format)) return false;

        return true;
    }

    private boolean checkType(String carNum, String[] format) {
        for (int i = 0; i < format.length; i++) {
            String type = format[i];

            if (type.equalsIgnoreCase("ch")) {
                if (Character.isDigit(carNum.charAt(i))) return true;
            }

            if (type.equalsIgnoreCase("num")) {
                if (!Character.isDigit(carNum.charAt(i))) return true;
            }
        }
        return false;
    }
}
```
