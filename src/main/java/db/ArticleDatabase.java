package db;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import model.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArticleDatabase {
    private static final Logger logger = LoggerFactory.getLogger(ArticleDatabase.class);
    private static final AtomicLong sequence = new AtomicLong(0L);
    private static final Long INCREASE_STEP = 1L;
    private static final Map<Long, Article> articles = new ConcurrentHashMap<>();

    public static void add(Article article) {
        articles.put(getNextArticleId(), article);
        logger.debug("[Database] Success Add Article={}", article.body());
    }

    private static long getNextArticleId() {
        return sequence.addAndGet(INCREASE_STEP);
    }

    public static Article findById(long id) {
        return articles.get(id);
    }

    public static Optional<Article> findLatest(String userId) {
        return articles.values().stream()
                .filter(article -> article.isCreatedBy(userId))
                .sorted(Comparator.comparing(Article::createdAt).reversed())
                .limit(1)
                .findAny();
    }

    public static void clear() {
        articles.clear();
        sequence.set(0L);
    }
}
