package org.helper.rpc;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String content) {
        return "hello " + content;
    }
}
