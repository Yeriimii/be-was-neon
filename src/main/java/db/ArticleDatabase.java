package db;

import java.sql.SQLException;
import java.util.Optional;
import model.Article;

public interface ArticleDatabase {
    Article add(Article article) throws SQLException;

    Optional<Article> findById(long articleId) throws SQLException;

    Optional<Article> findLatest(String userId) throws SQLException;
}
