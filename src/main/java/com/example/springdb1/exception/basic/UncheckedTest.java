package com.example.springdb1.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

// 언체크 예외는 "신경쓰고 싶지 않은 예외 처리를 무시"할 수 있고 "불필요한 예외 의존관계를 참조하지 않게" 도와준다!

@Slf4j
public class UncheckedTest {

    @Test
    void unchecked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throw() {
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyUncheckedException.class);
    }

    /**
     * RuntimeException을 상속받은 예외는 언체크 예외가 된다.
     */
    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * UnChecked 예외는
     * 직접 선언적으로 예외를 잡거나, 던지지 않아도 된다.
     * 예외를 잡지 않으면 자동으로 밖으로 던진다.
     */
    static class Service {

        Repository repository = new Repository();

        // 모든 예외는 잡거나 던질 수 있다. 체크, 언체크 예외의 차이점은 그냥 컴파일러가 체크해주냐 안해주냐의 차이이다.
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                log.info("언체크 예외 처리, message = {}", e.getMessage(), e);
            }
        }

        // 예외를 안잡아도 된다. 자동으로 상위로 넘어간다. 체크 예외와 다르게 직접 throws를 선언해주지 않아도 된다.
        public void callThrow() {
            repository.call();
        }
    }

    static class Repository {
        public void call()
            throws MyUncheckedException // 해도 되긴 하다. 중요한 예외는 직접 선언하여 예외 발생 여부를 한눈에 파악할 수도 있다.
        { // 언체크 예외는 throws를 생략할 수 있다. (예외처리 생략 기능)
            throw new MyUncheckedException("exception");
        }
    }


}
