package com.example.springdb1.exception.translator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.example.springdb1.connection.ConnectionConst.*;

// 디비 별로 SQLException의 에러코드로 어떤 예외가 발생했는지 핸들링할 수 있는데
// 디비마다 동일한 예외에 대한 에러코드가 다 다르기 때문에 직접 다 핸들링해서 예외를 변환시키는 것은 고역이다.
// 스프링은 데이터 접근 계층에 대한 예외를 추상화 해서 제공한다.
// SQLErrorCodeSQLExceptionTranslator 예외 변환기를 통해 디비에 맞는 적절한 추상화된 예외로 변환해준다.
// sql-error-codes.xml 파일에 디비별로 예외에 대한 에러코드를 표로 구성해준다.

@Slf4j
public class SpringExceptionTranslatorTest {
    DataSource dataSource;


    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }


    @Test
    void sqlExceptionErrorCode() {
        String sql = "select vbad grammer";

        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeQuery();
        } catch (SQLException e) {
            Assertions.assertEquals(e.getErrorCode(), 42122);
            int errorcode = e.getErrorCode();
            log.info("error", e);
        }
    }

    @Test
    void exception() {
        String sql = "select asdf";

        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeQuery();
        } catch (SQLException e) {
            Assertions.assertEquals(e.getErrorCode(), 42122);

            // sql-error-codes 파일에서 디비별 에러코드를 찾아서 어떤 예외인지 판별한다.
            SQLErrorCodeSQLExceptionTranslator exceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);

            // 스프링의 예외 추상화 계층 DataAccessException 최상위, NonTransient~와 Transient~ 예외로 나누어진다.
            // Transient~는 일시적인 예외, 재시도 하면 정상 동작할 여지가 있는 예외이다. ex. Lock 관련 예외 등
            // NonTransient~는 일시적이지 않은 예외, 반복 실행해도 실패한다. ex. sql 문법 예외 등

            // BadSqlGrammarException
            DataAccessException resultEx = exceptionTranslator.translate("task", sql, e);
            log.info("error", resultEx);

            Assertions.assertEquals(resultEx.getClass(), BadSqlGrammarException.class);
        }

    }

}
