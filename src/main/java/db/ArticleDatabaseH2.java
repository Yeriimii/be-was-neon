package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.sql.DataSource;
import model.Article;
import org.h2.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArticleDatabaseH2 implements ArticleDatabase {
    private static final Logger logger = LoggerFactory.getLogger(ArticleDatabaseH2.class);
    private final DataSource dataSource = DataSourceUtil.getDataSource();

    @Override
    public Article add(Article article) {
        String sql = "INSERT INTO ARTICLE(BODY, CREATED_BY, CREATED_AT, IMAGE_PATH) VALUES (?, ?, ?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, article.body());
            pstmt.setString(2, article.createdBy());
            pstmt.setTimestamp(3, Timestamp.valueOf(article.createdAt()));
            pstmt.setString(4, article.imagePath());
            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                long generatedId = rs.getLong(1);
                article.setId(generatedId);
            }
        } catch (SQLException e) {
            logger.error("DB INSERT FAILED={}", e.getMessage());
        } finally {
            close(con, pstmt, rs);
        }
        return article;
    }

    @Override
    public Optional<Article> findById(long articleId) {
        String sql = "SELECT * FROM ARTICLE WHERE ARTICLE_ID = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, articleId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Article article = convertToArticle(rs);
                return Optional.of(article);
            }
        } catch (SQLException e) {
            logger.error("DB SELECT FAILED={}", e.getMessage());
        } finally {
            close(con, pstmt, rs);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Article> findLatest(String userId) {
        String sql = "SELECT * FROM ARTICLE WHERE CREATED_BY = ? ORDER BY CREATED_AT DESC LIMIT 1";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Article article = convertToArticle(rs);
                return Optional.of(article);
            }
        } catch (SQLException e) {
            logger.error("DB SELECT FAILED={}", e.getMessage());
        } finally {
            close(con, pstmt, rs);
        }
        return Optional.empty();
    }

    public void delete(long articleId) {
        String sql = "DELETE FROM ARTICLE WHERE ARTICLE_ID = ?";
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, articleId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("DB SELECT FAILED={}", e.getMessage());
        } finally {
            close(con, pstmt, null);
        }
    }

    public void resetIdWithOne() {
        String sql = "ALTER TABLE ARTICLE ALTER COLUMN ARTICLE_ID RESTART WITH 1";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("DB SELECT FAILED={}", e.getMessage());
        } finally {
            close(con, pstmt, null);
        }
    }

    private Article convertToArticle(ResultSet rs) throws SQLException {
        long articleId = rs.getLong("article_id");
        String body = rs.getString("body");
        String createdBy = rs.getString("created_by");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        String imagePath = rs.getString("image_path");

        return new Article(body, createdBy, createdAt, imagePath).setId(articleId);
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
