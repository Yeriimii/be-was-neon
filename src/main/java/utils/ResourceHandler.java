package utils;

import static utils.HttpConstant.CRLF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResourceHandler.class);
    public static final String STATIC_PATH = "./src/main/resources/static";
    public static final String TEMPLATE_PATH = "./src/main/resources/templates";
    public static final String INDEX_HTML = "index.html";
    public static final Map<String, String> FILE_EXTENSION_MAP = Map.of(
            ".html","text/html",
            ".css", "text/css",
            ".js", "application/javascript",
            ".ico", "image/x-icon",
            ".png", "image/png",
            ".jpg", "image/jpeg",
            ".svg", "image/svg+xml");


    public static byte[] read(String filePath) {
        File file = new File(filePath);
        byte[] byteArray = new byte[(int) file.length()]; // 파일의 크기만큼의 바이트 배열 생성

        // FileInputStream을 사용하여 파일 열기
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(byteArray); // 파일 내용을 바이트 배열에 읽어들임
        } catch (IOException e) {
            logger.error("[RESOURCE HANDLER ERROR] {}", e.getMessage());
        }

        return byteArray;
    }

    /**
     * 템플릿 파일을 읽어온다. 매개 변수가 디렉토리인 경우 해당 디렉토리의 'index.html' 파일을 읽는다.
     * 파일 이름인 경우 해당 파일을 읽는다. 파일이 존재하지 않거나 읽을 수 없는 경우 빈 문자열("")을 반환한다.
     * @param templateName 템플릿 파일의 경로 (디렉토리 또는 파일)
     * @return 템플릿 파일의 내용을 문자열로 반환하며, 파일이 존재하지 않거나 읽을 수 없는 경우 빈 문자열("")을 반환한다.
     */
    public static String readTemplate(String templateName) {
        // 디렉토리인 경우 인덱스 파일 추가
        if (new File(TEMPLATE_PATH + templateName).isDirectory()) {
            templateName += INDEX_HTML;
        }

        File file = new File(TEMPLATE_PATH + templateName);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().collect(Collectors.joining(CRLF));
        } catch (IOException e) {
            logger.error("[RESOURCE HANDLER ERROR] {}", e.getMessage());
        }

        return ""; // 파일이 존재하지 않거나 읽을 수 없는 경우 빈 문자열 반환
    }

    public static String getExtension(String uri) {
        if (uri.contains(".")) {
            return uri.substring(uri.lastIndexOf("."));
        }
        return uri;
    }
}
