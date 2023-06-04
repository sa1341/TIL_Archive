# Reactive Streams

Reactive Streams를 살펴보기 앞서서 `Reactive manifesto`에 대해서 알아봤습니다.

간단하게 Reactive Streams를 사용하기 위해서 어떠한 구성요소를 사용해야 돼는지에 대한 가이드라인 입니다. 즉, 대중들에게 공개하는 소프트웨어 아키텍쳐에 대한 선언문 이라고 생각하면 됩니다. 

여기서 4가지의 핵심 가치를 제시하고 있습니다.

- Responsive(응답성)
- Elastic(유연성)
- Resilient(복원력)
- Message Driven(메시지 기반)

## Reactive Programming

Reactive Programming 사상은 비동기 데이터 stream을 사용하는 패러다임입니다. 따라서 모든것이 이벤트로 구성되고 이벤트를 통해서 전파되어야 합니다

- event-driven 해야합니다.
- 데이터의 전달, 에러, 완료까지 모두 이벤트로 취급합니다.
- Reactive manifesto의 Responsive, Resilient, Elastic, Message Drvien 까지 모두 해당합니다.

