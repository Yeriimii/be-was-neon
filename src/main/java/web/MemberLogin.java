package web;

import static http.HttpRequest.*;
import static utils.HttpConstant.CRLF;

import http.Cookie;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import manager.UserManager;
import session.SessionManager;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 회원 로그인을 처리하는 클래스입니다.
 * StaticHtmlProcessor를 상속받아 GET 요청일 때는 상위 클래스의 처리를 따르고,
 * POST 요청일 때는 로그인을 수행합니다.
 * 로그인이 실패하면 login-failed.html로 리다이렉션하고,
 * 성공하면 index.html로 리다이렉션하며 세션을 생성하여 등록합니다.
 *
 * @author yelly
 * @version 1.0
 */
public class MemberLogin extends StaticHtmlProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MemberLogin.class);
    private static final String SESSION_NAME = "SID";
    private final UserManager userManager = new UserManager();
    private final SessionManager sessionManager = new SessionManager();

    /**
     * HTTP 요청을 처리합니다.
     * GET 요청일 때는 상위 클래스의 처리를 따르고,
     * POST 요청일 때는 로그인을 수행합니다.
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     */
    @Override
    public void process(HttpRequest request, HttpResponse response) {
        if (request.getMethod().equals(HttpMethod.GET)) {
            super.process(request, response);
            return;
        }

        String id = request.getParameter("id");
        String password = request.getParameter("password");

        Optional<User> optionalUser = userManager.login(id, password);

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
