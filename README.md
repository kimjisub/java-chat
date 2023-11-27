# java chat

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
