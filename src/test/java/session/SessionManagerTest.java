package session;

import static org.assertj.core.api.Assertions.*;

import http.Cookie;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import session.SessionManager.SessionUser;

class SessionManagerTest {

    private final SessionManager sessionManager = new SessionManager();
    private final User testUser = new User("yelly", "yelly123", "yelly", "y@test.com");

    @AfterEach
    void clear() {
        sessionManager.clear();
    }

    @DisplayName("세션을 생성하면 UUID를 생성한다")
    @Test
    void createSession() {
        // given & when
        String session = sessionManager.createSession();

        // then
        assertThat(session).isNotEmpty();
        assertThat(session).containsPattern(".+-.+-.+-.+-.+");
    }

    @DisplayName("세션 test-1234-5678, User 객체를 세션에 저장할 수 있다")
    @Test
    void enroll() {
        // given
        String sessionId = "test-1234-5678";

        // when
        sessionManager.enroll(sessionId, testUser);
        Optional<SessionUser> session = sessionManager.getSession(sessionId);

        // then
        assertThat(session.isPresent()).isTrue();
        assertThat(session.get().id()).isEqualTo(testUser.getUserId());
    }

    @DisplayName("test-1234-5678에 해당하는 세션을 제거한 뒤 해당 세션 아이디로 세션을 찾으면 빈 Optional 을 반환한다")
    @Test
    void delete() {
        // given
        String sessionId = "test-1234-5678";
        sessionManager.enroll(sessionId, testUser);

        // when
        sessionManager.delete(sessionId);
        Optional<SessionUser> session = sessionManager.getSession(sessionId);

        // then
        assertThat(session.isEmpty()).isTrue();
        assertThatThrownBy(session::get).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("쿠키(SID=test-test)로부터 testUser에 해당하는 세션 아이디(test-test)를 가져올 수 있다")
    @Test
    void findSessionId_success() {
        // given
        String sessionKey = "SID";
        String sessionId = "test-test";

        /* 테스트 유저와 매핑되는 세션 아이디를 쿠키에 설정 */
        Cookie cookie1 = new Cookie(sessionKey, sessionId);
        Cookie cookie2 = new Cookie("MyKey", "MyId"); // 더미 쿠키

        List<Cookie> cookies = List.of(cookie1, cookie2);

        sessionManager.enroll(sessionKey, testUser); // 세션에 유저 등록

        // when
        String result = sessionManager.findSessionId(cookies, "SID");

        // then
        assertThat(result).isEqualTo("test-test");
    }
}