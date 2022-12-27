# MiniKube 명령어 

Docker Desktop에서 쿠버네티스를 single cluster로 이용할 수 있는 MiniKube를 지원하고 있어서 한번 사용해봤습니다.

기본적으로 많이 사용하는 명령어는 아래와 같습니다.


1. pod 목록 조회

```
kubectl get pods
```

2. deployment 목록 조회

```
kubectl get deployment
```

3. 서비스 확인

```
kubectl get services
```


4. Nginx 서버 실행

```
kubectl run sample-nginx --image=nginx --port=80
```


5. pod 삭제

```
kubectl delete pod/sample-nginx
```


pod가 k8s에서 배포하는 최소단위라면 deployment의 정의가 무엇인지  궁금해서 구글링하여 찾아봤습니다.

deployment는 `stateless 앱`을 배포할때 사용되는 가장 기본이 되는 컨트롤러입니다. 
 
k8s의 deployment 컨트롤러는 지속적으로 배포된 인스턴스들을 모니터링하고, 스케일링 및 관리를 하는 역할입니다. 유저에 의해 정해진 적정 개수를 유지하게끔 실행 중인 `pod`의 개수를 관리해줍니다.

> 기본적으로 pod는 노드에 의존하기 때문에 만약 특정 노드가 down되는 경우에는 delpoyment가 자동으로 다른 노드로 pod를 띄워줍니다.

 
예를 들어서 어떤 워커노드에 pod를 생성하면, 그 pod가 down되면 k8s에서 해당 pod의 상태값을 체크하여 사용자가 pod 생성 시 지정한 개수만큼 k8s에서 다시 해당 pod를 생성해서 띄우는 방식으로 동작하고 있습니다.

간략하게 deployment는 pod의 상위호환으로 생각하면 이해하기 쉽습니다.
실제로 deployment로 생성한 pod인 경우에는 delete로 삭제하여도 replica로 지정한 개수만큼 계속 인스턴스가 올라 간것을 확인할 수 있었습니다.

> 참고 블로그: https://blog.naver.com/PostView.nhn?isHttpsRedirect=true&blogId=sory1008&logNo=221566576268&categoryNo=0&parentCategoryNo=80&viewDate=&currentPage=1&postListTopCurrentPage=1&from=postView
