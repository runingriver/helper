package org.helper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class FutureTaskTest {
    private static final Logger logger = LoggerFactory.getLogger(FutureTaskTest.class);
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Test
    public void FutureTest() {
        FutureTask<TestEntity<Entity1>> futureTask = new FutureTask(new TestCallable());
        executorService.submit(futureTask);
        try {
            //注:此处判定不妥,如果call方法抛异常,get()会将异常抛出来!
            if (futureTask.get() != null) {
                TestEntity<Entity1> entity1TestEntity = futureTask.get();
                logger.info("{}", entity1TestEntity.toString());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static class TestCallable implements Callable<TestEntity<Entity1>> {
        @Override
        public TestEntity<Entity1> call() throws Exception {
//            Entity1 entity1 = new Entity1();
//            entity1.setA(111);
//            TestEntity<Entity1> entity1TestEntity = new TestEntity<>();
//            entity1TestEntity.setMessage("泛型和回调的嵌套使用");
//            entity1TestEntity.setAge(23);
//            entity1TestEntity.setField(entity1);
//            return entity1TestEntity;
            throw new NullPointerException("空指针异常");
        }
    }

    public static class TestEntity<T> {
        private String message;

        private int age;

        private T field;

        public T getField() {
            return field;
        }

        public void setField(T field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "TestEntity{" +
                    "age=" + age +
                    ", message='" + message + '\'' +
                    ", field=" + field +
                    '}';
        }
    }

    private static class Entity1 {
        private int a;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        @Override
        public String toString() {
            return "Entity1{" +
                    "a=" + a +
                    '}';
        }
    }

}
