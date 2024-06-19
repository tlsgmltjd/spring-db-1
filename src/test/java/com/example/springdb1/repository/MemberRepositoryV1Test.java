package com.example.springdb1.repository;

import com.example.springdb1.domain.Member;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static com.example.springdb1.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void be() {
        // 기본 driverManager - 항상 새로운 커넥션을 획득
//        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        // 커넥션 풀을 사용하면 커넥션을 얻어올 때 마다 새로운 커넥션을 가져오지 않고 풀에 있는 커넥션을 사용하고 릴리즈시 풀로 반환한다.
        // 구현체가 바뀌어도 repository의 코드는 변경되지 않는다. DataSource라는 공통 인터페이스가 있어서 가능하다 DI, OCP
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        repository = new MemberRepositoryV1(dataSource);
    }

    private static final String MEMBER_ID = "memberV20";

    @Test
    void crud() throws SQLException {
        // save
        Member member = new Member(MEMBER_ID, 10000);
        repository.save(member);

        // findById
        Member findMember = repository.findById(MEMBER_ID);
        log.info("findMember={}", findMember);

        // Member 객체에서 @Data 어노테이션을 사용하고 있기 때문에 equals, hashcode 메서드를 오버라이딩 하고 있다.
        // -> equals 비교시 생성 객체, 찾은 객체가 각각 생성 됐더라도 같은 객체 취급을 받는다.
        assertThat(findMember).isEqualTo(member);

        // update: money: 10000 -> 20000
        int updateMoney = 20000;
        repository.update(member.getMemberId(), updateMoney);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(updateMoney);

        // delete
        repository.delete(member.getMemberId());
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}