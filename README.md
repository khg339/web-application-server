# 🔧 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

<br/><br/><br/>

# 📝 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

<br/><br/><br/>

# 📁 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

<br/><br/>

### 📌 요구사항 1 - http://localhost:8080/index.html 로 접속시 응답
- Log : 구글링을 통해 기본적인 메소드 사용

- HTTP Header & body
  - HTTP 헤더는 클라이언트와 서버가 요청 또는 응답으로 부가적인 정보를 전송할 수 있도록 해준다. <br/>
  부가적 정보라 함은, 대표적으로 "요청자", "컨텐트 타입", "캐싱" 등등(수십 가지)가 있다.

  - HTTP 본문에는 실제 데이터 컨텐츠가 나타난다. HTML, 이미지, CSS 또는 JavaScript 파일 등이 올 수 있다.

- byte[] Files.readAllBytes(Path filepath) <br/>
: 매개변수로 파일의 경로를 받아 파일의 모든 내용을 byte로 변환시킴

- .toPath() <br/>
: Path객체로 변환시킴

<br/><br/>

### 📌 요구사항 2 - get 방식으로 회원가입
- 회원가입 페이지(/user/create)일 경우에만 "?"로 url과 params를 구분해야한다.
	(경우를 나누지 않았더니 index.html을 접근할 때 오류가 발생했다. "?"를 포함하지 않기 때문)
  
- 단위테스트를 어떻게 해야할 지 감이 잘 잡히지 않았다.
	HttpRequestUtils.parseQueryString() 을 테스트할 때, User 객체를 생성하여 값을 비교해주었다.
  
<br/><br/>

### 📌 요구사항 3 - post 방식으로 회원가입
* 

### 📌 요구사항 4 - redirect 방식으로 이동
* 

### 📌 요구사항 5 - cookie
* 

### 📌 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
