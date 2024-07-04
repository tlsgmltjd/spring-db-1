package com.example.springdb1.exception.translator;

import com.example.springdb1.domain.Member;
import com.example.springdb1.repository.ex.MyDbException;
import com.example.springdb1.repository.ex.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static com.example.springdb1.connection.ConnectionConst.*;

// 예외 변환을 하여 SQLException 같이 특정 기술에 종속적인 예외 객체를 직접 구현한 예외 객체로 변환하여 던졌다.
// 이를 받은 상위 계층에서는 특정 기술에 종속적이지 않은 커스텀 예외를 핸들링하여 예외를 처리하고 불필요한 의존관계가 발생하지 않는다.
// -> 하지만 지금은 SQLException의 예외코드를 직접 비교하는 방식이다. 이런 방식은 디비 드라이버 마다 코드가 달라 변경할 때마다 바꾸어주어야한다.
// -> 스프링이 해당 문제에 대해 예외를 추상화하여 도와준다.

@Slf4j
public class ExTranslatorV1Test {

    Repository repository;
    Service service;

    @BeforeEach
    void init() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @Test
    void duplicateKeySave() {
        service.create("myId");
        service.create("myId");
    }

    @Slf4j
    @RequiredArgsConstructor
    static class Service {
        private final Repository repository;

        public void create(String memberId) {
            try {
                repository.save(new Member(memberId, 10000));
                log.info("savedId={}", memberId);
            } catch (MyDuplicateKeyException e) {
                log.info("키 중복, 복구 시도");
                String retryId = generateNewId(memberId);
                log.info("retryId={}", retryId);
                repository.save(new Member(retryId, 10000));
            } catch (MyDbException e) {
                log.info("데이터 접근 계층 예외", e);
                throw e;
            }
        }

        private String generateNewId(String memberId) {
            return memberId + new Random().nextInt(10000);
        }
    }

    @RequiredArgsConstructor
    static class Repository {

        private final DataSource dataSource;
        public Member save(Member member) {
            String sql = "insert into member_db_1(member_id, money) values (? ,?)";

            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = dataSource.getConnection();
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2, member.getMoney());
                pstmt.executeUpdate();
                return member;
            } catch (SQLException e) {

                // PK Duplicate error code
                if (e.getErrorCode() == 23505) {
                    throw new MyDuplicateKeyException(e);
                }

                throw new MyDbException(e);

            } finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(conn);
            }
        }

    }
}
