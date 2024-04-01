package manager;

import db.ArticleDatabase;
import db.ArticleDatabaseH2;
import java.util.Optional;
import model.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArticleManager {
    private static final Logger logger = LoggerFactory.getLogger(ArticleManager.class);
    private final ArticleDatabase articleDatabase = new ArticleDatabaseH2();

    public Article addArticle(Article article) {
        return articleDatabase.add(article);
    }

    public Optional<Article> findLatestArticle(String userId) {
        return articleDatabase.findLatest(userId);
    }

    public Optional<Article> findArticleById(int articleId) {
        return articleDatabase.findById(articleId);
    }
}
