package com.example.springdb1.repository;

import com.example.springdb1.domain.Member;

import java.sql.SQLException;

// 인터페이스 구현체의 메서드가 체크예외를 던지려면 해당 인터페이스의 메서드 시그니처에 체크예외를 던지는 부분이 선언되어 있어야한다.
// 이렇게 된다면 구현 기술을 쉽게 변경하려고 인터페이스를 만들었는데 특정 기술에 종속적인 인터페이스가 되어버린다.
public interface MemberRepositoryEx {
    Member save(Member member) throws SQLException;
    Member findById(String memberId) throws SQLException;
    void update(String memberId, int money) throws SQLException;
    void delete(String memberId) throws SQLException;
}
