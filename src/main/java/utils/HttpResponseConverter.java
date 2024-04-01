package utils;

import http.HttpResponse;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpResponse 객체로의 변환을 담당하는 유틸리티 클래스입니다.
 *
 * @author yelly
 * @version 1.0
 */
public class HttpResponseConverter {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponseConverter.class);

    /**
     * OutputStream을 HttpResponse로 변환합니다.
     * @param out OutputStream 객체
     * @return HttpResponse 객체
     * @throws IllegalStateException 변환 실패 시 발생하는 예외
     */
    public static HttpResponse convertToHttpResponse(OutputStream out) {
        Optional<HttpResponse> response = Optional.of(new HttpResponse(new DataOutputStream(out)));

        return response.orElseThrow(() -> new IllegalStateException("[RESPONSE CONVERTER ERROR] convert fail"));
    }
}
