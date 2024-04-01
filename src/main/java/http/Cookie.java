package http;

import static utils.HttpConstant.EQUAL;
import static utils.HttpConstant.SP;
import static utils.HttpConstant.SPLITTER;

/**
 * 쿠키 클래스는 HTTP 쿠키를 표현합니다.
 *
 * @author yelly
 * @version 1.0
 */
public class Cookie {

    private final String cookieKey;
    private String cookieValue;

    /**
     * 주어진 쿠키 키와 값을 가지고 쿠키 객체를 생성합니다.
     *
     * @param cookieKey   쿠키 키
     * @param cookieValue 쿠키 값
     */
    public Cookie(String cookieKey, String cookieValue) {
        this.cookieKey = cookieKey;
        this.cookieValue = cookieValue + SPLITTER + SP;
    }

    /**
     * 쿠키의 경로를 설정합니다.
     *
     * @param path 경로
     */
    public void setPath(String path) {
        cookieValue += "Path=" + path + SPLITTER + SP;
    }

    /**
     * 쿠키의 최대 수명을 설정합니다.
     *
     * @param maxAge 최대 수명
     */
    public void setMaxAge(int maxAge) {
        cookieValue += "Max-Age=" + maxAge + SPLITTER + SP;
    }

    /**
     * 쿠키를 문자열로 반환합니다.
     *
     * @return 쿠키 문자열
     */
    public String getCookie() {
        return cookieKey + EQUAL + cookieValue;
    }

    /**
     * 쿠키의 키를 반환합니다.
     *
     * @return 쿠키 키
     */
    public String getCookieKey() {
        return cookieKey;
    }

    /**
     * 쿠키의 값만을 반환합니다.
     *
     * @return 쿠키 값
     */
    public String getCookieValue() {
        return cookieValue;
    }
}
