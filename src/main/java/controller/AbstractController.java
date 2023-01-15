package controller;

import http.HttpRequest;
import http.HttpResponse;

public abstract class AbstractController implements Controller{
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if(request.getMethod().equals("GET")) doGet(request, response);
        else doPost(request, response);
    }

    public void doGet(HttpRequest request, HttpResponse response){};

    public void doPost(HttpRequest request, HttpResponse response){};
}
