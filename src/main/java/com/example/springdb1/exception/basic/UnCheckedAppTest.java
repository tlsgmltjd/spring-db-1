package com.example.springdb1.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

// 엔체크 예외는 처리할 수 없다면 별도의 선언 없이 두면 된다.
// 대부분의 시스템 예외는 복구 불가능한 예외이기 때문에 다른 계층이 이러한 예외에 대해 신경쓰지 않아도 된다.
// 런타임 예외는 예외를 처리하는 부분을 생략할 수 있기 때문에 controlleradvice 같은 곳에서 공통으로 처리해주기만 하면 된다.
// 체크 예외를 잡아서 언체크 예외로 전환하여 던지는것도 가능하다.

@Slf4j
public class UnCheckedAppTest {

    @Test
    void unchecked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(controller::request)
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void printException() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
            log.info("ex", e); // 로그 마지막 파라미터에 예외를 넣어주면 로그에 스택 트레이스를 출력할 수 있다.
        }
    }

    static class Controller {

        Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectionException("연결 실패");
        }
    }

    static class Repository {
        public void call() {
            try {
                runSql();
            } catch (SQLException e) {
                throw new RuntimeSqlException(e); // 예외를 포함시키면 기존 예외에 대한 정보도 함께 스텍트레이스에 출력됨 (Caused by)

                // 예외를 전환할 때 기존 예외에 대한 정보를 포함시키지 않으먄 실제 예외에 대한 정보를 받을 수 없다.
                // 예외 전환시 꼭 기존 예외를 포함해야한다!!!!!!!!!!!!!!!!!!!!
            }
        }

        public void runSql() throws SQLException {
            throw new SQLException();
        }

    }

    static class RuntimeConnectionException extends RuntimeException {
        public RuntimeConnectionException(String message) {
            super(message);
        }
    }

    static class RuntimeSqlException extends RuntimeException {
        public RuntimeSqlException(Throwable cause) {
            super(cause);
        }
    }

}
