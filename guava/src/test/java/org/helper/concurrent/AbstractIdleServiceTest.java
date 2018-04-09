package org.helper.concurrent;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service;

/**
 * Guava包里的Service接口用于封装一个服务对象的运行状态、包括start和stop等方法
 */
public class AbstractIdleServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractIdleServiceTest.class);

    public static class FunctionalTest {
        private static final Logger logger = LoggerFactory.getLogger(FunctionalTest.class);

        // 实现AbstractIdleService,并测试方法使用
        private static class DefaultService extends AbstractIdleService {
            @Override
            protected void startUp() throws Exception {
            }

            @Override
            protected void shutDown() throws Exception {
            }
        }

        @Test
        public void testServiceStartStop() throws Exception {
            AbstractIdleService service = new DefaultService();
            service.startAsync().awaitRunning();
            logger.info("{} == {}", Service.State.RUNNING, service.state());
            service.stopAsync().awaitTerminated();
            logger.info("{} == {}", Service.State.TERMINATED, service.state());
        }

        @Test
        public void testStart_failed() throws Exception {
            final Exception exception = new Exception("deliberate");
            AbstractIdleService service = new DefaultService() {
                @Override
                protected void startUp() throws Exception {
                    throw exception;
                }
            };
            try {
                // 服务开启时会调用startUp方法
                service.startAsync().awaitRunning();
            } catch (RuntimeException e) {
                logger.error("error", e);
            }
            logger.info("{} == {}", Service.State.FAILED, service.state());
        }

        @Test
        public void testStop_failed() throws Exception {
            final Exception exception = new Exception("deliberate");
            AbstractIdleService service = new DefaultService() {
                @Override
                protected void shutDown() throws Exception {
                    throw exception;
                }
            };
            service.startAsync().awaitRunning();
            try {
                // 服务停止的时候会调用shutDown方法
                service.stopAsync().awaitTerminated();
            } catch (RuntimeException e) {
                logger.error("error:{}", e.getCause(), e);
            }
            logger.info("{} == {}", Service.State.FAILED, service.state());
        }
    }

    @Test
    public void testStart() {
        TestService service = new TestService();
        logger.info("0 == {}", service.startUpCalled);
        service.startAsync().awaitRunning();
        logger.info("1 == {}", service.startUpCalled);
        logger.info("{} == {}", Service.State.RUNNING, service.state());

        // 比较下面两个是否是包含关系
        logger.info("service.transitionStates:{}", service.transitionStates);
        logger.info("Service.State:{}", Service.State.STARTING);
    }

    @Test
    public void testStart_failed() {
        final Exception exception = new Exception("deliberate");
        TestService service = new TestService() {
            @Override
            protected void startUp() throws Exception {
                super.startUp();
                throw exception;
            }
        };
        logger.info("startUpCalled 0 == {}", service.startUpCalled);
        try {
            service.startAsync().awaitRunning();
        } catch (RuntimeException e) {
            logger.error("{}:二者是否相等:", e.getCause(), exception);
        }
        logger.info("1 == {}", service.startUpCalled);

        logger.info("{} == {}", Service.State.FAILED, service.state());

        // 比较下面两个是否是包含关系
        logger.info("service.transitionStates:{}", service.transitionStates);
        logger.info("Service.State:{}", Service.State.STARTING);
    }

    @Test
    public void testStop_withoutStart() {
        TestService service = new TestService();
        service.stopAsync().awaitTerminated();
        logger.info("0=={}", service.startUpCalled);
        logger.info("0=={}", service.shutDownCalled);
        logger.info("{}=={}", Service.State.TERMINATED, service.state());
        // size == 0?
        logger.info("transition size:{}", service.transitionStates);
    }

    @Test
    public void testStop_afterStart() {
        TestService service = new TestService();
        service.startAsync().awaitRunning();
        logger.info("1=={}", service.startUpCalled);
        logger.info("0=={}", service.shutDownCalled);
        service.stopAsync().awaitTerminated();
        logger.info("1=={}", service.startUpCalled);
        logger.info("1=={}", service.shutDownCalled);
        logger.info("{}=={}", Service.State.TERMINATED, service.state());

        // 是否顺序包含:Service.State.STARTING, Service.State.STOPPING
        logger.info("transition:{}", service.transitionStates);
    }

    public void testStop_failed() {
        final Exception exception = new Exception("deliberate");
        TestService service = new TestService() {
            @Override
            protected void shutDown() throws Exception {
                super.shutDown();
                throw exception;
            }
        };
        service.startAsync().awaitRunning();
        logger.info("startUpCalled 1=={}", service.startUpCalled);
        logger.info("shutDownCalled 0=={}", service.shutDownCalled);
        try {
            service.stopAsync().awaitTerminated();

        } catch (RuntimeException e) {
            logger.error("二者是否相等:{} == {}", e.getCause(), exception);
        }
        logger.info("1=={}", service.startUpCalled);
        logger.info("1=={}", service.shutDownCalled);
        logger.info("{}=={}", Service.State.FAILED, service.state());
        // 是否顺序包含:Service.State.STARTING, Service.State.STOPPING
        logger.info("transition:{}", service.transitionStates);
    }

    @Test
    public void testServiceToString() {
        AbstractIdleService service = new TestService();
        logger.info("TestService [NEW] == {}", service.toString());
        service.startAsync().awaitRunning();
        logger.info("TestService [RUNNING] == {}", service.toString());
        service.stopAsync().awaitTerminated();
        logger.info("TestService [TERMINATED]=={}", service.toString());
    }

    @Test
    public void testTimeout() throws Exception {
        // Create a service whose executor will never run its commands
        Service service = new TestService() {
            @Override
            protected Executor executor() {
                return new Executor() {
                    public void execute(Runnable command) {
                        logger.info("enter execute.");
                    }
                };
            }

            @Override
            protected String serviceName() {
                return "Foo";
            }
        };
        try {
            service.startAsync().awaitRunning(1, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            logger.info("Timed out waiting for Foo [STARTING] to reach the RUNNING state. == ", e);
        }
    }

    private static class TestService extends AbstractIdleService {
        int startUpCalled = 0;
        int shutDownCalled = 0;
        final List<State> transitionStates = Lists.newArrayList();

        @Override
        protected void startUp() throws Exception {
            logger.info("startUpCalled :0 == {}", startUpCalled);
            logger.info("shutDownCalled :0 == {}", shutDownCalled);

            startUpCalled++;
            logger.info("{} == {}", State.STARTING, state());
        }

        @Override
        protected void shutDown() throws Exception {
            logger.info("0 == {}", startUpCalled);
            logger.info("0 == {}", shutDownCalled);
            shutDownCalled++;
            logger.info("{} == {}", State.STARTING, state());
        }

        @Override
        protected Executor executor() {
            transitionStates.add(state());
            return directExecutor();
        }
    }

}
