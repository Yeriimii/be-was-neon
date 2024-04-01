package webserver;

import static http.HttpStatus.INTERNAL_SERVER_ERROR;
import static http.HttpStatus.STATUS_NOT_FOUND;
import static utils.HttpRequestConverter.convertToHttpRequest;
import static utils.HttpResponseConverter.convertToHttpResponse;

import error.ResponseErrorUtil;
import http.HttpRequest;
import http.HttpResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.Processor;
import web.UriMapper;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final Socket connection;
    private HttpRequest request;
    private HttpResponse response;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            // request + response 분리
            request = convertToHttpRequest(in);
            response = convertToHttpResponse(out);

            // HttpRequest를 처리할 Processor 찾기
            Optional<Processor> optionalProcessor = findProcessor(request.getPath());

            // Processor가 존재하면 로직 실행, 없으면 404 status 반환
            optionalProcessor.ifPresentOrElse(processor -> processor.process(request, response),
                    ResponseErrorUtil.forward(response, STATUS_NOT_FOUND));
        } catch (IOException e) {
            logger.error("[REQUEST HANDLER ERROR] {}", e.getMessage());
        } catch (Exception e) {
            logger.error("[REQUEST HANDLER ERROR] Internal Server Error {}", e.getMessage());
            ResponseErrorUtil.forward(response, INTERNAL_SERVER_ERROR).run(); // 500 server error
        }
    }

    public Optional<Processor> findProcessor(String uri) {
        return UriMapper.getInstance().getProcessor(uri);
    }
}
