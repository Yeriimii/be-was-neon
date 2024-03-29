package utils;

import static org.assertj.core.api.Assertions.*;

import http.HttpRequest.MultiPart;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceHandlerTest {

    @AfterEach
    void deleteDirectory() {
        File directory = new File("./src/test/resources/media", "yelly");
        if (directory.exists()) {
            directory.delete();
        }

        File testFile = new File("./src/main/resources/media/test/test 전용 파일.png");
        if (testFile.exists()) {
            testFile.delete();
        }

        File testFolder = new File("./src/main/resources/media/test");
        if (testFolder.exists()) {
            testFolder.delete();
        }
    }

    @DisplayName("src/main/resources/static/index.html 파일을 읽으면 바이트의 길이는 0을 초과한다")
    @Test
    void read_success() {
        // given
        String filePath = "src/main/resources/static/index.html";

        // when
        byte[] bytes = ResourceHandler.read(filePath);

        // then
        assertThat(bytes.length).isGreaterThan(0);
    }

    @DisplayName("존재하지 않는 파일을 읽으면 바이트의 길이는 0이다")
    @Test
    void read_fail() {
        // given
        String filePath = "not-found-path";

        // when
        byte[] bytes = ResourceHandler.read(filePath);

        // then
        assertThat(bytes.length).isEqualTo(0);
    }

    @DisplayName("확장자 .html, .css, .svg, .js를 포함하는 경로에서 확장자만 추출할 수 있다")
    @Test
    void getExtension() {
        // given
        List<String> uriList = List.of("/index.html", "/min.css", "/img/test.svg", "/main.js");

        // when
        List<String> extensions = uriList.stream()
                .map(ResourceHandler::getExtension)
                .toList();

        // then
        assertThat(extensions.size()).isEqualTo(4);
        assertThat(extensions).contains(
                "html",
                "css",
                "svg",
                "js"
        );
    }

    @DisplayName("존재하는 '/' 디렉토리는 'templates/index.html' 파일을 읽기 때문에 반환 문자열 길이는 0을 초과한다")
    @Test
    void read_template_success_when_exists_directory() {
        // given
        String filePath = "/";

        // when
        String template = ResourceHandler.readTemplate(filePath);

        // then
        assertThat(template.length()).isGreaterThan(0);
        assertThat(template.startsWith("<!DOCTYPE html>")).isTrue();
    }

    @DisplayName("존재하는 'templates/index.html' 파일의 반환 문자열 길이는 0을 초과한다")
    @Test
    void read_template_success_when_exists_file() {
        // given
        String filePath = "/index.html";

        // when
        String template = ResourceHandler.readTemplate(filePath);

        // then
        assertThat(template.length()).isGreaterThan(0);
        assertThat(template.startsWith("<!DOCTYPE html>")).isTrue();
    }

    @DisplayName("'templates/index' 이라는 존재하지 않는 디렉토리의 반환 문자열 길이는 0이다")
    @Test
    void read_template_fail_when_not_exist_directory() {
        // given
        String filePath = "/index";

        // when
        String template = ResourceHandler.readTemplate(filePath);

        // then
        assertThat(template.length()).isEqualTo(0);
        assertThat(template.isEmpty()).isTrue();
    }

    @DisplayName("'templates/not_exist/index.html' 이라는 존재하지 않는 파일의 반환 문자열 길이는 0이다")
    @Test
    void read_template_fail_when_not_exist_file() {
        // given
        String filePath = "not_exist/index.html";

        // when
        String template = ResourceHandler.readTemplate(filePath);

        // then
        assertThat(template.length()).isEqualTo(0);
        assertThat(template.isEmpty()).isTrue();
    }

    @DisplayName("'./src/test/resources/media/yelly' 라는 디렉토리가 존재하지 않으면 해당 경로에 폴더를 생성할 수 있다")
    @Test
    void createDirectory_success_when_not_exist_directory() {
        // given
        String path = "yelly";

        // when
        boolean result = ResourceHandler.createDirectory("./src/test/resources/media", path);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("'./src/test/resources/media/test' 라는 디렉토리가 존재하면 해당 경로에 폴더를 생성할 수 없다")
    @Test
    void createDirectory_fail_when_exist_directory() {
        // given
        String path = "test";

        // when
        boolean result = ResourceHandler.createDirectory("./src/test/resources/media", path);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("'test 전용 파일.png' 이름으로 된 이미지 파일을 저장할 수 있다")
    @Test
    void saveImage_success() throws IOException {
        // given
        File imageFile = new File("./src/test/resources/media/was-logout-2.png");

        FileInputStream fis = new FileInputStream(imageFile);
        byte[] imageBytes = fis.readAllBytes();
        fis.close();

        String partName = "testFile";
        String fileName = "test 전용 파일.png";
        String contentType = "image/png";
        MultiPart multiPart = new MultiPart(partName, fileName, contentType, imageBytes);

        // when
        ResourceHandler.createDirectory("./src/main/resources/media", "test");
        ResourceHandler.saveImage(multiPart, "/media/test" + "/" + fileName);

        // then
        assertThat(new File("./src/main/resources/media/test/test 전용 파일.png").exists()).isTrue();
    }
}