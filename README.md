# java-chat



## 주제 선정

- 서버, 클라이언트 구조로 n:n 채팅을 구현합니다.
- 서버는 클라이언트의 접속을 기다리고, 클라이언트는 서버에 접속하여 채팅을 주고받습니다.
- 서버는 수신한 메시지를 모든 클라이언트에게 브로드캐스팅합니다.
- 새로운 클라이언트가 접속할 때마다 이전 채팅 기록을 전송합니다.
- 서버는 채팅 기록을 로그 파일에 저장합니다.
- 서버는 클라이언트의 접속 및 접속 종료 등의 이벤트를 채팅방에 표시합니다.

## 인터페이스 설계

- 서버는 cli 환경에서 실행되며, 클라이언트는 swing을 이용한 gui 환경에서 실행됩니다.
- 서버는 env 또는 args를 통해 포트를 지정할 수 있습니다. (기본값: 8080, args 우선)
- 클라이언트는 서버의 ip와 포트를 GUI에서  입력받아 접속합니다.
- 채팅방 인터페이스는 카카오톡을 참고하였습니다.

## 프로토콜

TCP/IP 위에서 동작하기 때문에 ASCII 표준에 따라 목적에 따라 문자열을 인코딩하여 전송합니다.

- 0x17: 요청 구분자
- 0x1E: 요청 내 항목 구분자

## 추상화

4단계의 추상화 과정을 통해 구현합니다

1. ASCII를 이용한 프로토콜을 MessageProtocol 클래스에 정의
2. 서버와 클라이언트가 각각 송수신할 수 있는 인터페이스 정의
3. 쓰레드 및 소켓을 처리하는 ChatClient, ChatServer 클래스 정의
4. GUI를 처리하는 Main 클래스 정의


커스텀 프로토콜
메시지 송수신 프로토콜 기반 클라이언트 서버 인터페이스 만듦
이걸 한번 더 이용해서 스레드 이용해서 클라이언트
사용해서 gui에서 사용

콜백함수


## KeyWord

<hr>

- [funciton]()
    - Callback 함수

- [접근지정자]()
- [클래스]()
    - 캡슐화
    - 상속
    - 다형성
    - 업캐스트
    - 오버라이딩
    - 동적바인딩
-  [모듈 (JavaBase module)]()
    - net
    - util
    - io
        - BufferedReader
        - IOException
        - InputStreamReader
- [컬렉션]()
    - ArrayList\<E>

- [GUI]()
    - 라이브러리
        - AWT (Abstract Windowing Toolkit)
        - Swing package
- [스레드&멀티태스킹]()
    - 멀티스레딩
    - 스레드 동기화
- [Network]()
    - TCP / IP
    - 포트
    - Socket programming
