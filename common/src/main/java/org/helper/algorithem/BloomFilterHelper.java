package org.helper.algorithem;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 布隆过滤器
 *
 * @author hzz 18-1-18
 */
public class BloomFilterHelper {
    private static final Logger logger = LoggerFactory.getLogger(BloomFilterHelper.class);

    public static void main(String[] args) {

        BloomFilter<String> stringBloom = BloomFilter.create(new Funnel<String>() {
            @Override
            public void funnel(String from, PrimitiveSink into) {
                into.putString(from, Charsets.UTF_8);
            }
        }, 1024 * 1024 * 1024 * 4, 0.0001);
    }

}
