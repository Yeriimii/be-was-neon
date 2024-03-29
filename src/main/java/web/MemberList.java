package web;

import static utils.HttpConstant.CRLF;

import db.UserDatabaseInMemory;
import http.Cookie;
import http.HttpRequest;
import http.HttpResponse;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import model.User;
import session.SessionManager;
import session.SessionManager.SessionUser;

public class MemberList extends DynamicHtmlProcessor {
    private final SessionManager sessionManager = new SessionManager();

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
        changeHtml("<!-- target list -->", "<!-- end list -->", createUserTable(UserDatabaseInMemory.findAll()));

        /* http response 작성 */
        responseHeader200(response, getContentType(request));
        responseMessage(response, htmlBuilder);

        response.flush();
    }

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
