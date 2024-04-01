package web;

import static utils.ResourceHandler.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * URI와 그에 해당하는 Processor를 매핑하는 클래스입니다.
 * Singleton 패턴을 사용하여 인스턴스를 관리합니다.
 *
 * @author yelly
 * @version 1.0
 */
public class UriMapper {
    private volatile static UriMapper instance;
    private static final Logger logger = LoggerFactory.getLogger(UriMapper.class);
    private static final Map<String, Processor> URI_MAP = new HashMap<>();

    /**
     * UriMapper의 생성자입니다.
     * 기본 URI 매핑을 설정합니다.
     */
    private UriMapper() {
        setUriMap();
    }

    /**
     * UriMapper의 인스턴스를 반환합니다.
     * @return UriMapper 인스턴스
     */
    public static synchronized UriMapper getInstance() {
        if (instance == null) {
            synchronized (UriMapper.class) {
                if (instance == null) {
                    instance = new UriMapper();
                }
            }
        }
        return instance;
    }

    private static void setUriMap() {
        URI_MAP.put("/", new DynamicHtmlProcessor());
        URI_MAP.put("/static", new StaticHtmlProcessor());
        URI_MAP.put("/registration", new MemberSave());
        URI_MAP.put("/login", new MemberLogin());
        URI_MAP.put("/logout", new MemberLogout());
        URI_MAP.put("/user/list", new MemberList());
        URI_MAP.put("/article", new ArticleWrite());
    }

    /**
     * 주어진 URI에 해당하는 Processor를 반환합니다.
     * @param uri URI
     * @return 해당 URI에 매핑된 Processor의 Optional 객체
     */
    public Optional<Processor> getProcessor(String uri) {
        if (URI_MAP.containsKey(uri)) {
            return Optional.of(URI_MAP.get(uri));
        }
        if (FILE_EXTENSION_MAP.containsKey(getExtension(uri))) {
            return Optional.of(URI_MAP.get("/static"));
        }
        logger.debug("[STATIC MAPPER] NOT FOUND: {}", uri);
        return Optional.empty();
    }
}
