package web;


import static utils.HttpConstant.CRLF;

import http.HttpRequest;
import http.HttpRequest.HttpMethod;
import http.HttpResponse;
import java.util.function.Function;
import manager.UserManager;
import model.User;

/**
 * 회원가입을 처리하는 클래스입니다.
 * StaticHtmlProcessor를 상속받아 POST 메서드로 요청이 오면 회원 정보를 생성하고 회원가입을 수행합니다.
 * 회원가입에 성공하면 "/"으로 리다이렉트하고, 실패하면 "/registration/failed.html"로 리다이렉트합니다.
 *
 * @author yelly
 * @version 1.0
 */
public class MemberSave extends StaticHtmlProcessor {
    private final UserManager userManager = new UserManager();
    private static final Function<Boolean, String> REDIRECT = isSuccess -> isSuccess ? "/" : "/registration/failed.html";

    /**
     * HTTP 요청을 처리합니다.
     * POST 메서드로 요청이 오면 회원 정보를 생성하고 회원가입을 수행합니다.
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     */
    @Override
    public void process(HttpRequest request, HttpResponse response) {
        if (request.getMethod() == HttpMethod.POST) {
            User user = createUser(request);
            view(response, userManager.join(user));
            return;
        }

        super.process(request, response);
    }

    /**
     * HTTP 요청으로부터 회원 정보를 생성합니다.
     * @param request HTTP 요청 객체
     * @return 생성된 회원 정보 객체
     */
    private User createUser(HttpRequest request) {
        String id = request.getParameter("id");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        return new User(id, password, username, email);
    }

    /**
     * 회원가입 결과에 따라 리다이렉트 URL을 생성하고 응답을 반환합니다.
     * @param response HTTP 응답 객체
     * @param successJoin 회원가입 성공 여부
     */
    private void view(HttpResponse response, boolean successJoin) {
        responseHeader302(response, REDIRECT.apply(successJoin));
        response.setMessageBody(CRLF);

        response.flush();
    }
}
