package com.example.springdb1.service;

import com.example.springdb1.domain.Member;
import com.example.springdb1.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {
    private final MemberRepositoryV1 memberRepositoryV1;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        // transaction 시작 (set autocommit false)

        Member fromMember = memberRepositoryV1.findById(fromId);
        Member toMember = memberRepositoryV1.findById(toId);

        memberRepositoryV1.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepositoryV1.update(toId, toMember.getMoney() + money);

        // commit

    }

    // 트랜잭션이 유지되는 동안은 꼭 "같은 커넥션"을 유지해야한다.
    // -> 커넥션을 파라미터로 받아서 같은 커넥션을 사용하도록 하면 된다.v2

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException();
        }
    }
}
