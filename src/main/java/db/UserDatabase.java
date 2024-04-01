package db;

import java.util.Collection;
import java.util.Optional;
import model.User;

public interface UserDatabase {
    void add(User user);

    Collection<User> findAll();

    Optional<User> findById(String userId);

    void delete(String userId);
}
