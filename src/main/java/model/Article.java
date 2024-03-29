package model;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Article {
    private final String body;
    private final String createdBy; // 작성자 아이디
    private final LocalDateTime createdAt;
    private final String imagePath;

    public Article(String body, String createdBy, LocalDateTime createdAt, String imagePath) {
        this.body = body;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.imagePath = imagePath;
    }

    public String body() {
        return body;
    }

    public String createdBy() {
        return createdBy;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public String imagePath() {
        return imagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Article article)) {
            return false;
        }
        return Objects.equals(body, article.body) && Objects.equals(createdBy, article.createdBy)
                && Objects.equals(createdAt, article.createdAt) && Objects.equals(imagePath, article.imagePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body, createdBy, createdAt, imagePath);
    }

    @Override
    public String toString() {
        return "Article{" +
                "body='" + body + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", image='" + imagePath + '\'' +
                '}';
    }

    public boolean isCreatedBy(String userId) {
        return createdBy.equals(userId);
    }

    public boolean isImageExist() {
        return imagePath != null && !imagePath.isEmpty();
    }
}
