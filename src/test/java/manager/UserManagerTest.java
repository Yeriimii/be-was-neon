package manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Optional;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UserManagerTest {
    private final UserManager userManager = new UserManager();
    private final User testUser = new User("yelly", "yelly123", "yelly", "yelly@code.com");

    @BeforeEach
    void setUp() {
        userManager.join(testUser);
    }

    @AfterEach
    void clear() {
        userManager.deleteUser(testUser);

        // 임시 테스터
        User tempTester = new User("testId", "123", "tester", "test@test.com");
        userManager.deleteUser(tempTester);
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

    @DisplayName("회원가입을 처음하는 tester 유저에 대해 회원가입 결과는 참이다")
    @Test
    void join_success() {
        // given
        User tester = new User("testId", "123", "tester", "test@test.com");

        // when
        boolean result = userManager.join(tester);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("이미 존재하는 yelly 라는 아이디로 회원가입 시도하면 결과는 거짓이다")
    @Test
    void join_fail() {
        // given
        User tester = new User("yelly", "123", "yelly", "yelly@test.com");

        // when
        userManager.join(tester);
        boolean result = userManager.join(tester); // 중복 회원가입

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("회원가입 되어있는 2명을 모두 찾을 수 있다")
    @Test
    void findAllUser_size_2() {
        // given
        User tester = new User("testId", "123", "yelly", "yelly@test.com");
        userManager.join(tester);

        // when
        Collection<User> allUser = userManager.findAllUser();

        // then
        assertThat(allUser).size().isEqualTo(2);
        assertThat(allUser).contains(
                testUser, tester
        );
    }
}