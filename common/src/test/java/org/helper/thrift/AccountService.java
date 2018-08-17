package org.helper.thrift;

import java.util.HashMap;
import java.util.Map;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.helper.thrift.generated.Account;
import org.helper.thrift.generated.InvalidOperation;
import org.helper.thrift.generated.Operation;
import org.helper.thrift.generated.Request;

/**
 * accountService
 */
public class AccountService implements Account.Iface {
    private static Map<String, String> accounts = new HashMap<String, String>();
    @Override
    public String doAction(Request request) throws InvalidOperation {
        String name = request.getName();
        String pass = request.getPassword();
        Operation op = request.getOp();
        System.out.println(String.format("Get request[name:%s, pass:%s, op:%d]", name, pass, op.getValue()));
        if (name == null || name.length() == 0){
            throw new InvalidOperation(100, "param name should not be empty");
        }
        if (op == Operation.LOGIN) {
            String password = accounts.get(name);
            if (password != null && password.equals(pass)) {
                return "Login success!! Hello " + name;
            } else {
                return "Login failed!! please check your username and password";
            }
        } else if (op == Operation.REGISTER) {
            if (accounts.containsKey(name)) {
                return String.format("The username '%s' has been registered, please change one.", name);
            } else {
                accounts.put(name, pass);
                return "Register success!! Hello " + name;
            }
        } else {
            throw new InvalidOperation(101, "unknown operation: " + op.getValue());
        }
    }

    public static void main(String[] args) throws Exception {
        TServerSocket socket = new TServerSocket(9999);
        Account.Processor processor = new Account.Processor<Account.Iface>(new AccountService());
        TServer server = new TSimpleServer(new TServer.Args(socket).processor(processor));
        System.out.println("Starting the Account server...");
        server.serve();
    }
}

