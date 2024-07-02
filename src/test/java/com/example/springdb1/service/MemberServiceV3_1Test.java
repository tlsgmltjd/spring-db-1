package com.example.springdb1.service;

import com.example.springdb1.domain.Member;
import com.example.springdb1.repository.MemberRepositoryV1;
import com.example.springdb1.repository.MemberRepositoryV2;
import com.example.springdb1.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static com.example.springdb1.connection.ConnectionConst.*;

@Slf4j
class MemberServiceV3_1Test {
    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV3 memberRepositoryV3;
    private MemberRepositoryV1 memberRepositoryV1;
    private MemberServiceV3_1 memberServiceV3_1;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepositoryV3 = new MemberRepositoryV3(dataSource);
        memberRepositoryV1 = new MemberRepositoryV1(dataSource);

        PlatformTransactionManager txm = new DataSourceTransactionManager(dataSource);

        memberServiceV3_1 = new MemberServiceV3_1(txm, memberRepositoryV3);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepositoryV3.delete(MEMBER_A);
        memberRepositoryV3.delete(MEMBER_B);
        memberRepositoryV3.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이채")
    void accountTransfer() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepositoryV1.save(memberA);
        memberRepositoryV1.save(memberB);

        // when
        log.info("===============================");
        memberServiceV3_1.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);
        log.info("===============================");

        // then
        Member findMemberA = memberRepositoryV1.findById(memberA.getMemberId());
        Member findMemberB = memberRepositoryV1.findById(memberB.getMemberId());

        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(8000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이채중 예외 발생")
    void accountTransferEx() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_EX, 10000);
        memberRepositoryV1.save(memberA);
        memberRepositoryV1.save(memberB);

        // when
        log.info("===============================");
        Assertions.assertThatThrownBy(() -> memberServiceV3_1.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);
        log.info("===============================");

        // then
        Member findMemberA = memberRepositoryV1.findById(memberA.getMemberId());
        Member findMemberB = memberRepositoryV1.findById(memberB.getMemberId());

        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(10000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }
}