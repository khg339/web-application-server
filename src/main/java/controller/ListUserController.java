package controller;

import http.HttpRequest;
import http.HttpResponse;
import http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class ListUserController extends AbstractController{
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);
    @Override
    public void doGet(HttpRequest request, HttpResponse response) {

        HttpSession session = request.getSession();

        if(session.getAttribute("user") != null){ //로그인 값이 true(user 객체 존재)
            response.forward("/user/list.html");
        }else { //로그인이 안된 상태면
            response.sendRedirect("/user/login.html");
        }
    }
}
