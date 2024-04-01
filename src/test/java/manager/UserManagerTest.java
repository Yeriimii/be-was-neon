package manager;

import static org.assertj.core.api.Assertions.*;

import db.UserDatabaseInMemory;
import java.util.Optional;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UserManagerTest {
    private final UserManager userManager = new UserManager();
    private final User testUser = new User("yelly", "yelly123", "yelly", "yelly@code.com");;

    @BeforeEach
    void setUp() {
        UserDatabaseInMemory.addUser(testUser);
    }

    @AfterEach
    void clear() {
        UserDatabaseInMemory.clear();
    }

    @DisplayName("등록된 유저의 아이디 yelly, 패스워드를 yelly123에 대해 로그인에 성공하면 해당 유저를 반환한다")
    @ParameterizedTest(name = "입력값: id={0} password={1}")
    @CsvSource(value = "yelly, yelly123")
    void login_success(String id, String password) {
        // when
        Optional<User> optionalUser = userManager.login(id, password);

        // then
        assertThat(optionalUser.isPresent()).isTrue();
        assertThat(optionalUser.get()).isEqualTo(testUser);
    }

    @DisplayName("등록된 유저의 패스워드를 잘못 입력하면 로그인에 실패한다")
    @ParameterizedTest(name = "입력값: id={0} password={1}")
    @CsvSource(value = "yelly, fail")
    void login_fail_when_mistake_typing_password(String id, String failPassword) {
        // when
        Optional<User> optionalUser = userManager.login(id, failPassword);

        // then
        assertThat(optionalUser.isEmpty()).isTrue();
    }

    @DisplayName("등록되지 않은 유저에 대해 로그인에 실패한다")
    @ParameterizedTest(name = "입력값: id={0} password={1}")
    @CsvSource(value = "noRegister, yelly123")
    void login_fail_when_find_unregistered_user(String noRegisteredId, String password) {
        // when
        Optional<User> optionalUser = userManager.login(noRegisteredId, password);

        // then
        assertThat(optionalUser.isEmpty()).isTrue();
    }
}