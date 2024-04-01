package db;

import static db.ConnectionConst.PASSWORD;
import static db.ConnectionConst.URL;
import static db.ConnectionConst.USERNAME;

import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 데이터베이스 연결을 설정하는 유틸리티 클래스입니다.
 *
 * @author yelly
 * @version 1.0
 */
public class DataSourceUtil {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceUtil.class);
    private static volatile JdbcDataSource dataSource;

    private DataSourceUtil() {
    }

    /**
     * 데이터베이스의 DataSource를 반환합니다.
     *
     * @return DataSource 객체
     */
    public static synchronized DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DataSourceUtil.class) {
                if (dataSource == null) {
                    dataSource = new JdbcDataSource();
                    dataSource.setURL(URL);
                    dataSource.setUser(USERNAME);
                    dataSource.setPassword(PASSWORD);
                }
            }
        }
        logger.debug("get datasource = {}", dataSource);
        return dataSource;
    }
}
