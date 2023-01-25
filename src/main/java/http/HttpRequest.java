package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private HttpMethod method;
    private String path;
    private Map<String, String> header = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    public HttpRequest(InputStream inputStream) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            //http header 첫 번째 줄 읽기(요청라인)
            String line = br.readLine();

            //null 일 경우 예외처리
            if(line == null) return;

            //요청라인에 따라 GET, POST 처리
            processRequestLine(line);

            //헤더 정보 저장
            while(!"".equals(line = br.readLine())){
                log.info("header : {}", line);
                String[] info = line.split(": ");
                header.put(info[0], info[1]);
            }

            //POST 경우 본문읽기
            if(method.isPost()){
                String body = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));
                params = HttpRequestUtils.parseQueryString(body);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processRequestLine(String line){
        String[] tokens = line.split(" ");
        method = HttpMethod.valueOf(tokens[0]); //HTTP 메소드 저장

        if(method.isPost()){ //POST 방식
            path = tokens[1];
            return;
        }

        //GET 방식
        int index = tokens[1].indexOf("?");
        if(index == -1) path = tokens[1];
        else{
            path = tokens[1].substring(0, index); //Path 값
            params = HttpRequestUtils.parseQueryString(tokens[1].substring(index+1)); //쿼리스트링 값
        }
    }
    public HttpCookie getCookies(){
        return new HttpCookie(getHeader("Cookie"));
    }

    public String getHeader(String field){
        return header.get(field);
    }

    public String getParameter(String field){
        return params.get(field);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
