package db;

import static org.assertj.core.api.Assertions.*;

import java.util.Collection;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserDatabaseInMemoryTest {

    @BeforeEach
    void resetDatabase() {
        UserDatabaseInMemory.clear();
    }

    @DisplayName("testId를 가진 User 객체를 추가하면 데이트베이스에 Key가 User의 id로 추가된다")
    @Test
    void addUser() {
        // given
        User user = new User("testId", "1234", "testName", "testEmail");

        // when
        UserDatabaseInMemory.addUser(user);

        // then
        assertThat(UserDatabaseInMemory.findUserById("testId").get().getUserId()).isEqualTo("testId");
    }

    @DisplayName("User의 id로 User르 찾을 수 있다")
    @Test
    void findUserById() {
        // given
        User userA = new User("userA", "1234", "testA", "testEmailA");
        User userB = new User("userB", "7890", "testB", "testEmailB");

        UserDatabaseInMemory.addUser(userA);
        UserDatabaseInMemory.addUser(userB);

        // when
        User findUserA = UserDatabaseInMemory.findUserById("userA").get();
        User findUserB = UserDatabaseInMemory.findUserById("userB").get();

        // then
        assertThat(findUserA).isEqualTo(userA);
        assertThat(findUserA.getName()).isEqualTo("testA");

        assertThat(findUserB).isEqualTo(userB);
        assertThat(findUserB.getName()).isEqualTo("testB");
    }

    @DisplayName("userA, userB, userC 세 개의 User를 모두 찾을 수 있다")
    @Test
    void findAll() {
        // given
        User userA = new User("userA", "1234", "testA", "testEmailA");
        User userB = new User("userB", "7890", "testB", "testEmailB");
        User userC = new User("userC", "5555", "testC", "testEmailC");

        UserDatabaseInMemory.addUser(userA);
        UserDatabaseInMemory.addUser(userB);
        UserDatabaseInMemory.addUser(userC);

        // when
        Collection<User> users = UserDatabaseInMemory.findAll();

        // then
        assertThat(users.size()).isEqualTo(3);
        assertThat(users).contains(userA)
                .contains(userB)
                .contains(userC);
    }
}