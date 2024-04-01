package db;

import java.util.Optional;
import model.Article;

public interface ArticleDatabase {
    Article add(Article article);

    Optional<Article> findById(long articleId);

    Optional<Article> findLatest(String userId);
}
