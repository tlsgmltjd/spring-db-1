package com.example.springdb1.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

// 체크 예외는 대부분은 복구가 불가능 하다 이러한 체크 예외를 다른 계층으로 던지면 그 계층에서 해당 체크 예외를 처리할 수 있는 방법이 없는 경우가 많다
// 이런 체크 예외를 받은 다른 계층은 해당 예외에 대한 불필요한 의존관계를 가지게 된다.
// 그렇다고 Exception을 throws하면 비즈니스상 중요한 꼭 잡아서 처리해야하는 체크 예외까지 던져지게 되는 불상사가 발생할 수 있다.

// 체크 예외는 비즈니스상 꼭 잡아서 처리해야하는 중요한 예외일 때만 사용하는것을 권장하고 기본적으로는 언체크 예외를 사용하는 것이 좋다.

public class CheckedAppTest {

    @Test
    void checked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(controller::request)
                .isInstanceOf(Exception.class);
    }

    static class Controller {

        Service service = new Service();

        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("exception");
        }
    }

    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("exception");
        }
    }

}
