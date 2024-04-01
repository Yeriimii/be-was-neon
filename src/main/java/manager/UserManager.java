package manager;

import db.UserDatabase;
import db.UserDatabaseH2;
import java.util.Collection;
import java.util.Optional;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 사용자 관리자 클래스입니다.
 *
 * @author yelly
 * @version 1.0
 */
public class UserManager {
    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private final UserDatabase userDatabase = new UserDatabaseH2();

    /**
     * 사용자를 회원가입합니다.
     *
     * @param user 회원가입할 사용자 정보
     * @return 회원가입이 성공하면 true, 실패하면 false를 반환합니다.
     */
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

    /**
     * 사용자 로그인을 처리합니다.
     *
     * @param id       사용자 아이디
     * @param password 사용자 비밀번호
     * @return 로그인이 성공하면 해당 사용자 정보를, 실패하면 빈 Optional을 반환합니다.
     */
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

    /**
     * 모든 사용자를 조회합니다.
     *
     * @return 모든 사용자 정보를 담은 Collection
     */
    public Collection<User> findAllUser() {
        return userDatabase.findAll();
    }

    /**
     * 사용자를 삭제합니다.
     *
     * @param user 삭제할 사용자
     */
    public void deleteUser(User user) {
        userDatabase.delete(user.getUserId());
    }
}
