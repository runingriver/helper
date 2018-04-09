package org.helper.collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 阻塞队列,底层是带头结点的单链表
 */
public class BlockLinkQueue<E> implements Iterable<E> {
    private Node<E> head;
    private Node<E> tail;
    private final int capacity;
    private volatile AtomicInteger count = new AtomicInteger(0);

    private final ReentrantLock tailLock = new ReentrantLock();
    private final ReentrantLock headLock = new ReentrantLock();

    private final Condition fullCondition = tailLock.newCondition();

    private final Condition emptyCondition = headLock.newCondition();

    private static class Node<E> {
        E element;
        Node<E> next;

        public Node(E element) {
            this.element = element;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "element=" + element +
                    ", next=" + next +
                    '}';
        }
    }

    public BlockLinkQueue(int capacity) {
        this.capacity = capacity;
        tail = head = new Node<>(null);
    }

    private void enQueue(Node<E> node) {
        logger.info("enQueue:{}", node.toString());
        tail = tail.next = node;
    }

    private E deQueue() {
        logger.info("deQueue");
        Node<E> node = head.next;
        E result = node.element;
        //抛弃首节点
        head.next = head;
        //将出队的这个节点当做head节点,并置空element
        head = node;
        node.element = null;
        return result;
    }

    public void push(E e) throws InterruptedException {
        Node<E> node = new Node<>(e);
        tailLock.lock();
        try {
            while (count.get() == capacity) {
                logger.info("push:fullCondition wait;");
                fullCondition.await();
            }
            enQueue(node);
            count.getAndIncrement();
        } finally {
            tailLock.unlock();
        }

        headLock.lock();
        try {
            emptyCondition.signal();
        } finally {
            headLock.unlock();
        }

    }

    public E pull() throws InterruptedException {
        E node;
        headLock.lock();
        try {
            while (count.get() == 0) {
                logger.info("pull:emptyCondition wait;");
                emptyCondition.await();
            }
            node = deQueue();
            count.getAndDecrement();
        } finally {
            headLock.unlock();
        }

        tailLock.lock();
        try {
            fullCondition.signal();
        } finally {
            tailLock.unlock();
        }

        return node;
    }

    public void fullyLock() {
        headLock.lock();
        tailLock.lock();
    }

    public void fullyUnlock() {
        headLock.unlock();
        tailLock.unlock();
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    public class Itr implements Iterator<E> {
        private Node<E> previous;
        private Node<E> current;

        public Itr() {
            fullyLock();
            try {
                previous = head;
                current = head.next;
            } finally {
                fullyUnlock();
            }
        }


        @Override
        public boolean hasNext() {
            fullyLock();
            try {
                return current != null;
            } finally {
                fullyUnlock();
            }
        }

        @Override
        public E next() {
            fullyLock();
            try {
                E element = current.element;
                previous = current;
                current = current.next;
                return element;
            } finally {
                fullyUnlock();
            }
        }

        @Override
        public void remove() {
            fullyLock();
            try {
                previous.next = current.next;
                current = current.next;
                current.next = null;
                current.element = null;
            } finally {
                fullyUnlock();
            }
        }
    }


    private static final Logger logger = LoggerFactory.getLogger(BlockLinkQueue.class);

    public static void main(String[] args) {
        final BlockLinkQueue<Integer> blockLinkQueue = new BlockLinkQueue<>(15);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        logger.info("push,i:{}", i);
                        blockLinkQueue.push(new Integer(i));
                    }

                    Iterator<Integer> iterator = blockLinkQueue.iterator();
                    while (iterator.hasNext()) {
                        Integer next = iterator.next();
                        if (next.intValue() > 5) {
                            iterator.remove();
                        }
                    }

                    for (Integer integer : blockLinkQueue) {
                        logger.info("iter:{}", integer);
                    }
                } catch (InterruptedException e) {
                    logger.error("t1 exception.", e);
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    for (int i = 0; i < 10; i++) {
//                        Integer pull = blockLinkQueue.pull();
//                        logger.info("pull,i:{},node:{}", i, pull);
//                    }
//                } catch (InterruptedException e) {
//                    logger.error("t2 exception.", e);
//                }
            }
        });

        t1.start();
        t2.start();

        while (t1.isAlive() || t2.isAlive()) {
        }

        logger.info("done");
    }
}
