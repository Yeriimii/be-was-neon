package model;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Article {
    private final String body;
    private final String createdBy; // 작성자 아이디
    private final LocalDateTime createdAt;

    public Article(String body, String createdBy, LocalDateTime createdAt) {
        this.body = body;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Article) obj;
        return Objects.equals(this.body, that.body) &&
                Objects.equals(this.createdBy, that.createdBy) &&
                Objects.equals(this.createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body, createdBy, createdAt);
    }

    @Override
    public String toString() {
        return "Article[" +
                "body=" + body + ", " +
                "createdBy=" + createdBy + ", " +
                "createdAt=" + createdAt + ']';
    }

    public boolean isCreatedBy(String userId) {
        return createdBy.equals(userId);
    }
}
