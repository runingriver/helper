package org.helper.collections;

import com.google.common.collect.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

/**
 * @author hzz 18-3-26
 */
public class binTree {
    private static final Logger logger = LoggerFactory.getLogger(binTree.class);

    public static class Node<T> {
        T data;

        Node<T> lTree, rTree;

        public Node(T data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "Node{data=" + data + '}';
        }
    }

    /**
     * 层次打印二叉树
     */
    public static <T> void levelOrderPrint(Node<T> node) {
        if (node == null) {
            return;
        }
        Queue<Node<T>> queue = Queues.newLinkedBlockingQueue();
        queue.offer(node);
        do {
            Node<T> temp = queue.peek();
            logger.info(" {} ", temp);
            queue.remove();
            if (temp.lTree != null) {
                queue.offer(temp.lTree);
            }
            if (temp.rTree != null) {
                queue.offer(temp.rTree);
            }
        } while (!queue.isEmpty());
    }

    public static int getHight(Node node) {
        if (node == null) {
            return 0;
        }
        int height,lheight,rheight;
        lheight = getHight(node.lTree) + 1;
        rheight = getHight(node.rTree) + 1;
        height = lheight > rheight ? lheight : rheight;
        return height;
    }

}
