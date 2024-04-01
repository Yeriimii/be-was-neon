package web;


import static utils.HttpConstant.CRLF;

import http.HttpRequest;
import http.HttpRequest.HttpMethod;
import http.HttpResponse;
import java.util.function.Function;
import manager.UserManager;
import model.User;

public class MemberSave extends StaticHtmlProcessor {
    private final UserManager userManager = new UserManager();
    private static final Function<Boolean, String> REDIRECT = isSuccess -> isSuccess ? "/" : "/registration/failed.html";

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        if (request.getMethod() == HttpMethod.POST) {
            User user = createUser(request);
            view(response, userManager.join(user));
            return;
        }

        super.process(request, response);
    }

    private User createUser(HttpRequest request) {
        String id = request.getParameter("id");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        return new User(id, password, username, email);
    }

    private void view(HttpResponse response, boolean successJoin) {
        responseHeader302(response, REDIRECT.apply(successJoin));
        response.setMessageBody(CRLF);

        response.flush();
    }
}
