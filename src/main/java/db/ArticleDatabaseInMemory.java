package db;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import model.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 메모리 내부 데이터베이스를 사용하는 Article 데이터베이스 클래스입니다.
 *
 * @author yelly
 * @version 1.0
 */
public class ArticleDatabaseInMemory {
    private static final Logger logger = LoggerFactory.getLogger(ArticleDatabaseInMemory.class);
    private static final AtomicLong sequence = new AtomicLong(0L);
    private static final Long INCREASE_STEP = 1L;
    private static final Map<Long, Article> articles = new ConcurrentHashMap<>();

    /**
     * 주어진 Article을 데이터베이스에 추가합니다.
     *
     * @param article 추가할 Article 객체
     */
    public static void add(Article article) {
        articles.put(getNextArticleId(), article);
        logger.debug("[Database] Success Add Article={}", article.body());
    }

    /**
     * 다음 Article의 ID를 생성하고 반환합니다.
     *
     * @return 다음 Article의 ID
     */
    public static long getNextArticleId() {
        return sequence.addAndGet(INCREASE_STEP);
    }

    /**
     * 주어진 ID에 해당하는 Article을 데이터베이스에서 찾아 반환합니다.
     *
     * @param id 찾을 Article의 ID
     * @return ID에 해당하는 Article 객체
     */
    public static Article findById(long id) {
        return articles.get(id);
    }

    /**
     * 주어진 사용자가 작성한 최신 Article을 데이터베이스에서 찾아 반환합니다.
     *
     * @param userId 작성자의 ID
     * @return Optional<Article> 객체
     */
    public static Optional<Article> findLatest(String userId) {
        return articles.values().stream()
                .filter(article -> article.isCreatedBy(userId))
                .sorted(Comparator.comparing(Article::createdAt).reversed())
                .limit(1)
                .findAny();
    }

    /**
     * 데이터베이스의 모든 Article을 지웁니다.
     */
    public static void clear() {
        articles.clear();
        sequence.set(0L);
    }
}
