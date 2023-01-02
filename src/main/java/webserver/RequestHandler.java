package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.sun.javafx.collections.MappingChange;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

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
            //헤더정보 끝까지 읽기
            Map<String, String> headerInfo = new HashMap<>();
            while(!"".equals(line = br.readLine())){
                String[] info = line.split(": ");
                headerInfo.put(info[0], info[1]);
            }

            //#2 회원가입이면 body 에 저장된 회원정보 읽기
            if(url.startsWith("/user/create")){
                String params = IOUtils.readData(br, Integer.parseInt(headerInfo.get("Content-Length")));
                log.info("params = " + params);

                //#3 불러온 회원 정보를 User 클래스에 저장
                Map<String, String> userInfo = HttpRequestUtils.parseQueryString(params);
                User user = new User(userInfo.get("userId"), userInfo.get("password"), userInfo.get("name"), userInfo.get("email"));
                log.info("userId = " + user.getUserId() + ", password = " + user.getPassword() + ", name = " + user.getName() + ", email = " + user.getEmail());
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
