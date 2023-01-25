package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import controller.Controller;
import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import mapper.RequestMapping;
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
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            //세션 아이디 추가
            //Cookie 값 중 JSESSIONID 가 존재하지 않으면, 응답 헤더의 쿠키 값으로 추가
            if(request.getCookies().getCookie("JSESSIONID") == null){
                response.addHeader("Set-Cookie", "JSESSIONID=" + UUID.randomUUID()); //랜덤 아이디
            }

            Controller controller = RequestMapping.getController(request.getPath());
            if(controller == null){
                String path = getDefaultPath(request.getPath());
                response.forward(path);
            }
            else controller.service(request, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path){
        if(path.equals("/")) return "/index.html";
        return path;
    }
}