package web;

import static http.HttpMethod.POST;

import db.Database;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class MemberSave extends HtmlProcessor {

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        if (request.getMethod() == POST) {
            String id = request.getParameter("id");
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            Database.addUser(new User(id, password, username, email));

            responseHeader302(response, getContentType(request), "/index.html");
            responseMessage(response, "ok");

            response.flush();
            return;
        }
        super.process(request, response);
    }
}
