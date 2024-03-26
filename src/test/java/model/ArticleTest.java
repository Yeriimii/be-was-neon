package model;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import session.SessionManager.SessionUser;

class ArticleTest {

    private final SessionUser author = new SessionUser("yelly", "yyellyy", "yelly@test.com");

    @DisplayName("유저 아이디 yelly로 Article을 생성할 수 있다")
    @Test
    void create_article() {
        // given
        String textBody = "This is test article.\nTest is very easy.";

        LocalDateTime createdAt = LocalDateTime.parse("2024-03-26 00:00:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // when
        Article article = new Article(textBody, author.id(), createdAt);

        // then
        assertThat(article.body()).isEqualTo(textBody);
        assertThat(article.createdBy()).isEqualTo(author.id());
        assertThat(article.createdAt()).isEqualTo(createdAt);
    }

    @DisplayName("유저 아이디 yelly로 작성한 게시물이면 true를 반환하고, 그렇지 않으면 false를 반환한다")
    @Test
    void isCreatedBy() {
        // given
        String textBody = "This is test article.\nTest is very easy.";

        LocalDateTime createdAt = LocalDateTime.parse("2024-03-26 00:00:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Article article = new Article(textBody, author.id(), createdAt);

        // when
        boolean isCreatedByYelly = article.isCreatedBy("yelly");
        boolean isCreatedByGhost = article.isCreatedBy("ghost");

        // then
        assertThat(isCreatedByYelly).isTrue();
        assertThat(isCreatedByGhost).isFalse();
    }
}