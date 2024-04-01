package manager;

import db.UserDatabase;
import db.UserDatabaseH2;
import java.util.Collection;
import java.util.Optional;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserManager {
    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private final UserDatabase userDatabase = new UserDatabaseH2();

    public boolean join(User user) {
        String id = user.getUserId();

        if (!isExistId(id)) {
            return false;
        }

        userDatabase.add(user);
        return true;
    }

    private boolean isExistId(String id) {
        Optional<User> optionalUser = userDatabase.findById(id);
        if (optionalUser.isPresent()) {
            logger.error("이미 가입된 회원 아이디 입니다.");
            return false;
        }
        return true;
    }

    public Optional<User> login(String id, String password) {
        Optional<User> optionalUser = userDatabase.findById(id);

        /* 유저 아이디 검증 */
        if (optionalUser.isEmpty()) {
            logger.debug("[LOGIN MANAGER] Not Found Id = {}", id);
            return Optional.empty();
        }

        /* 패스워드 검증 */
        User user = optionalUser.get();
        if (!user.getPassword().equals(password)) {
            logger.debug("[LOGIN MANAGER] password error = {}", password);
            return Optional.empty();
        }

        /* 유저 반환 */
        return Optional.of(user);
    }

    public Collection<User> findAllUser() {
        return userDatabase.findAll();
    }

    public void deleteUser(User user) {
        userDatabase.delete(user.getUserId());
    }
}
