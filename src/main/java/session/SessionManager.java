package session;

import http.Cookie;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpConstant;

/**
 * 세션을 관리하는 클래스입니다.
 *
 * @author yelly
 * @version 1.0
 */
public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private static final Map<String, SessionUser> session = new ConcurrentHashMap<>();

    /**
     * 세션을 생성합니다.
     * @return 생성된 세션 ID
     */
    public String createSession() {
        logger.debug("[SESSION MANAGER] create session");
        return UUID.randomUUID().toString();
    }

    /**
     * 세션에 사용자를 등록합니다.
     * @param sessionId 세션 ID
     * @param user 사용자 정보
     */
    public void enroll(String sessionId, User user) {
        logger.debug("[SESSION MANAGER] register");
        session.put(sessionId, new SessionUser(user.getUserId(), user.getName(), user.getEmail()));
    }

    /**
     * 주어진 세션 ID에 해당하는 세션을 반환합니다.
     * @param sessionId 세션 ID
     * @return 세션 사용자 정보(Optional)
     */
    public Optional<SessionUser> getSession(String sessionId) {
        logger.debug("[SESSION MANAGER] get session = {}", sessionId);
        return Optional.ofNullable(session.get(sessionId));
    }

    /**
     * 주어진 세션 ID에 해당하는 세션을 삭제합니다.
     * @param sessionId 세션 ID
     */
    public void delete(String sessionId) {
        session.remove(sessionId);
    }

    /**
     * 모든 세션을 삭제합니다.
     */
    public void clear() {
        session.clear();
    }

    /**
     * 주어진 쿠키에서 세션 ID를 찾아 반환합니다.
     * @param cookies 쿠키 목록
     * @param sessionKey 세션 키
     * @return 세션 ID
     */
    public String findSessionId(List<Cookie> cookies, String sessionKey) {
        return cookies.stream()
                .filter(cookie -> cookie.getCookieKey().equals(sessionKey))
                .map(Cookie::getCookieValue)
                .map(this::cleanSessionId)
                .findAny()
                .orElse("");
    }

    private String cleanSessionId(String sessionId) {
        return sessionId.replace(HttpConstant.SPLITTER, "").trim();
    }

    /**
     * 세션 사용자 정보를 나타내는 불변 레코드입니다.
     */
    public record SessionUser(String id, String name, String email) {
    }
}