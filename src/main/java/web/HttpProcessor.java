package web;

import static utils.ResourceHandler.*;

import http.HttpRequest;
import http.HttpResponse;
import http.HttpStatus;

/**
 * HTTP 요청을 처리하는 추상 클래스입니다.
 * Processor를 구현하고 있으며, 기본적인 HTTP 응답을 생성하는 메서드를 제공합니다.
 *
 * @author yelly
 * @version 1.0
 */
public abstract class HttpProcessor implements Processor {
    public static final String BASIC_HTTP_VERSION = "HTTP/1.1";
    public static final String BASIC_CHAR_SET = "utf-8";

    /**
     * HTTP 요청을 처리합니다.
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     */
    @Override
    public void process(HttpRequest request, HttpResponse response) {
    }

    /**
     * HTTP 200 상태 코드를 포함한 응답 헤더를 설정합니다.
     * @param response HTTP 응답 객체
     * @param contentType 컨텐츠 타입
     */
    public void responseHeader200(HttpResponse response, String contentType) {
        response.setHttpVersion(BASIC_HTTP_VERSION)
                .setStatusCode(HttpStatus.STATUS_OK)
                .setContentType(contentType)
                .setCharset(BASIC_CHAR_SET);
    }

    /**
     * HTTP 302 상태 코드를 포함한 응답 헤더를 설정합니다.
     * @param response HTTP 응답 객체
     * @param location 리다이렉션 위치
     */
    public void responseHeader302(HttpResponse response, String location) { // TODO: HttpResponse 에 sendRedirect() 만들기
        response.setHttpVersion(BASIC_HTTP_VERSION)
                .setStatusCode(HttpStatus.STATUS_FOUND)
                .setLocation(location)
                .setCharset(BASIC_CHAR_SET);
    }

    /**
     * 바이트 배열 형태의 리소스를 응답 메시지에 설정합니다.
     * @param response HTTP 응답 객체
     * @param resource 바이트 배열 형태의 리소스
     */
    public void responseMessage(HttpResponse response, byte[] resource) {
        response.setContentLength(resource.length)
                .setMessageBody(resource);
    }

    /**
     * StringBuilder 형태의 문자열을 응답 메시지에 설정합니다.
     * @param response HTTP 응답 객체
     * @param builder StringBuilder 객체
     */
    public void responseMessage(HttpResponse response, StringBuilder builder) {
        response.setContentLength(builder.toString().getBytes().length)
                .setMessageBody(builder.toString());

        builder.setLength(0); // StringBuilder 초기화
    }

    /**
     * HTTP 요청의 컨텐츠 타입을 가져옵니다.
     * @param request HTTP 요청 객체
     * @return 컨텐츠 타입
     */
    public String getContentType(HttpRequest request) {
        String extension = getExtension(request.getRequestURI());
        return FILE_EXTENSION_MAP.getOrDefault(extension, "text/html");
    }
}
