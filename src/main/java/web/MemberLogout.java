package web;

import static utils.HttpConstant.CRLF;

import http.Cookie;
import http.HttpRequest;
import http.HttpResponse;
import java.util.List;
import java.util.Optional;
import session.SessionManager;
import session.SessionManager.SessionUser;

/**
 * 회원 로그아웃을 처리하는 클래스입니다.
 * StaticHtmlProcessor를 상속받아 사용자의 세션을 종료하고 로그아웃을 수행합니다.
 * 현재 세션을 확인하여 세션이 존재하지 않으면 루트('/')로 리다이렉션하고,
 * 세션이 존재하면 세션을 제거하고 쿠키를 만료시킨 후 루트('/')로 리다이렉션합니다.
 *
 * @author yelly
 * @version 1.0
 */
public class MemberLogout extends StaticHtmlProcessor {
    private static final String SESSION_NAME = "SID";
    private final SessionManager sessionManager = new SessionManager();

    /**
     * HTTP 요청을 처리합니다.
     * 현재 세션을 확인하여 세션이 존재하지 않으면 루트('/')로 리다이렉션하고,
     * 세션이 존재하면 세션을 제거하고 쿠키를 만료시킨 후 루트('/')로 리다이렉션합니다.
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     */
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
