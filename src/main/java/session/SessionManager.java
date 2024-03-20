package session;

import http.Cookie;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpConstant;

public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private static final Map<String, Object> session = new ConcurrentHashMap<>();

    public String createSession() {
        logger.debug("[SESSION MANAGER] create session");
        return UUID.randomUUID().toString();
    }

    public void enroll(String sessionId, Object value) {
        logger.debug("[SESSION MANAGER] register");
        session.put(sessionId, value);
    }

    public Optional<Object> getSession(String sessionId) {
        logger.debug("[SESSION MANAGER] get session = {}", sessionId);
        return Optional.ofNullable(session.get(sessionId));
    }

    public void delete(String sessionId) {
        session.remove(sessionId);
    }

    public void clear() {
        session.clear();
    }

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
}
