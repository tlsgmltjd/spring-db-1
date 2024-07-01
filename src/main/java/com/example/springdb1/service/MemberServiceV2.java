package com.example.springdb1.service;

import com.example.springdb1.domain.Member;
import com.example.springdb1.repository.MemberRepositoryV1;
import com.example.springdb1.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepositoryV2;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection conn = dataSource.getConnection();

        try {
            conn.setAutoCommit(false); // transaction 시작

            // 비즈니스 로직
            bizLogic(conn, fromId, toId, money);

            conn.commit(); // 성공시 커밋
        } catch (Exception e) {
            conn.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // 커넥션을 기본 값으로 돌려 둔 후 커넥션 풀로 돌려보냄
                    conn.close();
                } catch (Exception e) {
                    log.info("error", e);
                }
            }
        }

    }

    private void bizLogic(Connection conn, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepositoryV2.findById(conn, fromId);
        Member toMember = memberRepositoryV2.findById(conn, toId);

        memberRepositoryV2.update(conn, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepositoryV2.update(conn, toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException();
        }
    }
}
