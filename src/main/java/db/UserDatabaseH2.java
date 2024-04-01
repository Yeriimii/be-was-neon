package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import model.User;
import org.h2.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDatabaseH2 implements UserDatabase {
    private static final Logger logger = LoggerFactory.getLogger(UserDatabaseH2.class);
    private final DataSource dataSource = DataSourceUtil.getDataSource();

    @Override
    public void add(User user) {
        String sql = "insert into users(user_id, password, name, email) values (?, ?, ?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("DB INSERT FAILED={}", e.getMessage());
        } finally {
            close(con, pstmt, null);
        }
    }

    @Override
    public Optional<User> findById(String userId) {
        String sql = "select * from users where user_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = convertToUser(rs);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.error("DB SELECT FAILED={}", e.getMessage());
        } finally {
            close(con, pstmt, rs);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        String sql = "select * from users";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            return makeUserList(rs);
        } catch (SQLException e) {
            logger.error("DB SELECT FAILED={}", e.getMessage());
        } finally {
            close(con, pstmt, rs);
        }
        return new ArrayList<>();
    }

    @Override
    public void delete(String userId) {
        String sql = "delete from users where user_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("DB SELECT FAILED={}", e.getMessage());
        } finally {
            close(con, pstmt, null);
        }
    }

    private List<User> makeUserList(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            User user = convertToUser(rs);
            users.add(user);
        }
        return users;
    }

    private User convertToUser(ResultSet rs) throws SQLException {
        String userId = rs.getString("user_id");
        String password = rs.getString("password");
        String username = rs.getString("name");
        String email = rs.getString("email");

        return new User(userId, password, username, email);
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeSilently(con);
        JdbcUtils.closeSilently(stmt);
        JdbcUtils.closeSilently(rs);
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        logger.debug("connection={}, class={}", con, con.getClass());
        return con;
    }
}
