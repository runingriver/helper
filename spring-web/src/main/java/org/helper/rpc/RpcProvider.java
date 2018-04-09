package org.helper.rpc;

public class RpcProvider {
    public static void main(String[] args) throws Exception {
        HelloService service = new HelloServiceImpl();
        RpcFramework.export(service, 1234);
    }
}
