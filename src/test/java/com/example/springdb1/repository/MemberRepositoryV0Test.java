package com.example.springdb1.repository;

import com.example.springdb1.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    private static final String MEMBER_ID = "memberV1";

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
        Assertions.assertThat(findMember).isEqualTo(member);
    }
}