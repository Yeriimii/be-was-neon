package utils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static http.HttpRequest.*;
import static utils.HttpConstant.*;
import static utils.HttpRequestParser.*;

import http.Cookie;
import http.HttpRequest;
import http.HttpRequestBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestConverter {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestConverter.class);
    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final Predicate<String> CHECK_ALL_CONTENT_RECEIVED = request -> parseRequestBody(request).length()
            == parseContentLength(request);
    private static final Predicate<String> CHECK_END_OF_BOUNDARY = request -> request.endsWith("--" + CRLF);
    private static final HttpRequestBuilder REQUEST_BUILDER = new HttpRequestBuilder();

    public static HttpRequest convertToHttpRequest(InputStream in) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            /* request 전부 읽기 */
            readBytes(in, bos);

            /* 4가지 파싱 작업을 시킬 스레드 풀 생성 */
            ExecutorService executorPool = Executors.newFixedThreadPool(4);

            /* 비동기 파싱 객체 생성 */
            CompletableFuture<String> requestLineFuture = CompletableFuture.supplyAsync(
                    () -> decode(parseRequestLine(bos.toString(UTF_8))), executorPool);

            CompletableFuture<Map<String, String>> requestHeaderFuture = CompletableFuture.supplyAsync(
                    () -> parseHeader(bos.toString(UTF_8)), executorPool);

            CompletableFuture<String> requestBodyFuture = CompletableFuture.supplyAsync(
                    () -> decode(parseRequestBody(bos.toString(UTF_8))), executorPool);

            CompletableFuture<List<MultiPart>> multiPartFuture = CompletableFuture.supplyAsync(
                    () -> parseMultiPart(bos.toString(UTF_8)), executorPool);

            /* 비동기 파싱 실행 */
            CompletableFuture.allOf(requestLineFuture, requestHeaderFuture, requestBodyFuture, multiPartFuture).join();

            /* 파싱 결과 -> 비동기 빌드 */
            CompletableFuture<Void> buildFuture = buildHttpRequest(requestLineFuture, requestBodyFuture,
                    requestHeaderFuture, multiPartFuture);

            buildFuture.join();

            buildFuture.thenRun(executorPool::shutdown);
        } catch (IOException e) {
            logger.error("[REQUEST CONVERTER ERROR] {}", e.getMessage());
        }

        return REQUEST_BUILDER.build();
    }

    private static CompletableFuture<Void> buildHttpRequest(CompletableFuture<String> requestLineFuture,
                                                            CompletableFuture<String> requestBodyFuture,
                                                            CompletableFuture<Map<String, String>> requestHeaderFuture,
                                                            CompletableFuture<List<MultiPart>> multiPartFuture) {
        /* 파싱 후 HttpRequestBuilder 작업 생성 */
        CompletableFuture<Void> methodBuilder = requestLineFuture.thenAccept(
                requestLine -> REQUEST_BUILDER.setMethod(extractMethod(requestLine)));

        CompletableFuture<Void> uriBuilder = requestLineFuture.thenAccept(
                requestLine -> REQUEST_BUILDER.setRequestURI(extractRequestURI(requestLine)));

        CompletableFuture<Void> versionBuilder = requestLineFuture.thenAccept(
                requestLine -> REQUEST_BUILDER.setHttpVersion(extractHttpVersion(requestLine)));

        CompletableFuture<Void> paramsBuilder = requestLineFuture.thenAcceptBoth(requestBodyFuture,
                (requestLine, requestBody) -> REQUEST_BUILDER.setParameter(
                        parseParams(requestLine + NEWLINE + requestBody)));

        CompletableFuture<Void> headerBuilder = requestHeaderFuture.thenAccept(REQUEST_BUILDER::setHeaders);

        CompletableFuture<Void> cookieBuilder = requestHeaderFuture.thenAccept(
                header -> REQUEST_BUILDER.setCookies(extractCookies(header)));

        CompletableFuture<Void> partsBuilder = multiPartFuture.thenAccept(REQUEST_BUILDER::setParts);

        /* 비동기 빌드 Future 객체 전체 반환 */
        return CompletableFuture.allOf(methodBuilder, uriBuilder, versionBuilder, paramsBuilder, headerBuilder,
                cookieBuilder, partsBuilder);
    }

    private static void readBytes(InputStream in, ByteArrayOutputStream bos) throws IOException {
        byte[] buffer = new byte[8192]; // 8KB (InputStream 의 DEFAULT_BUFFER_SIZE)
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            /* 버퍼 만큼 읽기 */
            bos.write(buffer, 0, bytesRead);

            /* 읽은 만큼 문자열로 변환 */
            String request = bos.toString(UTF_8);

            /* 탈출 조건: multipart/form-data EOL 확인 -> Content-Length 만큼 읽었는지 확인 */
            if (CHECK_END_OF_BOUNDARY.or(CHECK_ALL_CONTENT_RECEIVED).test(request)) {
                break;
            }
        }
    }

    private static String decode(String request) {
        return URLDecoder.decode(request, UTF_8);
    }

    private static HttpMethod extractMethod(String requestLine) {
        return HttpMethod.valueOf(requestLine.split(SP)[METHOD_INDEX]);
    }

    private static HttpRequestUri extractRequestURI(String requestLine) {
        return new HttpRequestUri(requestLine.split(SP)[URL_INDEX]);
    }

    private static HttpVersion extractHttpVersion(String requestLine) {
        return new HttpVersion(requestLine.split(SP)[HTTP_VERSION_INDEX]);
    }

    private static List<Cookie> extractCookies(Map<String, String> headers) {
        String cookies = headers.get("Cookie");
        Map<String, String> cookieMap = parseParams(cookies);

        return cookieMap.entrySet().stream()
                .map(entry -> new Cookie(entry.getKey(), entry.getValue()))
                .toList();
    }
}
