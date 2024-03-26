package web;

import static utils.ResourceHandler.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UriMapper {
    private volatile static UriMapper instance;
    private static final Logger logger = LoggerFactory.getLogger(UriMapper.class);
    private static final Map<String, HttpProcessor> URI_MAP = new HashMap<>();

    private UriMapper() {
        setUriMap();
    }

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

    public Optional<HttpProcessor> getProcessor(String uri) {
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
