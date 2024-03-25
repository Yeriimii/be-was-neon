package web;

import http.Cookie;
import http.HttpRequest;
import http.HttpResponse;
import java.util.List;
import java.util.Optional;
import session.SessionManager;
import session.SessionManager.SessionUser;
import utils.ResourceHandler;

public class DynamicHtmlProcessor extends HttpProcessor {

    private final SessionManager sessionManager = new SessionManager();
    public final StringBuilder htmlBuilder = new StringBuilder();

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        List<Cookie> cookies = request.getCookie();

        /* 쿠키로부터 세션 아이디 가져오기 */
        String sessionId = sessionManager.findSessionId(cookies, "SID");

        /* 세션 아이디로부터 세션 유저(Optional) 가져오기 */
        Optional<SessionUser> sessionUser = sessionManager.getSession(sessionId);

        /* 기본 HTML 작성 */
        String htmlTemplate = readTemplate(request.getRequestURI());
        htmlBuilder.append(htmlTemplate);

        /* 세션 유저가 존재하면 user id 표시 */
        if (sessionUser.isPresent()) {
            SessionUser user = sessionUser.get();
            String dynamicHtml = createUserProfile(user.id());
            changeHtml("<!-- target -->", "<!-- end -->", dynamicHtml);
        }

        /* http response 작성 */
        responseHeader200(response, getContentType(request));
        responseMessage(response, htmlBuilder);

        response.flush();
    }

    public String readTemplate(String templateName) {
        return ResourceHandler.readTemplate(templateName);
    }

    /**
     * HTML 문자열에서 주어진 시작 문자열(start)과 끝 문자열(end) 사이의 내용을 지우고, 대체할 새로운 템플릿(template)을 삽입한다.
     *
     * @param start    대체할 문자열의 시작 부분
     * @param end      대체할 문자열의 마지막 부분
     * @param template 대체할 새로운 템플릿 문자열
     */
    public void changeHtml(String start, String end, String template) {
        htmlBuilder.replace(htmlBuilder.indexOf(start) + start.length(), htmlBuilder.indexOf(end), "");
        htmlBuilder.insert(htmlBuilder.indexOf(start), template);
    }

    private String createUserProfile(String userId) {
        return String.format("""
                <li class="header__menu__item">
                  <div class="comment__item__user">
                    <img class="comment__item__user__img" />
                    <p class="comment__item__user__nickname" style="padding: 15px"> %s </p>
                  </div >
                </li >
                <li class="header__menu__item">
                <a class="btn btn_contained btn_size_s" href="/article">글쓰기</a>
                </li >
                <li class="header__menu__item">
                <a href ="/logout" id="logout-btn" class="btn btn_danger btn_size_s">로그아웃</a>
                </li >
                """, userId);
    }
}
