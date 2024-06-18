package com.example.springdb1.connection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.example.springdb1.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {
    @Test
    void driverManager() throws SQLException {
        Connection conn1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection conn2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("conn1={}, class={}", conn1, conn1.getClass());
        log.info("conn1={}, class={}", conn2, conn2.getClass());
    }

    @Test
    void dataSourceDriverManager() throws SQLException {
        // DriverManagerDAtaSource - 항상 새로운 커넥션을 획득

        // DataSource: 데이터베이스 커넥션을 획득하는 방법을 추상화 하는 인터페이스
        // DriverManager, HikariCP connection pool 등 커넥션을 얻는 구현체를 의존하여 커넥션을 획득하는데
        // DataSource라는 인터페이스를 두어 구현체를 변경할 수 있게 구현되어 있다.

        // 기존에 DriverManager로 커넥션을 획득할때는 url, username, password같은 파라미터를 획득시마다 전달하여야한다.
        // DataSource를 사용한다면 DataSource를 생성시에만 파라미터를 전달하여 설정하고 그 후에 커넥션을 사용할 때는 메서드만 호출하면 된다.
        // 이렇게 설정과 사용을 분리하여 관심사와 의존성을 확실하게 분리할 수 있다.
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection conn1 = dataSource.getConnection();
        Connection conn2 = dataSource.getConnection();
        log.info("conn1={}, class={}", conn1, conn1.getClass());
        log.info("conn2={}, class={}", conn2, conn2.getClass());
    }
}
