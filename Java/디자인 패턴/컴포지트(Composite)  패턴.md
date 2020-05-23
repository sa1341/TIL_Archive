## 컴포지트(Composite) 패턴

빌딩의 장비들의 전원을 관리하는 제어 프로그램을 개발한다고 합시다. 이 프로그램을 만들기 위해 개별 장비의 전원을 켜고 끄는 기능을 제공하는 인터페이스를 정의하고, 장비 별로 알맞은 콘크리트 클래스를 구현했습니다. 또한, 개별 장비가 아닌 장비들을 하나로 묶어서 관리할 수 있도록 하기 위해 아래 그림처럼 
DeviceGroup 클래스를 추가하였습니다.

![Untitled Diagram (1)](https://user-images.githubusercontent.com/22395934/82339718-4fb7be80-9a29-11ea-84c8-eac272071ee0.png)

위 타입을 이용해서 장비들의 전원을 제어하는 코드는 다음과 같이 Device 타입과 DeviceList 타입을 구분해서 처리할 것입니다.

```java
public class PowerController {

    public void turnOn(Long deviceId) {
        Device device = findDeviceById(Long deviceId);
        device.turnOn();
    }
    // turnGroupOn()과 turnOn()은 개별/그룹 차이를 빼면 동일한 기능입니다.
    public void turnGroupOn(Long groupId) {
        DeviceGroup group = findGroupById(Long groupId);
        group.turnAllOn();
    }
}
```

위 코드의 단점은 PowerController 입장에서 봤을 때 장비나 장비 그룹의 전원을 켜는 동작은 동일한 동작임에도 불구하고 Device와 DeviceGroup을 구분해서 처리해야 한다는 점입니다. 전원 켜고/끄는 기능 외에 소비 전력 측정과 같은 새로운 기능이 추가될 경우 PowerController 클래스에는 turnOn()/turnGroupOn()처럼 거의 동일한 메서드가 추가 됩니다.

거의 동일한 코드가 중복된다는 점은 결국 복잡도를 높여서 코드의 수정이나 확장을 어렵게 만드는데, 이런 단점을 해소하기 위해 사용되는 패턴이 컴포지트(Composite) 패턴입니다. 컴포지트 패턴은 이 문제를 전체-부분을 구성하는 클래스가 동일 인터페이스를 구현하도록 만듬으로써 해결합니다.


![Untitled Diagram](https://user-images.githubusercontent.com/22395934/82339961-9d342b80-9a29-11ea-9039-be3041986086.png)

위의 그림은 컴포지트 패턴을 적용한 결과를 보여주고 있습니다.
여기서 DeviceGroup 클래스는 개별 Device를 하나의 그룹으로 묶어주는데, Aircon 클래스나 Light 클래스처럼 DeviceGroup 클래스도 Device 인터페이스를 상속받는 것을 알 수 있습니다. 즉, 부분(Aircon, Light등)과 전체(즉, DeviceGroup)를 한 개의 인터페이스로 추상화 한 것입니다.

- 컴포넌트 그룹을 관리합니다.
- 컴포지트에 기능 실행을 요청하면, 컴포지트는 포함하고 있는 컴포넌트들에게 기능 실행 요청을 위임합니다.

위 그림에서 DeviceGroup 클래스가 컴포지트에 해당하며 아래처럼 두 개의 책임을 구현할 수 있습니다.

```java
public class DeviceGroup implements Device {
    private List<Device> devices = new ArrayList<Device>)();

    public void addDevice(Device d) {
        devices.add(d);
    }

    public void removeDevice(Device d) {
        devices.remove(d);
    }

    public void turnOn() {
        for (Device device: devices) {
            device.turnOn(); // 관리하는 Device 객체들에게 실행 위임
        }
    }

    public void turnOff() {
        for (Device device: devices) {
            device.turnOff() // 관리하는 Device 객체들에게 실행 위임
        }
    }
}
```

위 코드에서 addDevice() 메서드와 removeDevice() 메서드는 DeviceGroup이 관리할 Device 객체들의 목록을 관리합니다. turnOn() 메서드와 turnOff() 메서드는 DeviceGroup이 관리하고 있는 Device 객체들에게 기능 실행을 위임합니다. 이는, 아래 코드처럼 DeviceGroup 객체에 Device  객체를 등록한 뒤에 DeviceGroup 객체의 turnOn() 메서드를 호출하면, 등록되어 있는 모든 Device 객체의 turnOn() 메서드가 호출된다는 것을 뜻합니다.

```java
Device device1 = ...;
Device device2 = ...;
DeviceGroup group = new DeviceGroup();
group.addDevice(device1);
group.addDevice(device2);

group.turnOn(); // device1과 device2의 turnOn() 실행
```
이제, DeviceGroup 클래스는 Device 타입이 되므로, 전원 제어 기능을 제공하는 PowerController 클래스는 Device 타입과 DeviceGroup 타입을 구분할 필요 없이 다음과 같이 Device 타입만을 이용해서 전원 관리를 할 수 있게 됩니다. 즉, 전체냐 부분이냐에 상관 없이 클라이언트는 단일 인터페이스 기능을 실행할 수 있는 장점이 생깁니다.


```java
public class PowerController {

    public void turnOn(Long deviceId) {
        //device의 실제 타입이 DeviceGroup인지 여부에 상관없이 동작
        Device device = findDeviceById(Long deviceId);
        device.turnOn();
    }
}
```

컴포지트 패턴을 사용할 때의 또 다른 장점은 컴포지트 자체도 컴포넌트이기 때문에, 컴포지트에 다른 컴포지트를 등록할 수 있다는 것입니다. 따라서 아래 코드처럼 각 층의 Light 객체를 모은 DeviceGroup 객체를 생성하고 다시 각 층 별로 생성된 DeviceGroup 객체를 모아서 하나의 DeviceGroup 객체에 등록하면, 전 층의 Light 객체를 켜거나 끄는 기능을 구현할 수 있게 됩니다.

```java
// 각 층의 Light 객체를 모은 그룹 생성
DeviceGroup firstFloorLightGroup = ...; // 1층의 모든 Light를 포함
DeviceGroup secondFloorLightGroup = ...; // 2층의 모든 Light를 포함
...

// 각 층의 DeviceGroup 객체를 하나의 DeviceGroup에 다시 등록
DeviceGroup allLightGroup = new DeviceGroup에 다시 등록
allLightGroup.add(firstFloorLightGroup);
allLightGroup.add(secondFloorLightGroup);
...

allLightGroup.turnOff(); // 모든 층의 Light 객체의 turnOff() 실행
```

## 컴포지트 패턴 구현의 고려 사항
컴포지트 패턴을 구현할 때 고려할 점은 컴포넌트를 관리하는 인터페이스를 어디서 구현할지에 대한 여부입니다. 컴포지트 패턴의 장점 중 하나는 클라이언트가 컴포지트와 컴포넌트를 구분하지 않고 컴포넌트 인터페이스만으로 프로그래밍 할 수 있게 돕는다는 점인데, 앞서 예제에서는 컴포지트인 DeviceGroup에 인터페이스를 정의했습니다.따라서 Device 그룹을 만들어야 하는 코드는 DeviceGroup 타입에 직접 접근해야 하는 상황이 발생합니다.

Device 타입에 컴포넌트를 관리하는 인터페이스를 추가하면, 클라이언트 입장에서 DeviceGroup 타입을 사용하지 않고도 그룹을 생성할 수 있게 됩니다. 예를 들어, 다음과 같이 Device 타입만 사용하는 것이 가능해집니다.(DeviceGroup 객체 자체를 생성하는 코드에서만 DeviceGroup 타입을 사용하게 됩니다.)

```java
public void addDeviceTo(Device device, Integer toDeviceId) {
    Device composite = findDevice(toDeviceId);
    composite.addDevice(device);
}
```

Device 타입에 컴포넌트를 관리하는 addDevice() 메서드와 removeDevice() 메서드가 정의되어 있을 경우, Light 클래스나 Aircon 클래스처럼 컴포지트가 아닌 클래스에서 이 두 기능이 정상적으로 동작하면 안됩니다. 예를 들어, Light 객체에 addDevice() 메서드를 이용해서 다른 객체를 추가하는 것은 논리적으로 말이 안됩니다. 이런 상황이 발생하지 않도록 하기 위해 Device 타입에 이들 기능에 익셉션을 발생시키는 기본 구현을 추가하고, DeviceGroup 클래스에서 알맞게 재정의하도록 구현할 수 있을 것입니다.

```java
// Device에 addDevice()와 removeDevice()에 대한 기본 기능 구현
public abstract class Device {
    public void addDevice(Device d) {
        throw new CanNotAddException("추가 할 수 없음");
    }
    
    public void removeDevice(Device d) {
        // 아무 것도 하지 않음
    }

    public abstract void turnOn();
    public abstract void turnOff();
}
```



