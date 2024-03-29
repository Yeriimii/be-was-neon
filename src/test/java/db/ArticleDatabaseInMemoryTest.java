package db;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import model.Article;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArticleDatabaseInMemoryTest {

    @AfterEach
    void clear() {
        ArticleDatabaseInMemory.clear();
    }

    @DisplayName("Article 하나를 저장하면 데이터베이스에 key가 1부터 저장된다")
    @Test
    void add_success() {
        // given
        LocalDateTime createdAt1 = LocalDateTime.parse("2024-03-26 00:00:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Article testArticle1 = new Article("test1", "yelly", createdAt1, null);

        // when
        ArticleDatabaseInMemory.add(testArticle1);
        Article firstSaveArticle = ArticleDatabaseInMemory.findById(1);

        // then
        assertThat(firstSaveArticle.body()).isEqualTo("test1");
    }

    @DisplayName("Article 2개를 저장하면 데이터베이스에 key가 1씩 증가되며 저장된다")
    @Test
    void add_success_continuously() {
        // given
        LocalDateTime createdAt1 = LocalDateTime.parse("2024-03-26 00:00:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime createdAt2 = LocalDateTime.parse("2024-03-26 00:00:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Article testArticle1 = new Article("test1", "yelly", createdAt1, null);
        Article testArticle2 = new Article("test2", "trolli", createdAt2, null);

        // when
        ArticleDatabaseInMemory.add(testArticle1);
        ArticleDatabaseInMemory.add(testArticle2);
        Article firstSaveArticle = ArticleDatabaseInMemory.findById(1);
        Article secondSaveArticle = ArticleDatabaseInMemory.findById(2);

        // then
        assertThat(firstSaveArticle.body()).isEqualTo("test1");
        assertThat(secondSaveArticle.body()).isEqualTo("test2");
    }

    @DisplayName("yelly가 작성한 게시물 2개 중 가장 최근에 작성한 3월 1일자 게시물을 찾을 수 있다")
    @Test
    void findLatest_success() {
        // given
        LocalDateTime earliestTime = LocalDateTime.parse("2024-01-01 00:00:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime latestTime = LocalDateTime.parse("2024-03-01 00:00:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 가장 오래돤 글
        Article testArticle1 = new Article("oldest", "yelly", earliestTime, null);

        // 가장 최신 글
        Article testArticle2 = new Article("latest", "yelly", latestTime, null);

        ArticleDatabaseInMemory.add(testArticle1);
        ArticleDatabaseInMemory.add(testArticle2);

        // when
        Article latest = ArticleDatabaseInMemory.findLatest("yelly").get();

        // then
        assertThat(latest.body()).isEqualTo("latest");
        assertThat(latest.createdBy()).isEqualTo("yelly");
        assertThat(latest.createdAt()).isEqualTo(latestTime);
    }

    @DisplayName("yelly가 작성한 게시물이 없으면 비어있는 Optional을 반환한다")
    @Test
    void findLatest_fail() {
        // when
        Optional<Article> latest = ArticleDatabaseInMemory.findLatest("yelly");

        // then
        assertThat(latest.isEmpty()).isTrue();
    }
}