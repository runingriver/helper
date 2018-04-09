package org.helper.collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 最近未被使用LRU缓存的三种实现
 */
public class LRUCache {
    private static final Logger logger = LoggerFactory.getLogger(LRUCache.class);

    /**
     * 1. 继承实现,线程不安全!
     * @param <K>
     * @param <V>
     */
    public static abstract class LRUCache1<K, V> extends LinkedHashMap<K, V> {
        private static final float FACTORY = 0.75f;
        private int capacity;

        public LRUCache1(int capacity) {
            super((int) (capacity / FACTORY + 1), FACTORY, true);
            //如果设置成capacity的初始大小,那么没等到capacity满就扩容了.
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return this.size() > capacity;
        }

        @Override
        public V get(Object key) {
            if (!containsKey(key)) {
                V v = create((K) key);
                put((K) key, v);
            }
            return super.get(key);
        }

        abstract V create(K key);
    }

    /**
     * 2. 组合实现
     * @param <K>
     * @param <V>
     */
    public abstract static class LRUCache2<K, V> {
        private LinkedHashMap<K, V> cacheMap;
        private int capacity;
        private static final float FACTORY = 0.75f;

        public LRUCache2(final int capacity) {
            this.capacity = capacity;
            int count = Math.round(capacity / 0.75f) + 1;
            cacheMap = new LinkedHashMap<K, V>(count, FACTORY, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry eldest) {
                    return cacheMap.size() > capacity;
                }
            };
        }

        public synchronized V get(K key) {
            if (!cacheMap.containsKey(key)) {
                cacheMap.put(key, create(key));
            }
            return cacheMap.get(key);
        }

        public synchronized void set(K key, V value) {
            cacheMap.put(key, value);
        }

        public synchronized void remove(K key) {
            cacheMap.remove(key);
        }

        abstract V create(K key);
    }

    /**
     * 3. LinkList+Map实现,利用map的查询效率,利用link的删改移的效率,map负责查询,link负责cache顺序
     * @param <K>
     * @param <V>
     */
    public abstract static class LRUCache3<K, V> {
        private int capacity;
        /**
         * map用于查找
         */
        private HashMap<K, CacheNode<K, V>> cacheMap;

        /**
         * 双链表用于移位,删除
         * @param <K>
         * @param <V>
         */
        class CacheNode<K, V> {
            K key;
            V value;
            CacheNode<K, V> pre;
            CacheNode<K, V> next;

            public CacheNode(K key, V value) {
                this.key = key;
                this.value = value;
            }
        }

        /**
         * 带头结点的双链表
         */
        CacheNode<K, V> head;

        public LRUCache3(int capacity) {
            this.capacity = capacity;
            int count = Math.round(capacity / 0.75f) + 1;
            cacheMap = new HashMap<>(count);
            head = new CacheNode<>(null, null);
            head.next = head;
            head.pre = head;
        }

        public synchronized V get(K key) {
            //缓存命中
            if (cacheMap.containsKey(key)) {
                CacheNode<K, V> node = cacheMap.get(key);
                moveToFirst(node);
                return node.value;
            }

            //缓存未命中
            V v = create(key);
            if (cacheMap.size() >= capacity) {
                CacheNode<K, V> node = removeLast();
                cacheMap.remove(node.key);
            }
            CacheNode<K, V> newNode = new CacheNode<>(key, v);
            cacheMap.put(key, newNode);
            addToFirst(newNode);

            return newNode.value;
        }

        public synchronized void set(K key, V value) {
            if (cacheMap.containsKey(key)) {
                CacheNode<K, V> cacheNode = cacheMap.get(key);
                if (!cacheNode.value.equals(value)) {
                    cacheNode.value = value;
                }
                moveToFirst(cacheNode);
            }
            //不包含该key
            if (cacheMap.size() >= capacity) {
                CacheNode<K, V> node = removeLast();
                cacheMap.remove(node.key);
            }
            V v = create(key);
            CacheNode<K, V> newNode = new CacheNode<>(key, v);
            cacheMap.put(key, newNode);
            addToFirst(newNode);
        }

        public synchronized void remove(K key) {
            if (!cacheMap.containsKey(key)) {
                return;
            }
            CacheNode<K, V> node = cacheMap.get(key);
            cacheMap.remove(key);
            removeNode(node);
        }

        private CacheNode<K, V> removeLast() {
            if (head.next.equals(head)) {
                return null;
            }

            CacheNode<K, V> tail = head.pre;
            head.pre = tail.pre;
            tail.pre.next = head;

            //回收
            tail.pre = tail.next = null;
            return tail;
        }

        private void addToFirst(CacheNode<K, V> node) {
            CacheNode<K, V> headNext = head.next;
            head.next = node;
            node.pre = head;

            headNext.pre = node;
            node.next = headNext;
        }

        private void moveToFirst(CacheNode<K, V> node) {
            if (head.next.equals(head) || head.next.next.equals(head)) {
                return;
            }

            if (node.next == null || node.pre == null) {
                return;
            }

            node.pre.next = node.next;
            node.next.pre = node.pre;
            node.next = head.next;
            head.next.pre = node;
            head.next = node;
            node.pre = head;
        }

        private void removeNode(CacheNode<K, V> node) {
            if (node.next == null || node.pre == null) {
                return;
            }
            if (node == head) {
                return;
            }

            node.next.pre = node.pre;
            node.pre.next = node.next;
            node.pre = node.next = null;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(50);
            builder.append("size:").append(cacheMap.size()).append(',');
            builder.append("data:");
            CacheNode<K, V> node = head.next;
            if (head.next == head) {
                builder.append("empty!");
            } else {
                builder.append(node.key).append('-').append(node.value).append(',');
                while (node.next != head) {
                    node = node.next;
                    builder.append(node.key).append('-').append(node.value).append(',');
                }
            }
            builder.deleteCharAt(builder.lastIndexOf(",")).append(".");
            return builder.toString();
        }

        abstract V create(K key);
    }

    public static void main(String[] args) {
        LRUCache3<Integer, String> cache3 = new LRUCache3<Integer, String>(5) {
            @Override
            String create(Integer key) {
                return key.toString();
            }
        };

        logger.info("first get 1 result:{}", cache3.get(1));
        logger.info("{}", cache3.toString());
        cache3.set(2, "2");
        logger.info("{}", cache3.toString());

        cache3.set(3, "3");
        logger.info("{}", cache3.toString());

        cache3.set(4, "4");
        logger.info("{}", cache3.toString());

        cache3.set(5, "5");
        logger.info("{}", cache3.toString());

        cache3.set(6, "6");
        logger.info("{}", cache3.toString());

        cache3.set(7, "7");
        logger.info("{}", cache3.toString());

        cache3.get(4);
        logger.info("{}", cache3.toString());

        cache3.get(1);
        logger.info("{}", cache3.toString());

    }

}
