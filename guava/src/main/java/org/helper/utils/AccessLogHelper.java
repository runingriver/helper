package org.helper.utils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * 枚举实现，方法选择
 * 亮点1：通过构造函数决定使用哪个processor工作,枚举的使用！
 * 亮点2：Multimaps.index，利用list对象构造一个MultiMap,Function的返回值作为key，List中的对象作为Value
 * 相应的有Maps.uniqueIndex方法，返回一个独一无二Key,Multimaps允许属性值重复，但是Value不重复。
 * 亮点3:使用CharMatcher空格和？分割字符，CharMatcher.WHITESPACE.or(CharMatcher.is('?'))
 */
public class AccessLogHelper {

    private List<RequestDescriptor> descriptors;

    public AccessLogHelper(String classPath, LineProcessorStrategy strategy) throws IOException {
        URL resource = Resources.getResource(classPath);
        this.descriptors = Resources.asCharSource(resource, Charsets.UTF_8).readLines(strategy.getLineProcessor());
    }

    public int getAccessCount() {
        return descriptors.size();
    }

    public int getAccessCount(String method) {
        Preconditions.checkNotNull(method);
        Multimap<String, RequestDescriptor> multimap = Multimaps.index(descriptors, new Function<RequestDescriptor, String>() {
            public String apply(RequestDescriptor input) {
                return input.method;
            }
        });
        return multimap.get(method).size();
    }

    public void display() {
        Multimap<String, RequestDescriptor> multimap = Multimaps.index(descriptors, new Function<RequestDescriptor, String>() {
            public String apply(RequestDescriptor input) {
                return input.groupKey;
            }
        });

        for (String groupKey : multimap.keySet()) {
            System.out.println("----------------------------------");
            System.out.println("groupKey : " + groupKey);
            for (RequestDescriptor descriptor : multimap.get(groupKey)) {
                System.out.println("uri : " + descriptor.uri);
            }
        }
    }

    private static enum LineProcessorStrategy {
        EXCEPTION_BLOCK {
            @Override
            LineProcessor<List<RequestDescriptor>> getLineProcessor() {
                return new ExceptionBlockedLineProcessor();
            }
        },
        EXCEPTION_IGNORE {
            @Override
            LineProcessor<List<RequestDescriptor>> getLineProcessor() {
                return new ExceptionIgnoredLineProcessor();
            }
        },
        EXCEPTION_THROWN {
            @Override
            LineProcessor<List<RequestDescriptor>> getLineProcessor() {
                return new ExceptionThrownLineProcessor();
            }
        };

        abstract LineProcessor<List<RequestDescriptor>> getLineProcessor();
    }

    private static class ExceptionIgnoredLineProcessor extends ExceptionBlockedLineProcessor {
        @Override
        public boolean processLine(String line) {
            try {
                super.processLine(line);
            } catch (Exception e) {
                //ignore
            }
            return true;
        }
    }

    private static class ExceptionBlockedLineProcessor extends ExceptionThrownLineProcessor {
        @Override
        public boolean processLine(String line) throws IOException {
            try {
                super.processLine(line);
            } catch (RuntimeException e) {
                return false;
            }
            return true;
        }
    }

    private static class ExceptionThrownLineProcessor implements LineProcessor<List<RequestDescriptor>> {
        List<RequestDescriptor> result = Lists.newArrayList();

        public boolean processLine(String line) throws IOException {
            RequestDescriptor descriptor = new RequestDescriptor(line);
            result.add(descriptor);
            return true;
        }

        public List<RequestDescriptor> getResult() {
            return result;
        }
    }

    public static class RequestDescriptor {
        /*GET或者POST*/
        String method;

        /*Request中的URI*/
        String uri;

        /*分类关键字*/
        String groupKey;

        static final CharMatcher SPLIT_SIGN = CharMatcher.javaDigit().or(CharMatcher.is('?'));

        static final Splitter LINE_SPLITTER = Splitter.on(SPLIT_SIGN).omitEmptyStrings().trimResults();

        RequestDescriptor(String line) {
            Preconditions.checkNotNull(line);
            List<String> splitResult = LINE_SPLITTER.splitToList(line);
            Preconditions.checkState(splitResult.size() >= 2, "%s 格式不正确", line);

            this.method = splitResult.get(0);
            this.uri = splitResult.get(1);

            Preconditions.checkState(this.uri.startsWith("/"));

            int groupKeyEndIndex = this.uri.indexOf("/", 1);
            if (groupKeyEndIndex == -1) {
                groupKeyEndIndex = this.uri.length();
            }
            groupKey = this.uri.substring(1, groupKeyEndIndex);
        }
    }

    //    access.log数据格式如下：
    //    GET /twell/querytwellDetailForMobile.htm?arg1=var1&arg2=var2
    //    POST /qta/roomcontrol/crmapi/query.json
    //    GET /contact/listContactPage.htm?arg1=var1&arg2=var2
    //    GET /user/getUserInfo.json?arg1=var1&arg2=var2
    //    GET /notification/queryMessageByUid.htm?arg1=var1&arg2=var2
    //    GET /location/getOneAuthCity.htm?arg1=var1&arg2=var2
    //    GET /twell/public.htm?arg1=var1&arg2=var2
    public static void main(String[] args) throws IOException {
        String path = "file/access.log";
        AccessLogHelper accessLogHelper = new AccessLogHelper(path, LineProcessorStrategy.EXCEPTION_IGNORE);

        System.out.println("count : " + accessLogHelper.getAccessCount());

        System.out.println("post count : " + accessLogHelper.getAccessCount("POST"));
        System.out.println("get count : " + accessLogHelper.getAccessCount("GET"));

        accessLogHelper.display();
    }
}
