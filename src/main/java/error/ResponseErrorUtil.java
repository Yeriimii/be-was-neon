package error;

import http.HttpResponse;
import http.HttpStatus;
import web.ErrorHtmlProcessor;

public class ResponseErrorUtil {

    private ResponseErrorUtil() {
    }

    public static Runnable forward(HttpResponse response, HttpStatus status) {
        return () -> new ErrorHtmlProcessor().responseError(response, status);
    }
}
