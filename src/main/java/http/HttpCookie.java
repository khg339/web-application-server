package http;

import util.HttpRequestUtils;

import java.util.Map;

public class HttpCookie {
    private Map<String, String> cookies;

    HttpCookie(String cookieValue){
        //cookieValue 에 포함된 Cookie 정보 불러오기
        cookies = HttpRequestUtils.parseCookies(cookieValue);
    }

    public String getCookie(String name){
        return cookies.get(name);
    }
}
