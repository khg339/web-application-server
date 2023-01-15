package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private DataOutputStream dos;
    private Map<String, String> header = new HashMap<>();

    HttpResponse(OutputStream outputStream){
        dos = new DataOutputStream(outputStream);
    }

    public void forward(String url){ //HTML, CSS, 자바스크립트를 읽어 응답으로 보내는 메소드
        try {
            if(url.endsWith(".css")) header.put("Content-Type", "text/css");
            else if(url.endsWith(".js")) header.put("Content-Type", "application/javascript");
            else header.put("Content-Type", "text/html;charset=utf-8");

            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            header.put("Content-Length", String.valueOf(body.length));

            response200Header(body.length);
            responseBody(body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendRedirect(String url){
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            processHeaders();
            dos.writeBytes("Location: " + url);
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    public void addHeader(String field, String value){
        header.put(field, value);
    }
    private void processHeaders(){ //header Map 에 저장된 내용 작성
        try {
            Set<String> keys = header.keySet();
            for(String key : keys){
                dos.writeBytes(key + ": " + header.get(key) + "\r\n");
            }
        }catch (IOException e){
            log.error(e.getMessage());
        }

    }
    private void response200Header(int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
