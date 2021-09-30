# SSL 인증서 개념

SSL 인증서는 클라이언트와 서버간의 통신을 제3자가 보증해주는 전자화된 문서입니다. 클라이언트가 서버에 접속한 직후에 서버는 클라이언트에게 이 인증서 정보를 전달합니다.클라이언트는 이 인증서 정보가 신뢰할 수 있는 것인지를 검증한 후에 다음 절차를 수행하게 됩니다. 

## SSL의 장점

- 통신 내용이 공격자에게 노출되는 것을 막을 수 있습니다.
- 클라이언트가 접속하려는 서버가 신뢰할 수 있는 서버인지를 판단할 수 있습니다.
- 통신 내용의 악의적인 변경을 방지할 수 있습니다.

## SSL에서 사용하는 암호화의 종류

SSL 인증서의 사용은 SSL의 동작방법에 상당히 많이 의지하기 때문에 SSL의 메커니즘을 이해하는 것이 차라리 빠른 길입니다.

`SSL의 핵심은 암호화입니다.` SSL은 보안과 성능상의 이유로 두가지 암호화 기법을 혼용해서 사용하고 있는데 SSL 동작방법을 이해하기 위해서는 이 암호화 기법들에 대한 이해가 필요합니다. 


## 대칭키

암호를 만드는 행위인 암호화를 할 때 사용하는 일종의 비밀번호를 키(key)라고 합니다. 이 키에 따라서 암호화된 결과가 달라지기 때문에 키를 모르면 암호를 푸는 행위인 복호화를 할 수 없습니다. 대칭키는 동일한 키로 암호화와 복호화를 같이할 수 있는 방식의 암호화 기법입니다. 

즉, 암호화를 할 때 1234라는 값을 사용했다면 복호화를 할 때 1234라는 값을 입력해야 한다는 것입니다. 이해하기 쉽도록 예제코드를 작성했습니다.

```bash
echo 'this is the plain text' > plaintext.txt

openssl enc -e -des3 -salt -in plaintext.txt -out cipertext.bin;
```

위 명령의 의미는 아래와 같습니다.

- enc -e -des3: des3 방식으로 암호화 함
- -in plaintext.txt -out cipertext.bin: plaintext.txt 파일을 암호화 한 결과를 cipertext.bin 파일에 저장함.

```bash
openssl enc -d -des3 -in cipertext.bin -out plaintext2.txt;
```

```bash
위의 명령은 enc -d -des3 -in cipertext.bin -out plaintext2.txt;
```
위의 명령은 enc -d 옵션으로 인해서 cipertext.bin 파일을 plaintext2.txt 파일로 복호화하겠다는 의미입니다.

## SSL 프로토콜 활성화 확인

```
openssl s_client -connect [해당 IP 및 도메인]:443 -ssl3 -- sslv3 프로토콜 통신 확인
openssl s_client -connect [해당 IP 및 도메인]:443 –ssl2 -- sslv2 프로토콜 통신 확인
openssl s_client -connect [해당 IP 및 도메인]:443 –tls1 -- tls1.0 프로토콜 통신 확인
openssl s_client -connect [해당 IP 및 도메인]:443 –tls1_1 -- tls1.1 프로토콜 통신 확인
openssl s_client -connect [해당 IP 및 도메인]:443 –tls1_2 -- tls1.2 프로토콜 통신 확인
```
