package http;

import static utils.HttpConstant.*;

import java.io.DataOutputStream;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP 응답 클래스입니다.
 * 이 클래스는 HTTP 응답을 생성하고 보낼 때 사용됩니다.
 * 응답 헤더 및 바디를 설정하고, 클라이언트로 응답을 보내는 메서드를 제공합니다.
 * 또한 쿠키를 추가하거나 리다이렉트를 설정하는 등의 기능을 지원합니다.
 *
 * @author yelly
 * @version 1.0
 */
public class HttpResponse {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private String contentType = "Content-Type: ";
    private String charset = "charset=";
    private String contentLength = "Content-Length: ";
    private String lastModified = "Last-Modified: ";
    private String location = "Location: ";
    private final DataOutputStream dos;

    /**
     * HttpResponse 객체를 생성합니다.
     *
     * @param dos DataOutputStream 객체
     */
    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    /**
     * HTTP 버전을 설정합니다.
     *
     * @param httpVersion HTTP 버전
     * @return HttpResponse 객체
     */
    public HttpResponse setHttpVersion(String httpVersion) {
        writeString(httpVersion + SP);
        return this;
    }

    /**
     * 상태 코드를 설정합니다.
     *
     * @param status HttpStatus 객체
     * @return HttpResponse 객체
     */
    public HttpResponse setStatusCode(HttpStatus status) {
        writeString(status.code + SP + status.message + CRLF);
        return this;
    }

    /**
     * 컨텐츠 타입을 설정합니다.
     *
     * @param contentType 컨텐츠 타입
     * @return HttpResponse 객체
     */
    public HttpResponse setContentType(String contentType) {
        this.contentType += contentType;
        writeString(this.contentType + SPLITTER + SP);
        return this;
    }

    /**
     * 문자셋을 설정합니다.
     *
     * @param charset 문자셋
     * @return HttpResponse 객체
     */
    public HttpResponse setCharset(String charset) {
        this.charset += charset;
        writeString(this.charset + CRLF);
        return this;
    }

    /**
     * 컨텐츠 길이를 설정합니다.
     *
     * @param contentLength 컨텐츠 길이
     * @return HttpResponse 객체
     */
    public HttpResponse setContentLength(int contentLength) {
        this.contentLength += contentLength;
        writeString(this.contentLength + CRLF);
        return this;
    }

    /**
     * 마지막 수정 시간을 설정합니다.
     *
     * @param lastModified 마지막 수정 시간
     * @return HttpResponse 객체
     */
    public HttpResponse setLastModified(LocalDateTime lastModified) {
        this.lastModified += lastModified.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        writeString(this.lastModified + CRLF);
        return this;
    }

    /**
     * 쿠키를 추가합니다.
     *
     * @param cookie Cookie 객체
     * @return HttpResponse 객체
     */
    public HttpResponse addCookie(Cookie cookie) {
        writeString("Set-Cookie: " + cookie.getCookie() + CRLF);
        return this;
    }

    /**
     * 쿠키 리스트 전체를 추가합니다.
     *
     * @param cookies 쿠키 목록
     * @return HttpResponse 객체
     */
    public HttpResponse addCookies(List<Cookie> cookies) {
        String cookieString = cookies.stream()
                .map(Cookie::getCookie)
                .collect(Collectors.joining());

        writeString("Set-Cookie: " + cookieString + CRLF);
        return this;
    }

    /**
     * Location 값을 설정합니다.
     *
     * @param location 위치
     * @return HttpResponse 객체
     */
    public HttpResponse setLocation(String location) {
        this.location += location;
        writeString(this.location + CRLF);
        return this;
    }

    /**
     * 문자열 형태의 메시지 바디를 설정합니다.
     *
     * @param stringMessageBody 문자열 형태의 메시지 바디
     * @return HttpResponse 객체
     */
    public HttpResponse setMessageBody(String stringMessageBody) {
        writeString(CRLF);
        writeString(stringMessageBody);
        writeString(CRLF);
        return this;
    }

    /**
     * 바이트 배열 형태의 메시지 바디를 설정합니다.
     *
     * @param bytesMessageBody 바이트 배열 형태의 메시지 바디
     * @return HttpResponse 객체
     */
    public HttpResponse setMessageBody(byte[] bytesMessageBody) {
        writeString(CRLF);
        writeBytes(bytesMessageBody);
        writeString(CRLF);
        return this;
    }

    /**
     * 버퍼를 비웁니다.
     */
    public void flush() {
        try {
            dos.flush();
        } catch (IOException e) {
            logger.error("[RESPONSE ERROR] flush error: {}", e.getMessage());
        }
    }

    private void writeString(String string) {
        try {
            dos.write(string.getBytes("UTF-8"));
        } catch (IOException e) {
            logger.error("[RESPONSE ERROR]: {}", e.getMessage());
        }
    }

    private void writeBytes(byte[] bytes) {
        try {
            dos.write(bytes);
        } catch (IOException e) {
            logger.error("[RESPONSE ERROR]: {}", e.getMessage());
        }
    }
}
