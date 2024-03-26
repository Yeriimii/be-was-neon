package web;

import static utils.HttpConstant.CRLF;

import db.ArticleDatabase;
import http.Cookie;
import http.HttpRequest;
import http.HttpRequest.HttpMethod;
import http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import model.Article;
import session.SessionManager;
import session.SessionManager.SessionUser;

public class ArticleWrite extends DynamicHtmlProcessor {
    private final SessionManager sessionManager = new SessionManager();

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        List<Cookie> cookies = request.getCookie();
        String sessionId = sessionManager.findSessionId(cookies, "SID");
        Optional<SessionUser> session = sessionManager.getSession(sessionId);

        // case 1: GET 요청 (로그인 x)
        if (session.isEmpty()) {
            /* 로그인 화면으로 리다이렉션 */
            responseHeader302(response, "/login");
            response.setMessageBody(CRLF);
            response.flush();
            return;
        }

        // case 2: GET 요청 (로그인 o)
        SessionUser sessionUser = session.get(); // 세션 유저 가져오기

        if (request.getMethod().equals(HttpMethod.GET)) {
            /* 기본 html 생성 */
            String template = readTemplate("/article/write.html");
            htmlBuilder.append(template);

            /* 로그인 유저 동적 페이지 생성 */
            changeHtml("<!-- target user -->", "<!-- end user -->", sessionUser.name());

            /* 200 응답, html 출력 */
            responseHeader200(response, getContentType(request));
            responseMessage(response, htmlBuilder);

            response.flush();
            return;
        }

        // case 3: POST 요청 (로그인 o)
        Article article = createArticle(request, sessionUser.id());
        ArticleDatabase.add(article);

        responseHeader302(response, "/");
        response.setMessageBody(CRLF);

        response.flush();
    }

    private Article createArticle(HttpRequest request, String userId) {
        String body = request.getParameter("article-body");

        return new Article(body, userId, LocalDateTime.now());
    }
}