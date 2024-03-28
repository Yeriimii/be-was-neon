package web;

import static utils.ResourceHandler.*;

import http.HttpRequest;
import http.HttpResponse;
import http.HttpStatus;

public abstract class HttpProcessor {
    public static final String BASIC_HTTP_VERSION = "HTTP/1.1";
    public static final String BASIC_CHAR_SET = "utf-8";

    abstract void process(HttpRequest request, HttpResponse response);

    public void service(HttpRequest request, HttpResponse response) {
        process(request, response);
    }

    public byte[] getBytes(HttpRequest request) { // TODO: StaticHttpProcessor 로 이동시키기
        String extension = getExtension(request.getRequestURI());

        if (FILE_EXTENSION_MAP.containsKey(extension)) { // /index.html, /min.css, ...
            return read(STATIC_PATH + request.getRequestURI());
        }
        if (request.getPath().equals("/")) { // localhost:8080/
            return read(STATIC_PATH + "/" + INDEX_HTML);
        }
        return read(STATIC_PATH + request.getPath() + "/" + INDEX_HTML); // /registration
    }

    public void responseHeader200(HttpResponse response, String contentType) {
        response.setHttpVersion(BASIC_HTTP_VERSION)
                .setStatusCode(HttpStatus.STATUS_OK)
                .setContentType(contentType)
                .setCharset(BASIC_CHAR_SET);
    }

    public void responseHeader302(HttpResponse response, String location) { // TODO: HttpResponse 에 sendRedirect() 만들기
        response.setHttpVersion(BASIC_HTTP_VERSION)
                .setStatusCode(HttpStatus.STATUS_FOUND)
                .setLocation(location)
                .setCharset(BASIC_CHAR_SET);
    }

    public void responseMessage(HttpResponse response, byte[] resource) {
        response.setContentLength(resource.length)
                .setMessageBody(resource);
    }

    public void responseMessage(HttpResponse response, StringBuilder builder) {
        response.setContentLength(builder.toString().getBytes().length)
                .setMessageBody(builder.toString());

        builder.setLength(0); // StringBuilder 초기화
    }

    public String getContentType(HttpRequest request) {
        String extension = getExtension(request.getRequestURI());
        return FILE_EXTENSION_MAP.getOrDefault(extension, "text/html");
    }
}
