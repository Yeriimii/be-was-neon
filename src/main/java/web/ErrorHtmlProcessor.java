package web;

import static utils.ResourceHandler.ERROR_PATH;
import static utils.ResourceHandler.read;

import http.HttpResponse;
import http.HttpStatus;
import java.io.File;

/**
 * 에러 HTML 페이지를 처리하는 클래스입니다.
 * HttpProcessor를 상속받아 HTTP 응답에 에러 페이지를 설정합니다.
 *
 * @author yelly
 * @version 1.0
 */
public class ErrorHtmlProcessor extends HttpProcessor {

    /**
     * HTTP 응답에 에러 페이지를 설정합니다.
     * @param response HTTP 응답 객체
     * @param status HTTP 상태 코드
     */
    public void responseError(HttpResponse response, HttpStatus status) {
        byte[] errorPage = read(ERROR_PATH + getErrorPagePath(status));
        responseErrorHeader(response, status);
        responseMessage(response, errorPage);
        response.flush();
    }

    /**
     * 에러 페이지의 파일 경로를 가져옵니다.
     * @param status HTTP 상태 코드
     * @return 에러 페이지의 파일 경로
     */
    private String getErrorPagePath(HttpStatus status) {
        return String.format(File.separator + "%d.html", status.code);
    }

    /**
     * HTTP 응답에 에러 헤더를 설정합니다.
     * @param response HTTP 응답 객체
     * @param status HTTP 상태 코드
     */
    private void responseErrorHeader(HttpResponse response, HttpStatus status) {
        response.setHttpVersion(BASIC_HTTP_VERSION)
                .setStatusCode(status)
                .setContentType("text/html")
                .setCharset(BASIC_CHAR_SET);
    }
}
