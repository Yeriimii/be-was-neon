package utils;

import static org.assertj.core.api.Assertions.*;
import static utils.HttpRequestParser.*;

import http.HttpRequest.MultiPart;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {

    @DisplayName("\"GET /home.html HTTP/1.1\nHost: developer.mozilla.org\"를 파싱하면 request line을 추출할 수 있다.")
    @Test
    void parse_request_line() {
        // given
        String requestHeader = "GET /home.html HTTP/1.1\nHost: developer.mozilla.org";

        // when
        String requestLine = parseRequestLine(requestHeader);

        // then
        assertThat(requestLine).isEqualTo("GET /home.html HTTP/1.1");
    }

    @DisplayName("'/index.html?name=str&id=str2&money=123'에서 쿼리파라미터를 추출하면 name=str, id=str2, money=123 3개 이다")
    @Test
    void parse_query_params() {
        // given
        String requestHeader = "/index.html?name=str&id=str2&money=123";

        // when
        Map<String, String> params = parseParams(requestHeader);

        // then
        assertThat(params.size()).isEqualTo(3);
        assertThat(params).containsEntry("name", "str")
                .containsEntry("id", "str2")
                .containsEntry("money", "123");
        assertThat(params).doesNotContainEntry("noKey", "noValue");
    }

    @DisplayName("'GET /home.html?name=str&id=str2&money=123 HTTP/1.1\nHost: developer.mozilla.org\nCache-Control: max-age=0\nPragma: no-cache'를 파싱하면 host를 추출할 수 있다.")
    @Test
    void parse_all_headers() {
        // given
        String headers = """
                GET /index.html?test1=test1&test2=test2 HTTP/1.1\r\n
                Host: localhost:8080\r\n
                Connection: keep-alive\r\n
                Pragma: no-cache\r\n
                Cache-Control: no-cache\r\n
                sec-ch-ua: "Chromium";v="122", "Not(A:Brand";v="24", "Google Chrome";v="122"\r\n
                sec-ch-ua-mobile: ?0\r\n
                sec-ch-ua-platform: "macOS"\r\n
                Upgrade-Insecure-Requests: 1\r\n
                User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36\r\n
                Accept: text/html,application/xhtml xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n
                Sec-Fetch-Site: same-origin\r\n
                Sec-Fetch-Mode: navigate\r\n
                Sec-Fetch-User: ?1\r\n
                Sec-Fetch-Dest: document\r\n
                Referer: http://localhost:8080/\r\n
                Accept-Encoding: gzip, deflate, br, zstd\r\n
                Accept-Language: ko,en-US;q=0.9,en;q=0.8,ko-KR;q=0.7\r\n
                Cookie: Idea-fcd223d4=b6a3f2fa-6bf3-46d9-9244-dc44850cb75f; JSESSIONID=98B78C0DDB07A7320453FE9FB565C7F3; Idea-fcd223d5=56a54692-c173-433b-b8a0-65b80db19507\r\n\r\n
                """;

        // then
        Map<String, String> headerMap = parseHeader(headers);

        // then
        assertThat(headerMap.size()).isEqualTo(18);
        assertThat(headerMap).containsEntry("Host", "localhost:8080");
        assertThat(headerMap).containsEntry("Connection", "keep-alive");
        assertThat(headerMap).containsEntry("Pragma", "no-cache");
        assertThat(headerMap).containsEntry("Cache-Control", "no-cache");
        assertThat(headerMap).containsEntry("sec-ch-ua", "\"Chromium\";v=\"122\", \"Not(A:Brand\";v=\"24\", \"Google Chrome\";v=\"122\"");
        assertThat(headerMap).containsEntry("sec-ch-ua-mobile", "?0");
        assertThat(headerMap).containsEntry("sec-ch-ua-platform", "\"macOS\"");
        assertThat(headerMap).containsEntry("Upgrade-Insecure-Requests", "1");
        assertThat(headerMap).containsEntry("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
        assertThat(headerMap).containsEntry("Accept", "text/html,application/xhtml xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        assertThat(headerMap).containsEntry("Sec-Fetch-Site", "same-origin");
        assertThat(headerMap).containsEntry("Sec-Fetch-Mode", "navigate");
        assertThat(headerMap).containsEntry("Sec-Fetch-User", "?1");
        assertThat(headerMap).containsEntry("Sec-Fetch-Dest", "document");
        assertThat(headerMap).containsEntry("Referer", "http://localhost:8080/");
        assertThat(headerMap).containsEntry("Accept-Encoding", "gzip, deflate, br, zstd");
        assertThat(headerMap).containsEntry("Accept-Language", "ko,en-US;q=0.9,en;q=0.8,ko-KR;q=0.7");
        assertThat(headerMap).containsEntry("Cookie", "Idea-fcd223d4=b6a3f2fa-6bf3-46d9-9244-dc44850cb75f; JSESSIONID=98B78C0DDB07A7320453FE9FB565C7F3; Idea-fcd223d5=56a54692-c173-433b-b8a0-65b80db19507");
    }

    @DisplayName("Cookie: MyCookie=Idea-fcd223d4=b6a3f2fa-6bf3-46d9-9244-dc44850cb75f; "
            + "JSESSIONID=98B78C0DDB07A7320453FE9FB565C7F3;"
            + "쿠키를 파싱하면 '=' 를 기준으로 Key-Value HashMap으로 파싱할 수 있다")
    @Test
    void parse_cookie() {
        // given
        String allCookie = "Cookie: MyCookie=Idea-fcd223d4=b6a3f2fa-6bf3-46d9-9244-dc44850cb75f; "
                + "JSESSIONID=98B78C0DDB07A7320453FE9FB565C7F3;";

        // when
        Map<String, String> cookieMap = parseParams(allCookie);

        // then
        assertThat(cookieMap.size()).isEqualTo(2);
        assertThat(cookieMap).containsEntry("MyCookie", "Idea-fcd223d4=b6a3f2fa-6bf3-46d9-9244-dc44850cb75f")
                .containsEntry("JSESSIONID", "98B78C0DDB07A7320453FE9FB565C7F3");
    }

    @DisplayName("Content-Length가 100 일 경우 해당 값을 파싱할 수 있다")
    @Test
    void parseContentLength_WhenContentLengthExists_ReturnContentLength() {
        // given
        String httpMessage = "POST /test HTTP/1.1\r\n"
                + "Host: localhost\r\n"
                + "Content-Length: 100\r\n"
                + "\r\n"
                + "This is the request body.";

        // when
        int contentLength = parseContentLength(httpMessage);

        // then
        assertThat(contentLength).isEqualTo(100);
    }

    @DisplayName("Content-Length 값이 존재하지 않는 경우 파싱한 값은 0이다")
    @Test
    void parseContentLength_WhenContentLengthNotExists_ReturnZero() {
        // given
        String httpMessage = "GET /test HTTP/1.1\r\n"
                + "Host: localhost\r\n"
                + "\r\n";

        // when
        int contentLength = parseContentLength(httpMessage);

        // then
        assertThat(contentLength).isZero();
    }

    @DisplayName("Request Body가 존재하는 경우 그 문자열을 그대로 저장한다")
    @Test
    void parseRequestBody_WhenRequestBodyExists_ReturnRequestBody() {
        // given
        String httpMessage = "POST /test HTTP/1.1\r\n"
                + "Host: localhost\r\n"
                + "Content-Length: 100\r\n"
                + "\r\n"
                + "This is the request body.";

        // when
        String requestBody = parseRequestBody(httpMessage);

        // then
        assertThat(requestBody).isEqualTo("This is the request body.");
    }

    @DisplayName("Request Body가 존재하지 않는 경우 빈 문자열을 반환한다")
    @Test
    void parseRequestBody_WhenRequestBodyNotExists_ReturnEmptyString() {
        // given
        String httpMessage = "GET /test HTTP/1.1\r\n"
                + "Host: localhost\r\n"
                + "\r\n";

        // when
        String requestBody = parseRequestBody(httpMessage);

        // then
        assertThat(requestBody).isEmpty();
    }

    @DisplayName("multipart/form-data가 존재하면 MultiPart 리스트를 반환한다")
    @Test
    void parseMultiPart_WhenMultiPartExists_ReturnMultiPartList() {
        // given
        String httpMessage = "POST /upload HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryesErKWzx2VMAbwfJ\r\n"
                + "\r\n"
                + "------WebKitFormBoundaryesErKWzx2VMAbwfJ\r\n"
                + "Content-Disposition: form-data; name=\"article-body\"\r\n"
                + "\r\n"
                + "test text\r\n"
                + "------WebKitFormBoundaryesErKWzx2VMAbwfJ\r\n"
                + "Content-Disposition: form-data; name=\"photo\"; filename=\"example.png\"\r\n"
                + "Content-Type: image/png\r\n"
                + "\r\n"
                + "�PNG"
                + "\r\n"
                + "------WebKitFormBoundaryesErKWzx2VMAbwfJ--\r\n";

        // when
        List<MultiPart> multiParts = parseMultiPart(httpMessage);

        // then
        assertThat(multiParts).hasSize(2);
        /* 텍스트 */
        MultiPart textMultiPart = multiParts.get(0);
        assertThat(textMultiPart.name()).isEqualTo("article-body");
        assertThat(textMultiPart.submittedFileName()).isNull();
        assertThat(textMultiPart.contentType()).isNull();
        assertThat(textMultiPart.partBody()).isEqualTo("test text".getBytes());

        /* 이미지 */
        MultiPart imageMultiPart = multiParts.get(1);
        assertThat(imageMultiPart.name()).isEqualTo("photo");
        assertThat(imageMultiPart.submittedFileName()).isEqualTo("example.png");
        assertThat(imageMultiPart.contentType()).isEqualTo("image/png");
        assertThat(imageMultiPart.partBody()).isEqualTo("�PNG".getBytes());
    }

    @DisplayName("multipart/form-data가 존재하지 않을 때 빈 리스트를 반환한다")
    @Test
    void parseMultiPart_WhenMultiPartNotExists_ReturnEmptyList() {
        // given
        String httpMessage = "GET /test HTTP/1.1\r\n"
                + "Host: localhost\r\n"
                + "\r\n";

        // when
        List<MultiPart> multiParts = parseMultiPart(httpMessage);

        // then
        assertThat(multiParts).isEmpty();
    }
}