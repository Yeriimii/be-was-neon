package utils;

import http.HttpResponse;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseConverter {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponseConverter.class);

    public static HttpResponse convertToHttpResponse(OutputStream out) {
        Optional<HttpResponse> response = Optional.of(new HttpResponse(new DataOutputStream(out)));

        return response.orElseThrow(() -> new IllegalStateException("[RESPONSE CONVERTER ERROR] convert fail"));
    }
}
