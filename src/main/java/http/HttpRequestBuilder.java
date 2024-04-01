package http;

import http.HttpRequest.HttpMethod;
import http.HttpRequest.HttpRequestUri;
import http.HttpRequest.HttpVersion;
import http.HttpRequest.MultiPart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP 요청 빌더 클래스입니다.
 *
 * @author yelly
 * @version 1.0
 */
public class HttpRequestBuilder {
    private HttpMethod method;
    private HttpRequestUri requestURI;
    private HttpVersion httpVersion;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameter = new HashMap<>();
    private List<Cookie> cookies = new ArrayList<>();
    private List<MultiPart> parts = new ArrayList<>();

    /**
     * HTTP 요청 객체를 생성하여 반환합니다.
     *
     * @return HTTP 요청 객체
     */
    public HttpRequest build() {
        return new HttpRequest(
                method, requestURI, httpVersion, headers, parameter, cookies, parts
        );
    }

    /**
     * HTTP 요청 메소드를 설정합니다.
     *
     * @param method HTTP 요청 메소드
     * @return 현재 HttpRequestBuilder 객체
     */
    public HttpRequestBuilder setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    /**
     * HTTP 요청 URI를 설정합니다.
     *
     * @param requestURI HTTP 요청 URI
     * @return 현재 HttpRequestBuilder 객체
     */
    public HttpRequestBuilder setRequestURI(HttpRequestUri requestURI) {
        this.requestURI = requestURI;
        return this;
    }

    /**
     * HTTP 버전을 설정합니다.
     *
     * @param httpVersion HTTP 버전
     * @return 현재 HttpRequestBuilder 객체
     */
    public HttpRequestBuilder setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    /**
     * HTTP 헤더를 설정합니다.
     *
     * @param headers HTTP 헤더 맵
     * @return 현재 HttpRequestBuilder 객체
     */
    public HttpRequestBuilder setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * HTTP 요청 파라미터를 설정합니다.
     *
     * @param parameter HTTP 요청 파라미터 맵
     * @return 현재 HttpRequestBuilder 객체
     */
    public HttpRequestBuilder setParameter(Map<String, String> parameter) {
        this.parameter = parameter;
        return this;
    }

    /**
     * 쿠키를 설정합니다.
     *
     * @param cookies 쿠키 리스트
     * @return 현재 HttpRequestBuilder 객체
     */
    public HttpRequestBuilder setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    /**
     * 멀티파트를 설정합니다.
     *
     * @param parts 멀티파트 리스트
     * @return 현재 HttpRequestBuilder 객체
     */
    public HttpRequestBuilder setParts(List<MultiPart> parts) {
        this.parts = parts;
        return this;
    }
}
