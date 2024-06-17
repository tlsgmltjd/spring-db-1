package com.example.springdb1.repository;

import com.example.springdb1.connection.DBConnectionUtil;
import com.example.springdb1.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

/*
* JDBC - DriverManager 사용
* */
@Slf4j
public class MemberRepositoryV0 {
    public Member save(Member member) throws SQLException {
        String sql = "insert into member_db_1(member_id, money) values (? ,?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try { // C, P, R은 AutoCloseable을 상속받아 try-with-resources 문법으로 자원 사용 후 반납 가능
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate(); // 반환값은 쿼리 후 영향 받은 row의 개수
            return member;
        } catch (SQLException e) {
            log.error("error : " + e);
            throw e;
        } finally {
            close(conn, pstmt, null);
        }

    }

    private void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("error rs close : " + e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("error stmt close : " + e);
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("error conn close : " + e);
            }
        }
    }

    private static Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
