# 💙 실습 과정에 대한 자세한 설명 및 정리
* velog : [실습 진행방법](https://velog.io/@khg339/Next-Step-3%EC%9E%A5-%EA%B0%9C%EB%B0%9C-%ED%99%98%EA%B2%BD-%EA%B5%AC%EC%B6%95-%EB%B0%8F-%EC%84%9C%EB%B2%84-%EC%8B%A4%EC%8A%B5-%EC%9A%94%EA%B5%AC%EC%82%AC%ED%95%AD) / 
[추가 공부](https://velog.io/@khg339/Next-Step-HTTP-%EC%9B%B9-%EC%84%9C%EB%B2%84-%EC%9D%B4%ED%95%B4)

<br/><br/><br/>

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
- POST로 데이터를 전달할 경우 전달하는 데이터는 HTTP 본문에 담긴다.

- Content-Length가 헤더의 길이를 뜻하는 줄 알고 잘못 구현했었다. Content-Length는 HTTP 헤더에 정의된 값으로, 헤더를 모두 읽어 Contet-Length 값을 추출해줘야 한다.

- 따라서 Map<String, String> 을 사용하여 ": "로 구분된 헤더 값을 모두 읽어들인다.

- HTTP 본문에 전달되는 데이터는 GET 방식으로 데이터를 전달할 때의 _이름=값_ 과 같다.

<br/><br/>

### 📌 요구사항 4 - redirect 방식으로 이동
- https://en.wikipedia.org/wiki/HTTP_302 를 참고하여 HTTP 302 코드 사용하기(response302Header 구현)

- HTTP 302는 페이지를 redirect할 때 사용한다.

- url이 "/user/create"일 경우, 즉 회원가입 창에서 회원 저장을 끝내면 redirect하기 위해 response302Header을 호출한다. 
	이외의 경우 else문을 통해 response202Header을 호출하고 본문을 출력한다.
	
<br/><br/>


### 📌 요구사항 5 - cookie
- 아이디 값에 해당하는 User가 없을 경우 NullPointError가 발생한다. 따라서 해당 아이디의 유저가 존재하는 지 부터 검사한다.

- 아이디가 존재할 경우 비밀번호 확인을 진행한다.

- redirect를 해줘야하기 때문에 202가 아닌 302를 변형한다.

- 성공했을 때와 실패했을 때 redirect 해주는 url이 다르기 때문에 이동할 주소와 쿠키값을 모두 전달받는 respose302HeaderWithCookie를 구현해야한다.

<br/><br/>

### 📌 요구사항 6 - 사용자 목록 출력
- 로그인이 안된 상태(logined=flase)일 때는 /user/login.html로 redirect한다.

- Collection을 통해 User 객체 집합을 DataBase에서 꺼내 저장한다.

- StringBuilder을 통해 body에 출력할 html정보를 담는다. 한마디로 StringBuilder에 사용자 목록 테이블을 작성하여 저장한다.

- URLDecoder을 통해 UTF-8로 디코딩 해줘야 모든 문자가 정상적으로 출력된다.

<br/><br/>

### 📌 요구사항 7 - CSS 지원
- 현재 구현된 response200Header 메소드에는 Content-Type이 text/html로 설정되어 있어 css파일이 입력돼도 html로 인식한다.

- 따라서 response200CssHeader 메소드를 별도로 구현하여 css파일이 입력되었을 때는 Content-Type이 text/css가 되도록 한다.

- Header 정보에서 Accept가 text/css면 response200CssHeader을 사용할 수 있도록 처리해준다.



### heroku 서버에 배포 후
* 
