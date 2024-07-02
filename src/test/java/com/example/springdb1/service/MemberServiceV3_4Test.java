package com.example.springdb1.service;

import com.example.springdb1.domain.Member;
import com.example.springdb1.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static com.example.springdb1.connection.ConnectionConst.*;

/**
 * DataSource, transaction manager 자동 등록
 */

// DataSource, transaction manager는 스프링이 자동으로 빈으로 등록해줌

// DataSource는 application.yml에 등록해둔 정보를 바탕으로 DataSource를 생성하여 빈으로 등록해줌
// transaction manager는 PlateformTransactionManager라는 인터페이스를 구현한 트랜잭션 매니저를 빈으로 자동 등록한다.
// 트랜잭션 매니저의 구현체는 데이터 접근 기술마다 다르기 때문에 스프링은 라이브러리를 보고 적절한 구현체룰 선택하여 빈으로 등록해준다!

// 스프링 자동 생성 매커니즘은 개발자가 수동으로 빈 등록시 자동 빈 등록이 되지 않음

@SpringBootTest
@Slf4j
class MemberServiceV3_4Test {
    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepositoryV3 memberRepositoryV3;
    @Autowired
    private MemberServiceV3_3 memberServiceV3_3;

    @TestConfiguration
    static class TestConfig {

        @Autowired
        private DataSource dataSource; // 스프링이 자동 등록 해주는 DataSource를 주입받는다.

        @Bean
        MemberRepositoryV3 memberRepositoryV3() {
            return new MemberRepositoryV3(dataSource);
        }

        @Bean
        MemberServiceV3_3 memberServiceV3_3() {
            return new MemberServiceV3_3(memberRepositoryV3());
        }

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
        memberRepositoryV3.save(memberA);
        memberRepositoryV3.save(memberB);

        // when
        log.info("===============================");
        memberServiceV3_3.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);
        log.info("===============================");

        // then
        Member findMemberA = memberRepositoryV3.findById(memberA.getMemberId());
        Member findMemberB = memberRepositoryV3.findById(memberB.getMemberId());

        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(8000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이채중 예외 발생")
    void accountTransferEx() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_EX, 10000);
        memberRepositoryV3.save(memberA);
        memberRepositoryV3.save(memberB);

        // when
        log.info("===============================");
        Assertions.assertThatThrownBy(() -> memberServiceV3_3.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);
        log.info("===============================");

        // then
        Member findMemberA = memberRepositoryV3.findById(memberA.getMemberId());
        Member findMemberB = memberRepositoryV3.findById(memberB.getMemberId());

        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(10000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }

    @Test
    void aopCheck() {
        // MemberServiceV3_3$$SpringCGLIB$$0
        // 트랜잭션 프록시 객체가 memberServiceV3_3를 상속받은 후 트랜잭션 관련 코드를 추가하여 스프링 빈에 등록해버린다!!!
        // 그렇기 때문에 빈으로 주입받은 클래스 정보를 출력해보면 프록시 객체가 출력된다.
        log.info("======== memberService class={}", memberServiceV3_3.getClass());
        log.info("======== memberRepository class={}", memberRepositoryV3.getClass());

        Assertions.assertThat(AopUtils.isAopProxy(memberServiceV3_3)).isTrue();
        Assertions.assertThat(AopUtils.isAopProxy(memberRepositoryV3)).isFalse();
    }

}