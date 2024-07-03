package com.example.springdb1.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

// 체크예외: 복구 가능성이 있는 예외, 컴파일러가 예외 처리를 강제화 한다. -> Exception을 상속 받은 예외 객체
// 언체크 예외: 복구 가능성이 없다고 판단되는 예외, 컴파일러가 예외 처리를 강제화하지 않음 (Error와 비슷하다.) -> RuntimeException을 상속 받은 예외 객체
@Slf4j
public class CheckedTest {

    @Test
    void checked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checked_throw() {
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyCheckedException.class);
    }

    /**
     * Exception을 상속받은 예외는 체크 예외가 된다.
     */
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    /**
     * Checked 예외는
     * 예외를 잡아서 처리하거나, 해당 메서드 호출 지점으로 예외를 던지거나 둘 중 하나를 필수로 수행해야한다.
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 예외를 잡아서 처리하는 코드
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                // 예외 처리 로직
                log.info("예외 처리, message = {}", e.getMessage(), e);
            }
        }

        /**
         * 체크 예외를 밖으로 던지는 코드
         * @throws MyCheckedException
         */
        public void callThrow() throws MyCheckedException {
            throw new MyCheckedException("exception");
        }
    }

    static class Repository {
        public void call() throws MyCheckedException {
            throw new MyCheckedException("exception");
        }
    }
}
