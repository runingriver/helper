package org.helper.main;


import java.util.ArrayList;
import java.util.List;

public class Test1Main {

    public static void main(String[] args) throws InterruptedException {
        //1M
        int size = 1024 * 1024;
        List<byte[]> list = new ArrayList<>();
        while (true) {
            System.out.println("hello");
            byte[] a = new byte[size];
            list.add(a);
        }
    }

}
