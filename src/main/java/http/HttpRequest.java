package http;

import static utils.HttpConstant.QUERY_PARAM_SYMBOL;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * HTTP 요청 클래스입니다.
 *
 * @author yelly
 * @version 1.0
 */
public class HttpRequest {
    private final HttpMethod method;
    private final HttpRequestUri requestURI;
    private final HttpVersion httpVersion;
    private final Map<String, String> headers;
    private final Map<String, String> parameter;
    private final List<Cookie> cookies;
    private final List<MultiPart> parts;

    protected HttpRequest(HttpMethod method, HttpRequestUri requestURI, HttpVersion httpVersion,
                          Map<String, String> headers, Map<String, String> parameter,
                          List<Cookie> cookies, List<MultiPart> parts) {
        this.method = method;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.parameter = parameter;
        this.cookies = cookies;
        this.parts = parts;
    }

    /**
     * HTTP 메소드를 반환합니다.
     *
     * @return HTTP 메소드
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * 요청 URI를 반환합니다.
     *
     * @return 요청 URI
     */
    public String getRequestURI() {
        return requestURI.uri();
    }

    /**
     * URI에서 쿼리 파라미터를 제외한 경로를 반환합니다.
     *
     * @return 경로
     */
    public String getPath() {
        return requestURI.uri().split(QUERY_PARAM_SYMBOL)[0]; // 쿼리 파라미터를 제외한 부분
    }

    /**
     * HTTP 버전을 반환합니다.
     *
     * @return HTTP 버전
     */
    public String getHttpVersion() {
        return httpVersion.version();
    }

    /**
     * 특정 헤더의 값을 반환합니다.
     *
     * @param headerName 헤더 이름
     * @return 헤더 값
     */
    public String getHeader(String headerName) {
        return headers.getOrDefault(headerName, "");
    }

    /**
     * 특정 파라미터의 값을 반환합니다.
     *
     * @param parameterName 파라미터 이름
     * @return 파라미터 값
     */
    public String getParameter(String parameterName) {
        return parameter.getOrDefault(parameterName, "");
    }

    /**
     * 쿠키 리스트를 반환합니다.
     *
     * @return 쿠키 리스트 (수정 불가능)
     */
    public List<Cookie> getCookie() {
        return Collections.unmodifiableList(cookies);
    }

    /**
     * 멀티파트 요청 중 특정 파트를 반환합니다.
     *
     * @param partName 파트 이름
     * @return 멀티파트 객체
     */
    public MultiPart getPart(String partName) {
        return parts.stream()
                .filter(part -> part.name.equals(partName))
                .findAny()
                .orElse(null);
    }

    /**
     * HTTP 요청 메소드 열거형입니다.
     */
    public enum HttpMethod {
        GET("GET"),
        POST("POST");

        public final String name;

        HttpMethod(String name) {
            this.name = name;
        }
    }

    /**
     * HTTP 요청 URI를 표현하는 불변 레코드 클래스입니다.
     */
    public record HttpRequestUri(String uri) {
    }

    /**
     * HTTP 버전을 표현하는 불변 레코드 클래스입니다.
     */
    public record HttpVersion(String version) {
    }

    /**
     * 멀티파트를 표현하는 불변 레코드 클래스입니다.
     */
    public record MultiPart(String name, String submittedFileName, String contentType, byte[] partBody) {

        @Override
        public String toString() {
            return "MultiPart[" +
                    "name=" + name + ", " +
                    "submittedFileName=" + submittedFileName + ", " +
                    "contentType=" + contentType + ", " +
                    "hasPartBody=" + (partBody.length > 0) + ']';
        }
    }
}
