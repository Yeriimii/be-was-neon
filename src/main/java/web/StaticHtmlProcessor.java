package web;

import static utils.ResourceHandler.*;

import http.HttpRequest;
import http.HttpResponse;
import java.io.File;

public class StaticHtmlProcessor extends HttpProcessor {
    @Override
    public void process(HttpRequest request, HttpResponse response) {
        byte[] resource = getBytes(request);

        responseHeader200(response, getContentType(request));
        responseMessage(response, resource);

        response.flush();
    }

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
