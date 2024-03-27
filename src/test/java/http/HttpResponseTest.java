package http;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    private ByteArrayOutputStream byteArrayOutputStream;

    @BeforeEach
    void setUp() {
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @DisplayName("HttpResponse 객체를 생성할 수 있다")
    @Test
    void createHttpResponse() {
        // given
        HttpResponse response = new HttpResponse(new DataOutputStream(byteArrayOutputStream));
        Cookie cookie = new Cookie("myCookie", "myValue");
        cookie.setPath("/index.html");

        // when
        response.setHttpVersion("HTTP/1.1")
                .setStatusCode(HttpStatus.STATUS_OK)
                .setContentType("text/html")
                .setCharset("utf-8")
                .setContentLength(100)
                .setLocation("/myLocation.html")
                .addCookie(cookie)
                .setLastModified(
                        LocalDateTime.parse(
                                "2024-03-13 13:00:12",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .setMessageBody("Hi");

        // then
        assertThat(byteArrayOutputStream.toString()).isEqualTo(
                "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html; charset=utf-8\r\n"
                        + "Content-Length: 100\r\n"
                        + "Location: /myLocation.html\r\n"
                        + "Set-Cookie: myCookie=myValue; Path=/index.html;\s\r\n"
                        + "Last-Modified: 2024-03-13T13:00:12\r\n"
                        + "\r\n"
                        + "Hi\r\n"
        );
    }

    @DisplayName("myCookie=myValue1; myCookie=myValue2; Path=/; 에 해당하는 쿠키 2개를 응답 헤더에 설정할 수 있다")
    @Test
    void addCookies() {
        // given
        HttpResponse response = new HttpResponse(new DataOutputStream(byteArrayOutputStream));

        Cookie cookie1 = new Cookie("myCookie", "myValue1");
        Cookie cookie2 = new Cookie("myCookie", "myValue2");
        cookie2.setPath("/");

        // when
        response.addCookies(List.of(cookie1, cookie2));

        // then
        assertThat(byteArrayOutputStream.toString())
                .isEqualTo("Set-Cookie: myCookie=myValue1; myCookie=myValue2; Path=/; \r\n");
    }
}