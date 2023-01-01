package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            //#1 HTTP 첫 번째 라인을 읽어서 요청 url 값 저장하기
            //BufferedReader을 통해 헤더 값 읽기
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            //http header 첫 번째 줄 읽기
            String line = br.readLine();
            //null 일 경우 예외처리
            if(line == null) return;
            //첫 번째 line 공백으로 나누기
            String[] tokens = line.split(" ");
            //두번째 토큰 값이 요청 URL
            String url = tokens[1];

            //#2 회원가입이면 요청 URL 과 이름 값 분리
            if(url.startsWith("/user/create")){
                int index = url.indexOf("?");
                String params = url.substring(index+1);
                url = url.substring(0, index);

                //#3 불러온 회원 정보를 User 클래스에 저장
                Map<String, String> userInfo = HttpRequestUtils.parseQueryString(params);
                User user = new User(userInfo.get("userId"), userInfo.get("password"), userInfo.get("name"), userInfo.get("email"));
            }

            DataOutputStream dos = new DataOutputStream(out);

            //webapp 밑에 있는 url 파일 경로를 byte 로 저장
            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
