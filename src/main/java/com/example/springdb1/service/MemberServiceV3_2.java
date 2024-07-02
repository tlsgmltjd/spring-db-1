package com.example.springdb1.service;

import com.example.springdb1.domain.Member;
import com.example.springdb1.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 트랜잭션 - 트랜잭션 템플릿 사용
 */
@Slf4j
public class MemberServiceV3_2 {

    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepositoryV3;

    public MemberServiceV3_2(PlatformTransactionManager txm, MemberRepositoryV3 memberRepositoryV3) {
        this.txTemplate = new TransactionTemplate(txm);
        this.memberRepositoryV3 = memberRepositoryV3;
    }

    public void accountTransfer(String fromId, String toId, int money) {
        // 트랜잭션 템플릿을 사용하면 기존에 트랜잭션 메니저를 직접 사용할 때 보다 템플릿 콜백 패턴으로 반복되는 코드를 템플릿으로 줄여준다.
        txTemplate.executeWithoutResult((status) -> bizLogic(fromId, toId, money));
    }

    private void bizLogic(String fromId, String toId, int money) {
        Member fromMember = memberRepositoryV3.findById(fromId);
        Member toMember = memberRepositoryV3.findById(toId);

        memberRepositoryV3.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepositoryV3.update(toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException();
        }
    }
}
