package http;

import static utils.HttpConstant.QUERY_PARAM_SYMBOL;
import static utils.HttpRequestParser.parseParams;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private final HttpMethod method;
    private final HttpRequestUri requestURI;
    private final HttpVersion httpVersion;
    private final Map<String, String> headers;
    private final Map<String, String> parameter;
    private final List<MultiPart> parts;

    protected HttpRequest(HttpMethod method, HttpRequestUri requestURI, HttpVersion httpVersion,
                          Map<String, String> headers, Map<String, String> parameter, List<MultiPart> parts) {
        this.method = method;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.parameter = parameter;
        this.parts = parts;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestURI() {
        return requestURI.uri();
    }

    public String getHttpVersion() {
        return httpVersion.version();
    }

    public String getHeader(String headerName) {
        return headers.getOrDefault(headerName, "");
    }

    public String getParameter(String parameterName) {
        return parameter.getOrDefault(parameterName, "");
    }

    public List<Cookie> getCookie() {
        String cookies = getHeader("Cookie");
        Map<String, String> cookieMap = parseParams(cookies);

        return cookieMap.entrySet().stream()
                .map(entry -> new Cookie(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<MultiPart> getParts() {
        return Collections.unmodifiableList(parts);
    }

    public enum HttpMethod {
        GET("GET"),
        POST("POST");

        public final String name;

        HttpMethod(String name) {
            this.name = name;
        }
    }

    public record HttpRequestUri(String uri) {
    }

    public record HttpVersion(String version) {
    }

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
