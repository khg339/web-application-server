package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import db.DataBase;
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
                System.out.println(info[0] + " : " + info[1]);
            }

            DataOutputStream dos = new DataOutputStream(out);

            //#2 회원가입이면 body 에 저장된 회원정보 읽기
            if(url.equals("/user/create")){
                String params = IOUtils.readData(br, Integer.parseInt(headerInfo.get("Content-Length")));
                log.info("params = " + params);

                //#3 불러온 회원 정보를 User 클래스에 저장
                Map<String, String> userInfo = HttpRequestUtils.parseQueryString(params);
                User user = new User(userInfo.get("userId"), userInfo.get("password"), userInfo.get("name"), userInfo.get("email"));
                log.info("userId = " + user.getUserId() + ", password = " + user.getPassword() + ", name = " + user.getName() + ", email = " + user.getEmail());

                //#4 Database에 User 저장
                DataBase.addUser(user);
                log.info("데이터베이스 저장 정보 : " + DataBase.findUserById(user.getUserId()).getUserId());

                //index.html 로 redirect
                response302Header(dos);
            }
            //#5 로그인이면 body에 저장된 로그인 정보 읽기
            else if (url.equals("/user/login")) {
                String params = IOUtils.readData(br, Integer.parseInt(headerInfo.get("Content-Length")));
                log.info("params = " + params);
                Map<String, String> loginInfo = HttpRequestUtils.parseQueryString(params);

                //#6 불러온 로그인 정보 확인 및 쿠키값 설정
                String cookie = "";
                if(DataBase.findUserById(loginInfo.get("userId")) == null){ //아이디가 없으면
                    cookie = "logined=false";
                    url = "/user/login_failed.html";
                }
                else{ //아이디가 존재하면
                    User user = DataBase.findUserById(loginInfo.get("userId"));
                    if(loginInfo.get("password").equals(user.getPassword())){ //비밀번호가 맞으면
                        cookie = "logined=true";
                        url = "/index.html";
                    }
                    else{ //비밀번호가 틀리면
                        cookie = "logined=false";
                        url = "/user/login_failed.html";
                    }
                }

                //#7 쿠키값에 따른 redirect
                response302HeaderWithCookie(dos, url, cookie);
            }
            //#8 사용자 목록 리스트 페이지
            else if (url.equals("/user/list")) {
                String cookie = HttpRequestUtils.parseCookies(headerInfo.get("Cookie")).get("logined");

                if(Boolean.parseBoolean(cookie)){ //로그인상태면
                    int idx = 1;
                    //사용자 목록 출력
                    Collection<User> users = DataBase.findAll();
                    StringBuilder sb = new StringBuilder();
                    sb.append("<table border='1'>");
                    sb.append("<thead><tr><th>#</th> <th>사용자 아이디</th> <th>이름</th> <th>이메일</th></tr></thead>");
                    for(User user : users){
                        sb.append("<tr>");
                        sb.append("<th scope=\"row\">" + idx + "</th><td>" + user.getUserId());
                        sb.append("</td><td>" + user.getName());
                        sb.append("</td><td>" + user.getEmail());
                        sb.append("</td><td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>");
                        sb.append("</tr>");
                    }
                    sb.append("</table>");

                    byte[] body = URLDecoder.decode(sb.toString(), "UTF-8").getBytes();
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }
                else{ //로그인이 안된 상태면
                    response302HeaderWithCookie(dos, "/user/login.html", "logined=false");
                }
            } else{
                //webapp 밑에 있는 url 파일 경로를 byte 로 저장
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
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

    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String url, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
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