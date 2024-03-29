package login;

import db.UserDatabaseInMemory;
import java.util.Optional;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginManager {
    private static final Logger logger = LoggerFactory.getLogger(LoginManager.class);

    public Optional<User> login(String id, String password) {
        Optional<User> optionalUser = UserDatabaseInMemory.findUserById(id);

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
}
