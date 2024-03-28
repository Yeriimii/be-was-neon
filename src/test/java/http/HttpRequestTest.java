package http;

import static org.assertj.core.api.Assertions.*;

import http.HttpRequest.HttpMethod;
import http.HttpRequest.HttpRequestUri;
import http.HttpRequest.HttpVersion;
import http.HttpRequest.MultiPart;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("요청 헤더에 존재하는 Cookie: SID=test-test; myCookie=myValue; 를 찾을 수 있다")
    @Test
    void getCookie() {
        // given
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", "SID=test-test; myCookie=myValue;");

        HttpRequest request = new HttpRequest(
                HttpMethod.GET, new HttpRequestUri("test"), new HttpVersion("HTTP/1.1"), headers, new HashMap<>(),
                List.of(new MultiPart("", "", "", new byte[] {}))
        );

        // when
        List<Cookie> cookies = request.getCookie();

        // then
        assertThat(cookies.size()).isEqualTo(2);
        assertThat(cookies).extracting("cookieKey").contains("SID", "myCookie");
        assertThat(cookies).extracting("cookieValue").contains("test-test; ", "myValue; ");
    }

    @DisplayName("HttpRequest 객체는 속성값 username에 해당하는 문자열 'yelly'를 가진 multipart를 가질 수 있다")
    @Test
    void create_multipart_attribute() {
        // given
        /* multipart 생성 */
        String attr = "username";
        byte[] partBody = "yelly".getBytes(StandardCharsets.UTF_8);
        MultiPart multiPart = new MultiPart(attr, null, null, partBody);

        /* mock request */
        HttpRequest mockRequest = new HttpRequest(HttpMethod.POST, new HttpRequestUri("/test"),
                new HttpVersion("HTTP/1.1"),
                null, null, List.of(multiPart));

        // when
        List<MultiPart> parts = mockRequest.getParts();

        // then
        assertThat(parts.size()).isEqualTo(1);
        assertThat(parts.get(0)).extracting("name").isEqualTo("username");
        assertThat(parts.get(0)).extracting("partBody").isEqualTo(partBody);
    }
}