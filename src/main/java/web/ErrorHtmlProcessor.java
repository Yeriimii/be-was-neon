package web;

import static utils.ResourceHandler.ERROR_PATH;
import static utils.ResourceHandler.read;

import http.HttpResponse;
import http.HttpStatus;
import java.io.File;

public class ErrorHtmlProcessor extends HttpProcessor {

    public void responseError(HttpResponse response, HttpStatus status) {
        byte[] errorPage = read(ERROR_PATH + getErrorPagePath(status));
        responseErrorHeader(response, status);
        responseMessage(response, errorPage);
        response.flush();
    }

    private String getErrorPagePath(HttpStatus status) {
        return String.format(File.separator + "%d.html", status.code);
    }

    private void responseErrorHeader(HttpResponse response, HttpStatus status) {
        response.setHttpVersion(BASIC_HTTP_VERSION)
                .setStatusCode(status)
                .setContentType("text/html")
                .setCharset(BASIC_CHAR_SET);
    }
}
