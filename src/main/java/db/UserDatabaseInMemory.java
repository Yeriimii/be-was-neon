package db;

import java.util.Optional;
import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 메모리 내부 데이터베이스를 사용하는 User 데이터베이스 클래스입니다.
 *
 * @author yelly
 * @version 1.0
 */
public class UserDatabaseInMemory {
    private static final Logger logger = LoggerFactory.getLogger(UserDatabaseInMemory.class);
    private static final Map<String, User> users = new HashMap<>();

    /**
     * 주어진 User 객체를 데이터베이스에 추가합니다.
     *
     * @param user 추가할 User 객체
     */
    public static void addUser(User user) {
        users.put(user.getUserId(), user);
        logger.debug("[Database] Success Add User !");
    }

    /**
     * 주어진 사용자 ID에 해당하는 사용자를 데이터베이스에서 찾아 반환합니다.
     *
     * @param userId 찾을 사용자의 ID
     * @return Optional<User> 객체
     */
    public static Optional<User> findUserById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    /**
     * 데이터베이스에 저장된 모든 사용자를 반환합니다.
     *
     * @return 사용자 목록
     */
    public static Collection<User> findAll() {
        return users.values().stream().toList();
    }

    /**
     * 데이터베이스의 모든 사용자를 삭제합니다.
     */
    public static void clear() {
        users.clear();
    }
}
