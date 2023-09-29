# Artillery를 이용한 성능 테스트

이전부터 훌륭한? 백엔드 개발자가 되기 위해서는 자신이 만든 API에 대해서 성능 테스를 해야하는 것은 중요하다고 생각했습니다.

그래서 `JMeter`라는 툴을 사용했지만, 최근에 `Artillery`라는 Node.js기반의 성능 테스트용 툴을 설치하여 사용해보고 나중에 단일 API 성능 테스트용으로 사용하기 좋을거 같아서 글을 작성하였습니다.

## Node.js 설치

일단 Artillery를 설치하기 위해서는 Node.js가 필수적입니다. [설치링크](https://nodejs.org/ko/)

현재 제 Mac에 설치된 버전은 `v16.13.0` 입니다.


## Artillery 설치

[Artillery 설치링크](https://www.artillery.io/docs/guides/getting-started/installing-artillery) 참조

위의 링크를 클릭하면 Artillery 툴의 설치방법을 상세하게 소개하고 있습니다. 

이제 vscode에서 터미널을 열고 명령어를 입력합니다.

```bash
npm install -g artillery
```


## 성능 테스트를 위한 Artillery 스크립트 작성

그 다음 vscode에 yaml 파일을 생성하여 아래와 같이 스크립트를 작성합니다.

#### io-test.yaml

```javascript
config:
  target: http://54.180.87.198
  phases:
    - duration: 60
      arrivalRate: 3
      name: Warm up 
    - duration: 120
      arrivalRate: 3
      rampTo: 50
      name: Ramp up load
    - duration: 9600
      arrivalRate: 50
      name: Sustained load   
  payload:
    path: "ratings_test_50k.csv"
    fields:
      - "content" 
scenarios:
  - name: "just post content"
  - flow:
      - post:
          url: "/post" 
          json:
            content: "{{ content }}"     
```

### 스크립트 문법

자세한 설명은 위에 Artillery 툴 설치하는 링크에서 
`core-concepts` 탭을 클릭하여 문서를 읽어보는 것을 추천합니다.

- target은 인스턴스로 접속할 외부 URL을 작성하면 됩니다. 

- duration: 성능을 측정하는 시간으로 위의 예시에서는 60초동안 수행하도록 설정했습니다. 

- arrivalRate는 매초 새로운 가상 유저를 만드는 수를 의미합니다. 위 예시에서는 총 60초 동안 180번의 가상 요청이 생성되는 것을 의미합니다.

- rampTo는 120초동안 초당 3번의 가상유저를 만들다가 급격하게 50개의 가상 요청을 생성하여 갑작스럽게 인스턴스에 부하를 주기 위해 사용하는 옵션입니다. 

>> rampTo는 대용량 트래픽을 처리 가능유무를 판단하기 위해서는 알면 좋은 유용한 옵션이라고 생각합니다.

- path는 실제 DataSet인 `csv` 파일의 경로를 나타냅니다.

- fields는 실제 인스턴스에 요청을 보내기 위해 필요한 필드명을 명시해줍니다.

- url에는 요청을 보내기 위핸 인스턴스의 URL을 의미합니다.

- content는 json 포맷으로 인스턴스에 전달하기 위한 필드를 명시하였습니다.

실제 csv 파일의 내용은 아래와 같이 작성되어 있습니다.

```csv
content
굳 ㅋ
GDNTOPCLASSINTHECLUB
뭐야 이 평점들은.... 나쁘진 않지만 10점 짜리는 더더욱 아니잖아
지루하지는 않은데 완전 막장임... 돈주고 보기에는....
3D만 아니었어도 별 다섯 개 줬을텐데.. 왜 3D로 나와서 제 심기를 불편하게 하죠??
"음악이 주가 된, 최고의 음악영화"
```

맨 위의 `content`가 필드가 됩니다. 각 필드에 해당하는 값들을 `{{ 필드명 }}`으로 추출해서 인스턴스의 요청 URL의 JSON 파라미터로 전달하고 있습니다.

## 스크립트 실행

이제 성능 테스트를 위한 스크립트를 작성했으면 실행 명령어를 다시 터미널에 입력합니다.

```bash
artillery run --output io-report-20220108.json io-test.yaml
```

이제 테스트가 끝날때까지 기다립니다. 위의 예제 스크립트에서는 거의 3시간정도 돌렸는데... 빠른 확인을 위해서는 5 ~ 10분정도로 `duration` 값을 조정하면 됩니다.


## 결과 html 파일 생성하기

성능 테스트가 완료되었다면 json 파일로 결과가 나옵니다. 이걸 보기 좋게 하기 위해 아래 명령어를 다시 터미널에 입력합니다.

```bash
artillery report ./io-report-20220108.json
```

해당 경로에 io-report-20220108.json.html 파일이 생성되는 것을 확인할 수 있습니다.


#### html 파일 결과화면

![스크린샷 2022-01-23 오후 11 46 34](https://user-images.githubusercontent.com/22395934/150684121-7d5621cf-decd-4a00-90ce-1a212ffd402d.png)

주로 봐야되는 그림은 `Latency at intervals` 차트입니다.

세로는 Latency(지연시간), 가로는 시간을 나타냅니다.
max는 가장 오래 걸린 요청 -> 응답시간을 의미하고, min은 가장 빠르게 온 요청 -> 응답시간을 의미합니다.

p95, p50 이라는 용어가 있는데 p95는 전체 HTTP 트랜잭션 중 가장 빠른 것부터 95%까지를 의미하고, p50은 전체 HTTP 트랜잭션 중 가장 빠른 것부터 50%까지 의미합니다.

## 실무에서 성능 테스트를 위한 팁

먼저, 항상 예상 TPS보다 여유롭게 성능 목표치를 잡습니다. 만약 예상 TPS가 1000정도라면 트래픽이 튀는 상황을 고려하여 최소 3000 ~ 4000정도로 여유롭게 인스턴스를 구성해야 합니다. 

마지막으로 API에 기대하는 Latency를 만족할 때까지 성능을 테스트해야합니다.

만약 단일 요청에 대한 Latency가 기대하는 Latency보다 높다면 이것은 scale-out으로 해결할 수 없고 코드나 다른 외부 API 호출하는 부분에서 병목이 발생했는지 확인할 필요가 있습니다.

> 참조: https://class101.net/products/T6HT0bUDKIH1V5i3Ji2M 
