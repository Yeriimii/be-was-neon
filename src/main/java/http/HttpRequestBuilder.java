package http;

import http.HttpRequest.HttpMethod;
import http.HttpRequest.HttpRequestUri;
import http.HttpRequest.HttpVersion;
import http.HttpRequest.MultiPart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestBuilder {
    private HttpMethod method;
    private HttpRequestUri requestURI;
    private HttpVersion httpVersion;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameter = new HashMap<>();
    private List<Cookie> cookies = new ArrayList<>();
    private List<MultiPart> parts = new ArrayList<>();

    public HttpRequest build() {
        return new HttpRequest(
                method, requestURI, httpVersion, headers, parameter, cookies, parts
        );
    }

    public HttpRequestBuilder setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpRequestBuilder setRequestURI(HttpRequestUri requestURI) {
        this.requestURI = requestURI;
        return this;
    }

    public HttpRequestBuilder setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    public HttpRequestBuilder setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpRequestBuilder setParameter(Map<String, String> parameter) {
        this.parameter = parameter;
        return this;
    }

    public HttpRequestBuilder setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    public HttpRequestBuilder setParts(List<MultiPart> parts) {
        this.parts = parts;
        return this;
    }
}
