package controller;

import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class ListUserController extends AbstractController{
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);
    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String cookie = HttpRequestUtils.parseCookies(request.getHeader("Cookie")).get("logined");
        if(cookie == null) cookie="false";

        if(Boolean.parseBoolean(cookie)){ //로그인 값이 true
            response.forward("/user/list.html");
        }else { //로그인이 안된 상태면
            response.sendRedirect("/user/login.html");
        }
    }
}
