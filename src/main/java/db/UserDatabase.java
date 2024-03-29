package db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import model.User;

public interface UserDatabase {
    void addUser(User user) throws SQLException;

    Collection<User> findAll() throws SQLException;

    Optional<User> findUserById(String userId);
}
