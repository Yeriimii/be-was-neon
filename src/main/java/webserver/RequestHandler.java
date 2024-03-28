package webserver;

import static http.HttpStatus.*;
import static utils.HttpRequestConverter.*;
import static utils.HttpResponseConverter.*;

import java.net.Socket;

import http.HttpResponse;
import http.HttpRequest;
import web.UriMapper;
import web.HttpProcessor;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final Socket connection;
    private final HttpRequest request;
    private HttpResponse response;
    private final Runnable responseEmpty = () -> {
        response.setHttpVersion("HTTP/1.1");
        response.setStatusCode(STATUS_NOT_FOUND);
        response.setContentType("text/plain");
        response.setCharset("utf-8");
        response.setContentLength(0);
        response.setMessageBody("not found");
        response.flush();
    };

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        request = convertToHttpRequest(connection);
        response = convertToHttpResponse(connection);
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        logger.debug("URI = {}", request.getRequestURI());

            // HttpRequest를 처리할 Processor 찾기
            Optional<HttpProcessor> optionalProcessor = findProcessor(request.getPath());

        // Processor가 존재하면 로직 실행, 없으면 404 status 반환
        optionalProcessor.ifPresentOrElse(this::handle, responseEmpty);
    }

    public void handle(HttpProcessor processor) {
        logger.debug("[REQUEST HANDLER] success service");
        processor.service(request, response);
    }

    public Optional<HttpProcessor> findProcessor(String uri) {
        return UriMapper.getInstance().getProcessor(uri);
    }
}
