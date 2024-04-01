package web;

import static utils.ResourceHandler.*;

import http.HttpRequest;
import http.HttpResponse;
import java.io.File;

/**
 * 정적 HTML 파일을 처리하는 클래스입니다.
 * HTTPProcessor 클래스를 상속하며, 요청에 대한 처리를 담당합니다.
 *
 * @author yelly
 * @version 1.0
 */
public class StaticHtmlProcessor extends HttpProcessor {

    /**
     * HTTP 요청 및 응답에 대한 처리를 수행합니다.
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     */
    @Override
    public void process(HttpRequest request, HttpResponse response) {
        byte[] resource = getBytes(request);

        responseHeader200(response, getContentType(request));
        responseMessage(response, resource);

        response.flush();
    }

    /**
     * HTTP 요청으로부터 바이트 배열 형태의 리소스를 가져옵니다.
     * @param request HTTP 요청 객체
     * @return 요청에 해당하는 리소스의 바이트 배열
     */
    public byte[] getBytes(HttpRequest request) {
        String extension = getExtension(request.getRequestURI());

        if (FILE_EXTENSION_MAP.containsKey(extension)) { // 파일 확장자가 존재하면
            if (request.getRequestURI().startsWith(MEDIA_PATH)) {
                return read(BASE_PATH + request.getRequestURI());
            }
            return read(BASE_PATH + STATIC_PATH + request.getRequestURI());
        }
        if (request.getPath().equals("/")) { // localhost:8080/
            return read(BASE_PATH + STATIC_PATH + File.separator + INDEX_HTML);
        }
        return read(BASE_PATH + STATIC_PATH + request.getPath() + File.separator + INDEX_HTML); // /registration
    }
}
