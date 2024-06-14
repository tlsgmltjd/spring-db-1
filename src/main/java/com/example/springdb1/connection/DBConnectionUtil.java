package com.example.springdb1.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.example.springdb1.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get c = {}, class = {}", connection, connection.getClass());

            // DriverManager가 드라이버 목록을 순회하면서 url 정보로 적절한 DB 드라이버를 찾아 반환해준다.

            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException();
        }
    }
}
