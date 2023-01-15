package controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import http.HttpRequest;
import http.HttpResponse;


public class CreateUserController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);
    @Override
    public void doPost(HttpRequest request, HttpResponse response){
        User user = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"), request.getParameter("email"));
        log.info("user : {}", user);

        DataBase.addUser(user);
        log.info("데이터베이스 저장 정보 : {}", DataBase.findUserById(user.getUserId()));

        response.sendRedirect("/index.html");
    }
}
