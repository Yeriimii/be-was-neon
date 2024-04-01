package error;

import http.HttpResponse;
import http.HttpStatus;
import web.ErrorHtmlProcessor;

/**
 * 응답 오류 유틸리티 클래스는 응답을 전달할 때 발생하는 오류를 처리하는 데 사용됩니다.
 *
 * @author yelly
 * @version 1.0
 */
public class ResponseErrorUtil {

    private ResponseErrorUtil() {
    }

    /**
     * 지정된 상태 코드로 응답을 전달하는 작업을 생성합니다.
     *
     * @param response 응답 객체
     * @param status   HTTP 상태 코드
     * @return 응답 오류를 처리하는 Runnable 작업
     */
    public static Runnable forward(HttpResponse response, HttpStatus status) {
        return () -> new ErrorHtmlProcessor().responseError(response, status);
    }
}
