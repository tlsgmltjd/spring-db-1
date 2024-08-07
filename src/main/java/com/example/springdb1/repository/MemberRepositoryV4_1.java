package com.example.springdb1.repository;

import com.example.springdb1.domain.Member;
import com.example.springdb1.repository.ex.MyDbException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/*
* 예외 누수 문제 해결
* checked -> runtime exception
* MemnerRepository 인터페이스 사용
* thorws SQLException 제거
* */
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV4_1 implements MemberRepository {

    private final DataSource dataSource;

    @Override
    public Member save(Member member) {
        String sql = "insert into member_db_1(member_id, money) values (? ,?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate(); // 반환값은 쿼리 후 영향 받은 row의 개수
            return member;
        } catch (SQLException e) {
            throw new MyDbException(e);
            // checked exception -> unchecked exception
            // 예외 발생시 로그에 진짜 원인이 남지 않는 문제가 발생하지 않도록 실제 발생한 exception을 변환하려는 unchecked exception에 넘겨주어야한다.
        } finally {
            close(conn, pstmt, null);
        }

    }

    @Override
    public Member findById(String memberId) {
        String sql = "select * from member_db_1 where member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }

        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            // 커넥션은 여기서 닫지 않는다. (같은 트랜잭션의 다른 작업에도 동일한 커넥션을 이어서 사용해야하기 때문)
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
        }
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "update member_db_1 set money = ? where member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public void delete(String memberId) {
        String sql = "delete from member_db_1 where member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    private void close(Connection conn, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);

        // 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해서 커넥션을 릴리즈 해야합니다.
        // 트랜잭션 동기화 매니저가 관리하는 커넥션이라면 닫지 않고 유지해주고 아니라면 커넥션을 닫는다.
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    private Connection getConnection() throws SQLException {
        // 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해서 커넥션을 가져와야한다.
        // 트랜잭션 동기화 매너저가 관리하는 커넥션이 있으면 해당 커넥션을 반환하고 없다면 커넥션을 새로 만들어서 반환한다.
        // 트랜잭션 동기화 매너지는 쓰레드로컬에서 관리되므로 다른 쓰레드에서 트랜잭션이 진행중인 커넥션을 사용하는 일은 없다.
        Connection connection = DataSourceUtils.getConnection(dataSource);
        log.info("======== conn={}, class={}", connection, connection.getClass());
        return connection;
    }
}
