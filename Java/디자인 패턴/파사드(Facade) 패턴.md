## 파사드(Facade) 패턴

직원 정보, 직원의 이력 정보, 그리고 직원에 대한 평가 정보를 읽어 와 화면에 보여주는 GUI 프로그램을 만들면 아래 그림과 같이 데이터를화면에 출력해 주는 GUIViewer 클래스는 각 데이터를 제공하는 Dao 객체에 직접 접근하는 구조를 갖게 될 것입니다.

![Untitled Diagram (3)](https://user-images.githubusercontent.com/22395934/81474854-102bee00-9243-11ea-92bd-2d96277a0a09.png)


HR팀으로부터 화면뿐만 아니라 XML이나 엑셀로 동일한 데이터를 추출해 달라는 요구 사항이 들어왔습니다.
이를 구현하기 위해 아래 그림과 같이 XMLExporter 클래스와 ExcelExporter 클래스를 구현하였습니다.

![Untitled Diagram](https://user-images.githubusercontent.com/22395934/81474957-e58e6500-9243-11ea-8520-1953558b4387.png)

위 그림의 구조에서 발생할 수 있는 문제점 중 가장 큰 것은 GUIViewer, XMLExporter, ExcelExporter 사이에서 코드 중복이 발생한다는 점입니다. 세 클래스는 모두 동일한 코드를 이용해서 EmpDao, ResumeDao, EvaluationDao 객체를 사용하고 데이터를 추출합니다.
이런 코드 중복에서 더 큰 문제는 완전히 똑같기 보다는 GUIViewer, XMLExporter, ExcellExporter 마다 약간씩 달라질 수 있다는 점입니다. 중복된 코드에서 미세한 차이가 발생하면 이후 변경해 주어야 할 때 미세한 차이점을 누락할 가능성이 높아지고, 이는 결극 프로그램에 버그를 만드는 원인이 됩니다.

또 다른 문제는 EmpDao, ResumeDao, EvaluationDao에 대해 직접적인 의존을 하고 있다는 점입니다. 필요한 건 이 세개의 Dao가 제공하는 데이터를 통합한 하나의 데이터인데, 이 데이터를 얻기 위해 개별 Dao 객체에 의존하고 있습니다. 따라서 이들 Dao들의 인터페이스에 일부 변화가 발생하면 이 Dao를 직접적으로 사용하고 있는 나머지 GUIViewer, XMLExporter, ExcelExporter에 모두 영향을 미치게 됩니다.

앞서 언급한 두개의 문제인 코드 중복과 직접적인 의존을 해결하는데 도움을 주는 패턴이 파사드(facade) 패턴입니다. `파사드 패턴은 서브 시스템을 감춰 주는 상위 수준의 인터페이스를 제공함으로써 이 문제를 해결합니다.` 앞서 예제에 파사드 패턴을 적용하면 구조는 아래 그림처럼 바뀝니다.

![Untitled Diagram (1)](https://user-images.githubusercontent.com/22395934/81475221-cf81a400-9245-11ea-93ce-b2f0e96852b2.png)

그림에서 EmpReportDaoFacade는 서브 시스템에 속한 각 Dao를 이용해서 클라이언트가 원하는 데이터를 제공하기 위한 인터페이스를 제공하고 있으며, 파사드 패턴에서 파사드의 역할을 맡습니다.

각 클라이언트는 이제 파사드를 이용해서 원하는 기능을 수행하게 됩니다. 파사드 패턴 적용전에는 각 클라이언트가 직접 서브 시스템에 접근했다면, 파사드 패턴 적용 후에는 아래 그림과 같이 파사드를 통해서 간접적으로 서브 시스템에 접근합니다.


#### 파사드 패턴 적용 전

```java 
public class GuiViewer {
  
    public void display() {
        ...
        Emp emp = empDao.select(id);
        ...
        Resume resume = resumeDao.select(id);
        ...
        Evaluation eval = evaluationDao.select(id);
        ...
    }      
}

public class ExcelExporter {
 
  public void display() {
        ...
        Emp emp = empDao.select(id);
        ...
        Resume resume = resumeDao.select(id);
        ...
        Evaluation eval = evaluationDao.select(id);
        ...
    }    
}
```

#### 파사드 패턴 적용 후

```java 
public class GuiViewer {
 
  public void display() {
        ...
        EmpReport rep = empReportDaoFacade.select(id); 
        ...
    }    
}

public class ExcelExporter {
  
   public void display() {
       ...
        EmpReport rep = empReportDaoFacade.select(id); 
        ...
    }    
}
```

위 코드를 보면 파사드 패턴을 적용함으로써 클라이언트 코드가 간결해지는 것을 알 수 있습니다. 클라이언트의 코드가 간결해지는 것 보다 더 큰 이득은 클라이언트와 서브 시스템 간의 직접적인 의존을 제거했다는 점입니다. 파사드 패턴을 적용하면 클라이언트는 파사드에만 의존하기 때문에, 서브 시스템의 일부가 변경되더라도 그 여파는 파사드로 한정될 가능성이 높습니다.

예를 들어, 파사드 패턴 적용 후 코드를 보면 EmpDao의 일부가 변경되더라도 그 변경의 여파는 EmpReportDaoFacade에만 영향을 주게 됩니다. 반면에 좌측에서 파사드 패턴 적용 전 코드에서는 EmpDao가 변경되면 GuiViewer와 ExcelExporter가 함께 영향을 받게됩니다. 이는 서브 시스템의 변경을 더 어렵게 만들게 됩니다.

## 파사드 패턴의 장점과 특징
클라이언트와 서브 시스템 간의 결합을 제거함으로써 얻을 수 있는 또 다른 이점은 파사드를 인터페이스로 정의함으로써 클라이언트의 변경 없이 서브 시스템 자체를 변경할 수 있다는 것입니다.

앞서 예제에서 Dao가 아닌 HTTP를 이용해서 데이터를 읽어 오도록 서브 시스템을 교체하더라도 아래 그림처럼 클라이언트의 영향 없이 알맞은 콘크리트 파사드 클래스를 구현해 주기만 하면 됩니다.

![Untitled Diagram (2)](https://user-images.githubusercontent.com/22395934/81475771-3bb1d700-9249-11ea-87e9-b18e62ae2097.png)

파사드 패턴을 적용한다고 해서 서브 시스템에 대한 직접적인 접근을 막는 것은 아닙니다. 파사드 패턴은 단지 여러 클라이언트에 중복된 서브 시스템 사용을 파사드로 추상화할 뿐입니다.
따라서 다수의 클라이언트에 공통된 기능은 파사드를 통해서 쉽게 서브 시스템을 사용할 수 있도록 하고, 보다 세밀한 제어가 필요한 경우에는 서브 시스템에 직접 접근하는 방식을 선택할 수 있습니다.

> 파사드 패턴을 클래스와 비교해보면, 파사드는 마치 서브 시스템의 상세함을 감춰 주는 인터페이스와 유사합니다. 파사드를 통해서 서브 시스템의 상세한 구현을 캡슐화하고, 이를 통해 상세한 구현이 변경되더라도 파사드를 사용하는 코드에 주는 영향을 줄일 수 있게 됩니다.

#### 참조: 객체지향과 디자인 패턴

