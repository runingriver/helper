package org.helper.base;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 模拟Splitter实现,理解Splitter的设计精髓,并讲HCharMatcher结合起来.
 * 迭代器的设计,值得领悟,如何去简化调用者的使用
 * 未完:
 * final CharMatcher split_sign = CharMatcher.whitespace().or(CharMatcher.is(':')).or(CharMatcher.is(','));
 * final Splitter line_splitter = Splitter.on(split_sign).omitEmptyStrings().trimResults();
 * 继续研究,实现
 */
public class HSplitter {
    private final boolean omitEmptyStrings;
    private final Strategy strategy;
    private final int limit;

    private HSplitter(Strategy strategy) {
        this(strategy, false, Integer.MAX_VALUE);
    }

    private HSplitter(Strategy strategy, boolean omitEmptyStrings, int limit) {
        this.strategy = strategy;
        this.omitEmptyStrings = omitEmptyStrings;
        this.limit = limit;
    }

    public static HSplitter on(char separator) {
        return on(HCharMatcher.is(separator));
    }

    public static HSplitter on(final HCharMatcher separatorMatcher) {
        Strategy strategy = new Strategy() {
            @Override
            public SplittingIterator iterator(HSplitter splitter, final CharSequence toSplitString) {
                return new SplittingIterator(splitter, toSplitString) {
                    @Override
                    int separatorStart(int start) {
                        return separatorMatcher.indexIn(toSplitString, start);
                    }

                    @Override
                    int separatorEnd(int separatorPosition) {
                        return separatorPosition + 1;
                    }
                };
            }
        };

        return new HSplitter(strategy);
    }

    public HSplitter omitEmptyStrings() {
        return new HSplitter(strategy, true, limit);
    }

    private Iterator<String> splittingIterator(CharSequence sequence) {
        return strategy.iterator(this, sequence);
    }

    public List<String> splitToList(CharSequence sequence) {
        Iterator<String> iterator = splittingIterator(sequence);
        List<String> result = new ArrayList<String>();

        while (iterator.hasNext()) {
            result.add(iterator.next());
        }

        return Collections.unmodifiableList(result);
    }

    private interface Strategy {
        Iterator<String> iterator(HSplitter splitter, CharSequence toSplit);
    }

    //该类实现迭代器功能.
    private abstract static class SplittingIterator extends AbstractIterator<String> {
        final CharSequence toSplit;
        final boolean omitEmptyStrings;

        abstract int separatorStart(int start);

        abstract int separatorEnd(int separatorPosition);

        int offset = 0;
        int limit;

        protected SplittingIterator(HSplitter splitter, CharSequence toSplit) {
            this.omitEmptyStrings = splitter.omitEmptyStrings;
            this.limit = splitter.limit;
            this.toSplit = toSplit;
        }

        //简单理解就是,计算Splitter类对象的下一个迭代结果
        protected String computeNext() {
            int nextStart = offset;
            while (offset != -1) {
                int start = nextStart;
                int end;

                int separatorPosition = separatorStart(offset);
                if (separatorPosition == -1) {
                    end = toSplit.length();
                    offset = -1;
                } else {
                    end = separatorPosition;
                    offset = separatorEnd(separatorPosition);
                }
                if (offset == nextStart) {
                    offset++;
                    if (offset >= toSplit.length()) {
                        offset = -1;
                    }
                    continue;
                }

                if (omitEmptyStrings && start == end) {
                    nextStart = offset;
                    continue;
                }



                return toSplit.subSequence(start, end).toString();
            }
            return endOfData();
        }
    }

    //拓展Iterator接口(不具备移除元素功能),提供给外部更简单的Iterator实现方式.
    private static abstract class AbstractIterator<T> implements Iterator<T> {
        private State state = State.NOT_READY;

        protected AbstractIterator() {}

        private enum State {
            READY,
            NOT_READY,
            DONE,
            FAILED,
        }

        private T next;

        protected abstract T computeNext();

        protected final T endOfData() {
            state = State.DONE;
            return null;
        }

        public final boolean hasNext() {
            if (state == State.FAILED) {
                throw new RuntimeException("Illegal state");
            }
            switch (state) {
                case READY:
                    return true;
                case DONE:
                    return false;
                default:
            }
            return tryToComputeNext();
        }

        private boolean tryToComputeNext() {
            state = State.FAILED;
            next = computeNext();
            if (state != State.DONE) {
                state = State.READY;
                return true;
            }
            return false;
        }

        public final T next() {
            if (!hasNext()) {
                throw new RuntimeException();
            }
            state = State.NOT_READY;
            T result = next;
            next = null;
            return result;
        }

        public final void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
