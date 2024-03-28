package web;

import static http.HttpRequest.*;
import static utils.HttpConstant.CRLF;

import http.Cookie;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import login.LoginManager;
import session.SessionManager;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemberLogin extends StaticHtmlProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MemberLogin.class);
    private static final String SESSION_NAME = "SID";
    private final LoginManager loginManager = new LoginManager();
    private final SessionManager sessionManager = new SessionManager();

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        if (request.getMethod().equals(HttpMethod.GET)) {
            super.process(request, response);
            return;
        }

        String id = request.getParameter("id");
        String password = request.getParameter("password");

        Optional<User> optionalUser = loginManager.login(id, password);

        /* 로그인 실패: login-failed.html 리다이렉션 */
        if (optionalUser.isEmpty()) {
            logger.debug("[LOGIN] failed login. userId={}, password={}", id, password);
            responseHeader302(response, request.getPath() + "/login-failed.html");
            response.setMessageBody(CRLF);
            response.flush();
            return;
        }

        /* 로그인 성공: /index.html 라다이렉션*/
        User loginUser = optionalUser.get();

        /* session 등록 */
        String sessionId = sessionManager.createSession();
        sessionManager.enroll(sessionId, loginUser);

        logger.debug("[LOGIN] success login. userId={}, sessionId={}", loginUser.getUserId(), sessionId);

        /* 응답 헤더 설정 */
        responseHeader302(response, "/");

        /* 쿠키 입력 */
        Cookie cookie = new Cookie(SESSION_NAME, sessionId);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.setMessageBody(CRLF);

        response.flush();
    }
}
