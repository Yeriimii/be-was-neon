package utils;

import static http.HttpRequest.*;
import static org.assertj.core.api.Assertions.*;

import http.HttpRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestConverterTest {

    private InputStream inputStream;

    @AfterEach
    void close() {
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("HTTP request header로 부터 HttpRequest를 생성할 수 있다")
    @Test
    void convert() {
        // given
        String header = """
                GET /index.html HTTP/1.1\r\n
                Host: localhost:8080\r\n
                Connection: keep-alive\r\n
                Referer: "https://www.google.com/"\r\n
                Pragma: no-cache\r\n
                Cache-Control: no-cache\r\n
                User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36\r\n
                Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n
                Accept-Encoding: gzip, deflate, br, zstd\r\n
                Accept-Language: ko,en-US;q=0.9,en;q=0.8,ko-KR;q=0.7\r\n
                Cookie: Idea-fcd223d4=b6a3f2fa-6bf3-46d9-9244-dc44850cb75f;\r\n
                If-Modified-Since: none;\r\n
                If-None-Match: none;\r\n
                """;
        inputStream = new ByteArrayInputStream(header.getBytes());

        // when
        HttpRequest httpRequest = HttpRequestConverter.convertToHttpRequest(inputStream);

        // then
        /* request line */
        assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(httpRequest.getRequestURI()).isEqualTo("/index.html");
        assertThat(httpRequest.getHttpVersion()).isEqualTo("HTTP/1.1");

        /* headers */
        assertThat(httpRequest.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(httpRequest.getHeader("Connection")).isEqualTo("keep-alive");
        assertThat(httpRequest.getHeader("Referer")).isEqualTo("\"https://www.google.com/\"");
        assertThat(httpRequest.getHeader("User-Agent")).isEqualTo("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");

        assertThat(httpRequest.getHeader("Accept")).isEqualTo("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        assertThat(httpRequest.getHeader("Accept-Encoding")).isEqualTo("gzip, deflate, br, zstd");
        assertThat(httpRequest.getHeader("Accept-Language")).isEqualTo("ko,en-US;q=0.9,en;q=0.8,ko-KR;q=0.7");

        assertThat(httpRequest.getHeader("Cookie")).isEqualTo("Idea-fcd223d4=b6a3f2fa-6bf3-46d9-9244-dc44850cb75f;");
        assertThat(httpRequest.getHeader("Cache-Control")).isEqualTo("no-cache");
        assertThat(httpRequest.getHeader("Pragma")).isEqualTo("no-cache");
        assertThat(httpRequest.getHeader("If-Modified-Since")).isEqualTo("none;");
        assertThat(httpRequest.getHeader("If-None-Match")).isEqualTo("none;");
    }

    @DisplayName("post 요청일 때 http body인 'id=yelly&password=myPassword&username=testName&email=test@test.com'를 쿼리 파라미터로 가져올 수 있다")
    @Test
    void post_convert() {
        // given
        String header = "POST /registration HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "Content-Length: 66\r\n"
                + "Cookie: Idea-fcd223d4=b6a3f2fa-6bf3-46d9-9244-dc44850cb75f;\r\n\r\n"
                + "id=yelly&password=myPassword&username=testName&email=test@test.com";
        inputStream = new ByteArrayInputStream(header.getBytes());

        // when
        HttpRequest httpRequest = HttpRequestConverter.convertToHttpRequest(inputStream);

        // then
        assertThat(httpRequest.getParameter("id")).isEqualTo("yelly");
        assertThat(httpRequest.getParameter("password")).isEqualTo("myPassword");
        assertThat(httpRequest.getParameter("username")).isEqualTo("testName");
        assertThat(httpRequest.getParameter("email")).isEqualTo("test@test.com");
    }
}