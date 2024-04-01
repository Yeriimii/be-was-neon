package model;

import java.util.Objects;

/**
 * 사용자 정보를 나타내는 클래스입니다.
 *
 * @author yelly
 * @version 1.0
 */
public class User {
    private String userId;
    private String password;
    private String name;
    private String email;

    /**
     * 사용자 정보를 초기화하는 생성자입니다.
     * @param userId 사용자 ID
     * @param password 비밀번호
     * @param name 이름
     * @param email 이메일
     */
    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    /**
     * 사용자 ID를 반환합니다.
     * @return 사용자 ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 비밀번호를 반환합니다.
     * @return 비밀번호
     */
    public String getPassword() {
        return password;
    }

    /**
     * 사용자 이름을 반환합니다.
     * @return 사용자 이름
     */
    public String getName() {
        return name;
    }

    /**
     * 이메일을 반환합니다.
     * @return 이메일
     */
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return Objects.equals(getUserId(), user.getUserId()) && Objects.equals(getPassword(),
                user.getPassword()) && Objects.equals(getName(), user.getName()) && Objects.equals(
                getEmail(), user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getPassword(), getName(), getEmail());
    }
}
