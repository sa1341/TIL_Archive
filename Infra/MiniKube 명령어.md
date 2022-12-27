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
