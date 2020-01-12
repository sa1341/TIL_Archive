# 프린터
## 문제설명
일반적인 프린터는 인쇄 요청이 들어온 순서대로 인쇄합니다. 그렇기 때문에 중요한 문서가 나중에 인쇄될 수 있습니다. 이런 문제를 보완하기 위해 중요도가 높은 문서를 먼저 인쇄하는 프린터를 개발했습니다. 이 새롭게 개발한 프린터는 아래와 같은 방식으로 인쇄 작업을 수행합니다.


1. 인쇄 대기목록의 가장 앞에 있는 문서(J)를 대기목록에서 꺼냅니다.
2. 나머지 인쇄 대기목록에서 J보다 중요도가 높은 문서가 한 개라도      존재하면 J를 대기목록의 가장 마지막에 넣습니다.
3. 그렇지 않으면 J를 인쇄합니다.


예를 들어, 4개의 문서(A, B, C, D)가 순서대로 인쇄 대기목록에 있고 중요도가 2 1 3 2 라면 C D A B 순으로 인쇄하게 됩니다.

내가 인쇄를 요청한 문서가 몇 번째로 인쇄되는지 알고 싶습니다. 위의 예에서 C는 1번째로, A는 3번째로 인쇄됩니다.

현재 대기목록에 있는 문서의 중요도가 순서대로 담긴 배열 priorities와 내가 인쇄를 요청한 문서가 현재 대기목록의 어떤 위치에 있는지를 알려주는 location이 매개변수로 주어질 때, 내가 인쇄를 요청한 문서가 몇 번째로 인쇄되는지 return 하도록 solution 함수를 작성해주세요.

## 제한사항

- 현재 대기목록에는 1개 이상 100개 이하의 문서가 있습니다.
- 인쇄 작업의 중요도는 1~9로 표현하며 숫자가 클수록 중요하다는 뜻입니다.
- location은 0 이상 (현재 대기목록에 있는 작업 수 - 1) 이하의 값을 가지며 대기목록의 가장 앞에 있으면 0, 두 번째에 있으면 1로 표현합니다.

## 입출력 예
![스크린샷 2020-01-12 오후 7 21 06](https://user-images.githubusercontent.com/22395934/72217418-b5bea180-3570-11ea-9bb5-11b94c9b6069.png)


## 입출력 예 설명

예제 #1

문제에 나온 예와 같습니다.

예제 #2

6개의 문서(A, B, C, D, E, F)가 인쇄 대기목록에 있고 중요도가 1 1 9 1 1 1 이므로 C D E F A B 순으로 인쇄합니다.



## 문제 풀이
```java
class Document{

    public int index;
    public int priority;

    public Document(int index, int priority) {
        this.index = index;
        this.priority = priority;
    }
}


public class Printer {
    public int solution(int[] priorities, int location) {

        int answer = 0;
        LinkedList<Document> queue = new LinkedList<>();
        // 결과 값을 가지고 있는 Queue 타입의 객체 
        LinkedList<Document> result = new LinkedList<>();

        for (int i = 0; i < priorities.length; i++) {
            queue.add(new Document(i, priorities[i]));
        }

        while(!queue.isEmpty()) {
            Document firstDoc = queue.poll();
            if (isMaximum(firstDoc, queue)) {
                result.add(firstDoc);
            } else {
                queue.add(firstDoc);
            }
        }

        // 정렬된 우선순위 값을 기준으로
        int order = 0;

        for (Document tempDoc : result){
            int value = tempDoc.index;
            if(location == value){
                answer = order + 1;
            }
            order++;
        }
        return answer;
    }

    public boolean isMaximum(Document firstDoc, LinkedList<Document> queue){

        if(queue.size() == 0){
            return  true;
        }

        for (Document temp : queue){
            if(firstDoc.priority < temp.priority){
               return false;
            }
        }
        return true;
    }
}
```

처음 이 문제를 보았을때 생각보다 쉬운거 같았는데... 막상 코드를 짜보니 자꾸 테스트케이스 실패가 났다... 이 알고리즘은 큐나 스택을 써야될거 같다고 위에 대놓고 명시를 해서 큐를 썼고 그 중에서 가장 많이 쓰인다는 LinkedList 큐를 사용하여 풀기로 하였다. 풀이 과정에서 우선 순위 값이 높은 순서대로 결과 Queue에 넣을 수는 있었지만 location에 해당하는 값이 우선 순위 기준으로 몇번째 인덱스에 있는지 찾는데 상당히 애를 먹었다. 그러다가.. 현재 내가 푸는 언어가 자바라는 것을 뒤늦게 깨닫고... 객체를 이용해서 인덱스와 우선 순위 값을 같이 관리하도록 Document 객체를 큐로 관리하도록 코드를 작성하였다. 이렇게 구현하니 직관적이면서 인덱스를 관리하는게 확실히 편해졌습니다. 프로그래머스에서 빡고수분들이 작성한 것을 보고는.. 도저희 저렇게 코드를 작성할 자신이 없었기 때문에 오래 걸리고 지저분하더라도 전 이게 더 직관적이라고 생각이 들고
가장 편한거 같아서 이렇게 풀었습니다.
