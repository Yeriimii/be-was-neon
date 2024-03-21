package web;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UriMapperTest {

    @DisplayName("'/' 또는 '/index.html'을 입력받으면 HtmlProcessor를 반환한다")
    @Test
    void getProcessor_root_processor() {
        // given
        String rootUrl = "/";
        String staticUrl = "/index.html";

        UriMapper mapper = UriMapper.getInstance();

        // when
        Optional<HttpProcessor> rootProcessor = mapper.getProcessor(rootUrl);
        Optional<HttpProcessor> indexProcessor = mapper.getProcessor(staticUrl);

        // then
        assertThat(rootProcessor.isPresent()).isTrue();
        assertThat(rootProcessor.get()).isInstanceOf(StaticHtmlProcessor.class);
        assertThat(indexProcessor.isPresent()).isTrue();
        assertThat(indexProcessor.get()).isInstanceOf(StaticHtmlProcessor.class);
    }

    @DisplayName("존재하지 않는 URL은 빈 Optional을 반환한다")
    @Test
    void getProcessor_not_found() {
        // given
        String notFoundUrl = "/not-found";

        UriMapper mapper = UriMapper.getInstance();

        // when
        Optional<HttpProcessor> notFoundProcessor = mapper.getProcessor(notFoundUrl);

        // then
        assertThat(notFoundProcessor.isEmpty()).isTrue();
    }
}