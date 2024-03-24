package web;

import static utils.HttpConstant.CRLF;

import db.Database;
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
    private final StringBuilder htmlBuilder = new StringBuilder();

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

        /* html 테이블 생성 */
        createHtmlHeader();
        createBodyTopPart();
        createUserProfile(userName);
        createTableHeader();
        createTableBody(Database.findAll());

        /* http response 작성 */
        responseHeader200(response, getContentType(request));
        responseMessage(response, htmlBuilder);

        response.flush();
    }

    private void createHtmlHeader() {
        // html header 생성
        htmlBuilder.append("<!DOCTYPE html>");
        htmlBuilder.append("<html>");
        htmlBuilder.append("<head>");
        htmlBuilder.append("<meta charset=\"UTF-8\" />");
        htmlBuilder.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />");
        htmlBuilder.append("<link href=\"../reset.css\" rel=\"stylesheet\" />");
        htmlBuilder.append("<link href=\"../global.css\" rel=\"stylesheet\" />");
        htmlBuilder.append("<link href=\"../main.css\" rel=\"stylesheet\" />");
        htmlBuilder.append("<style>");
        htmlBuilder.append("table {width: 100%; border-collapse: collapse; margin-top: 20px; box-shadow: 0 0 20px rgba(0, 0, 0, 0.1); border-radius: 10px; overflow: hidden;}");
        htmlBuilder.append("th, td {border: 1px solid #f0f0f0; padding: 12px; text-align: center; font-size: 14px;}");
        htmlBuilder.append("th {background-color: #f8f8f8;font-weight: 600;}");
        htmlBuilder.append("tbody tr:nth-child(even) {background-color: #fafafa;}");
        htmlBuilder.append("tbody tr:hover {background-color: #f5f5f5;}");
        htmlBuilder.append("</style>");
        htmlBuilder.append("</head>");
    }

    private void createBodyTopPart() {
        // html body 생성
        htmlBuilder.append("<body>");
        htmlBuilder.append("<div class=\"container\">");
        htmlBuilder.append("<header class=\"header\">");
        htmlBuilder.append("<a href=\"/\"><img src=\"../img/signiture.svg\" /></a>");
        htmlBuilder.append("<ul class=\"header__menu\">");
    }

    private void createUserProfile(String userId) {
        htmlBuilder.append("<li class=\"header__menu__item\">");
        htmlBuilder.append("<div class=\"comment__item__user\">");
        htmlBuilder.append("<img class=\"comment__item__user__img\" />");
        htmlBuilder.append("<p class=\"comment__item__user__nickname\" style=\"padding: 15px\">" + userId + "</p>");
        htmlBuilder.append("</div>");
        htmlBuilder.append("</li>");

        htmlBuilder.append("<li class=\"header__menu__item\">");
        htmlBuilder.append("<a class=\"btn btn_contained btn_size_s\" href=\"/article\">글쓰기</a>");
        htmlBuilder.append("</li>");

        htmlBuilder.append("<li class=\"header__menu__item\">");
        htmlBuilder.append("<a href=\"/logout\" id=\"logout-btn\" class=\"btn btn_danger btn_size_s\">로그아웃</a>");
        htmlBuilder.append("</li>");

        htmlBuilder.append("</ul>");
        htmlBuilder.append("</header>");
    }

    private void createTableHeader() {
        htmlBuilder.append("<div class=\"wrapper\">");
        htmlBuilder.append("<div class=\"page\">");
        htmlBuilder.append("<h2 class=\"page-title\">회원 리스트</h2>");
        htmlBuilder.append("<table>");
        htmlBuilder.append("<thead>");
        htmlBuilder.append("<tr>");
        htmlBuilder.append("<th>ID</th>");
        htmlBuilder.append("<th>이름</th>");
        htmlBuilder.append("<th>이메일</th>");
        htmlBuilder.append("</tr>");
        htmlBuilder.append("</thead>");
    }

    private void createTableBody(Collection<User> users) {
        htmlBuilder.append("<tbody>");

        /* User List 작성 */
        for (User user : users) {
            htmlBuilder.append("<tr>");
            htmlBuilder.append("<td>" + user.getUserId() + "</td>");
            htmlBuilder.append("<td>" + user.getName() + "</td>");
            htmlBuilder.append("<td>" + user.getEmail() + "</td>");
            htmlBuilder.append("</tr>");
        }

        htmlBuilder.append("</tbody>");
        htmlBuilder.append("</table>");
        htmlBuilder.append("</div>");
        htmlBuilder.append("</div>");
        htmlBuilder.append("</div>");
        htmlBuilder.append("</body>");
        htmlBuilder.append("</html>");
    }
}
