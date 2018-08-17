package org.helper.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.helper.thrift.generated.Account;
import org.helper.thrift.generated.InvalidOperation;
import org.helper.thrift.generated.Operation;
import org.helper.thrift.generated.Request;

public class AccountClient {

    public static void main(String[] args) throws TException {
        TTransport transport = new TSocket("localhost", 9999);
        transport.open();   //建立连接
        TProtocol protocol = new TBinaryProtocol(transport);
        Account.Client client = new Account.Client(protocol);
        //第一个请求， 登录 wuchong 帐号
        Request req = new Request("wuchong", "1234", Operation.LOGIN);
        request(client, req);
        //第二个请求， 注册 wuchong 帐号
        req.setOp(Operation.REGISTER);
        request(client, req);
        //第三个请求， 登录 wuchong 帐号
        req.setOp(Operation.LOGIN);
        request(client, req);
        //第四个请求， name 为空的请求
        req.setName("");
        request(client, req);
        transport.close();  //关闭连接
    }
    public static void request(Account.Client client, Request req) throws TException{
        try {
            String result = client.doAction(req);
            System.out.println(result);
        } catch (InvalidOperation e) {
            System.out.println(e.reason);
        }
    }
}
