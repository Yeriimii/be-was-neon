package utils;

import static java.nio.charset.StandardCharsets.*;

import http.HttpRequest.HttpMethod;
import http.HttpRequest.HttpRequestUri;
import http.HttpRequest.HttpVersion;
import http.HttpRequest.MultiPart;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum HttpRequestParser {
    REQUEST_LINE(Pattern.compile("(?s)(^GET|^POST) (/.*?)\\s(HTTP/.{1,3})")),
    METHOD(Pattern.compile("(?s)(^GET|^POST)")),
    URI(Pattern.compile("(?s)(?:^GET|^POST) (/.*?)\\sHTTP/.{1,3}")),
    VERSION(Pattern.compile("(?s)(?:^GET|^POST /.*?)(HTTP/.{1,3})")),
    HEADERS(Pattern.compile("(?m)^(.*?):\\s(.*?)\\r\\n")),
    QUERY_PARAMETER(Pattern.compile("([^?&=\\s]+)=([^&;\\s]+)")),
    REQUEST_MESSAGE(Pattern.compile("\\r\\n\\r\\n((?!------WebKitFormBoundary).+\\s*)+")),
    CONTENT_LENGTH(Pattern.compile("Content-Length\\s?:\\s?(\\d*)")),
    MULTI_PART_ATTR(Pattern.compile("(?s)form-data;\\sname=\"(.*?)\".*?(?:filename=\"(.*?)\".*?Content-Type:\\s(image/.*?))?\\r\\n")),
    MULTI_PART_BODY(Pattern.compile("(?s)form-data;.*?\\r\\n\\r\\n(.*?)\\r\\n------WebKitForm")),
    ;

    private final Pattern compiledPattern;

    HttpRequestParser(Pattern compiledPattern) {
        this.compiledPattern = compiledPattern;
    }

    public static String parseRequestLine(String header) {
        Matcher requestLineMatcher = REQUEST_LINE.compiledPattern.matcher(header);
        if (requestLineMatcher.find()) {
            return requestLineMatcher.group();
        }
        return "";
    }

    public static HttpMethod parseMethod(String httpMessage) {
        Matcher methodMatcher = METHOD.compiledPattern.matcher(httpMessage);
        if (methodMatcher.find()) {
            return HttpMethod.valueOf(methodMatcher.group(1));
        }
        return HttpMethod.GET;
    }

    public static HttpRequestUri parseUri(String httpMessage) {
        Matcher uriMatcher = URI.compiledPattern.matcher(httpMessage);
        if (uriMatcher.find()) {
            return new HttpRequestUri(uriMatcher.group(1));
        }
        return new HttpRequestUri("/");
    }

    public static HttpVersion parseVersion(String httpMessage) {
        Matcher versionMatcher = VERSION.compiledPattern.matcher(httpMessage);
        if (versionMatcher.find()) {
            return new HttpVersion(versionMatcher.group(1));
        }
        return new HttpVersion("HTTP/1.1");
    }

    public static Map<String, String> parseHeader(String header) {
        Map<String, String> headers = new HashMap<>();

        Matcher headerMatcher = HEADERS.compiledPattern.matcher(header);
        while (headerMatcher.find()) {
            headers.put(headerMatcher.group(1), headerMatcher.group(2));
        }
        return Collections.unmodifiableMap(headers);
    }

    public static Map<String, String> parseParams(String header) {
        Map<String, String> paramMap = new HashMap<>();

        Matcher paramMatcher = QUERY_PARAMETER.compiledPattern.matcher(header);
        while (paramMatcher.find()) {
            paramMap.put(paramMatcher.group(1), paramMatcher.group(2)); // key = 인덱스 1, value = 인덱스 2
        }
        return Collections.unmodifiableMap(paramMap);
    }

    /**
     * Content-Length의 값을 파싱한다. 찾지 못하면 0을 반환한다.
     *
     * @param httpMessage HTTP Message
     * @return Content-Length 값. 찾지 못하면 0을 반환한다.
     */
    public static int parseContentLength(String httpMessage) {
        Matcher lengthMatcher = CONTENT_LENGTH.compiledPattern.matcher(httpMessage);
        if (lengthMatcher.find()) {
            return Integer.parseInt(lengthMatcher.group(1));
        }
        return 0;
    }

    /**
     * Request Message를 파싱한다. 찾지 못하면 빈 문자열("")을 반환한다.
     *
     * @param httpMessage HTTP Message
     * @return 문자열 Request Message를 반환한다.
     */
    public static String parseRequestBody(String httpMessage) {
        Matcher messageMatcher = REQUEST_MESSAGE.compiledPattern.matcher(httpMessage);
        if (messageMatcher.find()) {
            return messageMatcher.group(1);
        }
        return "";
    }

    /**
     * HTTP 메시지에서 Content-Type이 multipart인 부분을 파싱하여 MultiPart 객체의 리스트를 반환한다.
     * 이미지 파일은 MultiPart 객체의 contentType에 해당 확장자(ex. png, jpg, gif 등)를 입력한다.
     * Encoding 형식은 ISO_8859_1 를 따른다.
     *
     * @param httpMessage HTTP Message
     * @return 파싱된 MultiPart 객체의 리스트. 발견된 MultiPart가 없을 경우 빈 리스트를 반환한다.
     */
    public static List<MultiPart> parseMultiPart(String httpMessage) {
        List<MultiPart> multiParts = new ArrayList<>();

        Matcher attrMatcher = MULTI_PART_ATTR.compiledPattern.matcher(httpMessage);
        Matcher bodyMatcher = MULTI_PART_BODY.compiledPattern.matcher(httpMessage);
        while (attrMatcher.find()) {
            /* HTML <input> 태그의 name 속성의 값 */
            String name = attrMatcher.group(1);

            /* 전송한 파일의 이름 */
            String submittedFileName = encodeByUtf8(attrMatcher.group(2));

            /* Content-Type 라인 */
            String contentType = attrMatcher.group(3);

            /* 본문 부분: 텍스트, 이미지 모두 byte[]로 처리 */
            byte[] partBody = null;
            if (bodyMatcher.find(attrMatcher.start())) {
                partBody = httpMessage.substring(bodyMatcher.start(1), bodyMatcher.end(1)).getBytes(ISO_8859_1);
            }

            multiParts.add(new MultiPart(name, submittedFileName, contentType, partBody));
        }
        return Collections.unmodifiableList(multiParts);
    }

    private static String encodeByUtf8(String submittedFileName) {
        if (submittedFileName != null) {
            submittedFileName = new String(submittedFileName.getBytes(ISO_8859_1), UTF_8);
        }
        return submittedFileName;
    }
}
