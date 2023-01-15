package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController{
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);
    @Override
    public void doPost(HttpRequest request, HttpResponse response){
        //로그인 정보 확인
        User user = DataBase.findUserById(request.getParameter("userId"));
        //DB에 해당 id가 존재하지 않으면
        if(user == null){
            response.addHeader("Set-Cookie", "logined=false");
            response.forward("/user/login_failed.html");
        }else{ //DB에 해당 id가 존재하면
            if(user.getPassword().equals(request.getParameter("password"))){ //비밀번호가 일치할 경우
                response.addHeader("Set-Cookie", "logined=true");
                response.sendRedirect("/index.html");
            }else{ //비밀번호가 틀리면
                response.addHeader("Set-Cookie", "logined=false");
                response.forward("/user/login_failed.html");
            }
        }
    }
}
