package manager;

import db.ArticleDatabase;
import db.ArticleDatabaseH2;
import java.util.Optional;
import model.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 게시글 관리자 클래스입니다.
 *
 * @author yelly
 * @version 1.0
 */
public class ArticleManager {
    private static final Logger logger = LoggerFactory.getLogger(ArticleManager.class);
    private final ArticleDatabase articleDatabase = new ArticleDatabaseH2();

    /**
     * 게시글을 추가합니다.
     *
     * @param article 추가할 게시글
     * @return 추가된 게시글
     */
    public Article addArticle(Article article) {
        return articleDatabase.add(article);
    }

    /**
     * 특정 사용자의 최신 게시글을 찾습니다.
     *
     * @param userId 사용자 ID
     * @return 최신 게시글 (Optional)
     */
    public Optional<Article> findLatestArticle(String userId) {
        return articleDatabase.findLatest(userId);
    }

    /**
     * 게시글 ID를 기반으로 게시글을 찾습니다.
     *
     * @param articleId 게시글 ID
     * @return 해당 게시글 (Optional)
     */
    public Optional<Article> findArticleById(int articleId) {
        return articleDatabase.findById(articleId);
    }
}
