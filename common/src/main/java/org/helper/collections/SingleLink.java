package org.helper.collections;

/**
 * 带头节点的单链表
 */
public class SingleLink<E> {

    private final Node<E> head;

    public static class Node<E> {
        E data;
        Node<E> next;

        public Node(E data) {
            this.data = data;
        }
    }

    public SingleLink(Node<E> node) {
        this.head = new Node<>(null);
        head.next = node;
    }

    public SingleLink() {
        this.head = new Node<>(null);
    }

    public Node<E> getHead() {
        return head;
    }

    public void printLink() {
        Node<E> node = head.next;
        while (node != null) {
            System.out.println(node.data.toString());
            node = node.next;
        }
    }

    public void insertInFront(E e) {
        Node<E> node = new Node<>(e);
        node.next = head.next;
        head.next = node;
    }

    public void insertInLast(E e) {
        Node<E> node = head;
        while (node.next != null) {
            node = node.next;
        }
        Node<E> addNode = new Node<>(e);
        node.next = addNode;
    }

    public void insertAfter(Node<E> head, E e) {
        Node<E> node = head.next;
        while (node.next != null && !node.data.equals(e)) {
            node = node.next;
        }
        if (node == null) {
            return;
        }
        Node<E> dataNode = new Node<>(e);
        dataNode.next = node.next;
        node.next = dataNode;
    }

    public boolean isOrdered() {
        Node<E> node = head.next;
        Node<E> pNode = node.next;
        boolean isOrdered = true;
        while (pNode != null) {
            //比较,这里模拟
            if (node.data.equals(pNode.data)) {
                isOrdered = false;
            }
            node = pNode;
            pNode = pNode.next;
        }
        return isOrdered;
    }

    /**
     * 三个指针实现
     */
    public void reverse() {
        Node<E> pre, cur, rear;
        cur = head.next;
        pre = rear = null;
        while (cur.next != null) {
            rear = cur.next;
            cur.next = pre;
            pre = cur;
            cur = rear;
        }
        cur.next = pre;
        head.next = cur;
    }

    /**
     * 栈实现
     */
    public void reverse2() {
        Node<E>[] stack = new Node[100];
        Node<E> node = head.next;
        if (node == null) {
            return;
        }
        int top = 0;
        while (node != null) {
            stack[top++] = node;
            node = node.next;
        }

        head.next = node = stack[--top];
        while (top > 0) {
            node.next = stack[--top];
            node = node.next;
        }
        stack[0].next = null;
    }

    /**
     * 递归实现
     */
    public Node<E> reverse3(Node<E> node) {
        if (node.next == null) {
            head.next = node;
            return node;
        }
        Node<E> nextNode = reverse3(node.next);
        nextNode.next = node;
        node.next = null;
        return node;
    }

    /**
     * 一个临时变量实现
     * 临时变量保存前一个节点引用
     * eg:temp只向的下一个元素就是cur指针的上一个元素
     */
    public Node<E> reversr4(Node<E> head) {
        Node<E> cur = head;
        Node<E> temp = new Node<>(null);
        while (cur != null) {
            Node<E> next = cur.next;
            cur.next = temp.next;
            temp.next = cur;
            cur = next;
        }
        return temp.next;
    }

    public static void main(String[] args) {
        SingleLink<Integer> link = new SingleLink<>();
        for (int i = 0; i < 20; i++) {
            link.insertInLast(i);
        }
        link.printLink();

//        link.insertInFront(33);
//        link.printLink();

        link.reverse3(link.getHead().next);
        link.printLink();
    }

}
