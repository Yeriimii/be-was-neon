package web;

import static utils.HttpConstant.CRLF;

import http.Cookie;
import http.HttpRequest;
import http.HttpResponse;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import manager.UserManager;
import model.User;
import session.SessionManager;
import session.SessionManager.SessionUser;

/**
 * 회원 목록을 처리하는 클래스입니다.
 * DynamicHtmlProcessor를 상속받아 로그인된 사용자의 목록을 표시합니다.
 * 세션이 없으면 로그인 페이지로 리다이렉션합니다.
 *
 * @author yelly
 * @version 1.0
 */
public class MemberList extends DynamicHtmlProcessor {
    private final SessionManager sessionManager = new SessionManager();
    private final UserManager userManager = new UserManager();

    /**
     * HTTP 요청을 처리합니다.
     * 세션이 없으면 로그인 페이지로 리다이렉션하고,
     * 로그인된 사용자의 목록을 표시합니다.
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     */
    @Override
    public void process(HttpRequest request, HttpResponse response) {
        List<Cookie> cookies = request.getCookie();
        String sessionId = sessionManager.findSessionId(cookies, "SID");
        Optional<SessionUser> optionalSession = sessionManager.getSession(sessionId);

        if (optionalSession.isEmpty()) {
            /* http response 작성 */
            responseHeader302(response, "/login");
            response.setMessageBody(CRLF);
            response.flush();
            return;
        }

        /* 로그인 유저 정보 */
        SessionUser sessionUser = optionalSession.get();
        String userName = sessionUser.id();

        /* 기본 html 생성 */
        String template = readTemplate("/user/user-list.html");
        htmlBuilder.append(template);

        /* 가입된 User List 테이블 추가 */
        changeHtml("<!-- target user -->", "<!-- end user -->", userName);
        changeHtml("<!-- target list -->", "<!-- end list -->", createUserTable(userManager.findAllUser()));

        /* http response 작성 */
        responseHeader200(response, getContentType(request));
        responseMessage(response, htmlBuilder);

        response.flush();
    }

    /**
     * User 객체 리스트를 HTML 테이블로 변환합니다.
     * @param users User 객체 목록
     * @return HTML 테이블 문자열
     */
    private String createUserTable(Collection<User> users) {
        StringBuilder htmlBuilder = new StringBuilder();

        /* User List 작성 */
        for (User user : users) {
            htmlBuilder.append("<tr>");
            htmlBuilder.append("<td>" + user.getUserId() + "</td>");
            htmlBuilder.append("<td>" + user.getName() + "</td>");
            htmlBuilder.append("<td>" + user.getEmail() + "</td>");
            htmlBuilder.append("</tr>");
        }

        return htmlBuilder.toString();
    }
}
