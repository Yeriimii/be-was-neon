package db;

import java.util.Optional;
import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDatabaseInMemory {
    private static final Logger logger = LoggerFactory.getLogger(UserDatabaseInMemory.class);
    private static final Map<String, User> users = new HashMap<>();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
        logger.debug("[Database] Success Add User !");
    }

    public static Optional<User> findUserById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public static Collection<User> findAll() {
        return users.values().stream().toList();
    }

    public static void clear() {
        users.clear();
    }
}
