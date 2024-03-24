package web;

import static utils.HttpConstant.CRLF;

import http.Cookie;
import http.HttpRequest;
import http.HttpResponse;
import java.util.List;
import java.util.Optional;
import session.SessionManager;
import session.SessionManager.SessionUser;

public class MemberLogout extends StaticHtmlProcessor {
    private static final String SESSION_NAME = "SID";
    private final SessionManager sessionManager = new SessionManager();

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        List<Cookie> cookies = request.getCookie();
        String sessionId = sessionManager.findSessionId(cookies, SESSION_NAME);
        Optional<SessionUser> sessionUser = sessionManager.getSession(sessionId);

        /* 존재하지 않는 세션: 루트('/')로 리다이렉션 */
        if (sessionUser.isEmpty()) {
            responseHeader302(response, "/");
            response.setMessageBody(CRLF);
            return;
        }

        /* 존재하는 세션: 세션 제거 -> 쿠키 만료 -> 루트('/')로 리다이렉션 */
        sessionManager.delete(sessionId);

        Cookie cookie = new Cookie(SESSION_NAME, sessionId);
        cookie.setMaxAge(0);

        responseHeader302(response, "/");
        response.addCookie(cookie);
        response.setMessageBody(CRLF);

        response.flush();
    }
}
