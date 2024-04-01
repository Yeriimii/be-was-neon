package model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 게시글 정보를 나타내는 클래스입니다.
 *
 * @author yelly
 * @version 1.0
 */
public final class Article {
    private long id;
    private final String body;
    private final String createdBy; // 작성자 아이디
    private final LocalDateTime createdAt;
    private final String imagePath;

    /**
     * 게시글을 초기화하는 생성자입니다.
     *
     * @param body       본문
     * @param createdBy  작성자 아이디
     * @param createdAt  작성일시
     * @param imagePath  이미지 경로
     */
    public Article(String body, String createdBy, LocalDateTime createdAt, String imagePath) {
        this.body = body;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.imagePath = imagePath;
    }

    /**
     * 게시글 ID를 반환합니다.
     *
     * @return 게시글 ID
     */
    public long id() {
        return id;
    }

    /**
     * 본문을 반환합니다.
     *
     * @return 본문
     */
    public String body() {
        return body;
    }

    /**
     * 작성자 아이디를 반환합니다.
     *
     * @return 작성자 아이디
     */
    public String createdBy() {
        return createdBy;
    }

    /**
     * 작성일시를 반환합니다.
     *
     * @return 작성일시
     */
    public LocalDateTime createdAt() {
        return createdAt;
    }

    /**
     * 이미지 경로를 반환합니다.
     *
     * @return 이미지 경로
     */
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

    /**
     * 특정 사용자가 게시글을 작성한 것인지 여부를 확인합니다.
     *
     * @param userId 사용자 ID
     * @return 작성자인 경우 true, 아닌 경우 false를 반환합니다.
     */
    public boolean isCreatedBy(String userId) {
        return createdBy.equals(userId);
    }

    /**
     * 게시글에 이미지가 포함되어 있는지 여부를 확인합니다.
     *
     * @return 이미지가 포함되어 있는 경우 true, 아닌 경우 false를 반환합니다.
     */
    public boolean isImageExist() {
        return imagePath != null && !imagePath.isEmpty();
    }

    /**
     * 게시글 ID를 설정합니다.
     *
     * @param id 게시글 ID
     * @return 현재 Article 객체를 반환합니다.
     */
    public Article setId(long id) {
        this.id = id;
        return this;
    }
}
