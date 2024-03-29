package db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserDatabaseH2Test {

    private final UserDatabaseH2 db = new UserDatabaseH2();

    @AfterEach
    void delete() {
        db.delete("tester");
        db.delete("tester2");
    }

    @DisplayName("'tester'라는 아이디의 유저를 H2 데이터베이스에 저장할 수 있다")
    @Test
    void add() {
        // given
        User user = new User("tester", "123", "testUser", "test@test.com");

        // when
        db.add(user);
        Optional<User> optionalUser = db.findById("tester");

        // then
        assertThat(optionalUser.isPresent()).isTrue();
        assertThat(optionalUser.get()).isEqualTo(user);
    }

    @DisplayName("'tester2'라는 아이디를 가진 유저를 찾을 수 있다")
    @Test
    void findUserById_success() {
        // given
        User user = new User("tester2", "123", "testUser", "test@test.com");
        db.add(user);

        // when
        Optional<User> optionalUser = db.findById("tester2");

        // then
        assertThat(optionalUser.isPresent()).isTrue();
        assertThat(optionalUser.get()).isEqualTo(user);
    }

    @DisplayName("'noUser'라는 존재하지 않는 아이디로 유저를 찾을 수 없다")
    @Test
    void findUserById_not_found() {
        // when
        Optional<User> optionalUser = db.findById("noUser");

        // then
        assertThat(optionalUser.isEmpty()).isTrue();
    }

    @DisplayName("'tester', 'tester2' 아이디를 갖는 모든 유저를 찾을 수 있다")
    @Test
    void findAll() {
        // given
        User tester1 = new User("tester", "123", "yelly", "yelly@test.com");
        User tester2 = new User("tester2", "123", "jelly", "jelly@test.com");
        db.add(tester1);
        db.add(tester2);

        // when
        List<User> users = db.findAll();

        // then
        assertThat(users.size()).isEqualTo(2);
        assertThat(users).contains(tester1, tester2);
    }
}