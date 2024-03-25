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

public class HttpResponse {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private String contentType = "Content-Type: ";
    private String charset = "charset=";
    private String contentLength = "Content-Length: ";
    private String lastModified = "Last-Modified: ";
    private String location = "Location: ";
    private final DataOutputStream dos;

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public HttpResponse setHttpVersion(String httpVersion) {
        writeString(httpVersion + SP);
        return this;
    }

    public HttpResponse setStatusCode(HttpStatus status) {
        writeString(status.code + SP + status.message + CRLF);
        return this;
    }

    public HttpResponse setContentType(String contentType) {
        this.contentType += contentType;
        writeString(this.contentType + SPLITTER + SP);
        return this;
    }

    public HttpResponse setCharset(String charset) {
        this.charset += charset;
        writeString(this.charset + CRLF);
        return this;
    }

    public HttpResponse setContentLength(int contentLength) {
        this.contentLength += contentLength;
        writeString(this.contentLength + CRLF);
        return this;
    }

    public HttpResponse setLastModified(LocalDateTime lastModified) {
        this.lastModified += lastModified.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        writeString(this.lastModified + CRLF);
        return this;
    }

    public HttpResponse addCookie(Cookie cookie) {
        writeString("Set-Cookie: " + cookie.getCookie() + CRLF);
        return this;
    }

    public HttpResponse addCookies(List<Cookie> cookies) {
        String cookieString = cookies.stream()
                .map(Cookie::getCookie)
                .collect(Collectors.joining());

        writeString("Set-Cookie: " + cookieString + CRLF);
        return this;
    }

    public HttpResponse setLocation(String location) {
        this.location += location;
        writeString(this.location + CRLF);
        return this;
    }

    public HttpResponse setMessageBody(String stringMessageBody) {
        writeString(CRLF);
        writeString(stringMessageBody);
        writeString(CRLF);
        return this;
    }

    public HttpResponse setMessageBody(byte[] bytesMessageBody) {
        writeString(CRLF);
        writeBytes(bytesMessageBody);
        writeString(CRLF);
        return this;
    }

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
