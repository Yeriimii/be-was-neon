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

/**
 * H2 데이터베이스를 사용하는 User 데이터베이스 클래스입니다.
 *
 * @author yelly
 * @version 1.0
 */
public class UserDatabaseH2 implements UserDatabase {
    private static final Logger logger = LoggerFactory.getLogger(UserDatabaseH2.class);
    private final DataSource dataSource = DataSourceUtil.getDataSource();

    /**
     * 주어진 User 객체를 데이터베이스에 추가합니다.
     *
     * @param user 추가할 User 객체
     */
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

    /**
     * 주어진 사용자 ID에 해당하는 사용자를 데이터베이스에서 찾아 반환합니다.
     *
     * @param userId 찾을 사용자의 ID
     * @return Optional<User> 객체
     */
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

    /**
     * 데이터베이스에 저장된 모든 사용자를 반환합니다.
     *
     * @return 사용자 목록
     */
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

    /**
     * 주어진 사용자 ID에 해당하는 사용자를 데이터베이스에서 삭제합니다.
     *
     * @param userId 삭제할 사용자의 ID
     */
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

    /**
     * ResultSet에서 User 객체 목록을 생성합니다.
     *
     * @param rs ResultSet 객체
     * @return User 객체 목록
     * @throws SQLException SQL 예외 발생 시
     */
    private List<User> makeUserList(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            User user = convertToUser(rs);
            users.add(user);
        }
        return users;
    }

    /**
     * ResultSet에서 User 객체로 변환합니다.
     *
     * @param rs ResultSet 객체
     * @return User 객체
     * @throws SQLException SQL 예외 발생 시
     */
    private User convertToUser(ResultSet rs) throws SQLException {
        String userId = rs.getString("user_id");
        String password = rs.getString("password");
        String username = rs.getString("name");
        String email = rs.getString("email");

        return new User(userId, password, username, email);
    }

    /**
     * 데이터베이스 연결을 닫습니다.
     *
     * @param con  Connection 객체
     * @param stmt Statement 객체
     * @param rs   ResultSet 객체
     */
    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeSilently(con);
        JdbcUtils.closeSilently(stmt);
        JdbcUtils.closeSilently(rs);
    }

    /**
     * 데이터베이스 연결을 가져옵니다.
     *
     * @return Connection 객체
     * @throws SQLException SQL 예외 발생 시
     */
    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        logger.debug("connection={}, class={}", con, con.getClass());
        return con;
    }
}
