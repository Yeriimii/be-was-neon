package utils;

import static http.HttpRequest.*;
import static utils.HttpConstant.*;
import static utils.HttpRequestParser.*;

import http.Cookie;
import http.HttpRequest;
import http.HttpRequestBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
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

    public static HttpRequest convertToHttpRequest(Socket connection) {
        String header = makeOneLine(connection);

        return convert(header);
    }

    public static String makeOneLine(Socket connection) {
        StringBuilder requestHeader = new StringBuilder();
        try {
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            int contentLength = 0;

            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                if (contentLength == 0) {
                    contentLength = calculateContentLength(line);
                }
                requestHeader.append(decode(line)).append(CRLF);
            }

            if (contentLength > 0) {
                char[] body = new char[contentLength];
                reader.read(body, 0, contentLength);
                requestHeader.append(body);
            }
        } catch (IOException e) {
            logger.error("[REQUEST CONVERTER ERROR] {}", e.getMessage());
        }
        return decode(requestHeader.toString());
    }

    private static int calculateContentLength(String line) {
        int contentLength = 0;

        if (!line.startsWith("Content-Length")) {
            return contentLength;
        }

        return Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
    }

    private static String decode(String header) {
        try {
            return URLDecoder.decode(header, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpRequest convert(String header) {
        HttpRequestBuilder builder = new HttpRequestBuilder();

        /* request line */
        String[] requestLine = splitRequestLine(header);

        HttpMethod method = getMethod(requestLine);
        String requestURI = getRequestURI(requestLine);
        String httpVersion = getHttpVersion(requestLine);

        builder.setMethod(method);
        builder.setRequestURI(requestURI);
        builder.setHttpVersion(httpVersion);

        /* headers */
        builder.setHeaders(parseHeader(header));

        /* parameter */
        if (method == HttpMethod.GET) {
            builder.setParameter(parseParams(getFullUri(requestLine)));
        }
        if (method == HttpMethod.POST) {
            builder.setParameter(parseParams(getHttpBody(header)));
        }

        return builder.build();
    }

    private static String[] splitRequestLine(String header) {
        return parseRequestLine(header).split(SP); // 'GET /registration?id=test&password=1234 HTTP/1.1'
    }

    private static HttpMethod getMethod(String[] requestLine) {
        return HttpMethod.valueOf(requestLine[METHOD_INDEX]);
    }

    private static String getRequestURI(String[] requestLine) {
        return requestLine[URL_INDEX].split(QUERY_PARAM_SYMBOL)[0]; // '/registration?id=test&password=1234'에서 ? 기준 앞 부분
    }

    private static String getHttpVersion(String[] requestLine) {
        return requestLine[HTTP_VERSION_INDEX];
    }

    private static List<Cookie> extractCookies(Map<String, String> headers) {
        String cookies = headers.get("Cookie");
        Map<String, String> cookieMap = parseParams(cookies);

        return cookieMap.entrySet().stream()
                .map(entry -> new Cookie(entry.getKey(), entry.getValue()))
                .toList();
    }
}
