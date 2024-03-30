package db;

import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import model.Article;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArticleDatabaseH2Test {

    private final ArticleDatabaseH2 db = new ArticleDatabaseH2();

    @AfterEach
    void delete() {
        db.delete(1L);
        db.delete(2L);
        db.resetIdWithOne();
    }

    @DisplayName("게시물을 데이터베이스에 추가하면 자동으로 id가 0부터 1씩 증가한다")
    @Test
    void addArticle_auto_increment_id() throws SQLException {
        // given
        String body = "This is a test text";
        String createdBy = "yelly";
        LocalDateTime createdAt = LocalDateTime.of(2024, 3, 30, 0, 0, 0);
        String imagePath = "";

        Article article = new Article(body, createdBy, createdAt, imagePath);

        // when
        Article saveArticle = db.add(article);

        // then
        assertThat(saveArticle.id()).isEqualTo(1L);
        assertThat(saveArticle.body()).isEqualTo(body);
        assertThat(saveArticle.createdBy()).isEqualTo(createdBy);
        assertThat(saveArticle.createdAt()).isEqualTo(createdAt);
        assertThat(saveArticle.imagePath()).isEqualTo(imagePath);
    }

    @DisplayName("id가 2에 해당하고 userId가 monster 인 게시물을 찾을 수 있다")
    @Test
    void findArticleById() throws SQLException {
        // given
        String body = "This is a test text";
        String createdByYelly = "yelly";
        String createdByMonster = "monster";
        LocalDateTime createdAt = LocalDateTime.of(2024, 3, 30, 0, 0, 0);
        String imagePath = "";

        Article article1 = new Article(body, createdByYelly, createdAt, imagePath);
        Article article2 = new Article(body, createdByMonster, createdAt, imagePath);

        /* DB에 2개 저장 */
        db.add(article1);
        db.add(article2);

        // when
        Optional<Article> findArticle = db.findById(2L);

        // then
        assertThat(findArticle).isPresent();
        assertThat(findArticle.get()).extracting("createdBy").isEqualTo("monster");
    }

    @DisplayName("yelly 유저의 게시물 2개 중 가장 최신 게시물 12월 31일자 게시물 1개를 찾을 수 있다")
    @Test
    void findLatest() throws SQLException {
        // given
        String body = "This is a test text";
        String createdBy = "yelly";
        LocalDateTime createdAt1 = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        LocalDateTime createdAt2 = LocalDateTime.of(2024, 12, 31, 0, 0, 0);
        String imagePath = "";

        Article article1 = new Article(body, createdBy, createdAt1, imagePath); // 가장 오래된 게시물
        Article article2 = new Article(body, createdBy, createdAt2, imagePath); // 가장 최신 게시물

        /* DB에 2개 저장 */
        db.add(article1);
        db.add(article2);

        // when
        Optional<Article> latestArticle = db.findLatest("yelly");

        // then
        assertThat(latestArticle).isPresent();
        assertThat(latestArticle.get()).extracting("createdAt").isEqualTo(createdAt2);
    }
}